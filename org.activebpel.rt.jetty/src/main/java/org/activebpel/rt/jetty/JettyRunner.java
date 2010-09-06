package org.activebpel.rt.jetty;

import java.io.File;
import java.io.IOException;

import org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig;
import org.activebpel.rt.bpel.server.admin.IAeEngineAdministration;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.logging.AeFileLogger;
import org.activebpel.rt.bpel.server.logging.AeLoggingFilter;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Wrapper which allows for easily starting and stopping ActiveBPEL inside a
 * Jetty instance.
 * 
 * @author Antonio García Domínguez
 */
public class JettyRunner {

	static {
		// Set the correct system property so the Axis factories are used in
		// JDK6+. See this for details:
		// http://forums.sun.com/thread.jspa?threadID=5334141
		System.setProperty("javax.xml.soap.MessageFactory",
				"org.apache.axis.soap.MessageFactoryImpl");
		installBridgeFromJULtoSLF4J();
	}

	/**
	 * Installs a bridge from {@link java.util.logging} calls to SLF4J calls, so
	 * we can have a single log4j.properties file controlling all logging, and
	 * we don't miss important messages.
	 */
	private static void installBridgeFromJULtoSLF4J() {
		java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
		rootLogger.setLevel(java.util.logging.Level.ALL);
		java.util.logging.Handler[] handlers = rootLogger.getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			rootLogger.removeHandler(handlers[i]);
		}
		SLF4JBridgeHandler.install();
	}

	private File fMainDirectory;
	private Logger fLogger;
	private Server fServer;
	private String fLoggingFilterName;

	/**
	 * Creates a new instance of the runner.
	 * 
	 * @param mainDir
	 *            Directory where the logs and the deployed processes will be
	 *            saved to.
	 * @param port
	 *            Port on which Jetty (and thus ActiveBPEL) should listen.
	 * @param logLevel
	 *            Logging level for all WS-BPEL processes (see
	 *            {@link AeLoggingFilter} for a list of the valid values).
	 * @param logger
	 *            Log4J Logger which will receive all the messages.
	 * @throws IOException
	 *             There was a problem while accessing the main directory.
	 */
	public JettyRunner(File mainDir, int port, String logLevel, Logger logger)
			throws IOException {
		this.fLogger = logger;
		this.fMainDirectory = mainDir;
		this.fLoggingFilterName = logLevel;

		ensureMainDirectoryExists();
		configureLogging();
		setUpServer(port, logLevel);
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
				fMainDirectory.getCanonicalPath(), fServer.getConnectors()[0]
						.getPort()));
		fServer.start();

		// Wait for ActiveBPEL to be really running
		IAeEngineAdministration admin = getEngineAdmin();
		while (!admin.isRunning()) {
			fLogger.debug("ActiveBPEL is not running yet, waiting 500ms...");
			synchronized (this) {
				this.wait(500);
			}
		}

		IAeUpdatableEngineConfig config = admin.getEngineConfig()
				.getUpdatableEngineConfig();
		// Switch to the desired logging level
		config.setLoggingFilter(fLoggingFilterName);
		// Place the deployment and process logs inside the work directory
		config.setLoggingBaseDir(fMainDirectory.getCanonicalPath());
		// Tell all other components to update their configuration accordingly
		config.update();
	}

	/**
	 * Waits for the server to finish its execution.
	 * 
	 * @throws InterruptedException
	 *             The current thread was interrupted by a call to
	 *             {@link java.lang.Thread#interrupt()}.
	 */
	public void join() throws InterruptedException {
		fLogger.info("Waiting for server to finish...");
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
	 * Returns the engine administration object for the ActiveBPEL webapp
	 * running in this Jetty instance.
	 *
	 * @param IAeEngineAdministration
	 *            Admin object, or <code>null</code> if Jetty hasn't been
	 *            started yet.
	 */
	public IAeEngineAdministration getEngineAdmin() {
		return AeEngineFactory.getEngineAdministration();
	}

	/**
	 * Entry point from the command line.
	 * 
	 * @param args
	 *            Arguments received through the command line.
	 * @throws Exception
	 *             There was a problem while accessing the main work directory
	 *             or starting Jetty.
	 */
	public static void main(String[] args) throws Exception {
		final Logger logger = LoggerFactory.getLogger(JettyRunner.class);
		final JettyRunner runner = new JettyRunner(new File("/tmp/activebpel"),
				8080, AeLoggingFilter.FULL, logger);

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

		try {
			runner.start();
			runner.join();
		} catch (InterruptedException ex) {
		}
	}

	/* PRIVATE METHODS */

	private WebAppContext addWebappHandler(String contextPath,
			String resourcePath) {
		WebAppContext webapp = new WebAppContext();
		webapp.setServer(fServer);
		webapp.setContextPath(contextPath);
		webapp.setResourceBase(JettyRunner.class.getClassLoader().getResource(
				resourcePath).toExternalForm());
		return webapp;
	}

	/**
	 * Adds a FileAppender to the root Log4J logger, so everything gets saved to
	 * a file, in addition to whatever the existing log4j.properties provides.
	 */
	private void configureLogging() throws IOException {
		org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger
				.getRootLogger();
		FileAppender fileAppender = new FileAppender(
				new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-40c{1} [%5p] %m%n"),
				fMainDirectory.getCanonicalPath() + File.separator + "jetty.log",
				false, false, 8192);
		rootLogger.addAppender(fileAppender);
	}

	private void ensureMainDirectoryExists() throws IOException {
		if (fMainDirectory.exists()) {
			if (fMainDirectory.isFile()) {
				throw new IllegalArgumentException(fMainDirectory
						.getCanonicalPath()
						+ " already exists and is a file");
			}
		} else if (!fMainDirectory.mkdir()) {
			throw new IllegalArgumentException("Could not create directory "
					+ fMainDirectory.getCanonicalPath());
		}
	}

	private void setUpServer(int port, String logLevel) throws IOException {
		// Set up Jetty
		this.fServer = new Server(port);

		WebAppContext adminHandler = addWebappHandler("/BpelAdmin",
				"webapps/BpelAdmin");
		WebAppContext mainWebapp = addWebappHandler("/active-bpel",
				"webapps/active-bpel");
		mainWebapp.getSecurityHandler().setLoginService(new HashLoginService());

		// Normally, we'd like to set the servlet.home init parameter to the
		// value of fMainDirectory. However, we can't do it directly, as the
		// web.xml in o.a.rt.axis.bpel.web.war sets it to ${catalina.home}, and
		// we'd need to create a temporary web-override.xml and have Jetty use
		// it. We'll just set the catalina.home system property to the right
		// value, then.
		System.setProperty("catalina.home", fMainDirectory.getCanonicalPath());

		HandlerList handlerList = new HandlerList();
		handlerList.addHandler(adminHandler);
		handlerList.addHandler(mainWebapp);
		fServer.setHandler(handlerList);
	}

}
