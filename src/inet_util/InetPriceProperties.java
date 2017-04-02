package inet_util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class InetPriceProperties {
	private Properties props;
	private static final InetPriceProperties instance = new InetPriceProperties();

	private InetPriceProperties() {
		props = new Properties();
	}

	public static InetPriceProperties getInstance() {
		return instance;
	}

	public Properties getProperty() {
		return props;
	}

	public void init(String filePath) {
		try {
			props.load(new FileInputStream(filePath));
		}
		catch (IOException e) {
			System.err.println("Properties IO error " + e.toString());
		}
	}
}
