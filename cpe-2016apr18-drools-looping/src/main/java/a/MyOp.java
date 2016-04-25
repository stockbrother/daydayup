package a;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyOp {

	private static final Logger LOG = LoggerFactory.getLogger(MyOp.class);

	private MySessionWrapper sessionWrapper;

	public MyOp(MySessionWrapper sw) {
		this.sessionWrapper = sw;
	}

	public void newAlarmFindOrCreateGroup(MyObject mo) {
		LOG.info(">>newAlarmFindOrCreateGroup");//
		mo.getVar().setBoolean("NeedNavigationUpdate", true);//
		this.sessionWrapper.update(mo);//
		LOG.info("<<newAlarmFindOrCreateGroup");//
	}

	public void alarmUpdatedManageLifecycle(MyObject mo) {
		LOG.info(">>alarmUpdatedManageLifecycle");//

		mo.addStateChange("scX");
		
		//this.sessionWrapper.update(mo);//
		LOG.info("<<alarmUpdatedManageLifecycle");//
	}

	public void op3(MyObject mo) {
		LOG.info(">>op3");//
		LOG.info("<<op3");//
	}
}
