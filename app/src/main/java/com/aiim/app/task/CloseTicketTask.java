package com.aiim.app.task;

import org.nd4j.linalg.api.ndarray.INDArray;
import com.aiim.app.util.Session;

public class CloseTicketTask extends ThreadTask {
	
	private String team;
	private String details;
	
	public CloseTicketTask (String title, String details, String team) {
		super(title);
		this.setTeam(team);
		this.setDetails(details);
	}
	
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Override
	protected Object call() throws Exception {
        updateMessage("Closing ticket, please wait.");	
	    sqlStatement = con.prepareStatement(strBundle.getString("sqlUpdate5"));	 	
	    sqlStatement.setString(1, "Closed");
	    sqlStatement.setObject(2, appUtil.getDate());
	    sqlStatement.setString(3, Session.getCurrentTicket());
    	appUtil.executeSQL(con, sqlStatement);
        if (appUtil.getMode("trainMode").contains("OFF")) {
        	System.out.println("Will not retrain");
        }
        else if (appUtil.getMode("trainMode").contains("ON")) {
    	    INDArray features = network.getFeatures(details, Session.getDataSetIterator());
    	    if (!(features == null)) {
    	    	appUtil.retrain(team + ".txt", details);
    	    }
    	    else {
    	    	System.out.println("Will not retrain");
    	    }
        }
        else {
        	System.out.println("Will not retrain");
        }
        updateMessage("Ticket closed successfully");
        updateProgress(1, 1);
		return null;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

}
