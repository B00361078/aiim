package com.aiim.app.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;

/* The following class builds WordVectors required for the model along with related methods.
 * Neil Campbell 06/01/2022, B00361078
 */

public class WordVector {
	
	private File VECTOR_FILE;
	private String PATH;
	private int WINDOW_SIZE;
	private int LAYER_SIZE;
	private int MIN_FREQUENCY;
	private int ITERATIONS;
	private int SEED;
	private DefaultTokenizerFactory tokenizer;
	private BasicLineIterator sentenceIter;
	
	public WordVector() {
		WINDOW_SIZE = 5;
		LAYER_SIZE = 200;
		MIN_FREQUENCY = 1;
		ITERATIONS = 100;
		SEED = 42;
		tokenizer = new DefaultTokenizerFactory();
	}
	
	public Word2Vec buildVectors(String rawTextFilePath, String stopWordsPath) throws IOException {
		VECTOR_FILE = new File(rawTextFilePath);
		PATH = VECTOR_FILE.getPath();
	    // Strip white space before and after for each line
	     sentenceIter = new BasicLineIterator(PATH);
	    // Split on white spaces in the line to get words
	    tokenizer.setTokenPreProcessor(new CommonPreprocessor()); 
	    
	    Scanner s = new Scanner(new File(stopWordsPath));
	    ArrayList<String> list = new ArrayList<String>();
	    while (s.hasNextLine()){
	        list.add(s.nextLine());
	    }
	    s.close();
	    
	    Word2Vec vec = new Word2Vec.Builder().stopWords(list)
	            .minWordFrequency(MIN_FREQUENCY)
	            .iterations(ITERATIONS)
	            .layerSize(LAYER_SIZE)
	            .seed(SEED)
	            .windowSize(WINDOW_SIZE)
	            .iterate(sentenceIter)
	            .tokenizerFactory(tokenizer)
	            .build();
	    vec.fit();
	    return vec;
	}
	
	public Collection<String> viewSimilarWords (Word2Vec vec, String target, int numWords) {
		Collection<String> listOfWords = vec.wordsNearest(target, numWords);
	    System.out.println(numWords + "closest to " +target + ": " +listOfWords);
	    return listOfWords;
	}
	
	public void saveVectorToFile(Word2Vec vec, String filePath) {
		WordVectorSerializer.writeWord2VecModel(vec, filePath);
	}

	public WordVectors loadVectors(String path) {
		WordVectors wordVectors = WordVectorSerializer.loadStaticModel(new File(path)); 
		return wordVectors;
	}
}
