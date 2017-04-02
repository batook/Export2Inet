package inet_util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Properties;

public class PackFile {
	public final String ARC_EXT = ".zip";
	private boolean isReady = false;

	public void pack(String fullFileName) throws IOException {
		int exitValue = 0;
		exitValue = userProc("7za a -tzip " + fullFileName + ARC_EXT + " " + fullFileName);
		if (exitValue == 0)
			setReady(true);
		else
			setReady(false);
	}

	private int userProc(String cmd) throws IOException {
		InetPriceProperties.getInstance().init("Export2Inet.properties");
		Properties p = InetPriceProperties.getInstance().getProperty();
		System.setOut(new PrintStream(System.out, true, p.getProperty("Encoding")));
		Process proc = Runtime.getRuntime().exec(cmd);
		redirect(System.out, proc.getInputStream());
		redirect(System.err, proc.getErrorStream());
		try {
			return proc.waitFor();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
			return -1;
		}
	}

	private void redirect(PrintStream out, InputStream in) {
		Runnable streamRedirect = new StreamRedirect(out, in);
		new Thread(streamRedirect).start();
	}

	public synchronized boolean isReady() {
		return isReady;
	}

	public synchronized void setReady(boolean isReady) {
		this.isReady = isReady;
	}
}

class StreamRedirect implements Runnable {
	private InputStream in;
	private PrintStream out;

	public StreamRedirect(PrintStream out, InputStream in) {
		this.in = in;
		this.out = out;
	}

	public void run() {
		try {
			BufferedReader buffer = new BufferedReader(new InputStreamReader(in, "cp866"));
			String line = null;
			while ((line = buffer.readLine()) != null)
				out.println(line);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
