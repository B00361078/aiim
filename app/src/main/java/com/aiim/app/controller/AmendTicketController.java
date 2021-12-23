package com.aiim.app.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import com.aiim.app.ai.AI;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.model.Note;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.util.AppUtil;
import com.aiim.app.util.Session;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/* The following class handles the dashboard interface by switching the current subScene. Part of MVC design Pattern as a controller.
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
	@FXML private ChoiceBox assignedTeam;
	@FXML private Button backBtn;
	@FXML private Button assignBtn;
	@FXML private Button statusBtn;
	@FXML private Button nteBtn;
	@FXML private TableView<Note.Builder> noteTable;
	private PreparedStatement sqlStatement;
	private String assignedTeamID;
	private String teamName;
	private String ticketStatus;
	private String message;
	private String updatedTeamID;
	private PreparedStatement sqlStatment;
	private ResultSet rs;
	private ResourceBundle strBundle;
	private AppUtil appUtil;
	
	public void initialize() throws Exception, SQLException {
		
		con = DatabaseConnect.getConnection();
		strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
		appUtil = new AppUtil();
		ticketNo.setText(Session.getCurrentTicket());
		setDetails();
		assignBtn.setVisible(false);
		statusBtn.setVisible(false);
		nteBtn.setVisible(false);
		assignedTeam.setDisable(true);
		setDisplay(Session.getPermissionLevel());
		noteTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		noteTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		updateTable();
		noteTable.setRowFactory( tv -> {
    	    TableRow<Note.Builder> row = new TableRow<>();
    	    row.setOnMouseClicked(event -> {
    	        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
    	            String rowData = row.getItem().noteID;
    	            try {
						viewNote(rowData);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

    	           
    	        }
    	    });
    	    return row ;
    	});
		status(ticketStatus);
		assignedTeam.getItems();	
    	//reporter.setText(Session.getFullName());
    	//reporter.setText(Session.getFullName());
    	details.setWrapText(true);
    	details.setEditable(false);
    	assignedTeam.setOnAction((event) -> {
		    	Alert alert = new Alert(AlertType.CONFIRMATION);
	    		alert.setHeaderText("Are you sure you want to change the assigned team?");
	    		alert.showAndWait();
	    		if (alert.getResult() == ButtonType.OK) {
	    			try {
	    				sqlStatement = con.prepareStatement("USE [honsdb] SELECT teamID FROM tblTeam WHERE name = ?");
						sqlStatement.setString(1, assignedTeam.getValue().toString());
						ResultSet rs = sqlStatement.executeQuery();

			        	while(rs.next()){
			        		updatedTeamID = rs.getString(1);
			        	}
	    				
						sqlStatement = con.prepareStatement("USE [honsdb] UPDATE tblTicket SET updatedTeam = ?, dateUpdated = ?, assignee = ? WHERE ticketID = ?");
						sqlStatement.setString(1, updatedTeamID);
		        	    sqlStatement.setObject(2, appUtil.getDate());
		        	    sqlStatement.setString(3, null);
		        	    sqlStatement.setString(4, Session.getCurrentTicket());
		        	    if (sqlStatement.executeUpdate() == 1){
		        			con.commit();
		        			System.out.println("team updated");
		        			Alert alert2 = new Alert(AlertType.INFORMATION);
		    	    		alert2.setHeaderText("Team updated successfully.");
		    	    		alert2.showAndWait();
		        		}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	 	
	    		}
		});
    }
    

    private void status (String status) {
    	if(status.contains("raised")) {
    		statusBtn.setText("Move to In Progress");
    	}
    	else if (status.contains("inprogress")) {
    		statusBtn.setText("Close Ticket");
    	}
    	else {
    		statusBtn.setVisible(false);
    		nteBtn.setVisible(false);
			assignedTeam.setDisable(true);
    	}
    }
    @FXML private void clickStatus () throws Exception {
    	changeStatus(statusBtn.getText());
    }
    private void changeStatus (String command) throws Exception {
    	switch (command) {
    	case "Move to In Progress":
    		java.util.Date date = new java.util.Date();
    	    java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());
    	    con.setAutoCommit(false);	
    	    sqlStatement = con.prepareStatement("USE [honsdb] UPDATE tblTicket SET status = ?, dateUpdated = ? WHERE ticketID = ?");	 	
    	    sqlStatement.setString(1, "inprogress");
    	    sqlStatement.setObject(2, sqlDate);
    	    sqlStatement.setString(3, Session.getCurrentTicket());
    	    if (sqlStatement.executeUpdate() == 1){
    			con.commit();
    			System.out.println("Status updated");
    			status.setText("inprogress");
    			statusBtn.setText("Close Ticket");
    		}
    		else {
    			throw new Exception("Error");
    		}
    		break;
    	case "Close Ticket":
    		Alert alert = new Alert(AlertType.CONFIRMATION);
    		alert.setHeaderText("Are you sure you want to close this ticket?");
    		alert.showAndWait();
    		if (alert.getResult() == ButtonType.OK) {
    			java.util.Date date1 = new java.util.Date();
        	    java.sql.Timestamp sqlDate1 = new java.sql.Timestamp(date1.getTime());
        	    con.setAutoCommit(false);	
        	    sqlStatement = con.prepareStatement("USE [honsdb] UPDATE tblTicket SET status = ?, dateUpdated = ? WHERE ticketID = ?");	 	
        	    sqlStatement.setString(1, "closed");
        	    sqlStatement.setObject(2, sqlDate1);
        	    sqlStatement.setString(3, Session.getCurrentTicket());
        	    if (sqlStatement.executeUpdate() == 1){
        			con.commit();
        			System.out.println("Status updated");
        			status.setText("closed");
        			statusBtn.setVisible(false);
        			nteBtn.setVisible(false);
        			assignedTeam.setDisable(true);
        		}
        		else {
        			throw new Exception("Error");
        		}
    		}
    		//DataSetIterator iter = Session.getMyIter().getUpdatedDataSetIterator(details.getText(), assignedTeam.getValue().toString());
    		//Session.getMyIter().retrain(iter);
    		break;
    		// check training mode on
    		
    		// check verbatim suitable for training
    		//append text to file
    		//model fit
    		//update files in db
    	}
    }
    private void setDetails() throws Exception {
    	con.setAutoCommit(false);
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
    	sqlStatement = con.prepareStatement("SELECT name from tblTeam");
    	rs = sqlStatement.executeQuery();
    	while(rs.next()){
    		assignedTeam.getItems().add(rs.getString(1));
        }
    	
    	sqlStatement = con.prepareStatement("SELECT name from tblTeam WHERE teamID = ?");
    	sqlStatement.setString(1, assignedTeamID);
    	
    	rs = sqlStatement.executeQuery();
    	while(rs.next()){
    		teamName = rs.getString(1);
        }
 
    	assignedTeam.setValue(teamName);
    }
    
    public void setAssignee(String sqlString) {
    	if (sqlString == null) {
    		assignee.setText("Unassigned");
    		assignBtn.setVisible(true);
    	}
    	else {
    		assignee.setText(sqlString);
    	}
    	
    }
    public void assignToMe() throws Exception { 
    	java.util.Date date = new java.util.Date();
	    java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());
	    con.setAutoCommit(false);	
	    sqlStatement = con.prepareStatement("USE [honsdb] UPDATE tblTicket SET assignee = ?, dateUpdated = ? WHERE ticketID = ?");	 	
	    sqlStatement.setString(1, Session.getUsername());
	    sqlStatement.setObject(2, sqlDate);
	    sqlStatement.setString(3, Session.getCurrentTicket());
	    if (sqlStatement.executeUpdate() == 1){
			con.commit();
			
		}
		else {
			throw new Exception("Error");
		}
	    assignee.setText(Session.getUsername());
		assignBtn.setVisible(false);
    }

    public void insert() throws Exception {
	    java.util.Date date = new java.util.Date();
	    java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());
	    con.setAutoCommit(false);
	    
	   
	    sqlStatment = con.prepareStatement("USE [honsdb] INSERT INTO tblNote (author,ticketRef,message,dateCreated) VALUES(?,?,?,?)");
	    
	    sqlStatment.setString(1, Session.getUsername());
	    sqlStatment.setString(2, Session.getCurrentTicket());
	    sqlStatment.setString(3, message);
	    sqlStatment.setObject(4, sqlDate);
	    		
	    		
	    		
	    		if (sqlStatment.executeUpdate() == 1)
	    		{
	    			con.commit();
	    			System.out.println("Note added");
	    		}
	    		else
	    		{
	    			throw new Exception("Error");
	    		}
    	
    }
    
    public void back() throws IOException {
    	ViewController.createInstance().switchToView(ViewNames.HOME);
    }
	private class MyTask extends Task {

        private MyTask() {
            updateTitle("Raise New Ticket");
        }

        @Override
        protected String call() throws Exception {
        	String mystr = "hello";
            updateMessage("Raising ticket, please wait.");
            System.out.println("my string");
            new AI();
        	//prediction = ai.classify(details.getText(), trainIter);
        	insert();
            TimeUnit.SECONDS.sleep(5);
            updateMessage("Ticket raised successfully");
            updateProgress(1, 1);
            return mystr;
        }

        @Override
        protected void running() {
            System.out.println("Raising ticket task is running...");
        }

        @Override
        protected void succeeded() {
            System.out.println("Raising ticket task is successful.");
        }
        
    }
    private void executeTask(Task<?> task) {
        Thread dbThread = new Thread(task, "dbThread");
        dbThread.setDaemon(true);
        dbThread.start();
    }

    // creates the Alert and necessary controls to observe the task
    private Alert createProgressAlert(Stage owner, Task<?> task) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.initOwner(owner);
        alert.titleProperty().bind(task.titleProperty());
        alert.contentTextProperty().bind(task.messageProperty());

        ProgressIndicator pIndicator = new ProgressIndicator();
        pIndicator.progressProperty().bind(task.progressProperty());
        alert.setGraphic(pIndicator);

        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
        alert.getDialogPane().lookupButton(ButtonType.OK)
                .disableProperty().bind(task.runningProperty());       

        alert.getDialogPane().cursorProperty().bind(
		Bindings.when(task.runningProperty())
                    .then(Cursor.WAIT)
                    .otherwise(Cursor.DEFAULT)
        );

        return alert;
    }
    
    @FXML public void addNote() throws Exception {
    	
    	Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add a note to " + Session.getCurrentTicket());
        ButtonType loginButtonType = new ButtonType("Add note", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        dialog.setResultConverter(ButtonType::getText);
        GridPane gridPane = new GridPane();
        
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 100, 10, 10));

        TextArea noteDetail = new TextArea();
        noteDetail.setPromptText("Add note details...");
        noteDetail.setWrapText(true);

        gridPane.add(noteDetail, 0, 0);

        dialog.getDialogPane().setContent(gridPane);
        String result = dialog.showAndWait().orElse(null);
 
        if (result == "Add note") {
        	message = noteDetail.getText();
        	insert();
        	noteTable.getItems().clear();
        	updateTable();

        }
        else 
        	System.out.println("cancelling");
        
    }
    
    public void updateTable() throws SQLException {

	sqlStatement = con.prepareStatement("USE honsdb select noteID,author,message FROM tblNote WHERE ticketRef = ?");
	sqlStatement.setString(1, Session.getCurrentTicket());
	
	rs = sqlStatement.executeQuery();
	while(rs.next()){
			
		String noteID = rs.getString(1);
		String author = rs.getString(2);
		String noteMessage = rs.getString(3);
		
		noteTable.getItems().add(new Note.Builder()
	    		.setNoteID(noteID)
	    		.setAuthor(author)
	    		.setNoteMessage(noteMessage));
	}
   }
    public void viewNote(String noteID) throws SQLException {
    	sqlStatement = con.prepareStatement("USE honsdb select author,ticketRef,message,dateCreated FROM tblNote WHERE noteID = ?");
    	sqlStatement.setString(1, noteID);
    	
    	rs = sqlStatement.executeQuery();
    	while(rs.next()){
    			
    		String author = rs.getString(1);
    		String ticketRef = rs.getString(2);
    		String noteMessage = rs.getString(3);
    		Date dateCreated = rs.getDate(4);
    		
    	Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Note details - "+noteID);
        ButtonType clseBtn = new ButtonType("Close", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(clseBtn);
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 100, 10, 10));

        TextArea noteDetail = new TextArea();
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
    }
    public void setDisplay(int permLevel) {
		switch(permLevel) {
			case 1:
				break;
			case 2:
				nteBtn.setVisible(true);
				statusBtn.setVisible(true);
				assignedTeam.setDisable(false);
				break;
			case 3:
				break;
		}
	}
}
    
