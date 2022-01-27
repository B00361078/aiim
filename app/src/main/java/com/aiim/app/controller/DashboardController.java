package com.aiim.app.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;

import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.model.Ticket;
import com.aiim.app.model.Ticket.Builder;
import com.aiim.app.model.WordVector;
import com.aiim.app.util.AppUtil;
import com.aiim.app.util.Session;
import com.aiim.app.view.ViewNames;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;

/* The following class handles the dashboard view. Part of MVC design Pattern as a controller.
 * Neil Campbell 06/01/2022, B00361078
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
	@FXML private RadioButton radioAll;
	@FXML private RadioButton radioRaised;
	@FXML private RadioButton radioProg;
	@FXML private RadioButton radioClosed;
	private Connection con;
	private ResultSet rs;
	private PreparedStatement sqlStatement;
	private ResourceBundle strBundle;
	private ObservableList<Ticket.Builder> list;
	private ArrayList<RadioButton> radioBtns;
	private ToggleGroup radioGrp;	
   
    public void initialize() throws Exception  {
    	con = DatabaseConnect.getConnection();
    	strBundle = ResourceBundle.getBundle("bundle");
    	settingsBtn.setVisible(false);
    	raiseNewBtn.setVisible(false);
    	setDisplay(Session.getPermissionLevel());
    	ticketTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    	ticketTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    	updateTable();
    	radioBtns = new ArrayList<RadioButton>();
    	radioBtns.add(radioClosed);
    	radioBtns.add(radioAll);
    	radioBtns.add(radioRaised);
    	radioBtns.add(radioProg);
    	radioGrp = new ToggleGroup();
    	initialiseRadios();
    	setRowFactory();
    	radioAll.setSelected(true);
    	//AppUtil appUtil = new AppUtil();
    	//appUtil.evaluateAcc();
//    	String currentDirectory = Paths.get("").toAbsolutePath().toString();
//    	WordVector wv = new WordVector();
//    	Word2Vec vec = wv.buildVectors(currentDirectory + "/files/vectors_raw.txt", currentDirectory+"/files/stop_words.txt");
//    	wv.viewSimilarWords(vec, "guidewire", 10);
    }
    
    public void setRowFactory() {
    	ticketTable.setRowFactory( tv -> {
    	    TableRow<Ticket.Builder> row = new TableRow<>();
    	    row.setOnMouseClicked(event -> {
    	        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
    	            String rowData = row.getItem().ticketID;
    	            Session.setCurrentTicket(rowData);
					ViewController.createInstance().setView(ViewNames.AMENDTICKET);
					try {
						ViewController.createInstance().switchToView(ViewNames.HOME);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	        }
    	    });
    	    return row ;
    	});
    }
    
    public void initialiseRadios() {
		for (RadioButton btn : radioBtns) {
			btn.setToggleGroup(radioGrp);
			setRadioAction(btn);
		}
    }
    
    public void setRadioAction (RadioButton button) {
    	button.setOnAction(event -> {
    		try {
    			ticketTable.getItems().clear();
				updateTable();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
    		list = ticketTable.getItems();
    		switch(button.getText()) {
    			case "All":
    				break;
    			case "Raised":
    				resetTable("Raised");
    				break;
    			case "In Progress":
    				resetTable("In Progress");
    				break;
    			case "Closed":
    				resetTable("Closed");
    				break;
    		}
    	});
    }

	private void resetTable(String filter) {
		ObservableList<Ticket.Builder> newlist = FXCollections.observableArrayList();
		for (Builder item : list) {
			if (item.status.contains(filter)) {
	    		newlist.add(new Ticket.Builder()
	    				.setTicketID(item.ticketID)
			    		.setStatus(item.status)
			    		.setDate(item.date)
			    		.setAssignedTeam(item.assignedTeam));
			}
		}
		ticketTable.getItems().clear();
		ticketTable.getItems().addAll(newlist);
	}


	public void updateTable() throws SQLException {

		switch(Session.getPermissionLevel()) {
		case 1:
			//agent
			sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect4"));
			sqlStatement.setString(1, Session.getUsername());
			break;
		case 2:
			//owner
			sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect5"));
			sqlStatement.setString(1, Session.getUsername());
			sqlStatement.setString(2, Session.getTeamID());
			break;
		case 3:
			//sysadmin
			sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect6"));
			break;
		}

    	rs = sqlStatement.executeQuery();
    	while(rs.next()){
    		ticketTable.getItems().add(new Ticket.Builder()
		    		.setTicketID(rs.getString(1))
		    		.setStatus(rs.getString(2))
		    		.setDate(rs.getDate(3).toString())
		    		.setAssignedTeam(rs.getString(4)));
    	}		    
	}
	
	public void setDisplay(int permLevel) {
		switch(permLevel) {
			case 1:
				raiseNewBtn.setVisible(true);
				settingsBtn.setVisible(false);
				break;
			case 2:
				raiseNewBtn.setVisible(false);
				settingsBtn.setVisible(false);
				break;
			case 3:
				settingsBtn.setVisible(true);
				raiseNewBtn.setVisible(false);
				break;
		}
	}
	   
    public void raiseIncidentView(ActionEvent event) {
        try {
        	ViewController.createInstance().setView(ViewNames.TICKET);	
        	ViewController.createInstance().switchToView(ViewNames.HOME);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }
    
    public void settingsView(ActionEvent event) {
        try {
        	ViewController.createInstance().setView(ViewNames.SETTINGS);	
        	ViewController.createInstance().switchToView(ViewNames.HOME);		
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }
}
