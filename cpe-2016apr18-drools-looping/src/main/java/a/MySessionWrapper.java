package a;

import java.util.Collection;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySessionWrapper {

	private static final Logger LOG = LoggerFactory.getLogger(MySessionWrapper.class);
	
	private StatefulKnowledgeSession session;

	private boolean insideFireAllRule;

	public void start() {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

		kbuilder.add(ResourceFactory.newFileResource("src/main/resources/a/r1.drl"), ResourceType.DRL);
		if (kbuilder.hasErrors()) {

			System.out.println(kbuilder.getErrors());

			return;

		}

		Collection<KnowledgePackage> kpkgs = kbuilder.getKnowledgePackages();
		KnowledgeBaseConfiguration kbc = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
		kbc.setOption(EventProcessingOption.CLOUD);
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbc);

		kbase.addKnowledgePackages(kpkgs);

		session = kbase.newStatefulKnowledgeSession();
		session.setGlobal("sessionWrapper", this);//
		session.setGlobal("op", new MyOp(this));
		session.addEventListener(new MyAgendaEventListener());
		session.addEventListener(new MyWorkingMemoryEventListener());

	}

	public StatefulKnowledgeSession getSession() {
		return session;
	}

	public FactHandle insert(Object mo) {
		return this.session.insert(mo);
	}

	public void update(Object object) {
		LOG.info(">>update");
		
		FactHandle fh = this.session.getFactHandle(object);
		this.session.update(fh, object);
		if (!this.insideFireAllRule) {
			this.insideFireAllRule = true;
			this.fireAllRules();
			this.insideFireAllRule = false;
			LOG.info("<<fireAllRule");
		}
		LOG.info("<<update");
		
	}

	public void doUpdate(FactHandle handle, Object object) {
		this.session.update(handle, object);
	}

	public void fireAllRules() {
		this.session.fireAllRules();
	}

	public FactHandle getFactHandle(MyObject mo) {
		//
		return this.session.getFactHandle(mo);//
	}

}
