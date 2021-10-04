package com.aiim.app.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ResourceBundle;

public class DatabaseConnect {
	
	private static volatile DatabaseConnect instance = null;
	private String dbEndpoint;
	private String dbPort;
	private String dbUser;
	private String dbPass;
	private ResourceBundle strBundle;
	
	private DatabaseConnect() {
		if(instance != null) {
			throw new RuntimeException("Use getInstance() method to create");
		}
	};
	
	public static DatabaseConnect getInstance() {
		if(instance == null) {
			synchronized (DatabaseConnect.class) {
				if(instance == null) {
					instance = new DatabaseConnect(); 
				}
			}
		}
		return instance;
	}
	
	public Connection getConnection() throws ClassNotFoundException {
		Connection con = null;

		try {
			strBundle = ResourceBundle.getBundle("com.aiim.app.resource.jdbc");
			dbEndpoint = strBundle.getString("dbEndpoint");
			dbPort = strBundle.getString("dbPort");
			dbUser = strBundle.getString("dbUser");
			dbPass = strBundle.getString("dbPass");
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionURL = "jdbc:sqlserver://"+dbEndpoint+":"+dbPort+";"+"user="+dbUser+";password="+dbPass;
			System.out.println(connectionURL);
			con = DriverManager.getConnection(connectionURL); 
			con.setAutoCommit(false);
			return con;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return con;
	}
}
