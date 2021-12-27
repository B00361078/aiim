package com.aiim.app;
	
import javafx.application.Application;
import org.apache.commons.io.FileUtils;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Connection;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import com.aiim.app.controller.ViewController;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.model.DataSetIter;
import com.aiim.app.resource.ViewNames;
import com.aiim.app.util.Session;

public class Main extends Application {
	
	private Connection con;
	private ResourceBundle strBundle;
	private Stage stage;
	private DataSetIterator trainIter;
	private String currentDirectory;
	private PreparedStatement sqlStatement;
	private ResultSet rs;
	private ArrayList<String> list;
	
	@Override
    public void start(Stage stage) throws Exception {
		this.stage = stage;
		ViewController.createInstance().setCurrentStage(stage);
		ViewController.createInstance().switchToView(ViewNames.LOGIN);
		strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
		con = DatabaseConnect.getConnection();
		System.out.println("con is " + con);
		checkDBConnect();
		setLabels();
		downloadFiles();
		DataSetIter iter = new DataSetIter();
    	trainIter = iter.getDataSetIterator();
    	Session.setDataIter(trainIter);	
    }
	@Override
	public void stop() throws IOException{
	    FileUtils.cleanDirectory(new File(currentDirectory+"/files")); 
	}
	public void downloadFiles() throws IOException, SQLException {
    	currentDirectory = Paths.get("").toAbsolutePath().toString();
		sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect2"));
		rs = sqlStatement.executeQuery();
			while (rs.next()) {
				String filename = rs.getString(1);
				Blob content = rs.getBlob(2);
				InputStream inputstr = content.getBinaryStream();
				Files.copy(inputstr, Paths.get(currentDirectory+"/files/"+filename), REPLACE_EXISTING);
			}
	}
    
    public void setLabels() throws SQLException {
    	list = new ArrayList<String> ();
    	sqlStatement = con.prepareStatement(strBundle.getString("sqlSelect3"));
		rs = sqlStatement.executeQuery();
			while (rs.next()) {
				String label = rs.getString(1);
				list.add(label);
			}
		//remove general label to leave only prediction labels
		list.remove("general");
		Session.setPredictionLabels(list);
    }
	
	void checkDBConnect() {
		if (con != null) {
			System.out.println("connected to db");
		}
		else {
			new javafx.scene.control.Alert(Alert.AlertType.ERROR, strBundle.getString("e11")).showAndWait();
			stage.close();
		}
		
	}
	// upload file from db
	
	public void upload () throws Exception {

		File file;
	    file = new File("cnn_model.zip");// ...(file is initialised)...
	    byte[] fileContent = Files.readAllBytes(file.toPath());
	    String filename = file.getName();
	    String mode = "ON";
	    long filelength = file.length();
	    long filelengthinkb = filelength/1024;
	    java.util.Date date = new java.util.Date();
	    Object param = new java.sql.Timestamp(date.getTime());
	    // The JDBC driver knows what to do with a java.sql type:
	    con.setAutoCommit(false);
	    
	    PreparedStatement prepared_statement3 = con.prepareStatement("USE [honsdb] UPDATE tblClassifier SET fileName=?,size=?,modDate=?,fileContent=? WHERE id=?");
		//prepared_statement3.setLong(1, filelengthinkb);
		//prepared_statement3.setBytes(2, fileContent);
		prepared_statement3.setString(1, filename);
		prepared_statement3.setLong(2, filelengthinkb);
		prepared_statement3.setObject(3, param);
		prepared_statement3.setBytes(4, fileContent);
		prepared_statement3.setInt(5, 1);
	   
	    
	    		
	    		if (prepared_statement3.executeUpdate() == 1)
	    		{
	    			con.commit();
	    			System.out.println("Byte Array Stored Successfully in SQL Server");
	    		}
	    		else
	    		{
	    			throw new Exception("Problem occured during Save");
	    		}
	    
	}

	//update to db
	public void update() throws SQLException, IOException {
		File file;
	    file = new File("trained_model.zip");// ...(file is initialised)...
	    byte[] fileContent = Files.readAllBytes(file.toPath());
	    String filename = file.getName();
	    String mode = "testmode";
	    long filelength = file.length();
	    long filelengthinkb = filelength/1024;
	    con.setAutoCommit(false);
	    //update db
		//PreparedStatement prepared_statement3 = con.prepareStatement("USE [honsdb] UPDATE tblClassifier SET size=?, fileContent=?, mode=? WHERE id=1");
		PreparedStatement prepared_statement3 = con.prepareStatement("USE [honsdb] UPDATE tblClassifier SET mode=? WHERE id=4");
		//prepared_statement3.setLong(1, filelengthinkb);
		//prepared_statement3.setBytes(2, fileContent);
		prepared_statement3.setString(1, "semi");
		
		if (prepared_statement3.executeUpdate() == 1)
		{
			con.commit();
			System.out.println("Updated successfully");
		}
		else
		{
			System.out.println("Problem occured during update");
		}
		
		//prepared_statement3.executeUpdate();
		//System.out.println("updated now");
		
	}
	
}
