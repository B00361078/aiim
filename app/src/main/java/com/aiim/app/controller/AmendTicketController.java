package com.aiim.app.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import com.aiim.app.cnn.MyIter;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.model.Note;
import com.aiim.app.model.Ticket;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.util.Session;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

/* The following class handles the dashboard interface by switching the current subScene. Part of MVC design Pattern as a controller.
 * Neil Campbell 14/12/2021, B00361078
 */

public class AmendTicketController {

	private int permLevel;
	private Connection con;
	private PreparedStatement stmt;
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
	private int VIEWPERMLEVEL;
	private String prediction;
	private String teamID;
	private static DataSetIterator trainIter;
	@FXML private Button raiseBtn;
	@FXML private Button assignBtn;
	@FXML private Button statusBtn;
	@FXML private Button nteBtn;
	@FXML private TableView<Note.Builder> noteTable;
	private PreparedStatement sqlStatement;
	private PreparedStatement sqlStatement2;
	private String assignedTeamID;
	private String teamName;
	private PreparedStatement sqlStatement3;
	private String ticketStatus;
	private String message;
	
   
	public void initialize() throws Exception, SQLException {
		assignBtn.setVisible(false);
		con = DatabaseConnect.getConnection();
		ticketNo.setText(Session.getCurrentTicket());
		setDetails();
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
		ObservableList<String> list = assignedTeam.getItems();	
    	//reporter.setText(Session.getFullName());
    	VIEWPERMLEVEL = 5;
    	checkHasPermission();
    	//reporter.setText(Session.getFullName());
    	details.setWrapText(true);
    	details.setEditable(false);
    	assignedTeam.getSelectionModel().selectedIndexProperty().addListener(
    	         (ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
    	            System.out.println(list.get((int) new_val));
    	      });
//    	raiseBtn.setOnAction(ae -> {
//            ae.consume();
//            raiseBtn.setDisable(true);
//            MyTask task = new MyTask();
//            task.setOnSucceeded(e -> task.getValue());
//            Alert alert = createProgressAlert(ViewController.createInstance().getCurrentStage(), task);
//            executeTask(task);
//            alert.showAndWait();
//            try {
//				ViewController.createInstance().switchToView(ViewNames.DASHBOARD);
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			});
    }
    
    private void checkHasPermission() {
    	if(Session.getPermissionLevel() >= VIEWPERMLEVEL) {
    		
    	}
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
    		break;
    	}
    }
    private void setDetails() throws Exception {
    	con.setAutoCommit(false);
    	sqlStatement = con.prepareStatement("SELECT reporter,assignee,status,dateRaised,assignedTeam,detail from tblTicket WHERE  ticketID = ?");
    	sqlStatement.setString(1, Session.getCurrentTicket());
    	ResultSet rs = sqlStatement.executeQuery();
    	while(rs.next()){
    		reporter.setText(rs.getString(1));
    		setAssignee(rs.getString(2));
    		status.setText(rs.getString(3));
    		ticketStatus = rs.getString(3).toString();
    		dateRaised.setText(rs.getDate(4).toString());
    		assignedTeamID = rs.getString(5);
    		details.setText(rs.getString(6));
        }
    	sqlStatement2 = con.prepareStatement("SELECT name from tblTeam");
    	ResultSet rs2 = sqlStatement2.executeQuery();
    	while(rs2.next()){
    		assignedTeam.getItems().add(rs2.getString(1));
        }
    	
    	sqlStatement3 = con.prepareStatement("SELECT name from tblTeam WHERE teamID = ?");
    	sqlStatement3.setString(1, assignedTeamID);
    	
    	ResultSet rs3 = sqlStatement3.executeQuery();
    	while(rs3.next()){
    		teamName = rs3.getString(1);
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
	    
	   
	    stmt = con.prepareStatement("USE [honsdb] INSERT INTO tblNote (author,ticketRef,message,dateCreated) VALUES(?,?,?,?)");
	    
	    stmt.setString(1, Session.getUsername());
	    stmt.setString(2, Session.getCurrentTicket());
	    stmt.setString(3, message);
	    stmt.setObject(4, sqlDate);
	    		
	    		
	    		
	    		if (stmt.executeUpdate() == 1)
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
            MyIter iter = new MyIter();
        	trainIter = iter.getDataSetIterator();
        	prediction = iter.ticketClassifier(details.getText(), trainIter);
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

	con = DatabaseConnect.getConnection();
	stmt = con.prepareStatement("USE honsdb select noteID,author,message FROM tblNote WHERE ticketRef = ?");
	stmt.setString(1, Session.getCurrentTicket());
	
	ResultSet rs = stmt.executeQuery();
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
    	stmt = con.prepareStatement("USE honsdb select author,ticketRef,message,dateCreated FROM tblNote WHERE noteID = ?");
    	stmt.setString(1, noteID);
    	
    	ResultSet rs = stmt.executeQuery();
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
}
    
