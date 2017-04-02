package inet_data;

import java.util.ArrayList;
import java.util.Iterator;

public class ToDoList implements IToDoList {
	private String listType;
	private ArrayList<IFileExport> items = new ArrayList<IFileExport>();

	public ToDoList(String listType) {
		this.listType = listType;
	}

	public void add(IFileExport item) {
		if (!items.contains(item)) {
			items.add(item);
		}
	}

	public void remove(IFileExport item) {
		if (items.contains(item)) {
			items.remove(items.indexOf(item));
		}
	}

	public int getNumberOfItems() {
		return items.size();
	}

	public Iterator<IFileExport> getIterator() {
		return items.iterator();
	}

	public String toString() {
		return listType;
	}
}
