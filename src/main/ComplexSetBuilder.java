package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;



import main.Element.SeqType;

import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

public class ComplexSetBuilder extends SetBuilder {

	SeqType type;
	
	public ComplexSetBuilder(String directory, int required_length, SeqType type) {
		super(directory, required_length);
		this.type = type;
	}
	
	public ArrayList<ComplexElement> getElements() throws IOException {
		
		File[] files = getRefFiles();
		ArrayList<ComplexElement> genes = new ArrayList<ComplexElement>();
		for (File file : files) {
			genes.addAll(readFasta(file));
		}

		return filterByLength(genes);
	}
	
	public ArrayList<ComplexElement> readFasta(File f) throws IOException {

		LinkedHashMap<String, ProteinSequence> fasta = FastaReaderHelper.readFastaProteinSequence(f);
		ArrayList<ComplexElement> genes = new ArrayList<ComplexElement> ();
		
		for (Entry<String, ProteinSequence> entry : fasta.entrySet() ) {
				ComplexElement gene = new ComplexElement(entry.getValue(), type);

				genes.add(gene);
				
		}
		fasta = null;
		return genes;
	}
	
	private ArrayList<ComplexElement> filterByLength(ArrayList<ComplexElement> genes) {
		ArrayList<ComplexElement> filtered = new ArrayList<ComplexElement>();
		if (genes.size() == 0) return filtered;
		int current_length = 0;
		int max_index = genes.size() - 1;
		int index = 0;
		boolean done = false;

		while (!done) {
			index = Generator.randInt(0,max_index);
			ComplexElement g = genes.get(index);
			
			if (current_length + g.getSequenceLength() > required_length)
				done = true;
			else {
				filtered.add((ComplexElement) g.clone());
				current_length += g.getSequenceLength();
			}
		}
		
		return filtered;
	}
	
}
