package com.aiim.app.task;

import org.nd4j.linalg.api.ndarray.INDArray;
import com.aiim.app.util.Session;

public class ClassifyTicketTask extends ThreadTask {
	
	private String details;
	private String prediction;
	
	public ClassifyTicketTask (String title, String details) {
		super(title);
		this.setDetails(details);
	}

	public void setDetails(String details) {
		this.details = details;
	}
	
	@Override
	protected Object call() throws Exception {
		updateMessage("Raising ticket, please wait.");

        if (appUtil.getMode("assignMode").contains("OFF")) {
        	prediction = "General";
        }
        else if (appUtil.getMode("assignMode").contains("ON")) {
	        INDArray features = network.getFeatures(details, Session.getDataSetIterator());
    	        if (!(features == null)) {
    	        	prediction = network.classify(features, Session.getModel());
    	        }
    	        else {
    	        	prediction = "General";
    	        }
        }
	    else {
	    	prediction = "General";
	    }
    	appUtil.insertTicket(prediction, details);
        updateMessage("Ticket raised successfully, raised to team - " + prediction);
        updateProgress(1, 1);
		return prediction;
	}
}
