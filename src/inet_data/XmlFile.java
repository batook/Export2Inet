package inet_data;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.xml.stream.XMLStreamException;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;

public abstract class XmlFile implements IFileExport {
	protected Connection oraSession;
	protected ResultSet cursor;
	protected OracleCallableStatement cstmnt;
	protected FileProperties fileProps;
	protected String companyName = "Техносила";
	protected String companyFullName = "Интернет-магазин Техносила";
	protected String url = "http://www.tehnosila.ru";
	protected String goodsURL = "http://www.tehnosila.ru/search/?text=";
	protected String goodsPic = "http://www.sv.ru/webroot/delivery/images/goods/100x100/";
	protected String goodsPicSuffix = ".jpg";

	public XmlFile(Connection oraSession, FileProperties fileProps) {
		this.oraSession = oraSession;
		this.fileProps = fileProps;
	}

	@Override
	public void createCursor() {
		String sql = "/* XML */ begin ? :=goods.F_PRICE_EXPORT2SITE(:P_INET_ID,:P_CITY_ID,:P_OEN_PRESPOINTID,:p_type); end;";
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
		String fileName = fileProps.getDirName() + File.separator + fileProps.getFilePrefix() + fileProps.getFileName() + "."
				+ fileProps.getFileExtension();
		try {
			createXml(fileName);
		}
		catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			cursor.close();
			cstmnt.close();
		}
	}

	protected abstract void createXml(String fileName) throws SQLException, XMLStreamException, IOException;

	@Override
	public String getFullName() {
		return fileProps.toString();
	}
}
