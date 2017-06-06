package daydayup.openstock.netease;

import daydayup.openstock.CommandBase;
import daydayup.openstock.CommandContext;
import daydayup.openstock.GlobalVars;
import daydayup.openstock.executor.Interruptable;

public class NeteaseDataDownloadCommand extends CommandBase<Void> implements Interruptable {
	NeteaseCollector c;

	@Override
	public Void execute(CommandContext cc) {
		String[] codeArray = GlobalVars.getInstance().getCorpNameService().getSortedCorpCodeArray();
		c = new NeteaseCollector(NeteaseUtil.getDataDownloadDir())//
				.types(new String[] { //
						NeteaseCollector.TYPE_zcfzb//
						, NeteaseCollector.TYPE_lrb//
						, NeteaseCollector.TYPE_xjllb//
				})//
				.corpCodes(codeArray)//
				.pauseInterval(5000)//
		;

		c.execute();
		return null;
	}

	@Override
	public void interrupt() {
		c.interrupt();
	}

}
