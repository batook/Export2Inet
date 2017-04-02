package inet_data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;

public class PresentsFile extends XmlFile {
	public PresentsFile(Connection oraSession, FileProperties fileProps) {
		super(oraSession, fileProps);
	}

	@Override
	public void createCursor() {
		String sql = "/* Present XML */ begin ? :=goods.F_PRICE_EXPORT2SITE3(:P_CITY_ID,:p_type); end;";
		try {
			cstmnt = (OracleCallableStatement) oraSession.prepareCall(sql);
			cstmnt.registerOutParameter(1, OracleTypes.CURSOR);
			cstmnt.setIntAtName("P_CITY_ID", fileProps.getCityId());
			cstmnt.setStringAtName("p_type", fileProps.getTaskName());
			cstmnt.execute();
			cursor = cstmnt.getCursor(1);
		}
		catch (SQLException e) {
			System.err.println(fileProps.getTaskName() + ": " + fileProps.getFileName() + " " + e.getMessage());
		}
	}

	@Override
	protected void createXml(String fileName) throws SQLException, XMLStreamException, IOException {
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		FileOutputStream stream = new FileOutputStream(fileName);
		XMLStreamWriter writer = outputFactory.createXMLStreamWriter(stream, fileProps.getFileEncoding());
		long date = System.currentTimeMillis();
		SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd H:mm");
		try {
			// Header
			writer.writeStartDocument(fileProps.getFileEncoding(), "1.0");
			writer.writeStartElement("presents");
			writer.writeAttribute("date", dateFmt.format(date));
			while (cursor.next()) {
				writer.writeStartElement("seq");
				writer.writeAttribute("name", cursor.getString("name"));
				writer.writeCharacters(cursor.getString("seq"));
				List<PresentList> itemList = getPresentList(cursor.getInt("id"), 0, null);
				for (PresentList items : itemList) {
					writer.writeStartElement("itemid");
					writer.writeAttribute("itemname", items.itemName);
					writer.writeCharacters(items.itemid);
					List<PresentList> presentList = getPresentList(cursor.getInt("id"), 1, items.itemid);
					for (PresentList presents : presentList) {
						writer.writeStartElement("present_itemid");
						writer.writeAttribute("present_itemname", presents.itemName);
						writer.writeCharacters(presents.itemid);
						writer.writeEndElement();
						writer.writeStartElement("priority");
						writer.writeCharacters(presents.priority);
						writer.writeEndElement();
					}
					writer.writeEndElement();
				}
				writer.writeEndElement();
			}
			writer.writeEndDocument();
			writer.flush();
			writer.close();
			stream.close();
		}
		finally {
			cursor.close();
			cstmnt.close();
		}
	}

	@Override
	public void checkOutDir() {
		File dir = new File(fileProps.getDirName());
		if (!dir.exists())
			dir.mkdirs();
	}

	private List<PresentList> getPresentList(int action_id, int type, String itemid) throws SQLException {
		String sql = "/* PresentList */ begin ? :=goods.F_PRICE_EXPORT2SITE3(:P_CITY_ID,:p_type,:P_REGIONS_ACTION_ID,:p_itemid); end;";
		OracleCallableStatement tmpCstmnt = (OracleCallableStatement) oraSession.prepareCall(sql);
		tmpCstmnt.registerOutParameter(1, OracleTypes.CURSOR);
		tmpCstmnt.setIntAtName("P_CITY_ID", 17);
		tmpCstmnt.setIntAtName("P_REGIONS_ACTION_ID", action_id);
		tmpCstmnt.setStringAtName("p_type", (type == 0 ? "PresentsList_M" : "PresentsList_D"));
		tmpCstmnt.setStringAtName("p_itemid", itemid);
		tmpCstmnt.execute();
		ResultSet cursor = tmpCstmnt.getCursor(1);
		List<PresentList> presentList = new ArrayList<PresentList>();
		if (type == 0)
			while (cursor.next())
				presentList.add(new PresentList(cursor.getString("itemid"), cursor.getString("itemname")));
		else
			while (cursor.next())
				presentList.add(new PresentList(cursor.getString("itemid"), cursor.getString("itemname"), cursor
						.getString("priority")));
		cursor.close();
		tmpCstmnt.close();
		return presentList;
	}

	static class PresentList {
		private final String itemid;
		private final String itemName;
		private final String priority;

		public PresentList(String itemid, String itemName) {
			this.itemid = itemid;
			this.itemName = itemName;
			this.priority = "";
		}

		public PresentList(String itemid, String itemName, String priority) {
			this.itemid = itemid;
			this.itemName = itemName;
			this.priority = priority;
		}
	}
}
