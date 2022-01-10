package com.aiim.app;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.ResourceBundle;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import com.aiim.app.controller.ViewController;
import com.aiim.app.resource.ViewNames;
import javafx.stage.Stage;

class MainIT extends ApplicationTest {
	
	private ResourceBundle strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");

	@Override
	public void start (Stage stage) throws Exception {
		ViewController.createInstance().setCurrentStage(stage);
		ViewController.createInstance().switchToView(ViewNames.LOGIN);
	}
	
	@Test void testSwitchToLoginPage() throws IOException {
		assertEquals("/com/aiim/app/view/login.fxml", ViewController.createInstance().getResource(), strBundle.getString("testError1"));
	}
	
	@Test
	public void testSuccessfulLogin() throws IOException {
	    clickOn("#usernameField").write("ncam0310");
	    clickOn("#passwordField").write("AvaArran");
	    clickOn("#loginBtn");
	    assertEquals("/com/aiim/app/view/dashboard.fxml", ViewController.createInstance().getResource(), strBundle.getString("testError1"));
	}

}
