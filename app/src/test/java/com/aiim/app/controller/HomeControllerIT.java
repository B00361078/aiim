package com.aiim.app.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import com.aiim.app.util.Session;
import com.aiim.app.view.ViewNames;

import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.stage.Stage;

class HomeControllerIT extends ApplicationTest {
	
	@FXML public SubScene subScene;
	
//	@Override
//	public void start (Stage stage) throws Exception {
//		ViewController.createInstance().setCurrentStage(stage);
//		//ViewController.createInstance().initialiseStage();
//		ViewController.createInstance().setRoot("/com/aiim/app/view/home.fxml");
//		ViewController.createInstance().initialiseStage();
//		ViewController.createInstance().switchToView(ViewNames.HOME);
//		//ViewController.createInstance().setCurrentSubScene(subScene);
//    	//ViewController.createInstance().switchToView(ViewNames.DASHBOARD);
//	}


	@Test
	void testLogout() throws IOException, SQLException {
		//clickOn("#menuButton");
	}

}
