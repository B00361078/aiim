package com.aiim.app.cnn;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.*;

/**
 * Convolutional Neural Networks for Sentence Classification - https://arxiv.org/abs/1408.5882
 *
 * Specifically, this is the 'static' model from there
 *
 * @author Alex Black
 */
public class MyCnn {

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

        int cnnLayerFeatureMaps = 100;      //Number of feature maps / channels / depth for each CNN layer
        PoolingType globalPoolingType = PoolingType.MAX;
        Random rng = new Random(12345); //For shuffling repeatability

        //Set up the network configuration. Note that we have multiple convolution layers, each wih filter
        //widths of 3, 4 and 5 as per Kim (2014) paper.

        ComputationGraphConfiguration config = new NeuralNetConfiguration.Builder()
            .weightInit(WeightInit.RELU)
            .activation(Activation.LEAKYRELU)
            .updater(Updater.ADAM)
            .convolutionMode(ConvolutionMode.Same)      //This is important so we can 'stack' the results later
           // .regularization(true).l2(0.0001)
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
        wordVectors = WordVectorSerializer.loadStaticModel(new File(currentDirectory + "/latestVectors.txt")); // need to add new vectors specific for it issues
        //WordVectors wordVectors = WordVectorSerializer.loadStaticModel(new File(WORD_VECTORS_PATH));
        trainIter = getDataSetIterator(true, wordVectors, batchSize, truncateReviewsToLength, rng);
        testIter = getDataSetIterator(false, wordVectors, batchSize, truncateReviewsToLength, rng);

        System.out.println("Starting training");
        for (int i = 0; i < nEpochs; i++) {
            net.fit(trainIter);
            //System.out.println("Epoch " + i + " complete. Starting evaluation:");

            //Run evaluation. This is on 25k reviews, so can take some time
//            Evaluation evaluation = net.evaluate(testIter);
//
//            System.out.println(evaluation.stats());
        }
        System.out.println("saving model");
        File trained_model = new File("trained_model.zip");
    	ModelSerializer.writeModel(net, trained_model, false);
    	
    	ticketClassifier("This is a sentence about guidewire claimcenter");
    }


    private static DataSetIterator getDataSetIterator(boolean isTraining, WordVectors wordVectors, int minibatchSize,
                                                      int maxSentenceLength, Random rng ){
        String path = FilenameUtils.concat(DATA_PATH, (isTraining ? "aclImdb/train/" : "aclImdb/test/"));
        String secdir = FilenameUtils.concat(path, "securitydata");
        String gwDir = FilenameUtils.concat(path, "gwdata");
        String telephonyDir = FilenameUtils.concat(path, "telephonydata");
        String financeDir = FilenameUtils.concat(path, "financedata");
        
        

        File fileSec = new File(secdir);
        File fileGW = new File(gwDir);
        File fileTel = new File(telephonyDir);
        File fileFinance = new File(financeDir);

        Map<String,List<File>> reviewFilesMap = new HashMap<>();
        reviewFilesMap.put("security", Arrays.asList(fileSec.listFiles()));
        reviewFilesMap.put("guidewire", Arrays.asList(fileGW.listFiles()));
        reviewFilesMap.put("telephony", Arrays.asList(fileTel.listFiles()));
        reviewFilesMap.put("finance", Arrays.asList(fileFinance.listFiles()));

        LabeledSentenceProvider sentenceProvider = new FileLabeledSentenceProvider(reviewFilesMap, rng);

        return new CnnSentenceDataSetIterator.Builder()
            .sentenceProvider(sentenceProvider)
            .wordVectors(wordVectors)
            .minibatchSize(minibatchSize)
            .maxSentenceLength(maxSentenceLength)
            .useNormalizedWordVectors(false)
            .build();
    }

    public static INDArray ticketClassifier(String verbatim) {
    	INDArray features = ((CnnSentenceDataSetIterator) trainIter).loadSingleSentence(verbatim);
    	INDArray predictions = net.outputSingle(features);
        List<String> labels = trainIter.getLabels();
               

        System.out.println("\n\nPredictions for my sentence is:");
        for( int i=0; i<labels.size(); i++ ){
            System.out.println("Prediction(" + labels.get(i) + ") = " + predictions.getDouble(i)); 
        }
        
        double max = predictions.getDouble(0);
        
        int i;

        for (i = 1; i < predictions.length(); i++) {
            if (predictions.getDouble(i) > max) {
              max = predictions.getDouble(i);
            }
        }
        System.out.println(i);
        System.out.println(max);
        System.out.println(labels.get(i-1) + " is the prediction");
    	
		return predictions;
    	
    }
   }
    

