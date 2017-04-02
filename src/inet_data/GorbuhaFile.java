package inet_data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class GorbuhaFile extends XmlFile {

	public GorbuhaFile(Connection oraSession, FileProperties fileProps) {
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
		writer.writeDTD("<!DOCTYPE gml_catalog SYSTEM \"shops.dtd\">");
		writer.writeStartElement("gml_catalog");
		writer.writeAttribute("date", dateFmt.format(date));
		writer.writeStartElement("shop");
		// Body
		writer.writeStartElement("offers");
		while (cursor.next()) {
			writer.writeStartElement("offer");
			writer.writeAttribute("id", cursor.getString("itemid"));
			// writer.writeEndElement();
			writer.writeStartElement("name");
			writer.writeCharacters(cursor.getString("name"));
			writer.writeEndElement();
			writer.writeStartElement("categoryId");
			writer.writeCharacters(cursor.getString("categoryId"));
			writer.writeEndElement();
			writer.writeStartElement("description");
			writer.writeCharacters(cursor.getString("descript"));
			writer.writeEndElement();
			writer.writeStartElement("price");
			writer.writeCharacters(cursor.getString("rubprice"));
			writer.writeEndElement();
			writer.writeStartElement("url");
			writer.writeCharacters(goodsURL + cursor.getString("itemid"));
			writer.writeEndElement();
			writer.writeStartElement("picture");
			writer.writeCharacters(goodsPic + cursor.getString("itemid") + goodsPicSuffix);
			writer.writeEndElement();
			writer.writeEndElement();
		}
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
