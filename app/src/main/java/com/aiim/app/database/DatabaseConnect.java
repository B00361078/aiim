package com.aiim.app.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ResourceBundle;

/* The following class handles the database connection. Singleton pattern.
 * Neil Campbell 06/01/2022, B00361078
 */

public class DatabaseConnect {
	
	private static volatile Connection con = null;
	private static String dbEndpoint;
	private static String dbPort;
	private static String dbUser;
	private static String dbPass;
	private static ResourceBundle strBundle;
	private static String connectionURL;
	
	private DatabaseConnect() {
		if(con != null) {
			throw new RuntimeException("Use getConnection() method to connect");
		}
	};
	
	public static Connection getConnection() {
		if(con == null) {
			synchronized (DatabaseConnect.class) {
				if(con == null) {
					try {
						strBundle = ResourceBundle.getBundle("com.aiim.app.resource.jdbc");
						dbEndpoint = strBundle.getString("dbEndpoint");
						dbPort = strBundle.getString("dbPort");
						dbUser = strBundle.getString("dbUser");
						dbPass = strBundle.getString("dbPass");
						Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
						connectionURL = "jdbc:sqlserver://"+dbEndpoint+":"+dbPort+";"+"user="+dbUser+";password="+dbPass;
						con = DriverManager.getConnection(connectionURL); 
						con.setAutoCommit(false);
						return con;
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					return con;
				}
			}
		}
		return con;
	}
}
