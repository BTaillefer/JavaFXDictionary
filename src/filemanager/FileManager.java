package filemanager;

public class FileManager {
	private String fileName; //Holds filename of our main dictionary
	private String textName; //Holds filename for our text
	
	public FileManager(String fileName, String textName) {
		this.fileName = fileName;
		this.textName = textName;
	}
	public FileManager() {
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getTextName() {
		return textName;
	}
	
	public void setTextName(String textName) {
		this.textName = textName;	
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
