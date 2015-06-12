package main;

import org.biojava.nbio.core.sequence.ProteinSequence;

public class TransposonElement extends Element {
	public String transposon;
	public String header;
	
	public TransposonElement(ProteinSequence p) {
		transposon = p.getSequenceAsString();
		header = p.getOriginalHeader();
	}

	
	public String toString() { 
		return "Transposon " + header;
	}
	
	public int getSequenceLength() {
		return transposon.length();
	}
}
