package com.aiim.app.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.digest.DigestUtils;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/* The following class handles the login and authentication of the application. Part of MVC design Pattern as a controller.
 * Neil Campbell 06/01/2022, B00361078
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
	
	public void initialize() throws Exception {
    	strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
    	ViewController.createInstance();
    	con = DatabaseConnect.getConnection();  	
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
}

