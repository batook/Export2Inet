package inet_util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;

public class OraSession {
	private Connection conn;
	private OracleDataSource ods;

	public OraSession() throws SQLException {
		InetPriceProperties.getInstance().init("Export2Inet.properties");
		Properties p = InetPriceProperties.getInstance().getProperty();
		ods = new OracleDataSource();
		ods.setDriverType(p.getProperty("DriverType"));
		ods.setServerName(p.getProperty("ServerName"));
		ods.setNetworkProtocol(p.getProperty("NetworkProtocol"));
		ods.setDatabaseName(p.getProperty("DatabaseName"));
		ods.setPortNumber(Integer.parseInt(p.getProperty("PortNumber")));
		ods.setUser(p.getProperty("Login"));
		ods.setPassword(p.getProperty("Password"));
	};

	public void open() throws SQLException {
		conn = ods.getConnection();
		conn.setAutoCommit(false);
	}

	public void close() throws SQLException {
		if (conn != null) {
			conn.close();
		}
	}

	public Connection getConnection() {
		return conn;
	}
}
