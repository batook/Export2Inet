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

public class RamblerFile extends XmlFile {

	public RamblerFile(Connection oraSession, FileProperties fileProps) {
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
		// Body
		writer.writeStartElement("offers");
		while (cursor.next()) {
			writer.writeStartElement("offer");
			writer.writeAttribute("id", cursor.getString("itemid"));
			// writer.writeEndElement();
			writer.writeStartElement("category");
			writer.writeCharacters(cursor.getString("pgn") + "->" + cursor.getString("gn"));
			writer.writeEndElement();
			writer.writeStartElement("title");
			writer.writeCharacters(cursor.getString("itemsort") + " " + cursor.getString("tm") + " "
					+ cursor.getString("model"));
			writer.writeEndElement();
			writer.writeStartElement("price");
			writer.writeCharacters(cursor.getString("rubprice"));
			writer.writeEndElement();
			writer.writeStartElement("currencyId");
			writer.writeCharacters("RUR");
			writer.writeEndElement();
			writer.writeStartElement("url");
			writer.writeCharacters(goodsURL + cursor.getString("itemid"));
			writer.writeEndElement();
			writer.writeStartElement("img");
			writer.writeCharacters(goodsPic + cursor.getString("itemid") + goodsPicSuffix);
			writer.writeEndElement();
			writer.writeStartElement("produceby");
			writer.writeCharacters(cursor.getString("tm"));
			writer.writeEndElement();
			writer.writeEmptyElement("warr");
			writer.writeStartElement("descript");
			writer.writeCharacters(cursor.getString("descript"));
			writer.writeEndElement();
			writer.writeStartElement("param0");
			writer.writeCharacters("Доставка на следующий день после подтверждения заказа");
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
