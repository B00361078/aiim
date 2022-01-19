package com.aiim.app.task;

import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.model.DataSetIter;
import com.aiim.app.util.Session;

/* Sub class of ThreadTask for loading model after successful connection to database.
 * Neil Campbell 19/01/2022, B00361078
 */

public class DataTask extends ThreadTask {
	
	public DataTask (String title) {
		super(title);
	}

	@Override
	protected Object call() throws Exception {
		updateMessage(strBundle.getString("dbConnect"));
    	con = DatabaseConnect.getConnection();
	    	if (con != null) {
	    		updateMessage(strBundle.getString("dbSuccess"));
		    	appUtil.setLabels();
				appUtil.downloadFiles();
				DataSetIter dataSetIter = new DataSetIter();
				Session.setModel(network.restoreModel(currentDirectory + "/files/cnn_model.zip"));
				Session.setDataSetIterator(dataSetIter.getDataSetIterator(true)); 
				updateMessage("Model loaded successfully");
		        updateProgress(1, 1);
	    	}
		return con;
	}
}
