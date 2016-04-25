package a;

import org.drools.definition.rule.Rule;
import org.drools.event.rule.ActivationCancelledEvent;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.AgendaGroupPoppedEvent;
import org.drools.event.rule.AgendaGroupPushedEvent;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.RuleFlowGroupActivatedEvent;
import org.drools.event.rule.RuleFlowGroupDeactivatedEvent;
import org.drools.runtime.rule.PropagationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAgendaEventListener implements AgendaEventListener {

	private static final Logger LOG = LoggerFactory.getLogger(MyAgendaEventListener.class);

	@Override
	public void activationCreated(ActivationCreatedEvent event) {
		PropagationContext pc = event.getActivation().getPropagationContext();
		Rule rule = event.getActivation().getRule();
		LOG.info("C:" + rule.getName() + ",number:" + pc.getPropagationNumber());
	}

	@Override
	public void activationCancelled(ActivationCancelledEvent event) {
		PropagationContext pc = event.getActivation().getPropagationContext();
		Rule rule = event.getActivation().getRule();
		LOG.info("CL:" + rule.getName() + ",number:" + pc.getPropagationNumber());
	}

	@Override
	public void beforeActivationFired(BeforeActivationFiredEvent event) {
		PropagationContext pc = event.getActivation().getPropagationContext();
		Rule rule = event.getActivation().getRule();
		LOG.info("B:" + rule.getName() + ",number:" + pc.getPropagationNumber());
	}

	@Override
	public void afterActivationFired(AfterActivationFiredEvent event) {
		PropagationContext pc = event.getActivation().getPropagationContext();
		Rule rule = event.getActivation().getRule();
		LOG.info("A:" + rule.getName() + ",number:" + pc.getPropagationNumber());
	}

	@Override
	public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
		LOG.info("event:" + event);
	}

	@Override
	public void agendaGroupPushed(AgendaGroupPushedEvent event) {
		LOG.info("event:" + event);
	}

	@Override
	public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
		LOG.info("event:" + event);
	}

	@Override
	public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
		LOG.info("event:" + event);
	}

	@Override
	public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
		LOG.info("event:" + event);

	}

	@Override
	public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
		LOG.info("event:" + event);

	}

}
