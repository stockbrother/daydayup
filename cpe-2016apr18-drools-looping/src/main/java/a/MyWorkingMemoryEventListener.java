package a;

import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.event.rule.ObjectUpdatedEvent;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyWorkingMemoryEventListener implements WorkingMemoryEventListener {
	private static final Logger LOG = LoggerFactory.getLogger(MyWorkingMemoryEventListener.class);

	@Override
	public void objectInserted(ObjectInsertedEvent event) {
		LOG.info("objectInserted");
	}

	@Override
	public void objectUpdated(ObjectUpdatedEvent event) {
		LOG.info("objectUpdated");
	}

	@Override
	public void objectRetracted(ObjectRetractedEvent event) {
		LOG.info("objectRetracted");
	}

}
