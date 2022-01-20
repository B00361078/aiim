package com.aiim.app.controller;

import java.io.IOException;
import java.util.ResourceBundle;

import com.aiim.app.view.ViewNames;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/* The following class handles the switching between views and is the only class aware of the path to each view. Part of MVC design Pattern as a controller.
 * This is also a Singleton.
 * Neil Campbell 06/01/2022, B00361078
 */

public class ViewController {
	
	private static volatile ViewController vc = null;
	private Parent root;
	private static ResourceBundle strBundle;
	private String viewResource;
	private Stage currentStage;
	private Scene mainScene;
	private Scene currentScene;	
	private ViewNames view;
	
	private ViewController() {
		if(vc != null) {
			throw new RuntimeException("Use createInstance() method to create");
		}
	};
	
	public static ViewController createInstance() {
		if(vc == null) {
			synchronized (ViewController.class) {
				if(vc == null) {
					try {	
						vc = new ViewController();
						strBundle = ResourceBundle.getBundle("bundle");
						return vc;
					}
					catch (Exception e) {
					}
					return vc;
				}
			}
		}
		return vc;
	}
	
	public void setCurrentStage(Stage stage) {
		this.currentStage = stage;
	}
	
	public Stage getCurrentStage() {
		return this.currentStage;
	}
	
	public void setCurrentScene(Scene scene) {
		this.currentScene = scene;
	}
		
	public void initialiseStage() {
		mainScene = new Scene(root, 800, 600);
        currentStage.setTitle(strBundle.getString("appName"));
        currentStage.setScene(mainScene);
        currentStage.setResizable(false);
        currentStage.show();
	}
	
	public void initialiseScene() {
		currentScene.setRoot(root);
	}
	
	private void setRoot(String viewResource2) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(viewResource));
		root = loader.load();
	}
	
	public String getResource() {
		return viewResource;
	}
	
	public String switchToView(ViewNames view) throws IOException {
		switch(view) {
		  case LOGIN:
				viewResource  = "/com/aiim/app/view/login.fxml";
				setRoot(viewResource);
				initialiseStage();
				return viewResource;
		  case DASHBOARD:
			  	viewResource  = "/com/aiim/app/view/dashboard.fxml";
			  	return viewResource;
		  case TICKET:
			  	viewResource  = "/com/aiim/app/view/ticket.fxml";
			  	return viewResource;
		  case SETTINGS:
			  	viewResource  = "/com/aiim/app/view/settings.fxml";
			  	return viewResource;
		  case HOME:
			  	viewResource  = "/com/aiim/app/view/home.fxml";
			  	setRoot(viewResource);
			  	initialiseScene();
			  	return viewResource;
		  case AMENDTICKET:
			  	viewResource  = "/com/aiim/app/view/amendTicket.fxml";
			  	return viewResource;
		  default:
			  currentStage.close();
			  return null;
			}
	}

	public ViewNames getView() {
		return view;
	}

	public void setView(ViewNames view) {
		this.view = view;
	}
}
