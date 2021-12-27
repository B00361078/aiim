package com.aiim.app.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.util.AppUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

/* The following class handles the dashboard interface by switching the current subScene. Part of MVC design Pattern as a controller.
 * Neil Campbell 14/12/2021, B00361078
 */

public class SettingController {
	
	//get value from session and set the radio button accordingly
	// allow task to enable/disable in db
	private PreparedStatement sqlStatement;
	private Connection con;
	@FXML private Button backBtn;
	@FXML private RadioButton radioOn;
	@FXML private RadioButton radioOff;
	private AppUtil appUtil;
	private Alert alert;
	private ResourceBundle strBundle;
   
	public void initialize() throws Exception {
		strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
		appUtil = new AppUtil();
		con = DatabaseConnect.getConnection();
		ToggleGroup radios = new ToggleGroup();
		setRadio(appUtil.getAIMode());
		radioOn.setToggleGroup(radios);
		radioOff.setToggleGroup(radios);
    	setAction(radioOn, "Are you sure you want to enable DataSetIter mode?", "ON", "Enabled");
    	setAction(radioOff, "Are you sure you want to disable DataSetIter mode?", "OFF", "Disabled");
    }
    
    public void setAction (RadioButton button, String message, String mode, String req) {
    	button.setOnAction(event -> {
            alert = new Alert(AlertType.CONFIRMATION);
            alert.setHeaderText(message);
            alert.showAndWait();
            	if (alert.getResult() == ButtonType.OK) {
            		try {
						updateMode(mode);
						alert = new Alert(AlertType.INFORMATION);
	            		alert.setHeaderText("DataSetIter mode now " + req);
					} catch (Exception e) {
						e.printStackTrace();
					}
            	}
            });
    }
    public void setRadio(String mode) throws Exception {
    	switch(mode) {
    	case "ON":
    		radioOn.setSelected(true);
    		radioOff.setSelected(false);
    		break;
    	case "OFF":
    		radioOn.setSelected(false);
    		radioOff.setSelected(true);
    		break;
    	}
    }

    public void updateMode(String mode) throws Exception {
	    con.setAutoCommit(false);	 
	    sqlStatement = con.prepareStatement(strBundle.getString("sqlUpdate1"));
	    sqlStatement.setString(1, mode);
	    sqlStatement.setObject(2, appUtil.getDate());
	    sqlStatement.setInt(3, 1);
    		if (sqlStatement.executeUpdate() == 1){
    			con.commit();
    		}
    		else {
    			throw new Exception("Error");
    		}
    }
    
    @FXML protected void back() throws IOException {
    	ViewController.createInstance().switchToView(ViewNames.DASHBOARD);
    }  
}
