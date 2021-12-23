package com.aiim.app.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ResourceBundle;
import com.aiim.app.database.DatabaseConnect;

import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;

public class AppUtil {
	
	private ResourceBundle strBundle;
	private PreparedStatement sqlStatment;
	private Connection con;
	private String statement;
	private ResultSet rs;
	private PreparedStatement sqlStatement;	
	
	public AppUtil() {
		strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
		con = DatabaseConnect.getConnection();
	}
	public String getAIMode() throws Exception {
		String mode = null;
		statement = strBundle.getString("sqlSelect1");
		sqlStatment = con.prepareStatement(statement);
		sqlStatment.setInt(1, 1);
		rs = sqlStatment.executeQuery();
			while(rs.next()){
	    		mode = rs.getString(1);
	        }
		return mode;
	}
	
	public Timestamp getDate() {
		java.util.Date date = new java.util.Date();
	    Timestamp timeStamp = new java.sql.Timestamp(date.getTime());
	    return timeStamp;
	}
	
	public Alert createProgressAlert(Stage owner, Task<?> task) {
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
	public void executeTask(Task<?> task) {
        Thread dbThread = new Thread(task, "dbThread");
        dbThread.setDaemon(true);
        dbThread.start();
    }
	
	public void uploadFile(String fileName, String mode) throws Exception {
		File file;
		String currentDirectory = Paths.get("").toAbsolutePath().toString();
		file = new File(currentDirectory + "/files/"+fileName);
	    byte[] fileContent = Files.readAllBytes(file.toPath());
	    long filelengthinkb = file.length()/1024;
	    con.setAutoCommit(false);
	    
	    sqlStatement = con.prepareStatement("USE [honsdb] UPDATE tblClassifier SET fileName=?,size=?,modDate=?,fileContent=? WHERE fileName=?");
	    sqlStatement.setString(1, fileName);
	    sqlStatement.setLong(2, filelengthinkb);
	    sqlStatement.setObject(3, getDate());
	    sqlStatement.setBytes(4, fileContent);
	    sqlStatement.setString(5, fileName);
    		if (sqlStatement.executeUpdate() == 1) {
    			con.commit();
    			System.out.println("Byte Array Stored Successfully in SQL Server");
    		}
    		else {
    			throw new Exception("Problem occured during Save");
    		}
	}
}
