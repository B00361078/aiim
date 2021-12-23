package com.aiim.app.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.aiim.app.database.DatabaseConnect;

public class UpdateModeCommand implements ThreadCommand {

	private Connection con;
	private PreparedStatement sqlStatement;

	@Override
	public void execute(String arg) throws Exception {
		con = DatabaseConnect.getConnection();
		java.util.Date date = new java.util.Date();
	    java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());
	    con.setAutoCommit(false);	 
	    sqlStatement = con.prepareStatement("USE [honsdb] UPDATE tblClassifier SET mode = ?, modDate = ? WHERE id = 1");	 	
	    sqlStatement.setString(1, arg);
	    sqlStatement.setObject(2, sqlDate);
	    
	    		if (sqlStatement.executeUpdate() == 1){
	    			con.commit();
	    			System.out.println("Mode updated");
	    		}
	    		else {	
	    		}

		}
	}
