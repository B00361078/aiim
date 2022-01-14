package com.aiim.app;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.ResourceBundle;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import com.aiim.app.controller.ViewController;
import com.aiim.app.resource.ViewNames;
import javafx.scene.Node;
import javafx.stage.Stage;

@TestMethodOrder(OrderAnnotation.class)

class MainIT extends ApplicationTest {
	
	private ResourceBundle strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
	
	
	@Override
	public void start (Stage stage) throws Exception {
		ViewController.createInstance().setCurrentStage(stage);
		ViewController.createInstance().switchToView(ViewNames.LOGIN);
	}
	
	@Order(1)
	@Test void testSwitchToLoginPage() throws IOException {
		assertEquals("/com/aiim/app/view/login.fxml", ViewController.createInstance().getResource(), strBundle.getString("testError1"));
	}
	
	@Order(2)
	@Test
	public void testSuccessfulLoginLogout() throws IOException {
	    clickOn("#usernameField").write("user2020");
	    clickOn("#passwordField").write("user2020");
	    clickOn("#loginBtn");
	    //assertEquals("/com/aiim/app/view/dashboard.fxml", ViewController.createInstance().getResource(), strBundle.getString("testError1"));
	    clickOn("#menuButton");
	    clickOn("#menuItemLogout");
	    assertEquals("/com/aiim/app/view/login.fxml", ViewController.createInstance().getResource(), strBundle.getString("testError1"));
	}
	
	@Order(3)
	@Test
	public void testUnSuccessfulLogin() throws IOException, InterruptedException {
	    clickOn("#usernameField").write("user20");
	    clickOn("#passwordField").write("user2020");
	    clickOn("#loginBtn");
	    FxAssert.verifyThat("OK", NodeMatchers.isVisible());
	}
	
	@Order(4)
	@Test
	public void testSettingsAccessible() throws IOException, InterruptedException {
	    clickOn("#usernameField").write("user2020");
	    clickOn("#passwordField").write("user2020");
	    clickOn("#loginBtn");
	    lookupById("#settingsBtn");
	    clickOn("#settingsBtn");
	    clickOn("#assignOff");
	}
	
	public <T extends Node> T lookupById(final String controlId) {
	    return (T) lookup(controlId).queryAll().iterator().next();
	}
	
}
