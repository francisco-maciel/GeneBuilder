package main;

import main.Generator.Mutation;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;

public class TransposonElement extends Element {
	public ProteinSequence transposon;
	public int  currentDepth;

	int insertions,removals,replacements;
	public TransposonElement(ProteinSequence p) {
		currentDepth = 0;
		insertions = 0;
		removals = 0;
		replacements = 0;
		try {
			transposon = new ProteinSequence(new String(p.getSequenceAsString()));
		} catch (CompoundNotFoundException e) {
			e.printStackTrace();
		}
		transposon.setOriginalHeader(new String (p.getOriginalHeader()));
	}


	public String toString() { 
		return "Transposon " + transposon.getOriginalHeader();
	}

	public int getSequenceLength() {
		return transposon.getLength();
	}

	public SeqType getType() {
		return SeqType.transposon;
	}

	public void applyMutation(Mutation mutation, int index) {

		String seq = transposon.getSequenceAsString();
		String changedSequence = null;
		ProteinSequence newTransposon = null;

		if (mutation == Mutation.removal) {
			removals++;
			changedSequence = seq.substring(0, index) + seq.substring(index+1);
		}
		else if (mutation == Mutation.insertion) {
			insertions++;

			changedSequence = seq.substring(0, index) + getrandomAminoacid() + seq.substring(index);
		}
		else if (mutation == Mutation.replacement) {
			replacements++;
			changedSequence = seq.substring(0, index) + getrandomAminoacid() + seq.substring(index + 1);
		}
		try {
			newTransposon = new ProteinSequence(changedSequence);
		} catch (CompoundNotFoundException e) {
			e.printStackTrace();
		}
		newTransposon.setOriginalHeader(transposon.getOriginalHeader());



		this.transposon = newTransposon;

	}

	protected Element clone() {

		TransposonElement clone= new TransposonElement(this.transposon);

		return clone;

	}

	public boolean canInsert(int maxDepth) {

		return currentDepth < maxDepth;
	}

	public String getSequenceAsString() {
		return transposon.getSequenceAsString();
	}
	
	
	public String getGFFLine(int seq_len, String seq_name) {
		return seq_name + "\t.\ttandem\t" + (seq_len) + "\t" + (seq_len + this.getSequenceLength()) + "\t.\t+\t.\treplen\t" + (this.getSequenceLength()) + ";\tid:\t" + this.getOriginalId() + ";\ttype:\t" + this.getOriginalType() + ";\tins:\t" + insertions +";\trem:\t" + removals +";\trepl:\t" + replacements; 
	}


	private String getOriginalType() {
		String arr[] = transposon.getOriginalHeader().split("\\s+", 2);
		return arr[1];
	}


	private String getOriginalId() {
		String arr[] = transposon.getOriginalHeader().split("\\s+", 2);
		return arr[0];
	}
}
