package com.aiim.app;
	
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.Connection;

import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;

import com.aiim.app.cnn.CnnSentenceClassificationExample;
import com.aiim.app.cnn.MyCnn;
import com.aiim.app.cnn.WordVector;
import com.aiim.app.controller.ViewController;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.resource.ViewNames;

public class Main extends Application {
	
	private ViewController viewController;
	private Connection con;
	private CnnSentenceClassificationExample cnn;
	
	@Override
    public void start(Stage stage) throws Exception {
		viewController = new ViewController();
		viewController.setCurrentStage(stage);
		viewController.switchToView(ViewNames.LOGIN);
		//con = DatabaseConnect.getConnection();
		//WordVector wv = new WordVector();
		//wv.generateVectors();
		//MyCnn.cnn();
		//CnnSentenceClassificationExample.cnn();
		//System.out.println(con);
		
    }
	
}
