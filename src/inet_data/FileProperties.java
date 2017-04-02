package inet_data;

import java.io.File;

public class FileProperties {
	private String taskName;
	private int inetId;
	private int cityId;
	private int days;
	private String oenPP;
	private String dirName;
	private String fileName;
	private String fileExtension;
	private String fileEncoding;
	private String filePrefix = "";

	public int getInetId() {
		return inetId;
	}

	public void setInetId(int inetId) {
		this.inetId = inetId;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getOenPP() {
		return oenPP;
	}

	public void setOenPP(String oenPP) {
		this.oenPP = oenPP;
	}

	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getFileEncoding() {
		return fileEncoding;
	}

	public void setFileEncoding(String fileEncoding) {
		this.fileEncoding = fileEncoding;
	}

	public String toString() {
		return dirName + File.separator + filePrefix + fileName + "." + fileExtension;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getFilePrefix() {
		return filePrefix;
	}

	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public int getDays() {
		return days;
	}
}
