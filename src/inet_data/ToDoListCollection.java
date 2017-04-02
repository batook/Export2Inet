package inet_data;

import java.util.HashMap;
import java.util.Iterator;

public class ToDoListCollection implements IToDoListCollection {
	private HashMap<String, ToDoList> lists = new HashMap<String, ToDoList>();

	public void add(ToDoList list) {
		if (!lists.containsKey(list.toString())) {
			lists.put(list.toString(), list);
		}
	}

	public void remove(ToDoList list) {
		if (lists.containsKey(list.toString())) {
			lists.remove(list.toString());
		}
	}

	public int getNumberOfItems() {
		return lists.size();
	}

	public Iterator<ToDoList> getIterator() {
		return lists.values().iterator();
	}

	public String toString() {
		return getClass().toString();
	}
}