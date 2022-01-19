package com.aiim.app;
	
import javafx.application.Application;
import org.apache.commons.io.FileUtils;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.util.ResourceBundle;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import com.aiim.app.controller.ViewController;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.task.DataTask;
import com.aiim.app.task.ThreadTask;
import com.aiim.app.util.AppUtil;

/* Main class to start the application.
 * Neil Campbell 19/01/2022, B00361078
 */

public class Main extends Application {
	
	private ResourceBundle strBundle;
	private Stage stage;
	private String currentDirectory;
	public AppUtil appUtil;
	private ThreadTask task;
	private Alert alert;
	private Thread thread;
	
	@Override
    public void start(Stage stage) throws Exception {
		this.stage = stage;
		appUtil = new AppUtil();
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
		if (task.getValue() == null) {
			new javafx.scene.control.Alert(Alert.AlertType.ERROR, strBundle.getString("e11")).showAndWait();
			stage.close();
		}	
	}
	
	public void dbAndModelLoad() throws Exception {
		task = new DataTask(strBundle.getString("dataTaskTitle"));
        task.setOnSucceeded(e -> {
        	alert.close();
        	checkDBConnect();
        	thread.interrupt();
        	}
        );
        alert = appUtil.createProgressAlert(ViewController.createInstance().getCurrentStage(), task);          
        thread = appUtil.startThread(task, strBundle.getString("threadName"));
        alert.show(); 		
	}
}
	
