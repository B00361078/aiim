package com.aiim.app.util;

import java.sql.SQLException;

import com.aiim.app.command.ThreadCommand;

import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;

public class ThreadTask extends Task {
	
	private final String mystr;
	private final String mode;
	private ThreadCommand command;
			
    public ThreadTask(String task, String mode, ThreadCommand command) {
        this.mystr = task;
        this.mode = mode;
        this.command = command;
		updateTitle(this.mystr + " Auto ticket assignment.");
    }

    @Override
    protected String call() throws Exception {
        updateMessage(mystr);
        callCommand(command, mode);//updateMode(mode);
        updateMessage(this.mystr + " Auto ticket assignment was successful.");
        updateProgress(1, 1);
        return mystr;
    }

    private void callCommand(ThreadCommand command, String arg) throws Exception {
		command.execute(arg);
	}

	@Override
    protected void running() {
        System.out.println("Raising ticket task is running...");
    }

    @Override
    protected void succeeded() {
        System.out.println("Raising ticket task is successful.");
    }
    public Alert createProgressAlert(Stage owner, Task<?> task) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.initOwner(owner);
        alert.titleProperty().bind(task.titleProperty());
        alert.contentTextProperty().bind(task.messageProperty());

        ProgressIndicator pIndicator = new ProgressIndicator();
        pIndicator.progressProperty().bind(task.progressProperty());
        alert.setGraphic(pIndicator);

        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
        alert.getDialogPane().lookupButton(ButtonType.OK)
                .disableProperty().bind(task.runningProperty());       

        alert.getDialogPane().cursorProperty().bind(
		Bindings.when(task.runningProperty())
                    .then(Cursor.WAIT)
                    .otherwise(Cursor.DEFAULT)
        );

        return alert;
    }
    private void executeTask(Task<?> task) {
        Thread dbThread = new Thread(task, "dbThread");
        dbThread.setDaemon(true);
        dbThread.start();
    }

}
