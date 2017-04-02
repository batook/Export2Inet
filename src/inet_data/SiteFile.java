package inet_data;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;

public class SiteFile implements IFileExport {
	private FileProperties fileProps;
	private Connection oraSession;
	private ResultSet cursor;
	private OracleCallableStatement cstmnt;
	private String Kat;

	public SiteFile(Connection oraSession, FileProperties fileProps, String Kat) {
		this.oraSession = oraSession;
		this.fileProps = fileProps;
		this.Kat = Kat;
	}

	@Override
	public void createCursor() {
		String sql = "/* Site */ begin ? :=goods.F_PRICE_EXPORT2SITE2(:p_type,1000,:p_kat); end;";
		try {
			cstmnt = (OracleCallableStatement) oraSession.prepareCall(sql);
			cstmnt.registerOutParameter(1, OracleTypes.CURSOR);
			cstmnt.setStringAtName("p_type", fileProps.getFileName());
		 cstmnt.setStringAtName("p_kat", Kat);
			cstmnt.execute();
			cursor = cstmnt.getCursor(1);
		}
		catch (SQLException e) {
			System.err.println(fileProps.getTaskName() + ": " + fileProps.getFileName() + " " + e.getMessage());
		}
	}

	public void createFile() throws SQLException {
		try {
			new TxtFile(cursor, fileProps).createTxtFile();
		}
		finally {
			cursor.close();
			cstmnt.close();
		}
	}

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
