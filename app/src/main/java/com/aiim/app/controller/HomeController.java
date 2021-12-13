package com.aiim.app.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.model.Ticket;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.util.Session;
import javafx.fxml.LoadException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/* The following class handles the dashboard interface by switching the current subScene. Part of MVC design Pattern as a controller.
 * Neil Campbell 07/05/2021, B00361078
 */

public class HomeController {

	@FXML public SubScene subScene;
	@FXML private MenuButton menuButton;
	@FXML private MenuItem menuItemLogout;;
    private ViewController viewController;
	private int permLevel;
	private Connection con;
	private PreparedStatement stmt;
   
    public void initialize() throws IOException, SQLException {
    	//viewController = ViewController.createInstance();
    	ViewController.createInstance().setCurrentSubScene(subScene);
    	menuButton.setText(Session.getFullName());
    	//updateTable();
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
