package com.aiim.app;
	
import javafx.application.Application;
import org.apache.commons.io.FileUtils;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;

import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import com.aiim.app.cnn.CnnSentenceClassificationExample;
import com.aiim.app.cnn.MyCnn;
import com.aiim.app.cnn.MyCnn2;
import com.aiim.app.cnn.MyIter;
import com.aiim.app.cnn.WordVector;
import com.aiim.app.controller.ViewController;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.util.Session;

public class Main extends Application {
	
	private ViewController viewController;
	private Connection con;
	private CnnSentenceClassificationExample cnn;
	private ResourceBundle strBundle;
	private Stage stage;
	//strBundle = ResourceBundle.getBundle("com.aiim.app.resources.bundle");
	private DataSetIterator trainIter;
	
	@Override
    public void start(Stage stage) throws Exception {
		//WordVector wv = new WordVector();
		//wv.generateVectors();
		//MyCnn2.cnn();
		this.stage = stage;
		//ViewController.createInstance();
		//ViewController vc = new ViewController();
		//viewController.createViewController();
		ViewController.createInstance().setCurrentStage(stage);
		//viewController.setCurrentStage(stage);
		//viewController.switchToView(ViewNames.LOGIN);
		ViewController.createInstance().switchToView(ViewNames.LOGIN);
		strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
		con = DatabaseConnect.getConnection();
		System.out.println("con is " + con);
		checkDBConnect();
		//downloadFiles();
		
		//download();er
		//update();
		//upload();
		//WordVector wv = new WordVector();
		//wv.generateVectors();
		//MyCnn2.cnn();
		//CnnSentenceClassificationExample.cnn();
		//System.out.println(con);
		
    }
	@Override
	public void stop() throws IOException{
		String currentDirectory = Paths.get("").toAbsolutePath().toString();
	    System.out.println("Stage is closing");
	    FileUtils.cleanDirectory(new File(currentDirectory+"/resources")); 
	}
	
	void checkDBConnect() {
		if (con != null) {
			System.out.println("connected to db");
		}
		else {
			new javafx.scene.control.Alert(Alert.AlertType.ERROR, strBundle.getString("e11")).showAndWait();
			stage.close();
		}
		
	}
	// upload file from db
	
	public void upload () throws Exception {

		File file;
	    file = new File("cnn_model.zip");// ...(file is initialised)...
	    byte[] fileContent = Files.readAllBytes(file.toPath());
	    String filename = file.getName();
	    String mode = "ON";
	    long filelength = file.length();
	    long filelengthinkb = filelength/1024;
	    java.util.Date date = new java.util.Date();
	    Object param = new java.sql.Timestamp(date.getTime());
	    // The JDBC driver knows what to do with a java.sql type:
	    con.setAutoCommit(false);
	    
	    PreparedStatement prepared_statement3 = con.prepareStatement("USE [honsdb] UPDATE tblClassifier SET fileName=?,size=?,modDate=?,fileContent=? WHERE id=?");
		//prepared_statement3.setLong(1, filelengthinkb);
		//prepared_statement3.setBytes(2, fileContent);
		prepared_statement3.setString(1, filename);
		prepared_statement3.setLong(2, filelengthinkb);
		prepared_statement3.setObject(3, param);
		prepared_statement3.setBytes(4, fileContent);
		prepared_statement3.setInt(5, 1);
	   
	    
	    		
	    		if (prepared_statement3.executeUpdate() == 1)
	    		{
	    			con.commit();
	    			System.out.println("Byte Array Stored Successfully in SQL Server");
	    		}
	    		else
	    		{
	    			throw new Exception("Problem occured during Save");
	    		}
	    
	}
	//download file from db
	public void downloadFiles() throws IOException, SQLException {
		PreparedStatement prepared_statement2 = con.prepareStatement("USE [honsdb] SELECT fileName,fileContent FROM tblClassifier");
		ResultSet rs = prepared_statement2.executeQuery();
		
		while (rs.next()) {
			String filename = rs.getString(1);
			Blob blob = rs.getBlob(2);
			InputStream inputstr = blob.getBinaryStream();
			Files.copy(inputstr, Paths.get(filename));
		}
		
	}
	//update to db
	public void update() throws SQLException, IOException {
		File file;
	    file = new File("trained_model.zip");// ...(file is initialised)...
	    byte[] fileContent = Files.readAllBytes(file.toPath());
	    String filename = file.getName();
	    String mode = "testmode";
	    long filelength = file.length();
	    long filelengthinkb = filelength/1024;
	    con.setAutoCommit(false);
	    //update db
		//PreparedStatement prepared_statement3 = con.prepareStatement("USE [honsdb] UPDATE tblClassifier SET size=?, fileContent=?, mode=? WHERE id=1");
		PreparedStatement prepared_statement3 = con.prepareStatement("USE [honsdb] UPDATE tblClassifier SET mode=? WHERE id=4");
		//prepared_statement3.setLong(1, filelengthinkb);
		//prepared_statement3.setBytes(2, fileContent);
		prepared_statement3.setString(1, "semi");
		
		if (prepared_statement3.executeUpdate() == 1)
		{
			con.commit();
			System.out.println("Updated successfully");
		}
		else
		{
			System.out.println("Problem occured during update");
		}
		
		//prepared_statement3.executeUpdate();
		//System.out.println("updated now");
		
	}
	
}
