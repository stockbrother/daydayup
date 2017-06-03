package daydayup.openstock.netease;

import daydayup.openstock.CommandBase;
import daydayup.openstock.CommandContext;
import daydayup.openstock.GlobalVars;

public class NeteaseDataDownloadCommand extends CommandBase {

	@Override
	public void execute(CommandContext cc) {
		String[] codeArray = GlobalVars.getInstance().getCorpNameService().getSortedCorpCodeArray();
		NeteaseCollector c = new NeteaseCollector(NeteaseUtil.getDataDownloadDir()).types(
				new String[] { NeteaseCollector.TYPE_zcfzb, NeteaseCollector.TYPE_lrb, NeteaseCollector.TYPE_xjllb })
				.corpCodes(codeArray);
		c.execute();
	}

}
