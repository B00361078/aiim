package com.aiim.app.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import org.nd4j.linalg.api.ndarray.INDArray;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.model.DataSetIter;
import com.aiim.app.model.Network;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.util.AppUtil;
import com.aiim.app.util.Session;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/* The following class handles the creation of new incidents. Part of MVC design Pattern as a controller.
 * Neil Campbell 06/01/2022, B00361078
 */

public class TicketController {

	private Connection con;
	@FXML private TextArea details;
	@FXML private Label reporter;
	private String prediction;
	private String teamID;
	@FXML private Button raiseBtn;
	private PreparedStatement sqlStatement;
	private Network network;
	private AppUtil appUtil;
	private ResourceBundle strBundle;
	private ResultSet rs;
	private Alert alert;
	private ThreadTask task;
	private Thread thread;
	public static String currentDirectory;
	
	public void initialize() throws Exception, SQLException {
		currentDirectory = Paths.get("").toAbsolutePath().toString();
		strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
		network = new Network();
		appUtil = new AppUtil();
		con = DatabaseConnect.getConnection();
    	reporter.setText(Session.getFullName());
    	reporter.setText(Session.getFullName());
    	details.setWrapText(true);
    	setRaiseAction();
    }

    public void insertTicket() throws Exception {
	    sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect8"));
	    sqlStatement.setString(1, prediction);
	    rs = sqlStatement.executeQuery();
	    	while(rs.next()){
	    		teamID = rs.getString(1);
	        }
    	sqlStatement = con.prepareStatement(strBundle.getString("sqlInsert1"));
    	sqlStatement.setString(1, details.getText());
    	sqlStatement.setString(2, Session.getUsername());
    	sqlStatement.setString(3, null);
    	sqlStatement.setInt(4, appUtil.isAutoAssigned(prediction));
    	sqlStatement.setString(5, teamID);
    	sqlStatement.setString(6, teamID);
    	sqlStatement.setString(7, "Raised");
    	sqlStatement.setObject(8, appUtil.getDate());
    	sqlStatement.setObject(9, appUtil.getDate());
    	appUtil.executeSQL(con, sqlStatement);
    }
    
    public void cancel() throws IOException {
    	ViewController.createInstance().setView(ViewNames.DASHBOARD);	
    	ViewController.createInstance().switchToView(ViewNames.HOME);
    }
    
    public void setRaiseAction() {
    	raiseBtn.setOnAction(ae -> {
            ae.consume();
            raiseBtn.setDisable(true);
            task = new ThreadTask();
            task.setOnSucceeded(e -> task.getValue());
            alert = appUtil.createProgressAlert(ViewController.createInstance().getCurrentStage(), task);          
            thread = appUtil.startThread(task, "dbThread");
            alert.showAndWait();
            try {
            	ViewController.createInstance().setView(ViewNames.DASHBOARD);
				ViewController.createInstance().switchToView(ViewNames.HOME);
				//stop the thread
				thread.interrupt();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
    }
    
	private class ThreadTask extends Task {

		private ThreadTask() {
            updateTitle("Raise New Ticket");
        }

        @Override
        protected String call() throws Exception {
        	
            updateMessage("Raising ticket, please wait.");

            if (appUtil.getMode("assignMode").contains("OFF")) {
            	prediction = "General";
            }
            else if (appUtil.getMode("assignMode").contains("ON")) {
            	appUtil.setLabels();
                appUtil.downloadFiles();
            	DataSetIter dataSetIter = new DataSetIter();
    	        INDArray features = network.getFeatures(details.getText(), dataSetIter.getDataSetIterator(true));
	    	        if (!(features == null)) {
	    	        	prediction = network.classify(features, network.restoreModel(currentDirectory + "/files/cnn_model.zip"));
	    	        }
	    	        else {
	    	        	prediction = "General";
	    	        }
            }
    	    else {
    	    	prediction = "General";
    	    }
        	insertTicket();
            updateMessage("Ticket raised successfully, raised to team - " + prediction);
            updateProgress(1, 1);
            return null;
        }
    }
}
