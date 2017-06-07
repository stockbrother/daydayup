package daydayup.openstock;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.uno.XComponentContext;

import daydayup.openstock.cninfo.CorpInfoRefreshCommand;
import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.executor.TaskConflictException;
import daydayup.openstock.executor.TaskExecutor;
import daydayup.openstock.netease.NeteaseDataDownloadCommand;
import daydayup.openstock.netease.NeteaseDataPreprocCommand;
import daydayup.openstock.netease.NeteaseWashed2DbCommand;
import daydayup.openstock.netease.NeteaseWashed2SheetCommand;
import daydayup.openstock.util.DocUtil;

public class OpenStock {
	
	private static final Logger LOG = LoggerFactory.getLogger(OpenStock.class);

	private static OpenStock ME;

	private DataBaseService dbs;

	private TaskExecutor commandExecutor = new TaskExecutor();

	private Map<String, Class> commandClassMap = new HashMap<>();

	public OpenStock() {
		this.dbs = DataBaseService.getInstance(EnvUtil.getDataDir(), EnvUtil.getDbName());
		commandClassMap.put("CorpInfoRefreshCommand", CorpInfoRefreshCommand.class);
		commandClassMap.put("NeteaseDataDownloadCommand", NeteaseDataDownloadCommand.class);
		commandClassMap.put("NeteaseDataPreprocCommand", NeteaseDataPreprocCommand.class);
		commandClassMap.put("NeteaseWashed2DbCommand", NeteaseWashed2DbCommand.class);
		// commandClassMap.put("InterruptAllTaskCommand",
		// InterruptAllTaskCommand.class);
		commandClassMap.put("CorpsApply2MemoryCommand", CorpsApply2MemoryCommand.class);
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

	public void execute(String command, XComponentContext xcc) {
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

	

	public void execute(CommandBase command, XComponentContext xcc) {
		CommandContext cc = new CommandContext(xcc);
		try {
			this.commandExecutor.execute(command, cc);
		} catch (TaskConflictException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MessageBoxUtil.showMessageBox(xcc, null, "Task Conflict Error", "Detail:" + e.getMessage());
		}

	}

}
