package com.aiim.app.controller;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import org.apache.commons.codec.digest.DigestUtils;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.model.DataSetIter;
import com.aiim.app.model.Network;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.util.AppUtil;
import com.aiim.app.util.Session;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/* The following class handles the login and authentication of the application. Part of MVC design Pattern as a controller.
 * Neil Campbell 06/01/2022, B00361078
 */
 
public class LoginController {
	@FXML private PasswordField passwordField;
    @FXML private TextField usernameField;
    private String saltString;
    private String hashString;
    byte[] fullhashbytes;
    private Connection con;
    private ResourceBundle strBundle;
    byte[] thesalt;
    byte[] passBytes;
    private String teamID;
	private String roleID;
	private int permLevel;
	private String teamName;
	private String user;
	private String fullname;
	private PreparedStatement sqlStatement;
	private ResultSet rs;
	private AppUtil appUtil;
	private DataSetIterator DataSetIterator;
	private Network network;
	private ThreadTask task;
	private Alert alert;
	private Thread thread;
	private static String currentDirectory;
	
	public void initialize() throws Exception {
    	strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
    	con = DatabaseConnect.getConnection(); 
    	appUtil = new AppUtil();
    	currentDirectory = Paths.get("").toAbsolutePath().toString();
    }

    @FXML protected void dashView(ActionEvent event) throws Exception  {
    	
    	if (usernameField.getText() == null | passwordField.getText() == null) {
    		new javafx.scene.control.Alert(Alert.AlertType.ERROR, strBundle.getString("e17")).showAndWait();
    	}
    	else {
        	sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect18"));
        	sqlStatement.setString(1, usernameField.getText());
        	rs = sqlStatement.executeQuery();
	        	while(rs.next()){
	        		user = rs.getString(1);
	        		fullname = rs.getString(4);
	        		hashString = rs.getString(5);
	        		saltString = rs.getString(8);
	        		teamID = rs.getString(6);
					roleID = rs.getString(7);
	            }
        	if (DigestUtils.sha1Hex(passwordField.getText()+saltString).equalsIgnoreCase(hashString)) {
        		Session.setUsername(user);
        		Session.setFullName(fullname);
        		Session.setTeamID(teamID);
        		sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect19"));
        		sqlStatement.setString(1, roleID);
	        	rs = sqlStatement.executeQuery();
		        	while(rs.next()){
		        		permLevel = rs.getInt("permissionLevel");
		            }
		        sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect20"));
		        sqlStatement.setString(1, teamID);
	        	rs = sqlStatement.executeQuery();
		        	while(rs.next()){
		        		teamName = rs.getString("name");
		            }
        		Session.setPermissionLevel(permLevel);
        		Session.setTeamName(teamName);
        		ViewController.createInstance().setCurrentScene(passwordField.getScene());
        		liveModelLoadCheck();
        		ViewController.createInstance().setView(ViewNames.DASHBOARD);
        		ViewController.createInstance().switchToView(ViewNames.HOME);
    		}
    		else {
    			new Alert(Alert.AlertType.ERROR, strBundle.getString("e10")).showAndWait();
        	}
    	}
    }
    //load model at login only
    public void liveModelLoadCheck() throws Exception {
    	
    	if (appUtil.getMode("mlMode").contains("OFF")) {
    		task = new ThreadTask();
            task.setOnSucceeded(e -> task.getValue());
            alert = appUtil.createProgressAlert(ViewController.createInstance().getCurrentStage(), task);          
            thread = appUtil.startThread(task, "dbThread");
            alert.showAndWait();
            thread.interrupt(); 		
    	}
    	else {
    		System.out.println("live loading only");
    	}
    }
    
    
    private class ThreadTask extends Task {

		private ThreadTask() {
			updateTitle("Loading model");
        }
        @Override
        protected String call() throws Exception {
        	updateMessage("Loading model, please wait.");
        	appUtil.setLabels();
    		appUtil.downloadFiles();
    		network = new Network();
    		DataSetIter dataSetIter = new DataSetIter();
    		Session.setModel(network.restoreModel(currentDirectory + "/files/cnn_model.zip"));
    		Session.setDataSetIterator(dataSetIter.getDataSetIterator(true)); 
    		updateMessage("Model loaded successfully");
            updateProgress(1, 1);
            return null;
        }
    }
    
}

