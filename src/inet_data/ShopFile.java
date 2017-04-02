package inet_data;

import inet_util.InetPriceProperties;
import inet_util.Send2FTP;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;

public class ShopFile implements IFileExport {
	private FileProperties fileProps;
	private Connection oraSession;
	private ResultSet cursor;
	private OracleCallableStatement cstmnt;
	private String Kat;

	public ShopFile(Connection oraSession, FileProperties fileProps, String Kat) {
		this.oraSession = oraSession;
		this.fileProps = fileProps;
		this.Kat = Kat;
	}

	@Override
	public void createCursor() {
		String sql = "/* Shop */ begin ? :=goods.F_PRICE_EXPORT2SITE(:P_INET_ID,:P_CITY_ID,:P_OEN_PRESPOINTID,:p_type,:P_DAY_COUNT,:P_KAT); end;";
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
			System.err.println(fileProps.getTaskName() + ": " + fileProps.getFileName() + " " + e.getMessage());
		}
	}

	@Override
	public void createFile() throws SQLException {
		InetPriceProperties.getInstance().init("Export2Inet.properties");
		Properties p = InetPriceProperties.getInstance().getProperty();
		if (fileProps.getFileExtension().equals("xml"))
			try {
				new Xml().createXml();
				inet_util.PackFile myPackFile = new inet_util.PackFile();
				myPackFile.pack(fileProps.toString());
				if (myPackFile.isReady() && Boolean.parseBoolean(p.getProperty("send2ftp"))) {
					System.out.println(fileProps.toString() + myPackFile.ARC_EXT + ", " + fileProps.getFilePrefix()
							+ fileProps.getFileName() + "." + fileProps.getFileExtension() + myPackFile.ARC_EXT + ", "
							+ p.getProperty("DestFolder"));
					Send2FTP myFtp = new Send2FTP(fileProps.toString() + myPackFile.ARC_EXT, fileProps.getFilePrefix()
							+ fileProps.getFileName() + "." + fileProps.getFileExtension() + myPackFile.ARC_EXT, p
							.getProperty("DestFolder"));
					Thread t = new Thread(myFtp);
					t.start();
					while (t.isAlive()) {
					}
				}
				File srcFile = new File(fileProps.toString() + myPackFile.ARC_EXT);
				if (srcFile.exists())
					srcFile.delete();
			}
			catch (XMLStreamException e) {
				System.out.println(fileProps.toString() + e.getMessage());
			}
			catch (IOException e) {
				System.out.println(fileProps.toString() + e.getMessage());
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

	class Xml {
		public String getReportDate() {
			Calendar calendar = Calendar.getInstance();
			Date date = calendar.getTime();
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			if ((hour >= 10) && (hour <= 23)) {
				calendar.set(Calendar.DAY_OF_MONTH, ++day);
				date = calendar.getTime();
			}
			SimpleDateFormat fmt = new SimpleDateFormat("dd.M.yyyy");
			return fmt.format(date);
		}

		public String getMD5(String input) {
			try {
				return ByteToHexString(MessageDigest.getInstance("MD5").digest(input.getBytes()));
			}
			catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			return "MD5 Error";
		}

		private String ByteToHexString(byte bytes[]) {
			StringBuilder buf = new StringBuilder(bytes.length * 2);
			int i;
			for (i = 0; i < bytes.length; i++) {
				if ((bytes[i] & 0xff) < 0x10)
					buf.append("0");
				buf.append(Long.toString(bytes[i] & 0xff, 16));
			}
			return buf.toString().toUpperCase();
		}

		protected void createXml() throws SQLException, XMLStreamException, IOException {
			String fileName = fileProps.getDirName() + File.separator + fileProps.getFilePrefix()
					+ fileProps.getFileName() + "." + fileProps.getFileExtension();
			StringBuilder buff = new StringBuilder();
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			FileOutputStream stream = new FileOutputStream(fileName);
			XMLStreamWriter writer = outputFactory.createXMLStreamWriter(stream, fileProps.getFileEncoding());
			// Header
			writer.writeStartDocument(fileProps.getFileEncoding(), "1.0");
			writer.writeStartElement("PRICES");
			writer.writeStartElement("DEPART");
			writer.writeCharacters("000000");
			writer.writeEndElement();
			writer.writeStartElement("REGION");
			writer.writeCharacters("(" + fileProps.getOenPP() + ") от " + getReportDate());
			writer.writeEndElement();
			// Body
			while (cursor.next()) {
				StringBuilder rubPrice = new StringBuilder(cursor.getString("RubPrice"));
				writer.writeStartElement("GOOD");
				writer.writeStartElement("NNUM");
				writer.writeCharacters(cursor.getString("FO_GGO_Code"));
				writer.writeEndElement();
				writer.writeEmptyElement("ZNUM");
				if (cursor.getString("SV_CONSUMERMERPROPETYS").equals("09"))
					rubPrice.append(".00");
				else
					rubPrice.append(".").append(cursor.getString("bonus"));
				writer.writeStartElement("PRICE");
				writer.writeCharacters(rubPrice.toString());
				writer.writeEndElement();
				Long price = Math.round(Double.parseDouble(rubPrice.toString()) * 1900);
				buff.append(cursor.getString("FO_GGO_Code")).append(price.toString());
				writer.writeStartElement("PRICEV");
				DecimalFormatSymbols dfs = new DecimalFormatSymbols();
				dfs.setDecimalSeparator('.');
				DecimalFormat df = new DecimalFormat("#0.000", dfs);
				if (cursor.getObject("Pri_value") == null)
					writer.writeCharacters("0.000");
				else
					writer.writeCharacters(df.format(Double.parseDouble(cursor.getString("Pri_value"))));
				writer.writeEndElement();
				writer.writeStartElement("DC");
				writer.writeCharacters(cursor.getString("DC"));
				writer.writeEndElement();
				writer.writeStartElement("PR");
				writer.writeCharacters(cursor.getString("PR"));
				writer.writeEndElement();
				writer.writeStartElement("UNIT");
				writer.writeCharacters(cursor.getString("UNIT"));
				writer.writeEndElement();
				writer.writeStartElement("KOEF");
				writer.writeCharacters(cursor.getString("KOEF"));
				writer.writeEndElement();
				writer.writeStartElement("DS");
				writer.writeCharacters(cursor.getString("DS"));
				writer.writeEndElement();
				writer.writeStartElement("BON");
				writer.writeCharacters("0.00");
				writer.writeEndElement();
				writer.writeEndElement();
			}
			// Lottery
			writer.writeStartElement("LOT");
			writer.writeStartElement("TS");
			writer.writeCharacters("0");
			writer.writeEndElement();
			writer.writeStartElement("TE");
			writer.writeCharacters("0");
			writer.writeEndElement();
			writer.writeStartElement("NT");
			writer.writeCharacters("0");
			writer.writeEndElement();
			writer.writeEndElement();
			// Hash
			writer.writeStartElement("HASH_CODE");
			writer.writeCharacters(getMD5(buff.toString()));
			writer.writeEndElement();
			//
			writer.writeEndElement();
			writer.writeEndDocument();
			writer.flush();
			writer.close();
			stream.close();
		}
	}

	@Override
	public void checkOutDir() {
		File dir = new File(fileProps.getDirName());
		if (!dir.exists())
			dir.mkdirs();
	}
}
