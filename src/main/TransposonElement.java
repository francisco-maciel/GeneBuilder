package main;

import org.biojava.nbio.core.sequence.ProteinSequence;

public class TransposonElement extends Element {
	ProteinSequence transposon;
	public TransposonElement(ProteinSequence p) {
		transposon = p;
	}

}
