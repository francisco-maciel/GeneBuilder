package main;

import org.biojava.nbio.core.sequence.ProteinSequence;

public class ComplexElement extends Element{
	public enum SeqType {
		gene,
		repetivive_element;
	}
	public String sequence;
	public String header;
	public SeqType type;
	
	public ComplexElement(ProteinSequence p, SeqType t) {
		this.sequence = p.getSequenceAsString();
		this.header = p.getOriginalHeader();

		this.type = t;
	}
	
	public String toString() { 
		return type + " " + header;
	}

	
	public int getSequenceLength() {
		return sequence.length();
	}
	
}
