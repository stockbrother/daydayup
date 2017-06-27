package ddu.test;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import daydayup.openstock.ooa.ServiceRegistraction;

public class Log4jConfig {
	static {
		//String configFile = ServiceRegistraction.class.getResource("/log4j2.xml").getFile();
		//configFile = "jar://target/openstock-impl-0.0.1-SNAPSHOT.jar!/log4j2.xml";
		/*
		String configFile = "jar:file:/D:/git/daydayup/openstock/openstock-impl/target/openstock-impl-0.0.1-SNAPSHOT.jar!/log4j2.xml";
		final LoggerContext ctx = Configurator.initialize("", ServiceRegistraction.class.getClassLoader(),
				configFile);
		ctx.start();
		*/
	}

	public static void main(String[] args) {
		//Logger LOG = LoggerFactory.getLogger("daydayup.openstock.MyLog");
		//if (LOG.isTraceEnabled()) {
		//	LOG.trace("abc");
		//}
	}
}
