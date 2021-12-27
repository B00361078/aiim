package com.aiim.app.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;
import org.deeplearning4j.iterator.LabeledSentenceProvider;
import org.deeplearning4j.iterator.provider.CollectionLabeledSentenceProvider;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import com.aiim.app.util.Session;


public class DataSetIter {
	
	public static String currentDirectory;
	private static WordVectors wordVectors;
	
	public DataSetIter() throws FileNotFoundException {
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
				LabeledSentenceProvider sentenceProvider = new CollectionLabeledSentenceProvider(sentences, sentenceLabels);
				
				return new CnnSentenceDataSetIterator.Builder()
				.sentenceProvider(sentenceProvider)
				.wordVectors(wordVectors)
				.minibatchSize(32)
				.maxSentenceLength(256)
				.useNormalizedWordVectors(false)
				.build();
		}
}
