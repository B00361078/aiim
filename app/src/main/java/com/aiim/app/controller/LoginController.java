package com.aiim.app.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Scanner;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.digest.DigestUtils;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.model.DataSetIter;
import com.aiim.app.model.Network;
import com.aiim.app.model.WordVector;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.util.AppUtil;
import com.aiim.app.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/* The following class handles the login and authentication of the application. Part of MVC design Pattern as a controller.
 * Neil Campbell 07/05/2021, B00361078
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
	private String currentDirectory;
	private WordVector wordVec;
	private Word2Vec wv;
	private ComputationGraph model;
	private ComputationGraph trainedModel;
	private WordVectors wordVectors;
	private DataSetIterator testIter;
        
    public void initialize() throws Exception {
    	strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
    	ViewController.createInstance();
    	con = DatabaseConnect.getConnection();
    	currentDirectory = Paths.get("").toAbsolutePath().toString();   	
    }

    @FXML protected void dashView(ActionEvent event) throws IOException, SQLException, ClassNotFoundException, NoSuchAlgorithmException, DecoderException  {
    	Scene scene = passwordField.getScene();
    	String username = usernameField.getText();
    	String password = passwordField.getText();
    	if (username == null | password == null) {
    		new javafx.scene.control.Alert(Alert.AlertType.ERROR, strBundle.getString("e17")).showAndWait();
    	}
    	else {
        	sqlStatement = con.prepareStatement("USE [honsdb] SELECT* FROM tblUser WHERE username = '" +username+"'");
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
        		sqlStatement = con.prepareStatement("USE [honsdb] SELECT* FROM tblRole WHERE roleID = '" +roleID+"'");
	        	rs = sqlStatement.executeQuery();
		        	while(rs.next()){
		        		permLevel = rs.getInt("permissionLevel");
		            }
		        sqlStatement = con.prepareStatement("USE [honsdb] SELECT* FROM tblTeam WHERE teamID = '" +teamID+"'");
	        	rs = sqlStatement.executeQuery();
		        	while(rs.next()){
		        		teamName = rs.getString("name");
		            }
        		Session.setPermissionLevel(permLevel);
        		Session.setTeamName(teamName);
        		
        		ViewController.createInstance().setCurrentScene(scene);
        		ViewController.createInstance().switchToView(ViewNames.HOME);
    			}
    		else {
    			new Alert(Alert.AlertType.ERROR, strBundle.getString("e10")).showAndWait();

        	}
	
    	}
    }

	private void checkSentences() throws SQLException, IOException {
		Network net = new Network();
		DataSetIter dataSetIter = new DataSetIter();
		appUtil = new AppUtil();// TODO Auto-generated method stub
		appUtil.setLabels();
		
		//for (String label : Session.getPredictionLabels()) {
			//String finalFile = "final_"+label+ ".txt";
			Scanner trainFile = new Scanner(new File(currentDirectory+ "/files/vectorTestRaw.txt"));
			System.out.println("trainfile is " +trainFile);
				while (trainFile.hasNextLine()){
					String myline = trainFile.nextLine();
					INDArray feat = net.getFeatures(myline, dataSetIter.getDataSetIterator(true) );
					if (feat != null) {
						System.out.println("line is good");
						FileWriter fw = new FileWriter(currentDirectory+"/testFiles/data.txt", true);
					    BufferedWriter bw = new BufferedWriter(fw);
					    bw.newLine();
					    bw.write(myline);
					    bw.close();
					}
					else 
						System.out.println("line is bad");
				}
			trainFile.close();
		//}			
	}
	public void updateFile(String filename) throws SQLException, IOException {
		File file;
	    file = new File(currentDirectory+"/files/"+filename);// ...(file is initialised)...
	    byte[] fileContent = Files.readAllBytes(file.toPath());
	    //String mode = "testmode";
	    long filelength = file.length();
	    long filelengthinkb = filelength/1024;
	    con.setAutoCommit(false);
		sqlStatement = con.prepareStatement("USE [honsdb] UPDATE tblClassifier SET size=?, modDate=?, fileContent=? WHERE fileName=?");
		sqlStatement.setLong(1, filelengthinkb);
		sqlStatement.setObject(2, appUtil.getDate());
		sqlStatement.setBytes(3, fileContent);
		sqlStatement.setString(4, filename);
		
		if (sqlStatement.executeUpdate() == 1)
		{
			con.commit();
			System.out.println("Updated successfully");
		}
		else
		{
			System.out.println("Problem occured during update");
		}
	}
}

