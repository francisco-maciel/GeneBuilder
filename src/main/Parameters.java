package main;

import java.io.File;

public class Parameters {

	public String seq_name;
	public int seq_length;
	public String transp_dir;
	public int transp_per;
	public int transp_depth;
	public String gene_dir;
	public int gene_per;
	public String repeat_dir;
	public int repeat_per;
	public int ins_rate;
	public int rem_rate;
	public int repl_rate;
	public String[] transposontypes;	
	
	Parameters(String[] args) throws InvalidParametersException {
		if (args.length < 12) throw new InvalidParametersException("Argument list too short");
		
		this.seq_name = args[0];
		this.seq_length = aproveRange(args[1], 0, Integer.MAX_VALUE);
		this.transp_dir = aproveDirectory(args[2]);
		this.transp_per = aproveRange(args[3], 0, 100);
		this.transp_depth = aproveRange(args[4], 0, Integer.MAX_VALUE);
		this.gene_dir = aproveDirectory(args[5]);
		this.gene_per = aproveRange(args[6], 0, 100);
		this.repeat_dir = aproveDirectory(args[7]);
		this.repeat_per = aproveRange(args[8], 0, 100);		
		this.ins_rate = aproveRange(args[9], 0, 1000);	
		this.rem_rate = aproveRange(args[10], 0, 1000);		
		this.repl_rate = aproveRange(args[11], 0, 1000);		
		
		int i = 12;
		transposontypes = new String[args.length - 12];
		
		while (i < args.length) {
			transposontypes[i - 12] = args[i];
			i++;
		}
		if (ins_rate + rem_rate + repl_rate > 1000) throw new InvalidParametersException("Too high mutation percentages");
		if (transp_per + gene_per + repeat_per > 100) throw new InvalidParametersException("Too high element percentages (over 100%)");
		

	};
	
	public int aproveRange(String value, int min, int max) throws InvalidParametersException {
		int val =  Integer.parseInt(value);
		if (!inRange(val,min,max)) throw new InvalidParametersException("Value out of range" + value);
		return val;
	}	
	
	public boolean inRange(int value, int min, int max) {
		return (value >= min && value <= max);
	}
	public String aproveDirectory(String d) throws InvalidParametersException {
		if (!isValidDirectory(d)) throw new InvalidParametersException("Invalid directory " + d);
		return d;
	}
	/**
	 * Checks if file is valid directory
	 * @param s Path of the file to check
	 * @return true if valid directory, false otherwise
	 */
	public static boolean isValidDirectory(String s) {
		
		File file = new File(s);
		if (file.exists() && file.isDirectory()) {
			return true;
		}
		else 
			return false;
	}
	
	/**
	 * Source: http://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java
	 * Checks if string is integer
	 * @param s string to match with integer
	 * @return true if valid integer, false otherwise
	 */
	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	/**
	 * Source: http://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java
	 * Checks if string is integer (including radix)
	 * @param string string to match with integer
	 * @param radix radix to optimize check
	 * @return true if valid integer, false otherwise
	 */
	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	
}


