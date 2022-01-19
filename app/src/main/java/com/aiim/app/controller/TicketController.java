package com.aiim.app.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.aiim.app.resource.ViewNames;
import com.aiim.app.task.ClassifyTicketTask;
import com.aiim.app.task.ThreadTask;
import com.aiim.app.util.AppUtil;
import com.aiim.app.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/* The following class handles the creation of new incidents. Part of MVC design Pattern as a controller.
 * Neil Campbell 06/01/2022, B00361078
 */

public class TicketController {

	@FXML private TextArea details;
	@FXML private Label reporter;
	@FXML private Button raiseBtn;
	private AppUtil appUtil;
	private Alert alert;
	private ThreadTask task;
	private Thread thread;
	private ResourceBundle strBundle;
	
	public void initialize() throws Exception, SQLException {
    	reporter.setText(Session.getFullName());
    	reporter.setText(Session.getFullName());
    	details.setWrapText(true);
    	setRaiseAction();
    	appUtil = new AppUtil();
    	strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
    }
    
    public void cancel() throws IOException {
    	ViewController.createInstance().setView(ViewNames.DASHBOARD);	
    	ViewController.createInstance().switchToView(ViewNames.HOME);
    }
    
    public void setRaiseAction() {
    	raiseBtn.setOnAction(ae -> {
            ae.consume();
            raiseBtn.setDisable(true);
            task = new ClassifyTicketTask(strBundle.getString("ticketTitle"), details.getText());
            task.setOnSucceeded(e -> {
            	ViewController.createInstance().setView(ViewNames.DASHBOARD);
            	try {
    				ViewController.createInstance().switchToView(ViewNames.HOME);
    				thread.interrupt();
    			} catch (IOException e1) {
    				e1.printStackTrace();
    			}
            }	
            );
            alert = appUtil.createProgressAlert(ViewController.createInstance().getCurrentStage(), task);          
            thread = appUtil.startThread(task, strBundle.getString("threadName"));
            alert.show();
            
		});
    }
}
