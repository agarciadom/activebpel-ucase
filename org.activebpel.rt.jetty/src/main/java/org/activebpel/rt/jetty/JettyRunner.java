package org.activebpel.rt.jetty;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Wrapper which allows for easily starting and stopping ActiveBPEL inside a
 * Jetty instance.
 * 
 * @author Antonio García Domínguez
 */
public class JettyRunner {

	/**
	 * Creates a new instance of the runner.
	 * 
	 * @param mainDir
	 *            Directory where the logs and the deployed processes will be
	 *            saved to.
	 * @param port
	 *            Port on which Jetty (and thus ActiveBPEL) should listen.
	 * @param logger
	 *            Log4J Logger which will receive all the messages.
	 */
	public JettyRunner(File mainDir, int port, Logger logger) {
		this.fMainDirectory = mainDir;
		this.fListeningPort = port;
		this.fLogger = logger;

		this.fServer = new Server(port);
		HandlerList handlerList = new HandlerList();

		{
			WebAppContext adminWebapp = new WebAppContext();
			adminWebapp.setServer(fServer);
			adminWebapp.setContextPath("/BpelAdmin");
			adminWebapp.setResourceBase(JettyRunner.class.getClassLoader()
					.getResource("webapps/BpelAdmin").toExternalForm());
			handlerList.addHandler(adminWebapp);
		}

		{
			WebAppContext engineWebapp = new WebAppContext();
			engineWebapp.setServer(fServer);
			engineWebapp.setContextPath("/active-bpel");
			engineWebapp.setResourceBase(JettyRunner.class.getClassLoader()
					.getResource("webapps/active-bpel").toExternalForm());
			engineWebapp.getSecurityHandler().setLoginService(new HashLoginService());
			handlerList.addHandler(engineWebapp);
		}

		fServer.setHandler(handlerList);
	}

	/**
	 * Starts the server.
	 * 
	 * @throws Exception
	 *             There was a problem while starting the server.
	 */
	public void start() throws Exception {
		fLogger.info(String.format(
				"Starting ActiveBPEL with main directory %s on port %d",
				fMainDirectory.getCanonicalPath(), fListeningPort));
		fServer.start();
	}

	/**
	 * Waits for the server to finish its execution.
	 * 
	 * @throws InterruptedException
	 *             The current thread was interrupted by a call to
	 *             {@link java.lang.Thread#interrupt()}.
	 */
	public void join() throws InterruptedException {
		fServer.join();
	}

	/**
	 * Stops the currently running server.
	 * 
	 * @throws Exception
	 *             There was a problem while stopping the server.
	 */
	public void stop() throws Exception {
		fLogger.info("Stopping ActiveBPEL....");
		fServer.stop();
	}

	/**
	 * Entry point from the command line.
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Logger.getRootLogger().setLevel(Level.INFO);
		final Logger logger = Logger.getLogger(JettyRunner.class);

		final JettyRunner runner = new JettyRunner(new File("/tmp/activebpel"), 8080, logger);

		/*
		 * Stop the server when the user presses Ctrl+C or otherwise interrupts
		 * the application.
		 */
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					runner.stop();
				} catch (Exception e) {
					logger.error("Error while stopping Jetty", e);
				}
			}
		});

	}

}
