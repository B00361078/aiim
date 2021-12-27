package com.aiim.app.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import com.aiim.app.database.DatabaseConnect;
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

/* The following class handles the dashboard interface by switching the current subScene. Part of MVC design Pattern as a controller.
 * Neil Campbell 14/12/2021, B00361078
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
	public static String currentDirectory;
	
   
	public void initialize() throws Exception, SQLException {
		currentDirectory = Paths.get("").toAbsolutePath().toString();
		strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
		network = new Network();
		appUtil = new AppUtil();
    	reporter.setText(Session.getFullName());
    	con = DatabaseConnect.getConnection();
    	reporter.setText(Session.getFullName());
    	details.setWrapText(true);
    	setRaiseAction();
    }

    public void insertTicket() throws Exception {
	    con.setAutoCommit(false);
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
    	sqlStatement.setInt(4, 1);
    	sqlStatement.setString(5, teamID);
    	sqlStatement.setString(6, teamID);
    	sqlStatement.setString(7, "raised");
    	sqlStatement.setObject(8, appUtil.getDate());
    	sqlStatement.setObject(9, appUtil.getDate());
    		if (sqlStatement.executeUpdate() == 1) {
    			con.commit();
    			System.out.println("Ticket raised");
    		}
    		else {
    			throw new Exception("Unable to insert");
    		}
    }
    
    public void cancel() throws IOException {
    	ViewController.createInstance().switchToView(ViewNames.HOME);
    }
    public void setRaiseAction() {
    	raiseBtn.setOnAction(ae -> {
            ae.consume();
            raiseBtn.setDisable(true);
            MyTask task = new MyTask();
            task.setOnSucceeded(e -> task.getValue());
            Alert alert = appUtil.createProgressAlert(ViewController.createInstance().getCurrentStage(), task);
            appUtil.executeTask(task);
            alert.showAndWait();
            try {
				ViewController.createInstance().switchToView(ViewNames.DASHBOARD);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			});
    	
    }
	private class MyTask extends Task {

		private MyTask() {
            updateTitle("Raise New Ticket");
        }

        @Override
        protected String call() throws Exception {
            updateMessage("Raising ticket, please wait.");
	            if (appUtil.getAIMode().contains("ON")) {
	            	prediction = network.classify(network.restoreModel(currentDirectory + "/files/cnn_model.zip"), details.getText(), Session.getDataIter());
	            }
	            else {
	        	prediction = "general";
	            }
        	insertTicket();
            updateMessage("Ticket raised successfully");
            updateProgress(1, 1);
            return prediction;
        }
    }
}
