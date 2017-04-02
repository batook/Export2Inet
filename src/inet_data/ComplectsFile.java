package inet_data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;

public class ComplectsFile implements IFileExport {

	private FileProperties fileProps;
	private Connection oraSession;
	private ResultSet cursor;
	private ResultSet proc_cursor;
	private Statement stmnt;
	private OracleCallableStatement cstmnt;

	public ComplectsFile(Connection oraSession, FileProperties fileProps) {
		this.oraSession = oraSession;
		this.fileProps = fileProps;
	}

	@Override
	public void createCursor() {
		String sql = "/* Complects */ select ID, LPAD(ID, 6, '0')as TEXT_ID from GOODS.SOPUT_RST";
		try {
			stmnt = oraSession.createStatement();
			cursor = stmnt.executeQuery(sql);
		}
		catch (SQLException e) {
			System.err.println("Complects: " + fileProps.getFileName() + " " + e.getMessage());
		}
	}

	@Override
	public void createFile() throws SQLException {
		String fileNameParent = fileProps.getDirName() + File.separator + "ComplectsParent.txt";
		String fileNameChild = fileProps.getDirName() + File.separator + "ComplectsChild.txt";
		try {
			PrintWriter fileParent = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(fileNameParent, false), fileProps.getFileEncoding()));
			PrintWriter fileChild = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileNameChild, false),
					fileProps.getFileEncoding()));
			fileParent.println("Код" + "\t" + "Номенклатура");
			fileChild.println("Код" + "\t" + "Номенклатура");
			ResultSet tmpCur;
			while (cursor.next()) {
				tmpCur = getCursor(cursor.getInt("id"), 1);
				while (tmpCur.next())
					fileParent.println(cursor.getString("Text_ID") + "\t" + tmpCur.getString("itemid"));
				tmpCur.close();
				cstmnt.close();
				tmpCur = getCursor(cursor.getInt("id"), 2);
				while (tmpCur.next())
					fileChild.println(cursor.getString("Text_ID") + "\t" + tmpCur.getString("itemid"));
				tmpCur.close();
				cstmnt.close();
				fileParent.flush();
				fileChild.flush();
			}
			fileParent.close();
			fileChild.close();
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			cursor.close();
			stmnt.close();
		}
	}

	private ResultSet getCursor(int id, int type) throws SQLException {
		String sql = "begin GOODS.PKG_UN_AC.LoadSoput(?,?,?,?); end;";
		cstmnt = (OracleCallableStatement) oraSession.prepareCall(sql);
		cstmnt.registerOutParameter(3, OracleTypes.CURSOR);
		cstmnt.setInt(1, id);
		cstmnt.setInt(2, type);
		cstmnt.setInt(4, 1);
		cstmnt.execute();
		proc_cursor = cstmnt.getCursor(3);
		return proc_cursor;
	}

	@Override
	public String getFullName() {
		return "Complects";
	}

	@Override
	public void checkOutDir() {
		File dir = new File(fileProps.getDirName());
		if (!dir.exists())
			dir.mkdirs();
	}
}
