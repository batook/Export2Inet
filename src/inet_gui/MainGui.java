package inet_gui;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

public class MainGui implements ActionListener {
	private JFrame mainFrame;
	private JTextField fieldSiteDir, fieldShopDir;
	private JLabel labelSiteDir, labelShopDir;
	private JButton btnStart, btnExit;
	private JPanel controlPanel, editPanel;
	private Properties props;

	public MainGui(Properties props) {
		this.props = props;
	}

	public void createGui() {
		mainFrame = new JFrame("Выгрузка в ИМ");
		Container content = mainFrame.getContentPane();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		editPanel = new JPanel(new GridLayout(0, 2));
		labelShopDir = new JLabel("TT Dir");
		fieldShopDir = new JTextField(props.getProperty("ResultDir"), 20);
		editPanel.add(labelShopDir);
		editPanel.add(fieldShopDir);
		labelSiteDir = new JLabel("Site Dir");
		fieldSiteDir = new JTextField(props.getProperty("ResultDir"), 20);
		editPanel.add(labelSiteDir);
		editPanel.add(fieldSiteDir);
		content.add(editPanel);
		controlPanel = new JPanel();
		controlPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		btnStart = new JButton("Start");
		btnExit = new JButton("Exit");
		controlPanel.add(btnStart);
		controlPanel.add(btnExit);
		content.add(controlPanel);
		btnStart.addActionListener(this);
		btnExit.addActionListener(this);
		mainFrame.addWindowListener(new WindowCloseManager());
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object originator = e.getSource();
		if (originator == btnStart) {
		}
		else if (originator == btnExit) {
			exitApplication();
		}
	}

	private class WindowCloseManager extends WindowAdapter {
		public void windowClosing(WindowEvent evt) {
			exitApplication();
		}
	}

	private void exitApplication() {
		System.exit(0);
	}
}
