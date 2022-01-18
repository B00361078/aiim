package com.aiim.app.task;

import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.model.DataSetIter;
import com.aiim.app.model.Network;
import com.aiim.app.util.Session;

public class DataTask extends ThreadTask {
	
	public DataTask (String title) {
		super(title);
	}

	@Override
	protected Object call() throws Exception {
		updateMessage("Checking database connection, please wait.");
    	con = DatabaseConnect.getConnection();
	    	if (con != null) {
	    		updateMessage("Database connected, loading model, please wait.");
		    	appUtil.setLabels();
				appUtil.downloadFiles();
				network = new Network();
				DataSetIter dataSetIter = new DataSetIter();
				Session.setModel(network.restoreModel(currentDirectory + "/files/cnn_model.zip"));
				Session.setDataSetIterator(dataSetIter.getDataSetIterator(true)); 
				updateMessage("Model loaded successfully");
		        updateProgress(1, 1);
	    	}
		return con;
	}
}
