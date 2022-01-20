package com.aiim.app.task;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.model.Network;
import com.aiim.app.util.AppUtil;
import javafx.concurrent.Task;

/* Super class which extends JavaFX task to allow for threading.
 * Neil Campbell 19/01/2022, B00361078
 */

public class ThreadTask extends Task {
	
	protected Network network;
	protected AppUtil appUtil;
	protected PreparedStatement sqlStatement;
	protected Connection con;
	protected ResourceBundle strBundle;
	protected String currentDirectory;
	
	protected ThreadTask(String taskTitle) {
		updateTitle(taskTitle);
		appUtil = new AppUtil();
		network = new Network();
		con = DatabaseConnect.getConnection();
		strBundle = ResourceBundle.getBundle("bundle");
		currentDirectory = Paths.get("").toAbsolutePath().toString();	
	}

	@Override
	protected Object call() throws Exception {
		return null;
	}
}
