package com.aiim.app.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import com.aiim.app.cnn.MyIter;
import com.aiim.app.database.DatabaseConnect;
//import org.json.simple.parser.ParseException;
//import com.app.model.AdminData;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.util.Session;
import com.aiim.app.util.Validation;

//import com.app.utils.Validation;
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
    private static final Charset UTF_16 = null;
    private Session session;
	@FXML private PasswordField passwordField;
    @FXML private TextField usernameField;
    private ViewController viewController;
    private String saltString;
    private String hashString;
    private String salt;
    byte[] fullhashbytes;
    private Connection con;
    private Validation validation;
    private ResourceBundle strBundle;
    PreparedStatement stmt;
   // private AdminData adminD;
    byte[] thesalt;
    byte[] passBytes;
	private String teamID;
	private String roleID;
	private int permLevel;
	private String teamName;
	private String user;
	private String fullname;
	private String firstName;
	private DataSetIterator trainIter;
        
    public void initialize() throws IOException {
    	//adminD = new AdminData();
    	strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
    	ViewController.createInstance();
    	validation = new Validation();
    	MyIter iter = new MyIter();
    	trainIter = iter.getDataSetIterator();
    	Session.setMyIter(iter);
    	Session.setIter(trainIter);
    }
    
    @FXML protected void dashView(ActionEvent event) throws IOException, SQLException, ClassNotFoundException, NoSuchAlgorithmException, DecoderException  {
    	Scene scene = passwordField.getScene();
    	String username = usernameField.getText();
    	String password = passwordField.getText();
    	if (username == null | password == null) {
    		new javafx.scene.control.Alert(Alert.AlertType.ERROR, strBundle.getString("e17")).showAndWait();
    	}
    	else {
    		con = DatabaseConnect.getConnection();
    		System.out.println("con is " + con);
        	stmt = con.prepareStatement("USE [honsdb] SELECT* FROM tblUser WHERE username = '" +username+"'");
        	ResultSet rs = stmt.executeQuery();
        	//stmt.executeUpdate();
        	while(rs.next()){
        		user = rs.getString(1);
        		firstName = rs.getString(2);
        		fullname = rs.getString(4);
        		hashString = rs.getString(5);
        		saltString = rs.getString(8);
        		teamID = rs.getString(6);
				roleID = rs.getString(7);
            }
        	//con.close();
        	if (DigestUtils.sha1Hex(passwordField.getText()+saltString).equalsIgnoreCase(hashString)) {
        		System.out.println("match");
        		
        		session = Session.createSession();
        		Session.setUsername(user);
        		Session.setFullName(fullname);
        		Session.setTeamID(teamID);
        		
        		System.out.println("printing session " + Session.getFullName());
        		
				
				stmt = con.prepareStatement("USE [honsdb] SELECT* FROM tblRole WHERE roleID = '" +roleID+"'");
	        	ResultSet rs1 = stmt.executeQuery();
	        	//stmt.executeUpdate();
	        	while(rs1.next()){
	        		permLevel = rs1.getInt("permissionLevel");
	            }
	        	
	        	stmt = con.prepareStatement("USE [honsdb] SELECT* FROM tblTeam WHERE teamID = '" +teamID+"'");
	        	ResultSet rs2 = stmt.executeQuery();
	        	//stmt.executeUpdate();
	        	while(rs2.next()){
	        		teamName = rs2.getString("name");
	            }
	        	
        		
        		
        		Session.setPermissionLevel(permLevel);
        		Session.setTeamName(teamName);
        		System.out.println(teamName);
        		
        		ViewController.createInstance().setCurrentScene(scene);
        		ViewController.createInstance().switchToView(ViewNames.HOME);
    			}
    		else {
    			new Alert(Alert.AlertType.ERROR, strBundle.getString("e10")).showAndWait();

        	}
	
    	}
    }
}

