package com.aiim.app.util;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.nn.graph.ComputationGraph;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.model.DataSetIter;
import com.aiim.app.model.Network;
import com.aiim.app.model.WordVector;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;

/* The following class holds common methods used across the application.
 * Neil Campbell 06/01/2022, B00361078
 */

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
		currentDirectory = Paths.get("").toAbsolutePath().toString();
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
	
	public int isAutoAssigned(String prediction) {
    	if (prediction.contains("General")) {
    		return 0;
    	}
    	else {
    		return 1;
    	}
    }
	
	public Thread startThread(Task task, String name) {
		Thread thread = new Thread(task, name);
        thread.setDaemon(true);
        thread.start();
        return thread;
	}
	
	public void executeSQL(Connection con, PreparedStatement sqlStatement) throws Exception {
		con.setAutoCommit(false);
		if (sqlStatement.executeUpdate() == 1) {
			con.commit();
		}
		else {
			throw new Exception(strBundle.getString("e3"));
		}
	}
	
	public void appendToFile(String filename, String details) throws IOException  {
		FileWriter fw = new FileWriter(currentDirectory+"/files/"+filename, true);
	    BufferedWriter bw = new BufferedWriter(fw);
	    bw.newLine();
	    bw.write(details);
	    bw.close();
	}
	
	public void retrain(String filename, String details) throws Exception {  	
		Network network = new Network();
		DataSetIter dataSetIter = new DataSetIter();
	    ComputationGraph currentModel = network.restoreModel(currentDirectory + "/files/cnn_model.zip");
		appendToFile(filename, details);
	    network.train(currentModel, dataSetIter.getDataSetIterator(true));
    	network.saveModel(currentModel, currentDirectory + "/files/cnn_model.zip");
    	updateFile(filename);
    	updateFile("cnn_model.zip");
    }
	//developer methods
	public void uploadNewModel () throws Exception {
		setLabels();
		downloadFiles();
		Network network = new Network();
		ComputationGraph model = network.buildModel();
		DataSetIter dateSetIter = new DataSetIter();
		ComputationGraph trainedModel = network.train(model, dateSetIter.getDataSetIterator(true));
		network.saveModel(trainedModel, currentDirectory + "/files/cnn_model.zip");
		updateFile("cnn_model.zip");
	}
	//developer methods
	public void uploadNewVectors() throws Exception {
		setLabels();
		downloadFiles();
		WordVector wv = new WordVector();
		Word2Vec wordVec = wv.buildVectors(currentDirectory + "/files/vectors_raw.txt", currentDirectory + "/files/stop_words.txt");
		wv.saveVectorToFile(wordVec, currentDirectory + "/files/word_vectors.txt");
		updateFile("word_vectors.txt");
	}
	
	public void updateFile(String filename) throws Exception {
	    File file = new File(currentDirectory+"/files/"+filename);
	    byte[] fileContent = Files.readAllBytes(file.toPath());
	    long filelength = file.length();
	    long filelengthinkb = filelength/1024;
		sqlStatement = con.prepareStatement(strBundle.getString("sqlUpdate6"));
		sqlStatement.setLong(1, filelengthinkb);
		sqlStatement.setObject(2, getDate());
		sqlStatement.setBytes(3, fileContent);
		sqlStatement.setString(4, filename);
		executeSQL(con, sqlStatement);
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
}
