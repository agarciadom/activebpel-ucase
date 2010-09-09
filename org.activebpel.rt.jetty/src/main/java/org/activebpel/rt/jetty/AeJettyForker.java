package org.activebpel.rt.jetty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Class which forks a Jetty instance running ActiveBPEL in a child JVM. It
 * checks whether ActiveBPEL has started or not through its stdout and lets the
 * user stop it remotely.
 * </p>
 * 
 * <p>
 * We need to fork a JVM for ActiveBPEL if we want to have several instances
 * running at the same time, as there are many singletons in the ActiveBPEL
 * codebase.
 * </p>
 * 
 * <p>
 * The class assumes that the forked instance will use the same classpath as the
 * running JVM. It ensures that the ActiveBPEL work directory exists and is
 * clean. If needed, it can create temporary directories. It will clean up the
 * bpr/ and process-logs/ subdirectories from .bpr and .log files if existing.
 * </p>
 * 
 * @author Antonio García Domínguez
 */
public class AeJettyForker {

	private final static int UNKNOWN_STATUS = -1;

	private File fMainDirectory;
	private int fPort;
	private String fLogLevel;
	private Process fProcess;
	private int fExitStatus = UNKNOWN_STATUS;

	private static Logger fLogger = LoggerFactory.getLogger(AeJettyForker.class);

	/**
	 * Creates a new instance, but does not start it.
	 * 
	 * @param mainDir
	 *            Main directory where the forked ActiveBPEL instance should
	 *            store the deployed .bpr, server log and process logs.
	 * @param port
	 *            Port on which ActiveBPEL should listen.
	 * @param logLevel
	 *            Log level for the server: should be either "full" or "none".
	 */
	public AeJettyForker(File mainDir, int port, String logLevel)
			throws IOException {
		fMainDirectory = mainDir;
		fPort = port;
		fLogLevel = logLevel;
		ensureMainDirectoryExists();
	}

	/**
	 * Returns the main directory, where the server log and the bpr,
	 * deployment-logs and process-logs directories are held.
	 */
	public File getMainDirectory() {
		return fMainDirectory;
	}

	/**
	 * Returns the directory where the .bpr files are held.
	 */
	public File getBPRDirectory() {
		return new File(fMainDirectory, "bpr");
	}

	/**
	 * Returns the directory where the process logs are held.
	 */
	public File getProcessLogDirectory() {
		return new File(fMainDirectory, "process-logs");
	}

	/**
	 * Returns the number of the port on which ActiveBPEL should listen.
	 */
	public int getPort() {
		return fPort;
	}

	/**
	 * Returns the logging level of the ActiveBPEL instance (either "none" or
	 * "full").
	 */
	public String getLogLevel() {
		return fLogLevel;
	}

	/**
	 * If it hasn't been started already, forks a new JVM and launches
	 * ActiveBPEL on it. Otherwise, it does nothing. The forked JVM has the same
	 * classpath as the running JVM, but nothing else is preserved.
	 */
	public void start() throws IOException {
		if (isRunning())
			return;

		final String javaPath = System.getProperty("java.home")
				+ File.separator + "bin" + File.separator + "java";
		final String javaClasspath = System.getProperty("java.class.path");
		final String javaClassname = AeJettyRunner.class.getCanonicalName();

		// Print debugging information: java exec, java classpath and name of the main class
		fLogger.debug("java executable: ", javaPath);
		if (fLogger.isDebugEnabled()) {
			fLogger.debug("current classpath is: ");
			String[] javaClasspathElements = javaClasspath.split(File.pathSeparator);
			for (String s : javaClasspathElements) {
				fLogger.debug(" - " + s);
			}
		}
		fLogger.debug("java classname: ", javaClassname);

		ProcessBuilder builder = new ProcessBuilder(javaPath, "-cp",
				javaClasspath, javaClassname,
				fMainDirectory.getCanonicalPath(), Integer.toString(fPort),
				fLogLevel);
		builder.redirectErrorStream(true);
		fProcess = builder.start();

		// We don't want to write anything into ActiveBPEL's stdin
		fProcess.getOutputStream().close();

		// Wait for ActiveBPEL to start while printing its output before going
		// on with our things
		final BufferedReader is = new BufferedReader(new InputStreamReader(
				fProcess.getInputStream()));
		String line;
		while ((line = is.readLine()) != null && !line.contains("Started")) {
			System.out.println(line);
		}

		// Ignore the rest of stdout and stderr of the process (we have the
		// server logs for that)
		fProcess.getInputStream().close();
	}

	/**
	 * Waits for the ActiveBPEL instance to finish its execution on its own.
	 */
	public int join() throws InterruptedException {
		if (!isRunning())
			return UNKNOWN_STATUS;
		return fProcess.waitFor();
	}

	/**
	 * Stops immediately the forked JVM process.
	 */
	public void stop() {
		if (!isRunning())
			return;
		fProcess.destroy();
	}

	/**
	 * Returns whether the process is running (<code>true</code>) or not (
	 * <code>false</code>).
	 */
	public boolean isRunning() {
		return fProcess != null && fExitStatus == UNKNOWN_STATUS;
	}

	/**
	 * Entry point from the command line. Useful for debugging, but not much
	 * else.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException {
		Logger logger = LoggerFactory.getLogger(AeJettyForker.class);

		AeJettyForker forker1 = new AeJettyForker(new File("/tmp/activebpel"), 8080, "full");
		AeJettyForker forker2 = new AeJettyForker(new File("/tmp/activebpel2"), 8081, "none");
		logger.info("Created forkers");

		forker1.start();
		forker2.start();		
		logger.info("2 ActiveBPEL instances forked and running on ports 8080 and 8081");

		logger.info("Exit status codes: " + forker1.join() + ", " + forker2.join());
	}

	/* ---- PRIVATE METHODS ---- */

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

	private void ensureMainDirectoryExists() throws IOException {
		if (fMainDirectory == null) {
			fMainDirectory = createTemporaryDirectory();
		} else if (fMainDirectory.exists()) {
			if (fMainDirectory.isFile()) {
				throw new IllegalArgumentException(fMainDirectory
						.getCanonicalPath()
						+ " already exists and is a file");
			} else {
				cleanUp(".bpr", getBPRDirectory());
				cleanUp(".log", getProcessLogDirectory());
			}
		} else if (!fMainDirectory.mkdir()) {
			throw new IllegalArgumentException("Could not create directory "
					+ fMainDirectory.getCanonicalPath());
		}
	}

	/**
	 * Deletes all files whose filename ends in a particular substring. Does not
	 * recurse into subdirectories.
	 * 
	 * @param suffix
	 *            Substring at the end of all the files to be deleted.
	 * @param dir
	 *            Directory containing the files to be deleted. If it does not
	 *            exist yet, this method will not do anything.
	 */
	private void cleanUp(final String suffix, final File dir) {
		if (!dir.exists())
			return;

		File[] delFiles = dir.listFiles(new FileFilter() {
			public boolean accept(File arg0) {
				return arg0.isFile() && arg0.getName().endsWith(suffix);
			}
		});
		for (File f : delFiles) {
			f.delete();
		}
	}

}
