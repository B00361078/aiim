package com.aiim.app.task;

import org.nd4j.linalg.api.ndarray.INDArray;
import com.aiim.app.util.Session;

/* Sub class of ThreadTask for closing ticket and retraining model.
 * Neil Campbell 19/01/2022, B00361078
 */

public class CloseTicketTask extends ThreadTask {
	
	private String team;
	private String details;
	
	public CloseTicketTask (String title, String details, String team) {
		
		super(title);
		this.setTeam(team);
		this.setDetails(details);
	}

	public void setDetails(String details) {
		this.details = details;
	}
	
	public void setTeam(String team) {
		this.team = team;
	}

	@Override
	protected Object call() throws Exception {
        updateMessage(strBundle.getString("closeTicket"));	
	    sqlStatement = con.prepareStatement(strBundle.getString("sqlUpdate5"));	 	
	    sqlStatement.setString(1, "Closed");
	    sqlStatement.setObject(2, appUtil.getDate());
	    sqlStatement.setString(3, Session.getCurrentTicket());
    	appUtil.executeSQL(con, sqlStatement);
        if (appUtil.getMode("trainMode").contains("OFF")) {
        	System.out.println(strBundle.getString("noTrain"));
        }
        else if (appUtil.getMode("trainMode").contains("ON")) {
    	    INDArray features = network.getFeatures(details, Session.getDataSetIterator());
    	    if (!(features == null)) {
    	    	appUtil.retrain(team + ".txt", details);
    	    }
    	    else {
    	    	System.out.println(strBundle.getString("noTrain"));
    	    }
        }
        else {
        	System.out.println(strBundle.getString("noTrain"));
        }
        updateMessage(strBundle.getString("closeSuccess"));
        updateProgress(1, 1);
		return null;
	}
}
