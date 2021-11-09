package com.aiim.app.cnn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;
import org.deeplearning4j.iterator.LabeledSentenceProvider;
import org.deeplearning4j.iterator.provider.CollectionLabeledSentenceProvider;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;


public class MyIter {
	
	public static String currentDirectory;
	private static WordVectors wordVectors;
	
	public MyIter() {
		currentDirectory = Paths.get("").toAbsolutePath().toString();
		wordVectors = WordVectorSerializer.loadStaticModel(new File(currentDirectory + "/latestVectors5.txt")); 
	}
	
		public DataSetIterator getDataSetIterator( ) throws FileNotFoundException{
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
				.minibatchSize(32)
				.maxSentenceLength(256)
				.useNormalizedWordVectors(false)
				.build();
		}
		public String ticketClassifier(String verbatim, DataSetIterator trainIter) throws IOException {
	    	ComputationGraph model = ModelSerializer.restoreComputationGraph(currentDirectory+"/trained_model_latest.zip");
	    	//File file = new File(currentDirectory+"/myfile");
	    	//INDArray features = readBinary(file);
	    	MyIter iter = new MyIter();
	    
	    	INDArray features = ((CnnSentenceDataSetIterator) iter.getDataSetIterator()).loadSingleSentence(verbatim);
			//CnnSentenceDataSetIterator = new CnnSentenceDataSetIterator();
	    	

	    	INDArray predictions = model.outputSingle(features);
	        List<String> labels = trainIter.getLabels();

	               

	        System.out.println("\n\nPredictions for my sentence is:");
	        for( int i=0; i<labels.size(); i++ ){
	        	
	            System.out.println("Prediction(" + labels.get(i) + ") = " + predictions.getDouble(i)); 
	            //System.out.printf("Prediction: %f\n", predictions.getDouble(i));
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
