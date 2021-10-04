package com.aiim.app;
	
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.Connection;
import com.aiim.app.controller.ViewController;
import com.aiim.app.database.DatabaseConnect;
import com.aiim.app.resource.ViewNames;

public class Main extends Application {
	
	private ViewController viewController;
	private Connection con;
	
	@Override
    public void start(Stage stage) throws Exception {
		viewController = new ViewController();
		viewController.setCurrentStage(stage);
		viewController.switchToView(ViewNames.LOGIN);
		con = DatabaseConnect.getInstance().getConnection();
		//System.out.println(con);
		
    }
	
}
