package com.aiim.app.cnn;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.deeplearning4j.eval.Evaluation;
//import org.deeplearning4j.examples.recurrent.word2vecsentiment.Word2VecSentimentRNN;
import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;
import org.deeplearning4j.iterator.LabeledSentenceProvider;
import org.deeplearning4j.iterator.provider.CollectionLabeledSentenceProvider;
import org.deeplearning4j.iterator.provider.FileLabeledSentenceProvider;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.layers.Convolution1DLayer;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.GlobalPoolingLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.PoolingType;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.activations.impl.ActivationReLU;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.util.*;

/**
 * Convolutional Neural Networks for Sentence Classification - https://arxiv.org/abs/1408.5882
 *
 * Specifically, this is the 'static' model from there
 *
 * @author Alex Black
 */
public class CnnSentenceClassificationExample {

    /** Data URL for downloading */
    public static final String DATA_URL = "http://ai.stanford.edu/~amaas/data/sentiment/aclImdb_v1.tar.gz";
    /** Location to save and extract the training/testing data */
    public static final String DATA_PATH = FilenameUtils.concat(System.getProperty("java.io.tmpdir"), "dl4j_w2vSentiment/");
    /** Location (local file system) for the Google News vectors. Set this manually. */
    //public static final String WORD_VECTORS_PATH = "C:/UWS/dlj4/deep-learning-samples/dl4j-examples/src/main/java/org/deeplearning4j/examples/convolution/sentenceclassification/model.txt";

    public static void cnn () throws Exception {
        //if(WORD_VECTORS_PATH.startsWith("/PATH/TO/YOUR/VECTORS/")){
          //  throw new RuntimeException("Please set the WORD_VECTORS_PATH before running this example");
        //}

        //Download and extract data
        //Word2VecSentimentRNN.downloadData();
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
                //.trainingWorkspaceMode(WorkspaceMode.SINGLE).inferenceWorkspaceMode(WorkspaceMode.SINGLE)
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
                    .dropOut(0.5)
                    .build(), "merge")
                .addLayer("out", new OutputLayer.Builder()
                    .lossFunction(LossFunctions.LossFunction.MCXENT)
                    .activation(Activation.SOFTMAX)
                    .nIn(3*cnnLayerFeatureMaps)
                    .nOut(2)    //2 classes: positive or negative
                    .build(), "globalPool")
                .setOutputs("out")
                .build();

        ComputationGraph net = new ComputationGraph(config);
        net.init();

        System.out.println("Number of parameters by layer:");
        for(Layer l : net.getLayers() ){
            System.out.println("\t" + l.conf().getLayer().getLayerName() + "\t" + l.numParams());
        }

        //Load word vectors and get the DataSetIterators for training and testing
        System.out.println("Loading word vectors and creating DataSetIterators");
        WordVectors wordVectors = WordVectorSerializer.loadStaticModel(new File("C:/UWS/dlj4/deep-learning-samples/dl4j-examples/src/main/java/org/deeplearning4j/examples/convolution/sentenceclassification/model.txt"));
        //WordVectors wordVectors = WordVectorSerializer.readWord2VecModel("C:/UWS/dlj4/deep-learning-samples/dl4j-examples/src/main/java/org/deeplearning4j/examples/convolution/sentenceclassification/model.txt");
        DataSetIterator trainIter = getDataSetIterator(true, wordVectors, batchSize, truncateReviewsToLength, rng);
        DataSetIterator testIter = getDataSetIterator(false, wordVectors, batchSize, truncateReviewsToLength, rng);
        //new CnnSentenceDataSetIterator(null, wordVectors, null, null, false, batchSize, nEpochs, false, null, vectorSize, truncateReviewsToLength, null, null, cnnLayerFeatureMaps)
        System.out.println("Starting training");

        for (int i = 0; i < nEpochs; i++) {
            net.fit(trainIter);
            System.out.println("Epoch " + i + " complete. Starting evaluation:");

            //Run evaluation. This is on 25k reviews, so can take some time
            Evaluation evaluation = net.evaluate(testIter);

            System.out.println(evaluation.stats());
        }


        //After training: load a single sentence and generate a prediction
        String gwfiletotest = FilenameUtils.concat(DATA_PATH, "aclImdb/test/gw//gw_01.txt");
        String contentsFirstNegative = FileUtils.readFileToString(new File(gwfiletotest));
        INDArray featuresFirstNegative = ((CnnSentenceDataSetIterator)testIter).loadSingleSentence(contentsFirstNegative);

        INDArray predictionsFirstNegative = net.outputSingle(featuresFirstNegative);
        List<String> labels = testIter.getLabels();

        System.out.println("\n\nPredictions for first gw review:");
        for( int i=0; i<labels.size(); i++ ){
            System.out.println("P(" + labels.get(i) + ") = " + predictionsFirstNegative.getDouble(i));
        }
    }


    private static DataSetIterator getDataSetIterator(boolean isTraining, WordVectors wordVectors, int minibatchSize,
                                                      int maxSentenceLength, Random rng ){
    	List<String> gwlabel = new ArrayList<>();
        final List<String> gwtraindata = new ArrayList<>();
        // add lgic for single file iteration
        gwlabel.add("Guidewire");
        gwlabel.add("GW");
        gwtraindata.add("I have an issue with Guidewire PolicyCenter");
        gwtraindata.add("I have an issue with Policcenter");

   
        
        
        SentenceIterator iter = new LineSentenceIterator(new File("C:/UWS/dlj4/deep-learning-samples/dl4j-examples/src/main/java/org/deeplearning4j/examples/convolution/sentenceclassification/rawdata.txt"));
		iter.setPreProcessor(new SentencePreProcessor() {
		    @Override
		    public String preProcess(String sentence) {
		    	gwtraindata.add(sentence);
		    	System.out.println(sentence);
		        return sentence.toLowerCase();
		    }
		});
		
        String path = FilenameUtils.concat(DATA_PATH, (isTraining ? "aclImdb/train/" : "aclImdb/test/"));
        String gwtrain = FilenameUtils.concat(path, "gw");
        String extrain = FilenameUtils.concat(path, "ex");

        File filePositive = new File(gwtrain);
        File fileNegative = new File(extrain);

        Map<String,List<File>> reviewFilesMap = new HashMap<>();
        
        
        reviewFilesMap.put("gw", Arrays.asList(filePositive.listFiles()));
        reviewFilesMap.put("ex", Arrays.asList(fileNegative.listFiles()));
 
        
		LabeledSentenceProvider sentenceProvider = new CollectionLabeledSentenceProvider(gwtraindata, gwlabel);
        

        return new CnnSentenceDataSetIterator.Builder()
            .sentenceProvider(sentenceProvider)
            .wordVectors(wordVectors)
            .minibatchSize(minibatchSize)
            .maxSentenceLength(maxSentenceLength)
            .useNormalizedWordVectors(false)
            .build();
    }
}
