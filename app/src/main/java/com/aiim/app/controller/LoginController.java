package com.aiim.app.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import org.apache.commons.codec.digest.DigestUtils;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.util.Session;
import com.aiim.app.view.ViewNames;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    	strBundle = ResourceBundle.getBundle("bundle");
    	con = DatabaseConnect.getConnection(); 
    }

    @FXML protected void dashView(ActionEvent event) throws Exception  {   	
    	if (usernameField.getText() == null | passwordField.getText() == null) {
    		new javafx.scene.control.Alert(Alert.AlertType.ERROR, strBundle.getString("e17")).showAndWait();
    	}
    	else {
        	sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect18"));
        	sqlStatement.setString(1, usernameField.getText());
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
        		sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect19"));
        		sqlStatement.setString(1, roleID);
	        	rs = sqlStatement.executeQuery();
		        	while(rs.next()){
		        		permLevel = rs.getInt("permissionLevel");
		            }
		        sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect20"));
		        sqlStatement.setString(1, teamID);
	        	rs = sqlStatement.executeQuery();
		        	while(rs.next()){
		        		teamName = rs.getString("name");
		            }
        		Session.setPermissionLevel(permLevel);
        		Session.setTeamName(teamName);
        		ViewController.createInstance().setCurrentScene(passwordField.getScene());
        		ViewController.createInstance().setView(ViewNames.DASHBOARD);
        		ViewController.createInstance().switchToView(ViewNames.HOME);
    		}
    		else {
    			new Alert(Alert.AlertType.ERROR, strBundle.getString("e10")).showAndWait();
        	}
    	}
    }        
}

