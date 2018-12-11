package dk.medcom.cda.test;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class TestWar {
	public static void main(final String[] args) throws Exception {
		
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
		
		final Server server = new Server(8080);
		server.setHandler(new WebAppContext(
				"target/cda-validator-service-1.1.0-SNAPSHOT.war", "/"));
		// server.setHandler(new WebAppContext("hitspValidation.war", "/"));
		server.start();
	}
}
