package main;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class TransposonSetBuilder {

	String directory;
	int percentage;
	String[] types;
	public TransposonSetBuilder(String directory, int percentage, String[] types ) {

		this.directory = directory;
		this.percentage = percentage;
		this.types = types;
	}
	
	public ArrayList<TransposonElement> getTransposons() {
		
		File[] files = getRefFiles();

		for (File file : files) {
		    System.out.println(file);
		}
		
		return null;
	}

	private File[] getRefFiles() {
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
