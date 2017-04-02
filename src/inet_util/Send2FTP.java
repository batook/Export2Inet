package inet_util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class Send2FTP implements Runnable {
	private String ftpServer;
	private String userName;
	private String password;
	private Properties props;
	private String destFolderName;
	private String srcFileName;
	private String destFileName;
	private String replyString;

	public Send2FTP() {
		InetPriceProperties.getInstance().init("Export2Inet.properties");
		props = InetPriceProperties.getInstance().getProperty();
		this.ftpServer = props.getProperty("ftp");
		this.userName = props.getProperty("username");
		this.password = props.getProperty("password");
	}

	public Send2FTP(String srcFileName, String destFileName, String destFolderName) {
		this();
		this.srcFileName = srcFileName;
		this.destFileName = destFileName;
		this.destFolderName = destFolderName;
	}

	public void run() {
		FTPClient ftpClient = new FTPClient();
		try {
			ftpClient.connect(ftpServer);
			System.out.print(ftpClient.getReplyString());
			// check Reply code
			if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				ftpClient.disconnect();
				System.out.println("Connection refused");
				return;
			}
			ftpClient.login(userName, password);
			System.out.print(ftpClient.getReplyString());
			ftpClient.changeWorkingDirectory(destFolderName);
			System.out.println("Workdir >>" + ftpClient.printWorkingDirectory());
			// Store File
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			FileInputStream input = new FileInputStream(srcFileName);
			ftpClient.storeFile(destFileName, input);
			replyString = ftpClient.getReplyString();
			System.out.print(getReplyString());
			input.close();
			ftpClient.logout();
			ftpClient.disconnect();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setDestFolderName(String destFolderName) {
		this.destFolderName = destFolderName;
	}

	public void setSrcFileName(String srcFileName) {
		this.srcFileName = srcFileName;
	}

	public void setDestFileName(String destFileName) {
		this.destFileName = destFileName;
	}

	public String getReplyString() {
		return replyString;
	}
}
