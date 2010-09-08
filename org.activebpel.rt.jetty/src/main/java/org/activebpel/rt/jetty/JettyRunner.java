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
		// Set the correct system property so we use the shaded Saxon8 TransformerFactory
		System.setProperty("urn:active-endpoints:java:system-property:transformer-factory-impl",
				"net.sf.saxon.TransformerFactoryImpl");

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
	 *            saved to. If <code>null</code>, a temporary directory will be
	 *            used. If the directory does not exist yet, it will be created.
	 *            Otherwise, it will be used as-is: the process logs will *not*
	 *            be cleaned up, as ActiveBPEL performs the cleanup before we
	 *            can change the base logging directory. You will have to clean
	 *            it up yourself, or use a different temporary directory each
	 *            time.
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
				fMainDirectory.getCanonicalPath(), getPort()));
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
	 * Returns the main directory for all ActiveBPEL files.
	 */
	public File getMainDirectory() {
		return fMainDirectory;
	}

	/**
	 * Returns the port number that the server is listening on to.
	 */
	public int getPort() {
		return fServer.getConnectors()[0].getPort();
	}

	/**
	 * Returns the directory for the process log files. If the server is not
	 * running yet, this method has undefined behavior.
	 */
	public File getProcessLogDirectory() {
		return ((AeFileLogger) AeEngineFactory.getLogger())
				.getProcessLogBaseDirectory();
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
		final JettyRunner runner = new JettyRunner(new File(
				System.getProperty("java.io.tmpdir"), "activebpel"),
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

	/** Creates a temporary directory. */
	private static File createTemporaryDirectory() throws IOException {
		File tmp = File.createTempFile("activebpel", "dir");
		if (!tmp.delete()) {
			throw new IOException("Could not delete temporary file: "
					+ tmp.getCanonicalPath());
		}
		if (!tmp.mkdir()) {
			throw new IOException("Could not create temporary dir: "
					+ tmp.getCanonicalPath());
		}
		return tmp;
	}

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
		if (fMainDirectory == null) {
			fMainDirectory = createTemporaryDirectory();
		} else if (fMainDirectory.exists()) {
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
