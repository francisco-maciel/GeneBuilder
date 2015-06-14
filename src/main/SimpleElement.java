package main;

import main.Generator.Mutation;

public class SimpleElement extends Element {
	public String aminoacid;

	public SimpleElement() {
		aminoacid = getrandomAminoacid();

	}


	public SimpleElement(String n) {
		aminoacid = n;
	}
	
	public String toString() {
		return "SimpleElement " + aminoacid;
	}
	
	public int getSequenceLength() {
		return 1;
	}
	
	public SeqType getType() {
		return SeqType.simple_element;
	}
	
	public void applyMutation(Mutation mutation, int i) {
		if (mutation == Mutation.replacement) this.aminoacid = getrandomAminoacid();
		else System.out.println("Warning unsuported mutation for simple element");
	}
	
	
	public boolean canInsert(int maxDepth) {

		return false;
	}
	
	public String getSequenceAsString() {
		return aminoacid;
	}
}
