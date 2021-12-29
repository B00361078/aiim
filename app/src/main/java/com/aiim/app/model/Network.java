package com.aiim.app.model;

import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;
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
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.exception.ND4JIllegalStateException;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import com.aiim.app.util.Session;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class Network {

    /** Data URL for downloading */
	public static String currentDirectory = Paths.get("").toAbsolutePath().toString();
	private static ComputationGraph model;
	private static int VECTORSIZE;
	private static int FEATURE_MAPS;
	private static PoolingType POOLTYPE;
	private static int OUTPUTS;
	
    /** Location to save and extract the training/testing data */
    //public static final String DATA_PATH = FilenameUtils.concat(System.getProperty("java.io.tmpdir"), "dl4j_w2vSentiment/");
    /** Location (local file system) for the Google News vectors. Set this manually. 
     * @return */
    //public static final String WORD_VECTORS_PATH = "/PATH/TO/YOUR/VECTORS/GoogleNews-vectors-negative300.bin.gz";

    public static ComputationGraph buildModel() throws Exception {
       
        VECTORSIZE = 300;               //Size of the word vectors. 300 in the Google News model
        FEATURE_MAPS = 1000;      //Number of feature maps / channels / depth for each CNN layer
        POOLTYPE = PoolingType.MAX;
        OUTPUTS = 4;
        new Random(12345);

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
                .kernelSize(3,VECTORSIZE)
                .stride(1,VECTORSIZE)
                .nIn(1)
                .nOut(FEATURE_MAPS)
                .build(), "input")
            .addLayer("cnn4", new ConvolutionLayer.Builder()
                .kernelSize(4,VECTORSIZE)
                .stride(1,VECTORSIZE)
                .nIn(1)
                .nOut(FEATURE_MAPS)
                .build(), "input")
            .addLayer("cnn5", new ConvolutionLayer.Builder()
                .kernelSize(5,VECTORSIZE)
                .stride(1,VECTORSIZE)
                .nIn(1)
                .nOut(FEATURE_MAPS)
                .build(), "input")
            .addVertex("merge", new MergeVertex(), "cnn3", "cnn4", "cnn5")      //Perform depth concatenation
            .addLayer("globalPool", new GlobalPoolingLayer.Builder()
                .poolingType(POOLTYPE)
                .build(), "merge")
            .addLayer("out", new OutputLayer.Builder()
                .lossFunction(LossFunctions.LossFunction.MCXENT)
                .activation(Activation.SOFTMAX)
                .nIn(3*FEATURE_MAPS)
                .nOut(OUTPUTS)
                .build(), "globalPool")
            .setOutputs("out")
            .build();

        model = new ComputationGraph(config);
        model.init();
        return model;
    }
    public ComputationGraph retrain(ComputationGraph model, DataSetIterator iter) throws IOException {
		model.fit(iter);
		return model;	
	}
    
    public ComputationGraph restoreModel(String path) throws Exception {
		ComputationGraph model = ModelSerializer.restoreComputationGraph(path);
	return model;
	}
    
    public void saveModel(ComputationGraph model, String path) throws Exception {
    	File modelFile = new File(path);     
    	ModelSerializer.writeModel(model, modelFile, false);
	}
    
    public INDArray getFeatures (String verbatim, DataSetIterator trainIter) {
    	INDArray features = null;
    	System.out.println("train labels - " + trainIter.getLabels());
    	try {
    		features = ((CnnSentenceDataSetIterator) trainIter).loadSingleSentence(verbatim);
    	}
    	catch (ND4JIllegalStateException e) {
    		e.printStackTrace();
    	}
		return features;
    }
    
    public String classify(INDArray features,ComputationGraph model) throws IOException {
    	
    	INDArray predictions = model.outputSingle(features);
        List<String> labels = Session.getPredictionLabels();
        System.out.println("\n\nPredictions for my sentence is:");
	        for( int i=0; i<labels.size(); i++ ){
	            System.out.println("Prediction(" + labels.get(i) + ") = " + predictions.getDouble(i)); 
	        }   
        int maxAt = 0;
	        for (int a = 0; a < predictions.length(); a++) {
	            maxAt = predictions.getDouble(a) > predictions.getDouble(maxAt) ? a : maxAt;
	        }
        System.out.println("max is at " + maxAt);
        String classification = labels.get(maxAt);

        System.out.println(classification);
    	//return the label classification here
		return classification.toString();
    } 
}
    

