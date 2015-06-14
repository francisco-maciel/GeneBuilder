package main;

import main.Generator.Mutation;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;

public class ComplexElement extends Element{

	public ProteinSequence sequence;
	public SeqType type;
	
	public ComplexElement(ProteinSequence p, SeqType t) {

		try {
			sequence = new ProteinSequence(new String(p.getSequenceAsString()));
		} catch (CompoundNotFoundException e) {
			e.printStackTrace();
		}
		sequence.setOriginalHeader(new String (p.getOriginalHeader()));
		
		this.type = t;
	}
	
	public String toString() { 
		return type + " " + sequence.getOriginalHeader();
	}

	
	public int getSequenceLength() {
		return sequence.getLength();
	}
	
	public SeqType getType() {
		return type;
	}
	
	public void applyMutation(Mutation mutation, int index) {
		String seq = sequence.getSequenceAsString();
		String changedSequence = null;
		ProteinSequence newComplex = null;
		
		if (mutation == Mutation.removal) {
			changedSequence = seq.substring(0, index) + seq.substring(index+1);
		}
		else if (mutation == Mutation.insertion) {
			changedSequence = seq.substring(0, index) + getrandomAminoacid() + seq.substring(index);
		}
		else if (mutation == Mutation.replacement) {
			changedSequence = seq.substring(0, index) + getrandomAminoacid() + seq.substring(index + 1);
		}
		try {
			 newComplex = new ProteinSequence(changedSequence);
		} catch (CompoundNotFoundException e) {
			e.printStackTrace();
		}
		
		newComplex.setOriginalHeader(sequence.getOriginalHeader());
		

		this.sequence = newComplex;

		
		
	}
	
	  protected Element clone() {
		  
		    ComplexElement clone= new ComplexElement(this.sequence, this.type);
		 
		    
		    return clone;
		 
		  }
	  
		public boolean canInsert(int maxDepth) {

			return false;
		}
		
		public String getSequenceAsString() {
			return sequence.getSequenceAsString();
		}
		
		public String getGFFLine(int seq_len, String seq_name) {
			if (this.type == SeqType.repetivive_element)
				return seq_name + "\t.\ttandem\t" + (seq_len) + "\t" + (seq_len + this.getSequenceLength()) + "\t.\t+\t.\treplen\t" + (this.getSequenceLength()) + ";\tid:\t" + this.getOriginalId() + ";\ttype:\tRepetitiveElement;"; 
			else if (this.type == SeqType.gene) 
				return seq_name + "\t.\tNXE\t" + (seq_len) + "\t" + (seq_len + this.getSequenceLength()) + "\t.\t+\t.\tgene_index\t" + (this.getSequenceLength()) + ";\tid:\t" + this.getOriginalId() + ";"; 
			else return null;
			
		}




		private String getOriginalId() {
			String arr[] = sequence.getOriginalHeader().split("\\s+", 2);
			return arr[0];
		}
	
}
