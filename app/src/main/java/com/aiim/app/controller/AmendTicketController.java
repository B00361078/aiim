package com.aiim.app.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.api.ndarray.INDArray;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.model.DataSetIter;
import com.aiim.app.model.Network;
import com.aiim.app.model.Note;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.util.AppUtil;
import com.aiim.app.util.Session;
import javafx.concurrent.Task;
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
	private ResultSet rs;
	private ResourceBundle strBundle;
	private AppUtil appUtil;
	private String currentDirectory;
	private Network network;
	
	public void initialize() throws Exception, SQLException {
		network = new Network();
		currentDirectory = Paths.get("").toAbsolutePath().toString();
		con = DatabaseConnect.getConnection();
		setStatusAction();
		strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
		appUtil = new AppUtil();
		ticketNo.setText(Session.getCurrentTicket());
		setDetails();
		status(ticketStatus);
		setDisplay(Session.getPermissionLevel());
		updateNoteTable();
		setNoteTable();
		setAssignedTeamAction();
		assignedTeam.getItems();	
    	details.setWrapText(true);
    	details.setEditable(false);
    }
	
	public void setNoteTable() {
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
		assignedTeam.setOnAction((event) -> {
	    	Alert alert = new Alert(AlertType.CONFIRMATION);
    		alert.setHeaderText(strBundle.getString("e1"));
    		alert.showAndWait();
    		if (alert.getResult() == ButtonType.OK) {
    			try {
    				sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect13"));
					sqlStatement.setString(1, assignedTeam.getValue().toString());
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
	        			Alert alert2 = new Alert(AlertType.INFORMATION);
	    	    		alert2.setHeaderText(strBundle.getString("e2"));
	    	    		alert2.showAndWait();
	    	    		ViewController.createInstance().switchToView(ViewNames.DASHBOARD);
	        		}
				} catch (Exception e) {
					e.printStackTrace();
				}	 	
    		}
		});
		
	}
	public void setStatusAction() {
    	statusBtn.setOnAction(ae -> {
            if (statusBtn.getText() == "Close Ticket") {
            	statusBtn.setDisable(true);
            	ae.consume();
            	
            	
            ThreadTask task = new ThreadTask();
            task.setOnSucceeded(e -> task.getValue());
            Alert alert = appUtil.createProgressAlert(ViewController.createInstance().getCurrentStage(), task);          
            Thread thread = new Thread(task, "thread");
            thread.setDaemon(true);
            thread.start();
            alert.showAndWait();
	            try {
					ViewController.createInstance().switchToView(ViewNames.DASHBOARD);
					//stop the thread
					thread.interrupt();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
            else if (statusBtn.getText() == "Move to In Progress") {
            	Alert alert = new Alert(AlertType.CONFIRMATION);
        		alert.setHeaderText("Are you sure you want to move to in progress?");
        		alert.showAndWait();
        		if (alert.getResult() == ButtonType.OK) {
            	try {
					con.setAutoCommit(false);
					sqlStatement = con.prepareStatement("USE [honsdb] UPDATE tblTicket SET status = ?, dateUpdated = ? WHERE ticketID = ?");	 	
	        	    sqlStatement.setString(1, "In Progress");
	        	    sqlStatement.setObject(2, appUtil.getDate());
	        	    sqlStatement.setString(3, Session.getCurrentTicket());
	        	    if (sqlStatement.executeUpdate() == 1){
	        			con.commit();
	        			status.setText("In Progress");
	        			statusBtn.setText("Close Ticket");
	        		}
	        		else {
	        			throw new Exception("Error");
	        		}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
            	
            }
        }
	});
    	
    	
    }

    private void status (String status) {
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
//    @FXML private void clickStatus () throws Exception {
//    	changeStatus(statusBtn.getText());
//    }
   
    private void retrain(ComputationGraph currentModel, DataSetIter dataSetIter) throws Exception {
    	//check is train mode on
    	// download latest model files
    	//appUtil.setLabels();
    	//appUtil.downloadFiles();
    	
    	
    	String filename = assignedTeam.getValue() + ".txt";
		FileWriter fw = new FileWriter(currentDirectory+"/files/"+filename, true);
	    BufferedWriter bw = new BufferedWriter(fw);
	    bw.newLine();
	    bw.write(details.getText());
	    bw.close();
	    network.retrain(currentModel, dataSetIter.getDataSetIterator(true));
    	network.saveModel(currentModel, currentDirectory + "/files/cnn_model.zip");
    	updateFile(filename);
    	updateFile("cnn_model.zip");
    }
	    	
    
    public void updateFile(String filename) throws SQLException, IOException {
		File file;
	    file = new File(currentDirectory+"/files/"+filename);// ...(file is initialised)...
	    byte[] fileContent = Files.readAllBytes(file.toPath());
	    String mode = "testmode";
	    long filelength = file.length();
	    long filelengthinkb = filelength/1024;
	    con.setAutoCommit(false);
		sqlStatement = con.prepareStatement("USE [honsdb] UPDATE tblClassifier SET size=?, modDate=?, fileContent=? WHERE fileName=?");
		sqlStatement.setLong(1, filelengthinkb);
		sqlStatement.setObject(2, appUtil.getDate());
		sqlStatement.setBytes(3, fileContent);
		sqlStatement.setString(4, filename);
		
		if (sqlStatement.executeUpdate() == 1)
		{
			con.commit();
			System.out.println("Updated successfully");
		}
		else
		{
			System.out.println("Problem occured during update");
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
    public void getAssignedTeam() throws SQLException {
    	sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect12"));
    	sqlStatement.setString(1, Session.getCurrentTicket());
    	rs = sqlStatement.executeQuery();
    	while(rs.next()){
    		assignedTeamID = rs.getString(1);
        }
    	sqlStatement = con.prepareStatement("SELECT name from tblTeam WHERE teamID = ?");
    	sqlStatement.setString(1, assignedTeamID);
    	rs = sqlStatement.executeQuery();
    	//set value here
    	while(rs.next()){
    		assignedTeam.setValue(rs.getString(1));
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
	    con.setAutoCommit(false);	
	    sqlStatement = con.prepareStatement("USE [honsdb] UPDATE tblTicket SET assignee = ?, dateUpdated = ? WHERE ticketID = ?");	 	
	    sqlStatement.setString(1, Session.getUsername());
	    sqlStatement.setObject(2, appUtil.getDate());
	    sqlStatement.setString(3, Session.getCurrentTicket());
	    if (sqlStatement.executeUpdate() == 1){
			con.commit();
		}
		else {
			throw new Exception("Error");
		}
	    assignee.setText(Session.getUsername());
		assignBtn.setDisable(true);
		statusBtn.setDisable(false);
		setDisplay(Session.getPermissionLevel());
    }

    public void insertNote() throws Exception {
	    con.setAutoCommit(false);
	    
	   
	    sqlStatement = con.prepareStatement("USE [honsdb] INSERT INTO tblNote (author,ticketRef,message,dateCreated) VALUES(?,?,?,?)");
	    
	    sqlStatement.setString(1, Session.getUsername());
	    sqlStatement.setString(2, Session.getCurrentTicket());
	    sqlStatement.setString(3, message);
	    sqlStatement.setObject(4, appUtil.getDate());
	    		
	    		
	    		
	    		if (sqlStatement.executeUpdate() == 1)
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
        	insertNote();
        	noteTable.getItems().clear();
        	updateNoteTable();

        }
        else 
        	System.out.println("cancelling");
        
    }
    
    public void updateNoteTable() throws SQLException {

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
					assignedTeam.setDisable(true);
				}
				break;
			case 3:
				break;
		}
	}
    private class ThreadTask extends Task {

		private ThreadTask() {
            updateTitle("Close Ticket");
        }

        @Override
        protected String call() throws Exception {
        	
            updateMessage("Closing ticket, please wait.");
            con.setAutoCommit(false);	
    	    sqlStatement = con.prepareStatement("USE [honsdb] UPDATE tblTicket SET status = ?, dateUpdated = ? WHERE ticketID = ?");	 	
    	    sqlStatement.setString(1, "Closed");
    	    sqlStatement.setObject(2, appUtil.getDate());
    	    sqlStatement.setString(3, Session.getCurrentTicket());
	    	    if (sqlStatement.executeUpdate() == 1){
	    			con.commit();
	    			System.out.println("Status updated");		
	    		}
	    		else {
	    			throw new Exception("Error");
	    		}
    	    appUtil.setLabels();
            appUtil.downloadFiles();
    	    DataSetIter dataSetIter = new DataSetIter();
    	    ComputationGraph currentModel = network.restoreModel(currentDirectory + "/files/cnn_model.zip");
    	    INDArray features = network.getFeatures(details.getText(), dataSetIter.getDataSetIterator(true));
	    	    if ((features  != null) && (appUtil.getMode("trainMode").contains("ON"))) {
	    	    	retrain(currentModel, dataSetIter);
	    	    }
	    	    else {
	    	    	System.out.println("Will not retrain");
	    	    }
            updateMessage("Ticket closed successfully");
            updateProgress(1, 1);
            return null;
        }
    }
}
    
