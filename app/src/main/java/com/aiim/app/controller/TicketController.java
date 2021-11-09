package com.aiim.app.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import com.aiim.app.cnn.MyCnn2;
import com.aiim.app.cnn.MyIter;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.model.Ticket;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.util.Session;
import javafx.fxml.LoadException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/* The following class handles the dashboard interface by switching the current subScene. Part of MVC design Pattern as a controller.
 * Neil Campbell 07/05/2021, B00361078
 */

public class TicketController {

    private ViewController viewController;
	private int permLevel;
	private Connection con;
	private PreparedStatement stmt;
	@FXML private TextArea details;
	@FXML private Label reporter;
	private static DataSetIterator trainIter;
   
    public void initialize() throws IOException, SQLException {
    	reporter.setText(Session.getFullname());
    	details.setWrapText(true);
    	//viewController = ViewController.createInstance();
    	//viewController.setCurrentSubScene(subScene);
    	//menuButton.setText(Session.getFullname());

    	//updateTable();
    	//viewController.switchToView(ViewNames.CLIENTS);
    }
    public void raise() throws Exception {
    	//MyCnn2.cnn();
    	MyIter iter = new MyIter();
    	trainIter = iter.getDataSetIterator();
    	String prediction = iter.ticketClassifier(details.getText(), trainIter);
    	reporter.setText(prediction);
    }
    public void cancel() throws IOException {
    	ViewController.createInstance().switchToView(ViewNames.HOME);
    }
	


    
    
    
}
