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

import com.aiim.app.database.DatabaseConnect;
//import org.json.simple.parser.ParseException;
//import com.app.model.AdminData;
import com.aiim.app.resource.ViewNames;
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
	@FXML private PasswordField passwordField;
    @FXML private TextField usernameField;
    private ViewController viewController;
    private String saltString;
    private String hashString;
    private String salt;
    byte[] fullhashbytes;
    private Connection con;
    //private Validation validation;
    private ResourceBundle strBundle;
    PreparedStatement stmt;
   // private AdminData adminD;
    byte[] thesalt;
    byte[] passBytes;
        
    public void initialize() throws IOException {
    	//adminD = new AdminData();
    	strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
    	viewController = new ViewController();
    	//validation = new Validation();
    }
    
    @FXML protected void dashView(ActionEvent event) throws IOException, SQLException, ClassNotFoundException, NoSuchAlgorithmException, DecoderException  {
    	Scene scene = passwordField.getScene();
    	String mystr = usernameField.getText();
    	
   
        	con = DatabaseConnect.getConnection();
        	stmt = con.prepareStatement("USE [honsdb] SELECT* FROM tblUser WHERE username = 'neil0310'");
        	ResultSet rs = stmt.executeQuery();
    		//stmt.executeUpdate();
        	while(rs.next()){
        		hashString = rs.getString(2);
        		saltString = rs.getString(5);
            }
        	con.close();
        	if (DigestUtils.sha1Hex(passwordField.getText()+saltString).equalsIgnoreCase(hashString)) {
        		System.out.println("match");
        		//viewController.setCurrentScene(scene);
            	//viewController.switchToView(ViewNames.DASHBOARD);
    			}
    		else {
    			new Alert(Alert.AlertType.ERROR, strBundle.getString("e10")).showAndWait();

        	}
    }
}
