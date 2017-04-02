package inet_data;

import java.util.Iterator;

public interface IToDoListCollection extends IIterating {
	public void add(ToDoList list);

	public void remove(ToDoList list);

	public int getNumberOfItems();

	public Iterator<ToDoList> getIterator();
}
