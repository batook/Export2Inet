package inet_data;

import inet_util.InetPriceProperties;
import inet_util.OraSession;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

public class Export2Inet {
	public static void main(String[] args) throws SQLException, UnsupportedEncodingException,
			FileNotFoundException {
		OraSession oraSession = new OraSession();
		oraSession.open();
		Connection conn = oraSession.getConnection();
		InetPriceProperties.getInstance().init("Export2Inet.properties");
		Properties props = InetPriceProperties.getInstance().getProperty();
		PrintWriter logFile = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
				"log.dat", false), "windows-1251")));
		// System.setOut(new PrintStream(System.out, true,
		// props.getProperty("CodePage")));
		// inet_gui.MainGui gui = new inet_gui.MainGui(props);
		// gui.createGui();
		if (args.length == 0) {
			TaskListCreator.createList(conn,true,"ALL");
			logFile.println(new SimpleDateFormat("dd.MM.yyyy H:mm").format(new Date()) + " Start ALL");
			// TaskListProcessor.printToDoListCollection(TaskListCreator.getListCollection());
			TaskListProcessor.process(TaskListCreator.getListCollection(), logFile, props);
		}
		else if (args[0].equals("-light")) {
			TaskListCreator.createList(conn,false,"ALL");
			logFile.println(new SimpleDateFormat("dd.MM.yyyy H:mm").format(new Date()) + " Start ALL");
			// TaskListProcessor.printToDoListCollection(TaskListCreator.getListCollection());
			TaskListProcessor.process(TaskListCreator.getListCollection(), logFile, props);
		}
		else if (args[0].equals("-9")) {
			TaskListCreator.createList(conn,true,"9");
			logFile.println(new SimpleDateFormat("dd.MM.yyyy H:mm").format(new Date()) + " Start 9");
			// TaskListProcessor.printToDoListCollection(TaskListCreator.getListCollection());
			TaskListProcessor.process(TaskListCreator.getListCollection(), logFile, props);
		}
		logFile.println(new SimpleDateFormat("dd.MM.yyyy H:mm").format(new Date()) + " Stop");
		logFile.close();
		conn.close();
		oraSession.close();
	}
}

class TaskListProcessor {
	@SuppressWarnings("unchecked")
	public static void process(IIterating collection, PrintWriter out, Properties props)
			throws UnsupportedEncodingException {
		System.setOut(new PrintStream(System.out, true, props.getProperty("CodePage")));
		Iterator elements = collection.getIterator();
		System.out.println("Задание - " + collection + ":");
		while (elements.hasNext()) {
			Object currentElement = elements.next();
			if (currentElement instanceof IIterating) {
				process((IIterating) currentElement, out, props);
				out.println();
			}
			else {
				System.out.println("\t" + ((IFileExport) currentElement).getFullName());
				try {
					((IFileExport) currentElement).createCursor();
					((IFileExport) currentElement).checkOutDir();
					((IFileExport) currentElement).createFile();
					out.println(new SimpleDateFormat("dd.MM.yyyy H:mm").format(new Date()) + " "
							+ ((IFileExport) currentElement).getFullName());
					out.flush();
				}
				catch (SQLException e) {
					out.println(((IFileExport) currentElement).getFullName() + " " + e.getMessage());
					out.flush();
				}
			}
		}
	}

	public static void printToDoListCollection(ToDoListCollection lists) {
		Iterator<ToDoList> elements = lists.getIterator();
		while (elements.hasNext()) {
			processToDoList(elements.next());
		}
	}

	public static void processToDoList(ToDoList list) {
		Iterator<IFileExport> elements = list.getIterator();
		System.out.println("Task - " + list + ":");
		while (elements.hasNext()) {
			IFileExport currentElement = elements.next();
			System.out.println("\t" + currentElement.getFullName());
			try {
				currentElement.createCursor();
				currentElement.createFile();
			}
			catch (SQLException e) {
				System.err.println(currentElement.getFullName() + " " + e.getMessage());
			}
		}
	}
}