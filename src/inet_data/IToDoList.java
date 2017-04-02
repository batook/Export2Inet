package inet_data;

import java.util.Iterator;

public interface IToDoList extends IIterating {
	public void add(IFileExport item);

	public void remove(IFileExport item);

	public int getNumberOfItems();

	public Iterator<IFileExport> getIterator();
}
