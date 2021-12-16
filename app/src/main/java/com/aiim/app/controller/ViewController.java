package com.aiim.app.controller;

import java.io.IOException;
import java.util.ResourceBundle;
import com.aiim.app.resource.ViewNames;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.stage.Stage;

/* The following class handles the switching between views and is the only class aware of the path to each view. Part of MVC design Pattern as a controller.
 * Neil Campbell 07/05/2021, B00361078
 */

public class ViewController {
	
	private static volatile ViewController vc = null;
	private Parent root;
	private static ResourceBundle strBundle;
	private String viewResource;
	private Stage currentStage;
	private Scene mainScene;
	private Scene currentScene;
	private SubScene currentSubScene;
	private int PERMLEVEL;
	
	
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
						strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
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
	public void setCurrentSubScene(SubScene subScene) {
		this.currentSubScene = subScene;
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
	public void initialiseSubScene() {
		currentSubScene.setRoot(root);
	}
	private void setRoot(String viewResource2) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(viewResource));
		root = loader.load();
	}
	
	public void switchToView(ViewNames view) throws IOException {
		switch(view) {
		  case LOGIN:
				viewResource  = "/com/aiim/app/view/login.fxml";
				setRoot(viewResource);
				initialiseStage();
			break;
		  case DASHBOARD:
			  	viewResource  = "/com/aiim/app/view/dashboard.fxml";
			  	setRoot(viewResource);
			  	initialiseSubScene();
		    break;
		  case TICKET:
			  	viewResource  = "/com/aiim/app/view/ticket.fxml";
			  	setRoot(viewResource);
			  	initialiseSubScene();
			break;
		  case SETTINGS:
			  	viewResource  = "/com/aiim/app/view/settings.fxml";
			  	setRoot(viewResource);
			  	initialiseSubScene();
			break;
		  case HOME:
			  	viewResource  = "/com/aiim/app/view/home.fxml";
			  	setRoot(viewResource);
			  	initialiseScene();
			break;
		  case AMENDTICKET:
			  	viewResource  = "/com/aiim/app/view/amendTicket.fxml";
			  	setRoot(viewResource);
			  	initialiseSubScene();
			break;
		  default:
			  currentStage.close();
			}
	}

	public int getViewPerm(ViewNames view) {
		switch(view) {
		case TICKET:
			PERMLEVEL = 5;;
		break;
		default:
			break;
		}
		// TODO Auto-generated method stub
		return PERMLEVEL;
	}
}
