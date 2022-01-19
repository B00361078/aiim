package com.aiim.app.controller;

import java.io.IOException;
import java.sql.SQLException;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/* The following class handles the home view internal view via border pane. Part of MVC design Pattern as a controller.
 * Neil Campbell 07/05/2021, B00361078
 */

public class HomeController  {

	@FXML private MenuButton menuButton;
	@FXML private MenuItem menuItemLogout;
	@FXML private BorderPane borderPane;
   
    public void initialize() throws IOException, SQLException {
    	ViewController.createInstance().getView();
    	menuButton.setText(Session.getFullName());
    	init(ViewController.createInstance().getView());
    }
	
    @FXML protected void logout(){
    	try {
			Stage currentStage = (Stage) menuButton.getScene().getWindow();
			ViewController.createInstance().setCurrentStage(currentStage);
			ViewController.createInstance().switchToView(ViewNames.LOGIN);
			Session.clearSession();
    	} catch (IOException e1) {
    		e1.printStackTrace();
    	}
    }

    public void init(ViewNames view) throws IOException {
    	
    	String location = ViewController.createInstance().switchToView(view);  	
    	FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(location));
		Parent root;
		try {
			root = loader.load();
			borderPane.setCenter(root);
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
    }
}
