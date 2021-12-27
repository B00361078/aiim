package com.aiim.app.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

public class WordVector {
	
	//String filePath = new ClassPathResource("raw_sentences.txt").getFile().getAbsolutePath();
	private File VECTOR_FILE;
	private String PATH;
	
	public Word2Vec buildVectors(String rawTextFilePath, String stopWordsPath) throws IOException {
	VECTOR_FILE = new File(rawTextFilePath);
	PATH = VECTOR_FILE.getPath();
	//System.out.println(filePath);

    // Strip white space before and after for each line
    SentenceIterator iter = new BasicLineIterator(PATH);
    // Split on white spaces in the line to get words
    TokenizerFactory t = new DefaultTokenizerFactory();
    
    t.setTokenPreProcessor(new CommonPreprocessor());
    System.out.println("printing out sentences");
    while (iter.hasNext()) {
    	System.out.println(iter.nextSentence());
    }
   
    
    Scanner s = new Scanner(new File(stopWordsPath));
    ArrayList<String> list = new ArrayList<String>();
    while (s.hasNextLine()){
        list.add(s.nextLine());
    }
    s.close();
    
    

    Word2Vec vec = new Word2Vec.Builder().stopWords(list)
            .minWordFrequency(1)
            .iterations(100)
            .layerSize(200)
            .seed(42)
            .windowSize(5)
            .iterate(iter)
            .tokenizerFactory(t)
            .build();
    vec.fit();
    return vec;
	}
	
	public Collection<String> viewSimilarWords (Word2Vec vec, String target, int numWords) {
		Collection<String> listOfWords = vec.wordsNearest(target, numWords);
	    System.out.println(numWords + "closest to " +target + ": " +listOfWords);
	    return listOfWords;
	}
	
	public void saveVectorToFile(Word2Vec vec) {
		WordVectorSerializer.writeWord2VecModel(vec, "latestVectors5.txt");
	}

}
