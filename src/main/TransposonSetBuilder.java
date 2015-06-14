package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

public class TransposonSetBuilder extends SetBuilder {

	String[] types;
	
	public TransposonSetBuilder(String directory, int required_length, String[] types ) {
		super(directory, required_length);
		this.types = types;
	}
	
	public ArrayList<TransposonElement> getTransposons() throws IOException {
		
		File[] files = getRefFiles();

		ArrayList<TransposonElement> transposons = new ArrayList<TransposonElement>();
		
		for (File file : files) {
			transposons.addAll(readFasta(file));
		}
		return filterByLength(transposons);
	}
	
	private ArrayList<TransposonElement> filterByLength(ArrayList<TransposonElement> transposons) {
		ArrayList<TransposonElement> filtered = new ArrayList<TransposonElement>();
		if (transposons.size() == 0) return filtered;
		int current_length = 0;
		int max_index = transposons.size() - 1;
		int index = 0;
		boolean done = false;
		
		while (!done) {
			index = Generator.randInt(0,max_index);
			TransposonElement t = transposons.get(index);
			
			if (current_length + t.getSequenceLength() > required_length)
				done = true;
			else {
				filtered.add((TransposonElement) t.clone());
				current_length += t.getSequenceLength();
			}
		}
		
		return filtered;
	}

	public ArrayList<TransposonElement> readFasta(File f) throws IOException {
		LinkedHashMap<String, ProteinSequence> fasta = FastaReaderHelper.readFastaProteinSequence(f);
		ArrayList<TransposonElement> transposons = new ArrayList<TransposonElement> ();
		
		for (Entry<String, ProteinSequence> entry : fasta.entrySet() ) {
			if (matchTypes(entry.getValue().getOriginalHeader()))
			{
				TransposonElement transposon = new TransposonElement(entry.getValue());
				transposons.add(transposon);
				
			}
		}

		fasta = null;
		return transposons;
	}

	private boolean matchTypes(String transposon_name) {
		boolean found = false;
		for (String type : types) {
			if (transposon_name.toLowerCase().contains(type.toLowerCase())) {
				found = true;
				break;
			}
		}
		return found;
	}

	
}
