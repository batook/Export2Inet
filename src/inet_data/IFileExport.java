package inet_data;

import java.sql.SQLException;

public interface IFileExport {

	public void createFile() throws SQLException;

	public void createCursor();

	public String getFullName();

	public void checkOutDir();
}
