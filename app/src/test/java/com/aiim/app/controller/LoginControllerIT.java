package com.aiim.app.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import com.aiim.app.util.Session;
import com.aiim.app.view.ViewNames;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

class LoginControllerIT extends ApplicationTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}
	
//	@Override
//	public void start (Stage stage) throws Exception {
//
//		
//		Session.setPermissionLevel(3);
//		String viewResource  = "/com/aiim/app/view/test.fxml";
//		String viewResource2  = "/com/aiim/app/view/ticket.fxml";
//		FXMLLoader loader = new FXMLLoader();
//		loader.setLocation(getClass().getResource(viewResource));
//		Parent root = loader.load();
//		
//		FXMLLoader loader2 = new FXMLLoader();
//		loader2.setLocation(getClass().getResource(viewResource2));
//		Parent root2 = loader2.load();
//		
//		Scene mainScene = new Scene(root, 800, 600);
//		Scene scene1 = new Scene(root2, 200, 200);
//		stage.setTitle("test screen");
//		stage.setScene(mainScene);
//		stage.setResizable(false);
//		stage.show();
//
//
//		//mainScene.getRoot().getChildren().add
//
//		((Pane) mainScene.getRoot()).getChildren().add(scene1.getRoot());
//		
//		
//		
//		
//		//ViewController.createInstance().setCurrentStage(stage);
//		//ViewController.createInstance().switchToView(ViewNames.LOGIN);
//	}
//
//	@Test
//	void test() {
//		assertEquals("true", "true");
//		lookupById("#reporter");
//		//clickOn("#button2");
//	}
//	
//	public <T extends Node> T lookupById(final String controlId) {
//	    return (T) lookup(controlId).queryAll().iterator().next();
//	}

}
