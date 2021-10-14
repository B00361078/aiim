package com.aiim.app.controller;

import java.io.IOException;
import com.aiim.app.resource.ViewNames;
import javafx.fxml.LoadException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/* The following class handles the dashboard interface by switching the current subScene. Part of MVC design Pattern as a controller.
 * Neil Campbell 07/05/2021, B00361078
 */

public class DashboardController {
	@FXML private Button clientBtn;
	@FXML private Button settingsBtn;
	@FXML private Button statsBtn;
	@FXML private Button logout;
	@FXML public SubScene subScene;
    private ViewController viewController;
   
    public void initialize() throws IOException {
    	viewController = new ViewController();
    	viewController.setCurrentSubScene(subScene);
    	//viewController.switchToView(ViewNames.CLIENTS);
    }

    
    @FXML protected void logout(){
    	try {
    			Stage currentStage = (Stage) logout.getScene().getWindow();
    			viewController.setCurrentStage(currentStage);
    			viewController.switchToView(ViewNames.LOGIN);
		
    	} catch (IOException e1) {
    		e1.printStackTrace();
    	}

    }
    
    @FXML protected void clientView(ActionEvent event) {
   	
        try {
        		viewController.setCurrentSubScene(subScene);
        		viewController.switchToView(ViewNames.CLIENTS);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }
    
    @FXML protected void settingsView(ActionEvent event) {
    	
        try {	viewController.setCurrentSubScene(subScene);
        		viewController.switchToView(ViewNames.SETTINGS);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }
    
    @FXML protected void statsView(ActionEvent event) throws IOException {
    	
        try {
        		viewController.setCurrentSubScene(subScene);
        		viewController.switchToView(ViewNames.STATISTICS);
			
		} catch (LoadException e1) {
			e1.printStackTrace();
		}
    }
}
