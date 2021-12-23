package com.aiim.app.ai;

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

import com.aiim.app.util.Session;


public class AI {
	
	public static String currentDirectory;
	private static WordVectors wordVectors;
	
	public AI() throws FileNotFoundException {
		currentDirectory = Paths.get("").toAbsolutePath().toString();
		wordVectors = WordVectorSerializer.loadStaticModel(new File(currentDirectory + "/files/word_vectors.txt")); 
		
	}
	
		public DataSetIterator getDataSetIterator( ) throws FileNotFoundException{
				List<String> sentences = new ArrayList<>();
				List<String> sentenceLabels = new ArrayList<>();
				for (String label : Session.getPredictionLabels()) {
				
				Scanner trainFile = new Scanner(new File(currentDirectory+ "/files/"+label+".txt"));
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
		
		public void retrain(ComputationGraph model, DataSetIterator iter) throws IOException {
			model.fit(iter);
			File retrained_model = new File("cnn_model.zip");     
	    	ModelSerializer.writeModel(model, retrained_model, false);	
		}
		public void save() {
			
		}
		
		public ComputationGraph restoreModel() throws Exception {
			ComputationGraph model = ModelSerializer.restoreComputationGraph(currentDirectory+"/files/cnn_model.zip");
		return model;
		}

		public String classify(ComputationGraph model,String verbatim, DataSetIterator trainIter) throws IOException {
	    	INDArray features = ((CnnSentenceDataSetIterator) trainIter).loadSingleSentence(verbatim);
	    	INDArray predictions = model.outputSingle(features);
	        List<String> labels = trainIter.getLabels();
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
