package com.aiim.app.util;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
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
	private ResultSet rs;
	private PreparedStatement sqlStatement;
	private String currentDirectory;
	private ArrayList<String> list;	
	
	public AppUtil() {
		strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
		con = DatabaseConnect.getConnection();
	}
	public String getMode(String param) throws Exception {
		String mode = null;
		switch(param) {
			case "assignMode":
				sqlStatment = con.prepareStatement(strBundle.getString("sqlSelect1"));
				break;
			case "trainMode":
				sqlStatment = con.prepareStatement(strBundle.getString("sqlSelect9"));
				break;
		}
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
	public void downloadFiles() throws IOException, SQLException {
    	currentDirectory = Paths.get("").toAbsolutePath().toString();
		sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect2"));
		rs = sqlStatement.executeQuery();
			while (rs.next()) {
				String filename = rs.getString(1);
				Blob content = rs.getBlob(2);
				InputStream inputstr = content.getBinaryStream();
				Files.copy(inputstr, Paths.get(currentDirectory+"/files/"+filename), REPLACE_EXISTING);
			}
	}
    
    public void setLabels() throws SQLException {
    	list = new ArrayList<String> ();
    	sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect3"));
		rs = sqlStatement.executeQuery();
			while (rs.next()) {
				String label = rs.getString(1);
				list.add(label);
			}
		//remove general label to leave only prediction labels
		list.remove("General");
		list.sort(String::compareToIgnoreCase);
		Session.setPredictionLabels(list);
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
