package main;

import java.io.File;
import java.io.FilenameFilter;

public class SetBuilder {

	String directory;
	int required_length;

	public SetBuilder(String directory, int required_length) {
		this.directory = directory;
		this.required_length = required_length;

		
	}
	
	protected File[] getRefFiles() {
		File dir = new File(this.directory);
		File [] files = dir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return !name.endsWith("~");
		    }
		});
		return files;
	}
	


}
