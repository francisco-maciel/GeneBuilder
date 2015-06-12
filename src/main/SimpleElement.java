package main;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;

public class SimpleElement extends Element {
	public String aminoacid;

	public SimpleElement() {
		int r = Generator.randInt(1, 4);
		switch (r) {
		case 1:
			aminoacid = "A";
			break;
		case 2:
			aminoacid = "G";
			break;		
		case 3:
			aminoacid = "T";
			break;		
		case 4:
			aminoacid = "C";
			break;
		default:
			break;
		}

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
}
