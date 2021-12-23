package com.aiim.app.controller;

import java.io.IOException;
import java.sql.SQLException;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.util.Session;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

/* The following class handles the dashboard interface by switching the current subScene. Part of MVC design Pattern as a controller.
 * Neil Campbell 07/05/2021, B00361078
 */

public class HomeController {

	@FXML public SubScene subScene;
	@FXML private MenuButton menuButton;
	@FXML private MenuItem menuItemLogout;;
   
    public void initialize() throws IOException, SQLException {
    	ViewController.createInstance().setCurrentSubScene(subScene);
    	menuButton.setText(Session.getFullName());
    	ViewController.createInstance().switchToView(ViewNames.DASHBOARD);
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
}
