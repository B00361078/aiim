package com.aiim.app.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.model.Note;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.task.CloseTicketTask;
import com.aiim.app.util.AppUtil;
import com.aiim.app.util.Session;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

/* The following class handles the amendment of tickets. Part of MVC design Pattern as a controller.
 * Neil Campbell 14/12/2021, B00361078
 */

public class AmendTicketController {

	private Connection con;
	@FXML private TextArea details;
	@FXML private Label reporter;
	@FXML private Label ticketNo;
	@FXML private Label status;
	@FXML private Label lbl1;
	@FXML private Label assignee;
	@FXML private Label dateRaised;
	@FXML private javafx.scene.control.TableColumn<Note, String> noteCol;
	@FXML private javafx.scene.control.TableColumn<Note, String> authorCol;
	@FXML private javafx.scene.control.TableColumn<Note, String> messageCol;
	@FXML private ChoiceBox assignedTeamMenu;
	@FXML private Button backBtn;
	@FXML private Button assignBtn;
	@FXML private Button statusBtn;
	@FXML private Button nteBtn;
	@FXML private TableView<Note.Builder> noteTable;
	private PreparedStatement sqlStatement;
	private String assignedTeamID;
	private String ticketStatus;
	private String message;
	private String updatedTeamID;
	private ResultSet rs;
	private ResourceBundle strBundle;
	private AppUtil appUtil;
	private Alert alert;
	private com.aiim.app.task.ThreadTask task;
	private Thread thread;
	private Dialog<String> dialog;
	private ButtonType btn;
	private TextArea noteDetail;
	private GridPane gridPane;
	private String author;
	private String ticketRef;
	private String noteMessage;
	private Date dateCreated;
	
	public void initialize() throws Exception, SQLException {
		con = DatabaseConnect.getConnection();
		appUtil = new AppUtil();
		setStatusBtnAction();
		strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
		ticketNo.setText(Session.getCurrentTicket());
		setTicketDetails();
		setStatusBtnDisplay(ticketStatus);
		setDisplay(Session.getPermissionLevel());
		updateNoteTable();
		setNoteTableEvent();
		setAssignedTeamAction();
		assignedTeamMenu.getItems();	
    	details.setWrapText(true);
    	details.setEditable(false);
    }
	
	public void setNoteTableEvent() {
		noteTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		noteTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		noteTable.setRowFactory( tv -> {
			TableRow<Note.Builder> row = new TableRow<>();
    	    row.setOnMouseClicked(event -> {
    	        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
    	            String rowData = row.getItem().noteID;
    	            try {
						viewNote(rowData);
					} catch (SQLException e) {
						e.printStackTrace();
					}  
    	        }
    	    });
    	    return row ;
    	});
	}
	
	public void setAssignedTeamAction() {
		assignedTeamMenu.setOnAction((event) -> {
	    	alert = new Alert(AlertType.CONFIRMATION);
    		alert.setHeaderText(strBundle.getString("e1"));
    		alert.showAndWait();
    		if (alert.getResult() == ButtonType.OK) {
    			try {
    				sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect13"));
					sqlStatement.setString(1, assignedTeamMenu.getValue().toString());
					rs = sqlStatement.executeQuery();
		        	while(rs.next()){
		        		updatedTeamID = rs.getString(1);
		        	}
					sqlStatement = con.prepareStatement(strBundle.getString("sqlUpdate3"));
					sqlStatement.setString(1, updatedTeamID);
	        	    sqlStatement.setObject(2, appUtil.getDate());
	        	    sqlStatement.setString(3, null);
	        	    sqlStatement.setString(4, Session.getCurrentTicket());
	        	    if (sqlStatement.executeUpdate() == 1){
	        			con.commit();
	        			alert = new Alert(AlertType.INFORMATION);
	    	    		alert.setHeaderText(strBundle.getString("e2"));
	    	    		alert.showAndWait();
	    	    		ViewController.createInstance().setView(ViewNames.DASHBOARD);	
	    	        	ViewController.createInstance().switchToView(ViewNames.HOME);
	        		}
				} catch (Exception e) {
					e.printStackTrace();
				}	 	
    		}
		});
	}
	
	public void setStatusBtnAction() {
    	statusBtn.setOnAction(ae -> {
            if (statusBtn.getText() == "Close Ticket") {
            	statusBtn.setDisable(true);
            	ae.consume();
	            task = new CloseTicketTask(strBundle.getString("closeTitle"), details.getText(), assignedTeamMenu.getValue().toString());
	            task.setOnSucceeded(e -> task.getValue());
	            alert = appUtil.createProgressAlert(ViewController.createInstance().getCurrentStage(), task);          
	            thread = appUtil.startThread(task, strBundle.getString("threadName"));
	            alert.showAndWait();
	            try {
	            	ViewController.createInstance().setView(ViewNames.DASHBOARD);	
	            	ViewController.createInstance().switchToView(ViewNames.HOME);
					//stop the thread
					thread.interrupt();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
            else if (statusBtn.getText() == "Move to In Progress") {
            	alert = new Alert(AlertType.CONFIRMATION);
        		alert.setHeaderText(strBundle.getString("moveProg"));
        		alert.showAndWait();
        		if (alert.getResult() == ButtonType.OK) {
	            	try {
						sqlStatement = con.prepareStatement(strBundle.getString("sqlUpdate7"));	 	
		        	    sqlStatement.setString(1, "In Progress");
		        	    sqlStatement.setObject(2, appUtil.getDate());
		        	    sqlStatement.setString(3, Session.getCurrentTicket());
		        	    if (appUtil.executeSQL(con, sqlStatement) == 1) {
		        	    	status.setText("In Progress");
		        			statusBtn.setText("Close Ticket");
		        	    	
		        	    }
		        		else {
		        			throw new Exception("Error");
		        		}
					} catch (Exception e) {
						e.printStackTrace();
					}	
        		}
            }
    	});
    }

    private void setStatusBtnDisplay (String status) {
    	if(status.contains("Raised")) {
    		statusBtn.setText("Move to In Progress");
    	}
    	else if (status.contains("In Progress")) {
    		statusBtn.setText("Close Ticket");
    	}
    	else {
    		statusBtn.setText("Closed");
    	}
    }
	    
    private void setTicketDetails() throws Exception {
    	sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect7"));
    	sqlStatement.setString(1, Session.getCurrentTicket());
    	rs = sqlStatement.executeQuery();
    	while(rs.next()){
    		reporter.setText(rs.getString(1));
    		setAssignee(rs.getString(2));
    		status.setText(rs.getString(3));
    		ticketStatus = rs.getString(3).toString();
    		dateRaised.setText(rs.getDate(4).toString());
    		assignedTeamID = rs.getString(5);
    		details.setText(rs.getString(6));
        }
    	sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect17"));
    	rs = sqlStatement.executeQuery();
    	while(rs.next()){
    		assignedTeamMenu.getItems().add(rs.getString(1));
        }	
    	setAssignedTeam();
    }
    
    public void setAssignedTeam() throws SQLException {
    	sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect12"));
    	sqlStatement.setString(1, Session.getCurrentTicket());
    	rs = sqlStatement.executeQuery();
    	while(rs.next()){
    		assignedTeamID = rs.getString(1);
        }
    	sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect16"));
    	sqlStatement.setString(1, assignedTeamID);
    	rs = sqlStatement.executeQuery();
    	while(rs.next()){
    		assignedTeamMenu.setValue(rs.getString(1));
        }	
    }
    
    public void setAssignee(String sqlString) {
    	if (sqlString == null) {
    		assignee.setText("Unassigned");
    		assignBtn.setDisable(false);
    	}
    	else {
    		assignee.setText(sqlString);
    	}
    }
    
    public void assignToMe() throws Exception { 	
	    sqlStatement = con.prepareStatement(strBundle.getString("sqlUpdate4"));	 	
	    sqlStatement.setString(1, Session.getUsername());
	    sqlStatement.setObject(2, appUtil.getDate());
	    sqlStatement.setString(3, Session.getCurrentTicket());
	    appUtil.executeSQL(con, sqlStatement);
	    assignee.setText(Session.getUsername());
		assignBtn.setDisable(true);
		statusBtn.setDisable(false);
		setDisplay(Session.getPermissionLevel());
    }

    public void insertNote() throws Exception {
	    sqlStatement = con.prepareStatement(strBundle.getString("sqlInsert2"));
	    sqlStatement.setString(1, Session.getUsername());
	    sqlStatement.setString(2, Session.getCurrentTicket());
	    sqlStatement.setString(3, message);
	    sqlStatement.setObject(4, appUtil.getDate());
	    appUtil.executeSQL(con, sqlStatement);
    }
    
    public void back() throws IOException {
    	ViewController.createInstance().setView(ViewNames.DASHBOARD);	
    	ViewController.createInstance().switchToView(ViewNames.HOME);
    }
    
    public void setDialog(String title, String btnText) {
    	dialog = new Dialog<>();
        dialog.setTitle(title);
        btn = new ButtonType(btnText, ButtonData.OK_DONE);
        dialog.setResultConverter(ButtonType::getText);
        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 100, 10, 10));
        noteDetail = new TextArea();
        dialog.getDialogPane().setContent(gridPane);
    }

    @FXML public void addNote() throws Exception {
    	
    	setDialog("Add a note to " + Session.getCurrentTicket(), "Add note");
        dialog.getDialogPane().getButtonTypes().addAll(btn, ButtonType.CANCEL);

        noteDetail.setPromptText("Add note details...");
        noteDetail.setWrapText(true);
        gridPane.add(noteDetail, 0, 0);
        String result = dialog.showAndWait().orElse(null);
        if (result == "Add note") {
        	message = noteDetail.getText();
        	insertNote();
        	noteTable.getItems().clear();
        	updateNoteTable();
        }
        else 
        	System.out.println("Cancelling note");
    }
    
    public void viewNote(String noteID) throws SQLException {
    	sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect15"));
    	sqlStatement.setString(1, noteID);
    	rs = sqlStatement.executeQuery();
    	while(rs.next()){
			author = rs.getString(1);
			ticketRef = rs.getString(2);
			noteMessage = rs.getString(3);
			dateCreated = rs.getDate(4);
    	}
		setDialog("Note details - "+noteID, "Close");
		dialog.getDialogPane().getButtonTypes().addAll(btn);
		noteDetail.setText(noteMessage);
		noteDetail.setEditable(false);
		Label lbl = new Label();
	    lbl.setText("Author: "+author);
		Label lbl2 = new Label();
		lbl2.setText("Ticket Ref: "+ticketRef);
		Labeled lbl3 = new Label();
		lbl3.setText("Created: "+ dateCreated.toString());
	    gridPane.add(lbl, 0, 0);
	    gridPane.add(lbl2, 0, 1);
	    gridPane.add(lbl3, 0, 2);
	    gridPane.add(noteDetail, 1, 0, 1, 5);
	    dialog.getDialogPane().setContent(gridPane);
	    dialog.showAndWait();
    }
    	
    public void updateNoteTable() throws SQLException {

		sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect14"));
		sqlStatement.setString(1, Session.getCurrentTicket());
		rs = sqlStatement.executeQuery();
		while(rs.next()){
			noteTable.getItems().add(new Note.Builder()
		    		.setNoteID(rs.getString(1))
		    		.setAuthor(rs.getString(2))
		    		.setNoteMessage(rs.getString(3)));
		}
	}
    
    public void setDisplay(int permLevel) {
		switch(permLevel) {
			case 1:
				assignBtn.setDisable(true);
				statusBtn.setDisable(true);
				nteBtn.setDisable(true);
				assignedTeamMenu.setDisable(true);
				break;
			case 2:
				if (assignee.getText().contains("Unassigned")) {
					statusBtn.setDisable(true);
				}
				else if (!assignee.getText().contains("Unassigned") && (!ticketStatus.contains("Closed"))) {
					assignBtn.setDisable(true);
				}
				else {
					assignBtn.setDisable(true);
					nteBtn.setDisable(true);
					statusBtn.setDisable(true);
					assignedTeamMenu.setDisable(true);
				}
				break;
			case 3:
				assignBtn.setDisable(true);
				statusBtn.setDisable(true);
				nteBtn.setDisable(true);
				assignedTeamMenu.setDisable(true);
				break;
		}
	}
}
    
