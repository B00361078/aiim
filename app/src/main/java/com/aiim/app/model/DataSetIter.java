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
	
		public DataSetIterator getDataSetIterator(boolean isTrainFiles) throws FileNotFoundException{
				List<String> sentences = new ArrayList<>();
				List<String> sentenceLabels = new ArrayList<>();
				for (String label : Session.getPredictionLabels()) {
					System.out.println(label);
					Scanner trainFile = new Scanner(new File(currentDirectory+(isTrainFiles ? "/files/" : "/testFiles/")+label+".txt"));
					System.out.println("trainfile is " +trainFile);
						while (trainFile.hasNextLine()){
						sentences.add(trainFile.nextLine());
						sentenceLabels.add(label);
						}
					trainFile.close();
				}
				LabeledSentenceProvider sentenceProvider = new CollectionLabeledSentenceProvider(sentences, sentenceLabels);
				System.out.println("done");
				return new CnnSentenceDataSetIterator.Builder()
				.sentenceProvider(sentenceProvider)
				.wordVectors(wordVectors)
				.minibatchSize(32)
				.maxSentenceLength(256)
				.useNormalizedWordVectors(false)
				.build();
		}
}
