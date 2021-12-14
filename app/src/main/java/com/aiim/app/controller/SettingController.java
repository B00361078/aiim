package com.aiim.app.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import com.aiim.app.cnn.MyIter;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.util.Session;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

/* The following class handles the dashboard interface by switching the current subScene. Part of MVC design Pattern as a controller.
 * Neil Campbell 14/12/2021, B00361078
 */

public class SettingController {
	
	//get value from session and set the radio button accordingly
	// allow task to enable/disable in db

	private int permLevel;
	private Connection con;
	private PreparedStatement stmt;
	private int VIEWPERMLEVEL;
	private String prediction;
	private String teamID;
	@FXML private Button backBtn;
	@FXML private RadioButton radioOn;
	@FXML private RadioButton radioOff;
	
   
	public void initialize() throws IOException, SQLException {
		ToggleGroup group = new ToggleGroup();
		radioOn.setToggleGroup(group);
		radioOff.setToggleGroup(group);
    	con = DatabaseConnect.getConnection();
    	VIEWPERMLEVEL = 5;
    	checkHasPermission();

    	radioOn.setOnAction(ae -> {
            ae.consume();
            //radioOn.setDisable(true);
            MyTask task = new MyTask("Enabling");
            task.setOnSucceeded(e -> task.getValue());
            Alert alert = createProgressAlert(ViewController.createInstance().getCurrentStage(), task);
            executeTask(task);
            alert.show();
			});
    	radioOff.setOnAction(ae -> {
            ae.consume();
            //radioOn.setDisable(true);
            MyTask task = new MyTask("Disabling");
            task.setOnSucceeded(e -> task.getValue());
            Alert alert = createProgressAlert(ViewController.createInstance().getCurrentStage(), task);
            executeTask(task);
            alert.show();
			});
    }
    
    private void checkHasPermission() {
    	if(Session.getPermissionLevel() >= VIEWPERMLEVEL) {
    		
    	}
    }

    public void insert() throws Exception {
    	PreparedStatement prepared_statement = null;
	    java.util.Date date = new java.util.Date();
	    java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());
	    con.setAutoCommit(false);
	    stmt = con.prepareStatement("USE [honsdb] SELECT* FROM tblTeam WHERE name = '" +prediction+"'");
	    ResultSet rs = stmt.executeQuery();
    	while(rs.next()){
    		teamID = rs.getString(1);
        }
	    prepared_statement = con.prepareStatement("USE [honsdb] INSERT INTO tblTicket (detail,reporter,assignee,autoGenerated,assignedTeam,updatedTeam,status,dateRaised,dateUpdated) VALUES(?,?,?,?,?,?,?,?,?)");
	    
	    	
	    		prepared_statement.setString(2, Session.getUsername());
	    		prepared_statement.setString(3, Session.getUsername());
	    		prepared_statement.setInt(4, 1);
	    		prepared_statement.setString(5, teamID);
	    		prepared_statement.setString(6, teamID);
	    		prepared_statement.setString(7, "raised");
	    		prepared_statement.setObject(8, sqlDate);
	    		prepared_statement.setObject(9, sqlDate);
	    		
	    		
	    		if (prepared_statement.executeUpdate() == 1)
	    		{
	    			con.commit();
	    			System.out.println("Ticket raised");
	    		}
	    		else
	    		{
	    			throw new Exception("Error");
	    		}
    	
    }
    
    public void cancel() throws IOException {
    	ViewController.createInstance().switchToView(ViewNames.HOME);
    }
	private class MyTask extends Task {
		
		private final String mystr;
				
        private MyTask(String string) {
            this.mystr = string;
			updateTitle(this.mystr + " Auto ticket assignment.");
        }

        @Override
        protected String call() throws Exception {
            updateMessage(this.mystr);
            System.out.println(mystr);

      
        	//insert();
            TimeUnit.SECONDS.sleep(3);
            updateMessage(this.mystr + " Auto ticket assignment was successful.");
            updateProgress(1, 1);
            return mystr;
        }

        @Override
        protected void running() {
            System.out.println("Raising ticket task is running...");
        }

        @Override
        protected void succeeded() {
            System.out.println("Raising ticket task is successful.");
        }
        
    }
    private void executeTask(Task<?> task) {
        Thread dbThread = new Thread(task, "dbThread");
        dbThread.setDaemon(true);
        dbThread.start();
    }

    // creates the Alert and necessary controls to observe the task
    private Alert createProgressAlert(Stage owner, Task<?> task) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.initOwner(owner);
        alert.titleProperty().bind(task.titleProperty());
        alert.contentTextProperty().bind(task.messageProperty());

        ProgressIndicator pIndicator = new ProgressIndicator();
        pIndicator.progressProperty().bind(task.progressProperty());
        alert.setGraphic(pIndicator);

        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
        alert.getDialogPane().lookupButton(ButtonType.OK)
                .disableProperty().bind(task.runningProperty());       

        alert.getDialogPane().cursorProperty().bind(
		Bindings.when(task.runningProperty())
                    .then(Cursor.WAIT)
                    .otherwise(Cursor.DEFAULT)
        );

        return alert;
    }
    @FXML protected void back() throws IOException {
    	ViewController.createInstance().switchToView(ViewNames.DASHBOARD);
    }
    
}
