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

	static {
		// Set the correct system property so the Axis factories are used in
		// JDK6+. See this for details:
		// http://forums.sun.com/thread.jspa?threadID=5334141
		System.setProperty("javax.xml.soap.MessageFactory",
				"org.apache.axis.soap.MessageFactoryImpl");
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

		// Switch to the desired logging level
		admin.getEngineConfig().getUpdatableEngineConfig().setLoggingFilter(
				fLoggingFilterName);
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
		Logger.getRootLogger().setLevel(Level.INFO);
		final Logger logger = Logger.getLogger(JettyRunner.class);

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
		mainWebapp.getServletContext().setInitParameter("servlet.home",
				this.fMainDirectory.getCanonicalPath());
		mainWebapp.getSecurityHandler().setLoginService(new HashLoginService());

		HandlerList handlerList = new HandlerList();
		handlerList.addHandler(adminHandler);
		handlerList.addHandler(mainWebapp);
		fServer.setHandler(handlerList);
	}

}
