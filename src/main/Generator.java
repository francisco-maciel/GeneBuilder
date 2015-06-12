package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import main.ComplexElement.SeqType;

public class Generator {

	public static final int MUTATIONREF = 1000;

	public static void main(String[] args) throws IOException {

		long startTime = System.nanoTime();    

		int totalT = 0, totalG = 0, totalR = 0;
		Parameters params = null;

		try {
			params = new Parameters(args);
		} catch (InvalidParametersException e) {
			e.printStackTrace();
			return;
		}
		TransposonSetBuilder TB = new TransposonSetBuilder(params.transp_dir, (int) (params.transp_per / 100.0 * params.seq_length), params.transposontypes);
		ComplexSetBuilder GB = new ComplexSetBuilder(params.gene_dir, (int) (params.gene_per / 100.0 * params.seq_length), SeqType.gene);
		ComplexSetBuilder RB = new ComplexSetBuilder(params.repeat_dir, (int) (params.repeat_per / 100.0 * params.seq_length), SeqType.repetivive_element);
		

		ArrayList<TransposonElement> transposons = TB.getTransposons();
		ArrayList<ComplexElement> genes = GB.getElements();		
		ArrayList<ComplexElement> repeats = RB.getElements();

		
		
		for (TransposonElement t: transposons)
			totalT += t.getSequenceLength();
		for (ComplexElement g: genes)
			totalG += g.getSequenceLength();
		for (ComplexElement r: repeats)
			totalR += r.getSequenceLength();
		
		int replacement_count = predictMutation(params.repl_rate, MUTATIONREF, params.seq_length);
		int insertion_count = predictMutation(params.ins_rate, MUTATIONREF, params.seq_length);
		int removal_count = predictMutation(params.rem_rate, MUTATIONREF, params.seq_length);

		printInputData(totalT, totalG, totalR, replacement_count, insertion_count , removal_count, transposons.size(), genes.size(), repeats.size());

		
		int nucleoidCount = totalT + totalG + totalR + insertion_count - removal_count;
		if (nucleoidCount > params.seq_length) {
			nucleoidCount -= insertion_count;
			insertion_count = 0;
			System.out.println("Warning Insertion mutation not realize for compromizing sequence size");
		}
		
		System.out.println("Random Nuncleoids created " + (params.seq_length - nucleoidCount));

		ArrayList<Element> sequence = new ArrayList<Element>();
		
		int randomNucleoidCount = insertRandomNucleoids(params, nucleoidCount,
				sequence);
		
		sequence.addAll(genes);
		sequence.addAll(repeats);
		Collections.shuffle(sequence);
		
		int currentLength = randomNucleoidCount + totalG + totalR;
		
		for (int i = 0; i < transposons.size(); i++) {
			System.out.println(i);
			int insertPosition = randInt(0, currentLength);
			int currentPosition = 0;
			int insertIndex = -1;
			int currentSequenceSize = 0;
			for (int element_index = 0; element_index < sequence.size(); element_index++)  {
				currentSequenceSize = sequence.get(element_index).getSequenceLength();
				if (currentPosition +  currentSequenceSize < insertPosition) {
					currentPosition += currentSequenceSize;
				}
				else if (currentPosition + currentSequenceSize == insertPosition)
				{
					insertIndex = element_index + 1;
					break;
				}
				else if  (currentPosition + currentSequenceSize > insertPosition){
					//TODO consider depth
					insertIndex = element_index;
					break;
				}
				
			}
			
			sequence.add(insertIndex, transposons.get(i));
			currentLength += transposons.get(i).getSequenceLength();
		}
		
		int seq_length = getSequenceLength(sequence);
		
		System.out.println("Final sequence length: " + seq_length);
		long estimatedTime = System.nanoTime() - startTime;
		System.out.println((int) ( estimatedTime / 1000000000.0));
	}

	private static int getSequenceLength(ArrayList<Element> sequence) {
		int seq_length = 0;
		for (Element e : sequence)
			seq_length += e.getSequenceLength();
		return seq_length;
	}

	private static int insertRandomNucleoids(Parameters params,
			int nucleoidCount, ArrayList<Element> sequence) {
		int randomNucleoidCount = 0;
		for (int i = nucleoidCount; i < params.seq_length; i++) {
			randomNucleoidCount++;
			sequence.add(new SimpleElement());
		}
		return randomNucleoidCount;
	}

	private static int predictMutation(int mut_rate, int mutationmax,
			int seq_length) {
		int count = 0;
		for (int i = 0; i < seq_length; i++) {
			if (randInt(1, mutationmax) <= mut_rate) count+= 1;
		}

		return count;
	}

	private static void printInputData(int totalT, int totalG, int totalR, int replacement_count, int insertion_count, int removal_count, int tSize, int gSize, int rSize) {

		System.out.println("[INPUT DATA]");

		System.out.println("Transposons to insert: " + tSize);
		System.out.println("Transposon total length: " + totalT);



		System.out.println("Genes to insert: " + gSize);
		System.out.println("Genes total length: " + totalG);



		System.out.println("Repetitive Elements to insert: " + rSize);
		System.out.println("Repetitive Elements total length: " + totalR);

		System.out.println("[MUTATIONS]");
		System.out.println("Replacement count: " + replacement_count);
		System.out.println("Insertion count: " + insertion_count);
		System.out.println("Removal count: " + removal_count);

	}







	/**
	 * Returns a pseudo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * source http://stackoverflow.com/questions/363681/generating-random-integers-in-a-range-with-java
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max) {

		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}
}
