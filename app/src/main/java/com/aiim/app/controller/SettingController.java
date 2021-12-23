package com.aiim.app.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import com.aiim.app.ai.AI;
import com.aiim.app.command.ThreadCommand;
import com.aiim.app.command.UpdateModeCommand;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.util.AppUtil;
import com.aiim.app.util.Session;
import com.aiim.app.util.ThreadTask;

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

public class SettingController extends Task {
	
	//get value from session and set the radio button accordingly
	// allow task to enable/disable in db
	private PreparedStatement sqlStatement;
	private int PERMLEVEL;
	private Connection con;
	private int VIEWPERMLEVEL;
	private String prediction;
	private String teamID;
	@FXML private Button backBtn;
	@FXML private RadioButton radioOn;
	@FXML private RadioButton radioOff;
	private AppUtil appUtil;
	private Alert alert;
	private MyTask task;

	
   
	public void initialize() throws Exception {
		PERMLEVEL = Session.getPermissionLevel();
		appUtil = new AppUtil();
		con = DatabaseConnect.getConnection();
		ToggleGroup radios = new ToggleGroup();
		setRadio(appUtil.getAIMode());
		radioOn.setToggleGroup(radios);
		radioOff.setToggleGroup(radios);
    	setAction(radioOn, "Enabling", "ON");
    	setAction(radioOff, "Disabling", "OFF");
    }
    

    public void setAction (RadioButton button, String message, String mode) {
    	button.setOnAction(event -> {
            event.consume();
            task = new MyTask(message, mode);
            //task = new ThreadTask(message, mode, new UpdateModeCommand());
            task.setOnSucceeded(e -> task.getValue());
            alert = appUtil.createProgressAlert(ViewController.createInstance().getCurrentStage(), task);
            appUtil.executeTask(task);
            alert.show();
		});
    }
    public void setRadio(String mode) throws Exception {
    	switch(mode) {
    	case "ON":
    		radioOn.setSelected(true);
    		radioOff.setSelected(false);
    		break;
    	case "OFF":
    		radioOn.setSelected(false);
    		radioOff.setSelected(true);
    		break;
    	}
    }

//    public void updateMode(String mode) throws Exception {
//	    java.util.Date date = new java.util.Date();
//	    java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());
//	    con.setAutoCommit(false);	 
//	    sqlStatement = con.prepareStatement("USE [honsdb] UPDATE tblClassifier SET mode = ?, modDate = ? WHERE id = 1");	 	
//	    sqlStatement.setString(1, mode);
//	    sqlStatement.setObject(2, sqlDate);
//	    
//	    		if (sqlStatement.executeUpdate() == 1){
//	    			con.commit();
//	    			System.out.println("Mode updated");
//	    		}
//	    		else {
//	    			throw new Exception("Error");
//	    		}
//    	
//    }
    

    public static void callCommand(ThreadCommand command, String mode) throws SQLException, Exception 
    {
        command.execute(mode);
    }
	private class MyTask extends Task {
		
		private final String mystr;
		private final String mode;
				
        private MyTask(String task, String mode) {
            this.mystr = task;
            this.mode = mode;
			updateTitle(this.mystr + " Auto ticket assignment.");
        }

        @Override
        protected String call() throws Exception {
            updateMessage(mystr);
            //updateMode(mode);
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





	@Override
	protected Object call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
    
}
