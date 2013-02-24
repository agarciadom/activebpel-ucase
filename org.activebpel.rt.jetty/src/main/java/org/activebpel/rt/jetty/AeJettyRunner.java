package org.activebpel.rt.jetty;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig;
import org.activebpel.rt.bpel.server.admin.IAeEngineAdministration;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
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
 * Jetty instance. Note: if you need to run more than one instance, you'll have
 * to fork each of them into their separate JVMs. See {@link AeJettyForker} for
 * details.
 * 
 * @see AeJettyForker
 * @author Antonio García Domínguez
 */
public class AeJettyRunner {

	private static final String ENGINE_CONFIG_NAME = "aeEngineConfig.xml";

	final static String RUNNING_MSG = "ActiveBPEL is now RUNNING";

	static {
		// Set the correct system property so we use the shaded Saxon8
		// TransformerFactory
		System
				.setProperty(
						"urn:active-endpoints:java:system-property:transformer-factory-impl",
						"net.sf.saxon.TransformerFactoryImpl");

		installBridgeFromJULtoSLF4J();
	}

	/**
	 * Installs a bridge from {@link java.util.logging} calls to SLF4J calls, so
	 * we can have a single log4j.properties file controlling all logging, and
	 * we don't miss important messages.
	 */
	private static void installBridgeFromJULtoSLF4J() {
		java.util.logging.Logger rootLogger = java.util.logging.Logger
				.getLogger("");
		rootLogger.setLevel(java.util.logging.Level.ALL);
		java.util.logging.Handler[] handlers = rootLogger.getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			rootLogger.removeHandler(handlers[i]);
		}
		SLF4JBridgeHandler.install();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(AeJettyRunner.class);
	private File fMainDirectory;
	private Server fServer;
	private String fLoggingFilterName;

	private FileAppender fAppender;

	private WebAppContext fAdminHandler;

	private WebAppContext fMainWebapp;

	static final String PROCESSLOG_SUBDIR_NAME = "process-logs";

	static final String DEPLOYMENT_SUBDIR_NAME = "bpr";

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
	public AeJettyRunner(File mainDir, int port, String logLevel)
			throws IOException {
		this.fMainDirectory = mainDir;
		this.fLoggingFilterName = logLevel;

		configureLogging();
		setUpServer(port, logLevel);
	}

	/**
	 * Starts the server.
	 * 
	 * @throws Exception
	 *             There was a problem while starting the server.
	 */
	public void start() throws Throwable {
		configureLogging();

		LOGGER.info(String.format(
				"Starting ActiveBPEL with main directory %s on port %d",
				fMainDirectory.getCanonicalPath(), getPort()));

		// Start Jetty and ensure that both contexts were successfully started
		fServer.start();
		if (fAdminHandler.getUnavailableException() != null) {
			throw fAdminHandler.getUnavailableException();
		}
		else if (fMainWebapp.getUnavailableException() != null) {
			throw fMainWebapp.getUnavailableException();
		}

		// Wait for ActiveBPEL to be really running
		IAeEngineAdministration admin = getEngineAdmin();
		while (!admin.isRunning()) {
			LOGGER.debug("ActiveBPEL is not running yet, waiting 500ms...");
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
		// Store unmatched correlated receives for only 5 seconds
		config.setUnmatchedCorrelatedReceiveTimeout(5);
		// Tell all other components to update their configuration accordingly
		config.update();

		// We need to print the "running" message through
		// stderr, so it will never be filtered away, in
		// addition to logging it
		System.err.println(RUNNING_MSG);
		LOGGER.info(RUNNING_MSG);
	}

	/**
	 * Waits for the currently running server to finish its execution.
	 *
	 * @throws Exception
	 *             There was a problem while stopping the server or waiting for
	 *             it to finish.
	 */
	public void join() throws Exception {
		LOGGER.info("Waiting for server to finish...");
		fServer.join();
	}

	/**
	 * Stops the currently running server without waiting for it to complete.
	 * 
	 * @throws Exception
	 *             There was a problem while stopping the server.
	 */
	public void stop() throws Exception {
		LOGGER.info("Stopping ActiveBPEL....");
		fServer.stop();
	}

	/**
	 * Uninstalls the file appender added by this runner.
	 */
	public void uninstallLogging() {
		org.apache.log4j.Logger.getRootLogger().removeAppender(fAppender);
		fAppender = null;
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
	 * Entry point from the command line.
	 * 
	 * @param args
	 *            Arguments received through the command line: work directory
	 *            for ActiveBPEL, port on which it should listen, and its
	 *            logging level ("none" or "full").
	 * @throws Throwable 
	 */
	public static void main(String[] args) throws Throwable {
		if (args.length != 3 || (!"none".equals(args[2]) && !"full".equals(args[2]))) {
			System.err.println("Usage: java "
					+ AeJettyRunner.class.getCanonicalName()
					+ " workdir port none|full");
			System.exit(1);
		}
		final String workdir = args[0];
		final int port = Integer.parseInt(args[1]);
		final String loggingLevel = "full".equals(args[2]) ? AeLoggingFilter.FULL
				: AeLoggingFilter.NONE;

		final AeJettyRunner runner = new AeJettyRunner(new File(workdir), port,
				loggingLevel);

		/*
		 * Stop the server when the user presses Ctrl+C or otherwise interrupts
		 * the application.
		 */
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					runner.stop();
				} catch (Exception e) {
					LOGGER.error("Error while stopping Jetty", e);
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
		webapp.setExtractWAR(false);
		webapp.setResourceBase(AeJettyRunner.class.getClassLoader()
				.getResource(resourcePath).toExternalForm());
		return webapp;
	}

	/**
	 * Adds a FileAppender to the root Log4J logger, so everything gets saved to
	 * a file, in addition to whatever the existing log4j.properties provides.
	 */
	private void configureLogging() throws IOException {
		if (fAppender != null) {
			return;
		}

		org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
		fAppender = new FileAppender(new PatternLayout(
				"%d{yyyy-MM-dd HH:mm:ss} %-40c{1} [%5p] %m%n"), fMainDirectory
				.getCanonicalPath()
				+ File.separator + "jetty.log", false, false, 8192);
		rootLogger.addAppender(fAppender);
	}

	private void setUpServer(int port, String logLevel) throws IOException {
		// Set up Jetty
		this.fServer = new Server(port);

		// Copy the default configuration into the bpr/ directory so ActiveBPEL does not complain
		copyEngineConfiguration();

		fAdminHandler = addWebappHandler("/BpelAdmin", "webapps/BpelAdmin");
		fMainWebapp = addWebappHandler("/active-bpel", "webapps/active-bpel");
		fMainWebapp.getSecurityHandler().setLoginService(new HashLoginService());

		// Normally, we'd like to set the servlet.home init parameter to the
		// value of fMainDirectory. However, we can't do it directly, as the
		// web.xml in o.a.rt.axis.bpel.web.war sets it to ${catalina.home}, and
		// we'd need to create a temporary web-override.xml and have Jetty use
		// it. We'll just set the catalina.home system property to the right
		// value, then.
		System.setProperty("catalina.home", fMainDirectory.getCanonicalPath());

		HandlerList handlerList = new HandlerList();
		handlerList.addHandler(fAdminHandler);
		handlerList.addHandler(fMainWebapp);
		fServer.setHandler(handlerList);
	}

	private void copyEngineConfiguration() throws FileNotFoundException, IOException {
		final File bprDir = new File(fMainDirectory, DEPLOYMENT_SUBDIR_NAME);
		bprDir.mkdir();
		InputStream is = null;
		OutputStream os = null;
		try {
			final File destFile = new File(bprDir, ENGINE_CONFIG_NAME);

			is = getClass().getResourceAsStream("/" + ENGINE_CONFIG_NAME);
			os = new FileOutputStream(destFile);
			final byte[] buf = new byte[2048];
			int count = 0;
			while ((count = is.read(buf)) != -1) {
				os.write(buf, 0, count);
			}

			LOGGER.debug("Copied default " + ENGINE_CONFIG_NAME + " to " + destFile);
		} finally {
			if (is != null) is.close();
			if (os != null) os.close();
		}
	}

}
