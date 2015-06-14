package main;

import main.Generator.Mutation;

public class Element implements Cloneable{

	public enum SeqType {
		gene,
		repetivive_element,
		transposon,
		simple_element;
	}
	public Element() {
		
		
		
	};
	public String toString() { 
		return "Abstract Element";
	}
	public int getSequenceLength() {
		System.out.println("WARNING abstract call");
		return 0;
	}
	public SeqType getType() 
	{
		System.out.println("WARNING abstract call");

		return null;
	}
	public void applyMutation(Mutation mutation, int i) {
		System.out.println("Warning abstract mutation");
	}
	

	protected String getrandomAminoacid() {
		String a = null;
		int r = Generator.randInt(1, 4);
		switch (r) {
		case 1:
			a = "A";
			break;
		case 2:
			a = "G";
			break;		
		case 3:
			a = "T";
			break;		
		case 4:
			a = "C";
			break;
		default:
			break;
		}
		return a;
	}
	
	public boolean canInsert(int maxDepth) {
		System.out.println("Warning abstract can insert");

		return false;
	}
	public String getSequenceAsString() {
		return null;
	}
	public String getGFFLine(int seq_len, String seq_name) {
		// TODO Auto-generated method stub
		return null;
	}
}
