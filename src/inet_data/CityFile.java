package inet_data;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;

public class CityFile implements IFileExport {

	private FileProperties fileProps;
	private Connection oraSession;
	private ResultSet cursor;
	private OracleCallableStatement cstmnt;
	private String Kat;

	public CityFile(Connection oraSession, FileProperties fileProps, String Kat) {
		this.oraSession = oraSession;
		this.fileProps = fileProps;
		this.Kat = Kat;
	}

	@Override
	public void createCursor() {
		String sql = "/* City */ begin ? :=goods.F_PRICE_EXPORT2SITE(:P_INET_ID,:P_CITY_ID,:P_OEN_PRESPOINTID,:p_type,:P_DAY_COUNT,:P_KAT); end;";
		try {
			cstmnt = (OracleCallableStatement) oraSession.prepareCall(sql);
			cstmnt.registerOutParameter(1, OracleTypes.CURSOR);
			cstmnt.setIntAtName("P_INET_ID", fileProps.getInetId());
			cstmnt.setIntAtName("P_CITY_ID", fileProps.getCityId());
			cstmnt.setStringAtName("P_OEN_PRESPOINTID", fileProps.getOenPP());
			cstmnt.setStringAtName("p_type", fileProps.getTaskName());
			cstmnt.setIntAtName("P_DAY_COUNT", fileProps.getDays());
			cstmnt.setStringAtName("P_KAT", Kat);
			cstmnt.execute();
			cursor = cstmnt.getCursor(1);
		}
		catch (SQLException e) {
			System.err.println("CityFile: " + fileProps.getFileName() + " " + e.getMessage());
		}
	}

	@Override
	public void createFile() throws SQLException {
		try {
			new TxtFile(cursor, fileProps).createTxtFile();
		}
		finally {
			cursor.close();
			cstmnt.close();
		}
	}

	@Override
	public String getFullName() {
		return fileProps.toString();
	}

	@Override
	public void checkOutDir() {
		File dir = new File(fileProps.getDirName());
		if (!dir.exists())
			dir.mkdirs();
	}
}
