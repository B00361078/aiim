package com.aiim.app;
	
import javafx.application.Application;
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
import com.aiim.app.resource.ViewNames;

public class Main extends Application {
	
	private Connection con;
	private ResourceBundle strBundle;
	private Stage stage;
	private String currentDirectory;
	
	@Override
    public void start(Stage stage) throws Exception {
		this.stage = stage;
		ViewController.createInstance().setCurrentStage(stage);
		ViewController.createInstance().switchToView(ViewNames.LOGIN);
		strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
		currentDirectory = Paths.get("").toAbsolutePath().toString(); 
		con = DatabaseConnect.getConnection();
		checkDBConnect();
    }
	
	@Override
	public void stop() throws IOException{
	    FileUtils.cleanDirectory(new File(currentDirectory+"/files")); 
	    
	}
	
	public void checkDBConnect() {
		if (con == null) {
			new javafx.scene.control.Alert(Alert.AlertType.ERROR, strBundle.getString("e11")).showAndWait();
			stage.close();
		}	
	}	
}
