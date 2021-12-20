package com.aiim.app.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.model.Ticket;
import com.aiim.app.model.Ticket.Builder;
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
import javafx.scene.control.TableRow;
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
    	settingsBtn.setVisible(false);
    	raiseNewBtn.setVisible(false);
    	setDisplay(Session.getPermissionLevel());
    	ticketTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    	ticketTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    	updateTable();
    	
    	ticketTable.setRowFactory( tv -> {
    	    TableRow<Ticket.Builder> row = new TableRow<>();
    	    row.setOnMouseClicked(event -> {
    	        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
    	            String rowData = row.getItem().ticketID;
    	            Session.setCurrentTicket(rowData);
    	            try {
						ViewController.createInstance().switchToView(ViewNames.AMENDTICKET);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	        }
    	    });
    	    return row ;
    	});
    }

	public void updateTable() throws SQLException {
		
	    	//ticketTable.getItems().clear();
		//ticketTable.getItems().add(new Ticket.Builder().setPersonID().setFName(fName).setSName(sName));
		con = DatabaseConnect.getConnection();
		switch(Session.getPermissionLevel()) {
		case 1:
			//agent
			stmt = con.prepareStatement("USE honsdb select b.ticketID, b.status, b.dateRaised, a.name from tblTicket as B join tblTeam as u on u.teamID = B.updatedTeam join tblTeam as a on a.teamID = B.updatedTeam WHERE B.reporter = '" +Session.getUsername()+"'");
			break;
		case 2:
			//owner
			stmt = con.prepareStatement("USE honsdb select b.ticketID, b.status, b.dateRaised, a.name from tblTicket as B join tblTeam as u on u.teamID = B.updatedTeam join tblTeam as a on a.teamID = B.updatedTeam WHERE (B.assignee IS NULL OR B.assignee = ?) AND B.updatedTeam = ?");
			stmt.setString(1, Session.getUsername());
			stmt.setString(2, Session.getTeamID());
			break;
		case 3:
			//sysadmin
			stmt = con.prepareStatement("USE honsdb select b.ticketID, b.status, b.dateRaised, a.name from tblTicket as B join tblTeam as u on u.teamID = B.updatedTeam join tblTeam as a on a.teamID = B.updatedTeam");
			break;
		}

		
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
	
	public void setDisplay(int permLevel) {
		switch(permLevel) {
			case 1:
				raiseNewBtn.setVisible(true);
				break;
			case 2:
				break;
			case 3:
				settingsBtn.setVisible(true);
				break;
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
