package com.aiim.app.util;

import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import com.aiim.app.cnn.MyIter;

public class Session {
	
	private static volatile Session session = null;
	private static int permissionLevel;
	private static String teamName;
	private static String username;
	private static String fullName;
	private static String teamID;
	private static String currentTicket;
	private static DataSetIterator iter;
	private static MyIter myIter;

	private Session() {
		if(session != null) {
			throw new RuntimeException("Use createSession() method to create");
		}
	};
	
	public static Session createSession() {
		if(session == null) {
			synchronized (Session.class) {
				if(session == null) {
					try {				
						return session;
					}
					catch (Exception e) {
					}
					return session;
				}
			}
		}
		return session;
	}
	

	public static String getUsername() {
		return username;
	}
	
	public static String getCurrentTicket() {
		return currentTicket;
	}
	public static DataSetIterator getIter() {
		return iter;
	}
	public static MyIter getMyIter() {
		return myIter;
	}

	public static void setUsername(String username) {
		Session.username = username;
	} 

	public static void setTeamName(String teamName) {
		Session.teamName = teamName;
	}
	public static void setTeamID(String teamID) {
		Session.teamID = teamID;
	}
	
	public static void setPermissionLevel(int permissionLevel) {
		Session.permissionLevel = permissionLevel;
	}
	public static int getPermissionLevel() {
		return permissionLevel;
	}
	public static String getTeamName() {
		return teamName;
	}
	public static String getTeamID() {
		return teamID;
	}
	public static String getFullName() {
		return fullName;
	}
	public static void setFullName(String fullname) {
		Session.fullName = fullname;
	}
	public static void setCurrentTicket(String currentTicket) {
		Session.currentTicket = currentTicket;
	}
	public static void clearSession () {
		setFullName(null);
		setPermissionLevel(1);
		setTeamName(null);
		setUsername(null);
		setTeamID(null);
		setCurrentTicket(null);
		session = null;
	}

	public static void setIter(DataSetIterator trainIter) {
		Session.iter = trainIter;// TODO Auto-generated method stub
		
	}

	public static void setMyIter(MyIter iter2) {
		Session.myIter = iter2;// TODO Auto-generated method stub
		
	}

}
