package inet_data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class MobiGuruFile extends XmlFile {

	public MobiGuruFile(Connection oraSession, FileProperties fileProps) {
		super(oraSession, fileProps);
	}

	@Override
	protected void createXml(String fileName) throws SQLException, XMLStreamException, IOException {
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		FileOutputStream stream = new FileOutputStream(fileName);
		XMLStreamWriter writer = outputFactory.createXMLStreamWriter(stream, fileProps.getFileEncoding());
		long date = System.currentTimeMillis();
		SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd H:mm");
		// Header
		writer.writeStartDocument(fileProps.getFileEncoding(), "1.0");
		writer.writeDTD("<!DOCTYPE yml_catalog SYSTEM \"shops.dtd\">");
		writer.writeStartElement("yml_catalog");
		writer.writeAttribute("date", dateFmt.format(date));
		writer.writeStartElement("shop");
		writer.writeStartElement("name");
		writer.writeCharacters(companyName);
		writer.writeEndElement();
		writer.writeStartElement("company");
		writer.writeCharacters(companyFullName);
		writer.writeEndElement();
		writer.writeStartElement("url");
		writer.writeCharacters(url);
		writer.writeEndElement();
		writer.writeStartElement("currencies");
		writer.writeEmptyElement("currency");
		writer.writeAttribute("id", "RUR");
		writer.writeAttribute("rate", "1");
		writer.writeEndElement();
		// Groups
		Statement grp_stmnt = oraSession.createStatement();
		ResultSet grp_cursor = grp_stmnt.executeQuery("select * from goods.v_price_export2site5");
		writer.writeStartElement("categories");
		while (grp_cursor.next()) {
			writer.writeStartElement("category");
			writer.writeAttribute("id", grp_cursor.getString("groupid"));
			if (grp_cursor.getObject("parentid") == null)
				writer.writeAttribute("parentid", "");
			else
				writer.writeAttribute("parentid", grp_cursor.getString("parentid"));
			writer.writeCharacters(grp_cursor.getString("name"));
			writer.writeEndElement();
		}
		writer.writeEndElement();
		grp_cursor.close();
		grp_stmnt.close();
		// Body
		writer.writeStartElement("offers");
		while (cursor.next()) {
			writer.writeStartElement("offer");
			writer.writeAttribute("id", cursor.getString("itemid"));
			writer.writeAttribute("type", "vendor.model");
			writer.writeAttribute("available", "true");
			// writer.writeEndElement();
			writer.writeStartElement("url");
			writer.writeCharacters(goodsURL + cursor.getString("itemid"));
			writer.writeEndElement();
			writer.writeStartElement("price");
			writer.writeCharacters(cursor.getString("rubprice"));
			writer.writeEndElement();
			writer.writeStartElement("currencyId");
			writer.writeCharacters("RUR");
			writer.writeEndElement();
			writer.writeStartElement("categoryId");
			writer.writeCharacters(cursor.getString("groupid"));
			writer.writeEndElement();
			writer.writeStartElement("picture");
			writer.writeCharacters(goodsPic + cursor.getString("itemid") + goodsPicSuffix);
			writer.writeEndElement();
			writer.writeStartElement("typePrefix");
			writer.writeCharacters(cursor.getString("itemsort"));
			writer.writeEndElement();
			writer.writeStartElement("vendor");
			writer.writeCharacters(cursor.getString("tm"));
			writer.writeEndElement();
			writer.writeEmptyElement("vendorCode");
			writer.writeStartElement("model");
			writer.writeCharacters(cursor.getString("model"));
			writer.writeEndElement();
			writer.writeStartElement("description");
			writer.writeCharacters(cursor.getString("descript"));
			writer.writeEndElement();
			writer.writeEndElement();
		}
		writer.writeEndElement();
		writer.writeEndElement();
		writer.writeEndElement();
		writer.writeEndDocument();
		writer.flush();
		writer.close();
		stream.close();
		new File(fileName).setLastModified(date);
	}

	@Override
	public void checkOutDir() {
		File dir = new File(fileProps.getDirName());
		if (!dir.exists())
			dir.mkdirs();
	}
}
