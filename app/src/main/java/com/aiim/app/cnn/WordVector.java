package com.aiim.app.cnn;

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
	
	public void generateVectors() throws IOException {
	String currentDirectory = Paths.get("").toAbsolutePath().toString();
	//String filePath = new ClassPathResource(currentDirectory + "/src/main/java/com/aiim/app/cnn/rawdata.txt").getFile().getAbsolutePath();
	//System.out.println(filePath);
	//String currentDirectory = Paths.get("").toAbsolutePath().toString();
	File f = new File(currentDirectory + "/src/main/java/com/aiim/app/cnn/rawtext3.txt");
	String filePath = f.getPath();
	//System.out.println(filePath);

    // Strip white space before and after for each line
    SentenceIterator iter = new BasicLineIterator(filePath);
    // Split on white spaces in the line to get words
    TokenizerFactory t = new DefaultTokenizerFactory();
    
    t.setTokenPreProcessor(new CommonPreprocessor());
    System.out.println("printing out sentences");
    while (iter.hasNext()) {
    	System.out.println(iter.nextSentence());
    }
   
    
    Scanner s = new Scanner(new File(currentDirectory + "/src/main/java/com/aiim/app/cnn/english_stop_words.txt"));
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



    // Write word vectors to file
    WordVectorSerializer.writeWord2VecModel(vec, "latestVectors3.txt");
    
    String word = "guidewire";
    String word2 = "ciso";
    // Prints out the closest 10 words to "day". An example on what to do with these Word Vectors.
    Collection<String> lst = vec.wordsNearest(word, 20);
    Collection<String> lstone = vec.wordsNearest(word2, 20);
    System.out.println("10 Words closest to " +word + ": " +lst);
    System.out.println("10 Words closest to " +word2 + ": " +lstone);
	}

}
