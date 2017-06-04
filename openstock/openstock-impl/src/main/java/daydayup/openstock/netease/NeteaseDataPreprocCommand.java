package daydayup.openstock.netease;

import daydayup.openstock.CommandBase;
import daydayup.openstock.CommandContext;
import daydayup.openstock.executor.Interruptable;

public class NeteaseDataPreprocCommand extends CommandBase implements Interruptable {
	NeteasePreprocessor pp;

	@Override
	public void execute(CommandContext cc) {

		pp = new NeteasePreprocessor(NeteaseUtil.getDataDownloadDir(), NeteaseUtil.getDataWashedDir());
		pp.types(NeteaseCollector.TYPE_zcfzb, NeteaseCollector.TYPE_lrb, NeteaseCollector.TYPE_xjllb);
		pp.execute();
	}

	@Override
	public void interrupt() {
		pp.interrupt();
	}

}
