package inet_data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class TxtFile {

	private FileProperties fileProps;
	private ResultSet cursor;

	public TxtFile(ResultSet cursor, FileProperties fileProps) {
		this.cursor = cursor;
		this.fileProps = fileProps;
	}

	public void createTxtFile() throws SQLException {
		PrintWriter file = null;
		String fileName = fileProps.getDirName() + File.separator + fileProps.getFilePrefix()
				+ fileProps.getFileName() + "." + fileProps.getFileExtension();
		try {
			file = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName, false), fileProps
					.getFileEncoding()));
			ResultSetMetaData rsmd = cursor.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();
			for (int i = 1; i <= numberOfColumns; i++)
				file.print((i < numberOfColumns ? rsmd.getColumnLabel(i) + "\t" : rsmd.getColumnLabel(i)));
			file.println();
			while (cursor.next()) {
				for (int i = 1; i <= numberOfColumns; i++) {
					if (cursor.getObject(i) == null)
						file.print((i < numberOfColumns ? "\t" : ""));
					else
						file.print((i < numberOfColumns ? cursor.getString(i) + "\t" : cursor.getString(i)));
				}
				file.println();
			}
		}
		catch (UnsupportedEncodingException e) {
			System.err.println(fileName + " UnsupportedEncoding");
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			System.err.println(fileName + " FileNotFound");
			e.printStackTrace();
		}
		finally {
			file.close();
		}
	}
}
