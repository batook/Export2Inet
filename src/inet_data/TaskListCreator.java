package inet_data;

import inet_util.InetPriceProperties;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class TaskListCreator {
	private static ToDoListCollection listCollection = new ToDoListCollection();
	private static ResultSet cursor;

	public static void createList(Connection conn, Boolean isFull, String Kat) throws SQLException {
		Statement stmnt = conn.createStatement();
		stmnt.execute("alter session set nls_numeric_characters='. '");
		//
		InetPriceProperties.getInstance().init("Export2Inet.properties");
		Properties p = InetPriceProperties.getInstance().getProperty();
		ToDoList listTXT = new ToDoList("txt");
		ToDoList listXML = new ToDoList("xml");
		//
		if (isFull == true) {
			cursor = stmnt.executeQuery("select * from goods.v_price_export2site4_1");
		}
		else {
			cursor = stmnt.executeQuery("select * from goods.v_price_export2site4_1 where days=1");
		}
		while (cursor.next()) {
			FileProperties shopProps = new FileProperties();
			shopProps.setTaskName("Site_TXT");
			shopProps.setFilePrefix(p.getProperty("IShopPrefix"));
			shopProps.setFileEncoding(p.getProperty("Encoding"));
			if (Kat == "9")
				shopProps.setDirName(p.getProperty("ResultDir9") + File.separator + "site");
			else
				shopProps.setDirName(p.getProperty("ResultDir") + File.separator + "site");
			shopProps.setFileName(cursor.getString("City_name"));
			shopProps.setFileExtension(listTXT.toString());
			shopProps.setInetId(cursor.getInt("Inet_id"));
			shopProps.setCityId(cursor.getInt("city_price_id"));
			shopProps.setOenPP(cursor.getString("oen_prespointid"));
			shopProps.setDays(cursor.getInt("days"));
			if (cursor.getInt("is_inet") == 1) {
				listTXT.add(new ShopFile(conn, shopProps, Kat));
			}
			//
			FileProperties wapTxt = new FileProperties();
			wapTxt.setTaskName("WAP_TXT");
			wapTxt.setFilePrefix(p.getProperty("IShopPrefix"));
			wapTxt.setFileEncoding(p.getProperty("Encoding"));
			wapTxt.setDirName(p.getProperty("ResultDir") + File.separator + "site");
			wapTxt.setFileName(cursor.getString("City_name") + "_wap");
			wapTxt.setFileExtension(listTXT.toString());
			wapTxt.setInetId(cursor.getInt("Inet_id"));
			wapTxt.setCityId(cursor.getInt("city_price_id"));
			wapTxt.setDays(cursor.getInt("days"));
			wapTxt.setOenPP(cursor.getString("oen_prespointid"));
			// listTXT.add(new WapFile(conn, wapTxt));
			//			
			FileProperties shopProps2 = new FileProperties();
			shopProps2.setTaskName("Site_XML");
			shopProps2.setFilePrefix(p.getProperty("ShopPrefix"));
			shopProps2.setFileEncoding(p.getProperty("Encoding"));
			if (Kat == "9")
				shopProps2.setDirName(p.getProperty("ResultDir9") + File.separator + "inet_tt");
			else
				shopProps2.setDirName(p.getProperty("ResultDir") + File.separator + "inet_tt");
			shopProps2.setFileName(cursor.getString("pp_name"));
			shopProps2.setFileExtension(listXML.toString());
			shopProps2.setInetId(cursor.getInt("Inet_id"));
			shopProps2.setCityId(cursor.getInt("city_price_id"));
			shopProps2.setOenPP(cursor.getString("oen_prespointid"));
			shopProps2.setDays(cursor.getInt("days"));
			listXML.add(new ShopFile(conn, shopProps2, Kat));
			//
			FileProperties shopProps3 = new FileProperties();
			shopProps3.setTaskName("PresentsList");
			shopProps3.setFilePrefix("present_");
			shopProps3.setFileEncoding(p.getProperty("Encoding"));
			if (Kat=="9")
			shopProps3.setDirName(p.getProperty("ResultDir9") + File.separator + "site");
			else
				shopProps3.setDirName(p.getProperty("ResultDir") + File.separator + "site");
			shopProps3.setFileName(cursor.getString("pp_name"));
			shopProps3.setFileExtension(listXML.toString());
			shopProps3.setCityId(cursor.getInt("city_price_id"));
			shopProps3.setDays(cursor.getInt("days"));
		 listXML.add(new PresentsFile(conn, shopProps3));
			//
		}
		cursor.close();
		//
		if (isFull == true) {
			cursor = stmnt.executeQuery("select * from goods.v_price_export2site6_1");
		}
		else {
			cursor = stmnt.executeQuery("select * from goods.v_price_export2site6_1 where days=1");
		}
		while (cursor.next()) {
			FileProperties cityProps = new FileProperties();
			cityProps.setTaskName("City_TXT");
			cityProps.setFilePrefix(p.getProperty("ShopPrefix"));
			cityProps.setFileEncoding(p.getProperty("Encoding"));
			if (Kat == "9")
				cityProps.setDirName(p.getProperty("ResultDir9") + File.separator + "site");
			else
				cityProps.setDirName(p.getProperty("ResultDir") + File.separator + "site");
			cityProps.setFileName(cursor.getString("City_name"));
			cityProps.setFileExtension(listTXT.toString());
			cityProps.setInetId(0);
			cityProps.setCityId(cursor.getInt("city_price_id"));
			cityProps.setOenPP("000");
			cityProps.setDays(cursor.getInt("days"));
			listTXT.add(new CityFile(conn, cityProps, Kat));
		}
		cursor.close();
		//
		if (isFull == true) {
			FileProperties wapXml = new FileProperties();
			wapXml.setTaskName("WAP_XML");
			wapXml.setFileEncoding(p.getProperty("Encoding"));
			wapXml.setDirName(p.getProperty("ResultDir") + File.separator + "wap");
			wapXml.setFileName("tehnosila-wap");
			wapXml.setFileExtension(listXML.toString());
			wapXml.setCityId(17);
			wapXml.setInetId(51);
			// listXML.add(new WapFile(conn, wapXml));
			//
			FileProperties yandexProps = new FileProperties();
			yandexProps.setTaskName("Yandex");
			yandexProps.setFileEncoding(p.getProperty("Encoding"));
			yandexProps.setDirName(p.getProperty("ResultDir") + File.separator + "yandex");
			yandexProps.setFileName("tehnosila-yandex");
			yandexProps.setFileExtension(listXML.toString());
			yandexProps.setCityId(17);
			yandexProps.setInetId(51);
			// listXML.add(new YandexFile(conn, yandexProps));
			//
			FileProperties ramblerProps = new FileProperties();
			ramblerProps.setTaskName("Rambler");
			ramblerProps.setFileEncoding(p.getProperty("Encoding"));
			ramblerProps.setDirName(p.getProperty("ResultDir") + File.separator + "rambler");
			ramblerProps.setFileName("tehnosila-rambler");
			ramblerProps.setFileExtension(listXML.toString());
			ramblerProps.setCityId(17);
			ramblerProps.setInetId(51);
			// listXML.add(new RamblerFile(conn, ramblerProps));
			//
			cursor = stmnt.executeQuery("select * from goods.v_price_export2site7");
			while (cursor.next()) {
				FileProperties svProps = new FileProperties();
				svProps.setTaskName("SV_XML");
				svProps.setFileEncoding(p.getProperty("Encoding"));
				svProps.setDirName(p.getProperty("ResultDir") + File.separator + "sv");
				svProps.setFileName(cursor.getString("Full_name"));
				svProps.setFileExtension(listXML.toString());
				svProps.setCityId(cursor.getInt("city_price_id"));
				svProps.setInetId(cursor.getInt("Inet_id"));
				svProps.setOenPP("SV");
				// listXML.add(new SVFile(conn, svProps));
			}
			cursor.close();
			//
			FileProperties mGuruProps = new FileProperties();
			mGuruProps.setTaskName("Mobiguru");
			mGuruProps.setFileEncoding(p.getProperty("Encoding"));
			mGuruProps.setDirName(p.getProperty("ResultDir") + File.separator + "mobiguru");
			mGuruProps.setFileName("tehnosila-mobiguru");
			mGuruProps.setFileExtension(listXML.toString());
			mGuruProps.setCityId(17);
			mGuruProps.setInetId(51);
			// istXML.add(new MobiGuruFile(conn, mGuruProps));
			//
			FileProperties gorbuhaProps = new FileProperties();
			gorbuhaProps.setTaskName("Gorbuha");
			gorbuhaProps.setFileEncoding(p.getProperty("Encoding"));
			gorbuhaProps.setDirName(p.getProperty("ResultDir") + File.separator + "gorbuha");
			gorbuhaProps.setFileName("tehnosila-gorbuha");
			gorbuhaProps.setFileExtension(listXML.toString());
			gorbuhaProps.setCityId(17);
			gorbuhaProps.setInetId(51);
			// listXML.add(new GorbuhaFile(conn, gorbuhaProps));
			//
			FileProperties complectsProps = new FileProperties();
			complectsProps.setTaskName("Complects");
			complectsProps.setFileEncoding(p.getProperty("Encoding"));
			complectsProps.setDirName(p.getProperty("ResultDir") + File.separator + "site");
			complectsProps.setFileExtension(listTXT.toString());
			// listTXT.add(new ComplectsFile(conn, complectsProps));
		}
		//
		if (isFull == true) {
			cursor = stmnt.executeQuery("select * from goods.v_price_export2site_list");
			while (cursor.next()) {
				FileProperties siteProps = new FileProperties();
				siteProps.setTaskName("Site");
				siteProps.setFileEncoding(p.getProperty("Encoding"));
				if (Kat == "9")
					siteProps.setDirName(p.getProperty("ResultDir9") + File.separator + "site");
				else
					siteProps.setDirName(p.getProperty("ResultDir") + File.separator + "site");
				siteProps.setFileName(cursor.getString("FILENAME"));
				siteProps.setFileExtension(listTXT.toString());
				listTXT.add(new SiteFile(conn, siteProps, Kat));
			}
			cursor.close();
		}
		//
		listCollection.add(listTXT);
		listCollection.add(listXML);
	}

	public static ToDoListCollection getListCollection() {
		return listCollection;
	}
}
