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
import javafx.concurrent.Task;
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

public class DashboardController {
	@FXML private TableView<Ticket.Builder> ticketTable;
	@FXML private javafx.scene.control.TableColumn<Ticket, String> ticketIDCol;
	@FXML private javafx.scene.control.TableColumn<Ticket, String> statusCol;
	@FXML private javafx.scene.control.TableColumn<Ticket, String> assignedCol;
	@FXML private javafx.scene.control.TableColumn<Ticket, String> dateCol;
	@FXML private MenuButton menuButton;
	@FXML private MenuItem menuItemLogout;;
	@FXML private Button raiseNewBtn;
	@FXML private Button settingsBtn;
	@FXML private Button logout;
    private ViewController viewController;
	private int permLevel;
	private Connection con;
	private PreparedStatement stmt;
	private PreparedStatement stmt2;
	private String assignedTeamName;
   
    public void initialize() throws Exception  {
    	ticketTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    	ticketTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    	updateTable();
    }
//    private void perms () {
//    	if (Session.getPermissionLevel() == 3){
//    		raiseNewBtn.setVisible(true);
//    	}
//    }
	public void updateTable() throws SQLException {
		
	    	//ticketTable.getItems().clear();
		//ticketTable.getItems().add(new Ticket.Builder().setPersonID().setFName(fName).setSName(sName));
		con = DatabaseConnect.getConnection();
		//stmt = con.prepareStatement("USE [honsdb] SELECT* FROM tblTicket WHERE reporter = '" +Session.getUsername()+"'");
		stmt = con.prepareStatement("USE honsdb select b.ticketID, b.status, b.dateRaised, a.name from tblTicket as B join tblTeam as u on u.teamID = B.updatedTeam join tblTeam as a on a.teamID = B.updatedTeam WHERE B.reporter = '" +Session.getUsername()+"'");
    	ResultSet rs = stmt.executeQuery();
    	while(rs.next()){
    			
    		String ticketID = rs.getString(1);
    		String status = rs.getString(2);
    		Date date = rs.getDate(3);
    		String assignedTeam = rs.getString(4);
    		

    		ticketTable.getItems().add(new Ticket.Builder()
		    		.setTicketID(ticketID)
		    		.setStatus(status)
		    		.setDate(date.toString())
		    		.setAssignedTeam(assignedTeam));

    	}		    
	    	}
	   
    
    @FXML protected void raiseIncidentView(ActionEvent event) {
   	
        try {
        		//viewController.setCurrentSubScene(subScene);
        	ViewController.createInstance().switchToView(ViewNames.TICKET);

			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }
    @FXML protected void settingsView(ActionEvent event) {
       	
        try {
        	ViewController.createInstance().switchToView(ViewNames.SETTINGS);		
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }
    
   
    
}
