package com.aiim.app.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.util.AppUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
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
	@FXML private RadioButton assignOn;
	@FXML private RadioButton assignOff;
	@FXML private RadioButton trainOn;
	@FXML private RadioButton trainOff;
	@FXML private Label percentage;
	private AppUtil appUtil;
	private Alert alert;
	private ResourceBundle strBundle;
	private ResultSet rs;
	private int totalClosed;
	private int totalAssignedCorrect;
	private float accuracy;
   
	public void initialize() throws Exception {
		strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
		appUtil = new AppUtil();
		con = DatabaseConnect.getConnection();
		ToggleGroup assignRadios = new ToggleGroup();
		ToggleGroup trainRadios = new ToggleGroup();
		setRadio("assignMode", appUtil.getMode("assignMode"));
		setRadio("trainMode", appUtil.getMode("trainMode"));
		assignOn.setToggleGroup(assignRadios);
		assignOff.setToggleGroup(assignRadios);
		trainOn.setToggleGroup(trainRadios);
		trainOff.setToggleGroup(trainRadios);
    	setAction(assignOn, "Are you sure you want to enable Auto-assign mode?", "ON", "Enabled", "Auto-assign");
    	setAction(assignOff, "Are you sure you want to disable Auto-assign mode?", "OFF", "Disabled", "Auto-assign");
    	setAction(trainOn, "Are you sure you want to enable Auto-train mode?", "ON", "Enabled", "Auto-train");
    	setAction(trainOff, "Are you sure you want to disable Auto-train mode?", "OFF", "Disabled", "Auto-train");
    	getPercentage();
    }
    
    public void setAction (RadioButton button, String message, String mode, String req, String modeName) {
    	button.setOnAction(event -> {
            alert = new Alert(AlertType.CONFIRMATION);
            alert.setHeaderText(message);
            alert.showAndWait();
            	if (alert.getResult() == ButtonType.OK) {
            		try {
						updateMode(mode, modeName);
						alert = new Alert(AlertType.INFORMATION);
	            		alert.setHeaderText(modeName +" mode now " + req);
	            		alert.show();
					} catch (Exception e) {
						e.printStackTrace();
					}
            	}
            });
    }
    
    public void setRadio(String mode, String modeVal) throws Exception {
    	switch(mode) {
    	case "assignMode":
    		if (modeVal.contains("ON")) {
    			assignOn.setSelected(true);
    		}
    		else {
    			assignOn.setSelected(false);
    			assignOff.setSelected(true);
    		}
    		break;
    	case "trainMode":
    		if (modeVal.contains("ON")) {
    			trainOn.setSelected(true);
    		}
    		else {
    			trainOn.setSelected(false);	
    			trainOff.setSelected(true);
    		}
    		break;
    	}
    }

    public void updateMode(String modeVal, String mode) throws Exception {
	    con.setAutoCommit(false);
	    switch(mode) {
		    case "Auto-assign":
		    	sqlStatement = con.prepareStatement(strBundle.getString("sqlUpdate1"));
		    	break;
		    case "Auto-train":
		    	sqlStatement = con.prepareStatement(strBundle.getString("sqlUpdate2"));
		    	break;
	    }
	    sqlStatement.setString(1, modeVal);
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
    
    private void getPercentage() throws SQLException {
    	sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect10"));
    	rs = sqlStatement.executeQuery();
	    	while(rs.next()){
	    		totalClosed = rs.getInt(1);
	    	}
    	sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect11"));
    	rs = sqlStatement.executeQuery();
	    	while(rs.next()){
	    		totalAssignedCorrect = rs.getInt(1);
	    	}
    	accuracy = (totalAssignedCorrect * 100.0f) / totalClosed;
    	percentage.setText(String.format("%.2f", accuracy));
    }
}
