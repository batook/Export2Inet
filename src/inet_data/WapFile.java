package inet_data;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;

public class WapFile implements IFileExport {

	private FileProperties fileProps;
	private Connection oraSession;
	private ResultSet cursor;
	private OracleCallableStatement cstmnt;

	public WapFile(Connection oraSession, FileProperties fileProps) {
		this.oraSession = oraSession;
		this.fileProps = fileProps;
	}

	@Override
	public void createCursor() {
		String sql = "/* WAP */ begin ? :=goods.F_PRICE_EXPORT2SITE(:P_INET_ID,:P_CITY_ID,:P_OEN_PRESPOINTID,:p_type); end;";
		try {
			cstmnt = (OracleCallableStatement) oraSession.prepareCall(sql);
			cstmnt.registerOutParameter(1, OracleTypes.CURSOR);
			cstmnt.setIntAtName("P_INET_ID", fileProps.getInetId());
			cstmnt.setIntAtName("P_CITY_ID", fileProps.getCityId());
			cstmnt.setStringAtName("P_OEN_PRESPOINTID", fileProps.getOenPP());
			cstmnt.setStringAtName("p_type", fileProps.getTaskName());
			cstmnt.execute();
			cursor = cstmnt.getCursor(1);
		}
		catch (SQLException e) {
			System.err.println(fileProps.getTaskName() + ": " + fileProps.getFileName() + " " + e.getMessage());
		}
	}

	@Override
	public void createFile() throws SQLException {
		if (fileProps.getFileExtension().equals("xml"))
			try {
				WapXml wapXML = new WapXml(oraSession, fileProps);
				wapXML.createCursor();
				wapXML.createFile();
			}
			finally {
				cursor.close();
				cstmnt.close();
			}
		if (fileProps.getFileExtension().equals("txt"))
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

	class WapXml extends YandexFile {

		public WapXml(Connection oraSession, FileProperties fileProps) {
			super(oraSession, fileProps);
			url = "http://www.sv.ru";
			goodsURL = "http://www.sv.ru/catalog/item/data/";
			goodsPic = "http://www.sv.ru/webroot/delivery/images/goods/171x171/";
		}
	}

	@Override
	public void checkOutDir() {
		File dir = new File(fileProps.getDirName());
		if (!dir.exists())
			dir.mkdirs();
	}
}
