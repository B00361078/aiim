package com.aiim.app.controller;

import java.io.IOException;
import java.util.ResourceBundle;
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
    @FXML private PasswordField passwordField;
    @FXML private TextField usernameField;
    private ViewController viewController;
    //private Validation validation;
    private ResourceBundle strBundle;
   // private AdminData adminD;
        
    public void initialize() throws IOException {
    	//adminD = new AdminData();
    	strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
    	viewController = new ViewController();
    	//validation = new Validation();
    }
    
    @FXML protected void dashView(ActionEvent event) throws IOException  {
    	Scene scene = passwordField.getScene();
    	
       // try {
		//	if(validation.stringValidator(passwordField.getText(), adminD.getPassword()) && 
				//	validation.stringValidator(usernameField.getText(), adminD.getUsername())== true) {
        	viewController.setCurrentScene(scene);
        	viewController.switchToView(ViewNames.DASHBOARD);
			//}
			//else
			//	new Alert(Alert.AlertType.ERROR, strBundle.getString("e10")).showAndWait();
		//} catch (IOException e1) {
			//e1.printStackTrace();
		}
    }
//}