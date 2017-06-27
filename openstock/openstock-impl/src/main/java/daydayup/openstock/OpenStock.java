package daydayup.openstock;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.uno.Exception;
import com.sun.star.uno.XComponentContext;

import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.document.ComponentContext;
import daydayup.openstock.document.StatusIndicator;
import daydayup.openstock.executor.TaskConflictException;
import daydayup.openstock.executor.TaskExecutor;
import daydayup.openstock.netease.NeteaseDataDownloadCommand;
import daydayup.openstock.netease.NeteaseDataWashingCommand;
import daydayup.openstock.netease.NeteaseWashed2DbCommand;
import daydayup.openstock.netease.NeteaseWashed2SheetCommand;
import daydayup.openstock.ooa.MessageBoxUtil;

public class OpenStock {

	private static final Logger LOG = LoggerFactory.getLogger(OpenStock.class);

	private static OpenStock ME;

	private DataBaseService dbs;

	private TaskExecutor commandExecutor = new TaskExecutor();

	private Map<String, Class> commandClassMap = new HashMap<>();

	Map<XComponentContext, Object> desktopObjectMap = new HashMap<>();

	public OpenStock() {
		this.dbs = DataBaseService.getInstance(EnvUtil.getDataDir(), EnvUtil.getDbName());
		commandClassMap.put("NeteaseDataDownloadCommand", NeteaseDataDownloadCommand.class);
		commandClassMap.put("NeteaseDataWashingCommand", NeteaseDataWashingCommand.class);
		commandClassMap.put("NeteaseWashed2DbCommand", NeteaseWashed2DbCommand.class);
		// commandClassMap.put("InterruptAllTaskCommand",
		// InterruptAllTaskCommand.class);
		commandClassMap.put("NeteaseWashed2SheetCommand", NeteaseWashed2SheetCommand.class);
		commandClassMap.put("SheetCommand", SheetCommand.class);

	}

	public static OpenStock getInstance() {
		//
		if (ME == null) {
			ME = new OpenStock();
		}
		return ME;
	}

	public DataBaseService getDataBaseService() {
		return this.dbs;
	}

	public Object getDesktop(XComponentContext xcc) {
		Object desktop = this.desktopObjectMap.get(xcc);
		if (desktop == null) {

			try {
				desktop = xcc.getServiceManager().createInstanceWithContext("com.sun.star.frame.Desktop", xcc);

			} catch (Exception e) {
				throw new RtException(e);
			}
			this.desktopObjectMap.put(xcc, desktop);
		}
		return desktop;
	}

	public void execute(String command, ComponentContext xcc) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("execute command:{}", command);
		}

		if ("InterruptAllTaskCommand".equals(command)) {
			this.commandExecutor.interruptAll();
			return;
		}

		Class cls = commandClassMap.get(command);
		if (cls == null) {
			LOG.warn("No this command:{}", command);
			return;
		}
		try {
			CommandBase cmd = (CommandBase) cls.newInstance();
			execute(cmd, xcc);
		} catch (InstantiationException e) {
			throw RtException.toRtException(e);
		} catch (IllegalAccessException e) {
			throw RtException.toRtException(e);
		}

	}

	public void execute(CommandBase command, ComponentContext xcc) {
		StatusIndicator si = xcc.createStatusIndicator();

		CommandContext cc = new CommandContext(xcc);
		try {
			this.commandExecutor.execute(command, cc);
		} catch (TaskConflictException e) {
			LOG.error("", e);
			// MessageBoxUtil.showMessageBox(xcc, null, "Task Conflict Error",
			// "Detail:" + e.getMessage());
		}
	}

}
