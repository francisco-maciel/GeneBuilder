package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

public class Generator {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Parameters params = null;
		try {
			params = new Parameters(args);
		} catch (InvalidParametersException e) {
			e.printStackTrace();
			return;
		}
		
		ArrayList<TransposonElement> transposon = (new TransposonSetBuilder(params.transp_dir, params.transp_per, params.transposontypes)).getTransposons();
/*		
		LinkedHashMap<String, ProteinSequence> a = FastaReaderHelper.readFastaProteinSequence(new File(args[0]));


		for (Entry<String, ProteinSequence> entry : a.entrySet() ) {
			System.out.println( entry.getValue().getOriginalHeader()  );
		}
		System.out.println(a.size());
*/
	}
}
