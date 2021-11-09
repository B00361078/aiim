package com.aiim.app.cnn;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;
import org.deeplearning4j.iterator.CnnSentenceDataSetIterator.Builder;
import org.deeplearning4j.iterator.LabeledSentenceProvider;
import org.deeplearning4j.iterator.provider.CollectionLabeledSentenceProvider;
import org.deeplearning4j.iterator.provider.FileLabeledSentenceProvider;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.GlobalPoolingLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.PoolingType;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.util.*;

/**
 * Convolutional Neural Networks for Sentence Classification - https://arxiv.org/abs/1408.5882
 *
 * Specifically, this is the 'static' model from there
 *
 * @author Alex Black
 */
public class MyCnn2 {

    /** Data URL for downloading */
	public static String currentDirectory = Paths.get("").toAbsolutePath().toString();
	private static WordVectors wordVectors;
	private static TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
	private static DataSetIterator trainIter;
	private static DataSetIterator testIter;
	private static ComputationGraph net;
    /** Location to save and extract the training/testing data */
    public static final String DATA_PATH = FilenameUtils.concat(System.getProperty("java.io.tmpdir"), "dl4j_w2vSentiment/");
    /** Location (local file system) for the Google News vectors. Set this manually. */
    //public static final String WORD_VECTORS_PATH = "/PATH/TO/YOUR/VECTORS/GoogleNews-vectors-negative300.bin.gz";

    public static void cnn() throws Exception {
       
    	System.out.println(DATA_PATH);
        //Basic configuration
        int batchSize = 32;
        int vectorSize = 300;               //Size of the word vectors. 300 in the Google News model
        int nEpochs = 1;                    //Number of epochs (full passes of training data) to train on
        int truncateReviewsToLength = 256;  //Truncate reviews with length (# words) greater than this

        int cnnLayerFeatureMaps = 1000;      //Number of feature maps / channels / depth for each CNN layer
        PoolingType globalPoolingType = PoolingType.MAX;
        Random rng = new Random(12345); //For shuffling repeatability

        //Set up the network configuration. Note that we have multiple convolution layers, each wih filter
        //widths of 3, 4 and 5 as per Kim (2014) paper.

        ComputationGraphConfiguration config = new NeuralNetConfiguration.Builder()
            .weightInit(WeightInit.RELU)
            .activation(Activation.LEAKYRELU)
            .updater(Updater.ADAM)
            .convolutionMode(ConvolutionMode.Same)      //This is important so we can 'stack' the results later
            .regularization(true).l2(0.0001)
            .learningRate(0.01)
            .graphBuilder()
            .addInputs("input")
            .addLayer("cnn3", new ConvolutionLayer.Builder()
                .kernelSize(3,vectorSize)
                .stride(1,vectorSize)
                .nIn(1)
                .nOut(cnnLayerFeatureMaps)
                .build(), "input")
            .addLayer("cnn4", new ConvolutionLayer.Builder()
                .kernelSize(4,vectorSize)
                .stride(1,vectorSize)
                .nIn(1)
                .nOut(cnnLayerFeatureMaps)
                .build(), "input")
            .addLayer("cnn5", new ConvolutionLayer.Builder()
                .kernelSize(5,vectorSize)
                .stride(1,vectorSize)
                .nIn(1)
                .nOut(cnnLayerFeatureMaps)
                .build(), "input")
            .addVertex("merge", new MergeVertex(), "cnn3", "cnn4", "cnn5")      //Perform depth concatenation
            .addLayer("globalPool", new GlobalPoolingLayer.Builder()
                .poolingType(globalPoolingType)
                .build(), "merge")
            .addLayer("out", new OutputLayer.Builder()
                .lossFunction(LossFunctions.LossFunction.MCXENT)
                .activation(Activation.SOFTMAX)
                .nIn(3*cnnLayerFeatureMaps)
                .nOut(4)    //2 classes: guidewire or ciso
                .build(), "globalPool")
            .setOutputs("out")
            .build();

        net = new ComputationGraph(config);
        net.init();

        System.out.println("Number of parameters by layer:");
        for(Layer l : net.getLayers() ){
            System.out.println("\t" + l.conf().getLayer().getLayerName() + "\t" + l.numParams());
        }

        //Load word vectors and get the DataSetIterators for training and testing
        System.out.println("Loading word vectors and creating DataSetIterators");
        wordVectors = WordVectorSerializer.loadStaticModel(new File(currentDirectory + "/latestVectors5.txt")); // need to add new vectors specific for it issues
        //WordVectors wordVectors = WordVectorSerializer.loadStaticModel(new File(WORD_VECTORS_PATH));
        trainIter = getDataSetIterator(true, wordVectors, batchSize, truncateReviewsToLength, rng);

        //testIter = getDataSetIterator(false, wordVectors, batchSize, truncateReviewsToLength, rng);

        System.out.println("Starting training");
        for (int i = 0; i < nEpochs; i++) {
            net.fit(trainIter);
            //System.out.println("Epoch " + i + " complete. Starting evaluation:");

            //Run evaluation. This is on 25k reviews, so can take some time
//            Evaluation evaluation = net.evaluate(testIter);
//
//            System.out.println(evaluation.stats());
        }
        System.out.println("done");
//        String currentDirectory = Paths.get("").toAbsolutePath().toString();
//        System.out.println(currentDirectory);
//        ComputationGraph model = ModelSerializer.restoreComputationGraph(currentDirectory+"/trained_model.zip");
//        System.out.println("here - " + model.getInputs());
//        INDArray features2 = ((CnnSentenceDataSetIterator) trainIter).loadSingleSentence("guidewire");
//        System.out.println("features2 is  " + features2);
//        File file = new File(currentDirectory+"/myfile");
//        saveBinary(features2, file);
//        INDArray featuresfinal = readBinary(file);
//        System.out.println("featuresfinal is  " + featuresfinal);
        
        
        //INDArray arr = model.getInputs();
        //System.out.println(myint);
        //INDArray labels = model.getLabels();
        //System.out.println("this is labels" + labels);
        System.out.println("saving model");
        File trained_model = new File("trained_model_latest.zip");     
    	ModelSerializer.writeModel(net, trained_model, false);
    	
    	//ticketClassifier("Guidewire services SVC GUIDEWIRE CLAIMCENTER SVC GUIDEWIRE CONTACTMANAGER SVC GUIDEWIRE POLICYCENTER These services are showing as pipeline. They are live services and need to be updated to reflect that. Please check any other HSN's or services that relate to Guidewire.\r\n"
    		//	);
    }

    public static void saveBinary(INDArray arr, File saveTo) throws IOException {
    	  BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(saveTo));
    	  DataOutputStream dos = new DataOutputStream(bos);
    	  Nd4j.write(arr, dos);
    	  dos.flush();
    	  dos.close();
    	  bos.close();
    	}
   


    public static INDArray readBinary(File myfile) throws IOException {
  	  INDArray myarr = Nd4j.readBinary(myfile);
  	  return myarr;

  	}// split this out, can either be train, test or update only - try loading with single sentence iterator
    private static DataSetIterator getDataSetIterator(boolean isTraining, WordVectors wordVectors, int minibatchSize,
                                                      int maxSentenceLength, Random rng ) throws FileNotFoundException{
    	List<String> outcomeLabels = new ArrayList<>();
    	List<String> sentences = new ArrayList<>();
    	List<String> sentenceLabels = new ArrayList<>();
    	outcomeLabels.add("finance");
    	outcomeLabels.add("guidewire");
    	outcomeLabels.add("security");
    	outcomeLabels.add("telephony");
    	for (String label : outcomeLabels) {
    		
    		Scanner trainFile = new Scanner(new File(currentDirectory+ "/"+label+".txt"));
    		System.out.println("trainfile is " +trainFile);
    		while (trainFile.hasNextLine()){
                sentences.add(trainFile.nextLine());
                sentenceLabels.add(label);
            }
    		trainFile.close();
        }
    	System.out.println(sentences);
    	System.out.println(sentenceLabels);
    	
        LabeledSentenceProvider sentenceProvider = new CollectionLabeledSentenceProvider(sentences, sentenceLabels);

        return new CnnSentenceDataSetIterator.Builder()
            .sentenceProvider(sentenceProvider)
            .wordVectors(wordVectors)
            .minibatchSize(minibatchSize)
            .maxSentenceLength(maxSentenceLength)
            .useNormalizedWordVectors(false)
            .build();
    }

    public static void ticketClassifier(String verbatim) throws IOException {
    	ComputationGraph model = ModelSerializer.restoreComputationGraph(currentDirectory+"/trained_model.zip");
    	//File file = new File(currentDirectory+"/myfile");
    	//INDArray features = readBinary(file);
    	MyIter iter = new MyIter();
    
    	//INDArray features = trainIter().loadSingleSentence(verbatim);
		//CnnSentenceDataSetIterator = new CnnSentenceDataSetIterator();
    	

    	//INDArray predictions = model.outputSingle(features);
        List<String> labels = trainIter.getLabels();

               

        System.out.println("\n\nPredictions for my sentence is:");
        for( int i=0; i<labels.size(); i++ ){
        	
         //   System.out.println("Prediction(" + labels.get(i) + ") = " + predictions.getDouble(i)); 
            //System.out.printf("Prediction: %f\n", predictions.getDouble(i));
        }
//                
//        int maxAt = 0;
//
//        for (int a = 0; a < predictions.length(); a++) {
//            maxAt = predictions.getDouble(a) > predictions.getDouble(maxAt) ? a : maxAt;
//        }
//        System.out.println("max is at " + maxAt);
//        String classification = labels.get(maxAt);
//
//        System.out.println(classification);
//    	//return the label classification here
	//return classification.toString();
//    	
    }
   }
    

