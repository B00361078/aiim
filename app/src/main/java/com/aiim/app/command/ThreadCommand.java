package com.aiim.app.command;

import java.sql.SQLException;

public interface ThreadCommand {
	public void execute(String arg) throws SQLException, Exception;
}
