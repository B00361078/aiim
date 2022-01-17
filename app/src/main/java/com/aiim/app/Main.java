package com.aiim.app;
	
import javafx.application.Application;
import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.util.ResourceBundle;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import com.aiim.app.controller.ViewController;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.model.DataSetIter;
import com.aiim.app.model.Network;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.util.AppUtil;
import com.aiim.app.util.Session;

public class Main extends Application {
	
	private Connection con;
	private ResourceBundle strBundle;
	private Stage stage;
	private String currentDirectory;
	public AppUtil appUtil;
	private ThreadTask task;
	private Alert alert;
	private Network network;
	private Thread thread;
	
	@Override
    public void start(Stage stage) throws Exception {
		this.stage = stage;
		appUtil = new AppUtil();
		network = new Network();
		ViewController.createInstance().setCurrentStage(stage);
		ViewController.createInstance().switchToView(ViewNames.LOGIN);
		strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
		currentDirectory = Paths.get("").toAbsolutePath().toString(); 
    	dbAndModelLoad();
    }
	
	@Override
	public void stop() throws IOException{
		try {
			FileUtils.cleanDirectory(new File(currentDirectory+"/files"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void checkDBConnect() {
		if (con == null) {
			new javafx.scene.control.Alert(Alert.AlertType.ERROR, strBundle.getString("e11")).showAndWait();
			stage.close();
		}	
	}
	
	public void dbAndModelLoad() throws Exception {
		task = new ThreadTask();
        task.setOnSucceeded(e -> {
        	alert.close();
        	checkDBConnect();
        	thread.interrupt();
        	}
        );
        alert = appUtil.createProgressAlert(ViewController.createInstance().getCurrentStage(), task);          
        thread = appUtil.startThread(task, "dbThread");
        alert.show(); 		
	}
	
	private class ThreadTask extends Task {
		
		private ThreadTask() {
			updateTitle("Loading data");
			
	    }
	    @Override
	    protected String call() throws Exception {
	    	updateMessage("Checking database connection, please wait.");
	    	con = DatabaseConnect.getConnection();
	    	if (con != null) {
	    		updateMessage("Database connected, loading model, please wait.");
		    	appUtil.setLabels();
				appUtil.downloadFiles();
				network = new Network();
				DataSetIter dataSetIter = new DataSetIter();
				Session.setModel(network.restoreModel(currentDirectory + "/files/cnn_model.zip"));
				Session.setDataSetIterator(dataSetIter.getDataSetIterator(true)); 
				updateMessage("Model loaded successfully");
		        updateProgress(1, 1);
	    	}
	        return null;
	    }
	}
}
	
