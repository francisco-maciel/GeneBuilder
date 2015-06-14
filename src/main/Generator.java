package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaWriterHelper;

import main.Element.SeqType;

public class Generator {
	public enum Mutation {
		removal,
		replacement,
		insertion
	}
	public static final int MUTATIONREF = 1000;

	public static void main(String[] args) throws IOException {

		long startTime = System.nanoTime();    
		System.out.println("Reading Input Files...");
		int totalT = 0, totalG = 0, totalR = 0;
		Parameters params = null;

		try {
			params = new Parameters(args);
		} catch (InvalidParametersException e) {
			e.printStackTrace();
			return;
		}

		//TODO change to gene directory (slower)
		params.gene_dir = params.repeat_dir;
		params.seq_length = 100000;
		//params.gene_per = 0;
		//params.repeat_per = 0;
		//params.transp_per = 0;


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

		System.out.println("[SEQUENCE]");
		System.out.println("Random Nuncleoids created " + (params.seq_length - nucleoidCount));
		System.out.println("Inserting nucleons...");

		ArrayList<Element> sequence = new ArrayList<Element>();

		int randomNucleoidCount = insertRandomNucleoids(params, nucleoidCount,
				sequence);

		sequence.addAll(genes);
		sequence.addAll(repeats);
		Collections.shuffle(sequence);

		int currentLength = randomNucleoidCount + totalG + totalR;

		System.out.println("Inserting Transposons...");
		insertTransposons(transposons, sequence, currentLength, params.transp_depth);


		int seq_length = getSequenceLength(sequence);

		System.out.println("Sequence length before mutations: " + seq_length);
		System.out.println("Applying mutations...");

		applyMutation(sequence, Mutation.removal, removal_count);
		applyMutation(sequence, Mutation.insertion, insertion_count);
		applyMutation(sequence, Mutation.replacement, replacement_count);


		seq_length = getSequenceLength(sequence);
		System.out.println("Expec sequence length: " + (randomNucleoidCount + totalG + totalR + totalT - removal_count + insertion_count));
		System.out.println("Final sequence length: " + seq_length);
		long estimatedTime = System.nanoTime() - startTime;
		System.out.println("Time taken: " + (int) ( estimatedTime / 1000000000.0) + " s");
		System.out.println("Writing to files...");

		writeFasta(sequence, params.seq_name);
		writeGFF(sequence, params.seq_name, params);
		writeElements(transposons, params.seq_name);
		//writeLoc(transposons, params.seq_name); ?? TODO

		System.out.println("Done.");

	}


	private static void writeFasta(ArrayList<Element> sequence, String name) {
		File f = new File(name + ".fasta");
		ProteinSequence pt = null;
		try {
			pt = new ProteinSequence(getSequenceString(sequence));
		} catch (CompoundNotFoundException e1) {
			e1.printStackTrace();
		}


		pt.setOriginalHeader(name.toUpperCase());
		List<ProteinSequence> list = new ArrayList<ProteinSequence>();
		list.add(pt);
		try {
			FastaWriterHelper.writeProteinSequence(f, list);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private static String getSequenceString(ArrayList<Element> sequence) {
		String seq = "";

		for (Element e : sequence) {
			seq += e.getSequenceAsString();
		}

		return seq;
	}



	private static void writeGFF(ArrayList<Element> sequence, String name, Parameters params) {
		File f = new File(name + ".gff");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(f);


			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

			bw.write("##gff-version 3");
			bw.newLine();
			bw.write("##seq_length: " + params.seq_length + "\ttransposon %:\t" + params.transp_per + "\tgene %:\t" + params.gene_per + "\trepetitive element %:\t" + params.repeat_per + "\tinsertion rate:\t"+params.ins_rate+"\tremotion rate:\t"+params.rem_rate+"\treplacement rate:\t"+params.rem_rate +"\ttransposon depth:\t" + params.transp_depth);
			bw.newLine();

			int seq_len = 1;
			String line;
			for (Element e : sequence) {
				line = e.getGFFLine(seq_len, params.seq_name);
				if (line != null) {
					bw.write(line);
					bw.newLine();
				}
				seq_len += e.getSequenceLength();
			}


			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	private static void writeElements(ArrayList<TransposonElement> transposons, String name) {
		File f = new File(name + "elements.fasta");
		List<ProteinSequence> list = new ArrayList<ProteinSequence>();

		ProteinSequence pt = null;
		String header;
		for (TransposonElement t: transposons) {
			header = t.transposon.getOriginalHeader();


			boolean exists = existsInSequence(list, header);

			if (!exists) {
				try {
					pt = new ProteinSequence(t.getSequenceAsString());
				} catch (CompoundNotFoundException e1) {
					e1.printStackTrace();
				}
				pt.setOriginalHeader(header);
				list.add(pt);
			}
		}

		try {
			FastaWriterHelper.writeProteinSequence(f, list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static boolean existsInSequence(List<ProteinSequence> list,
			String header) {
		boolean exists = false;
		for (ProteinSequence p : list) {
			if (p.getOriginalHeader().equals(header)) {
				exists = true;
				break;
			}

		}
		return exists;
	}

	private static void applyMutation(ArrayList<Element> sequence, Mutation mutation, int mutation_count) {

		int currentLength = getSequenceLength(sequence);
		int mutationPosition;
		int mutationCounter = 0;

		List <Integer> mutations = new ArrayList<Integer>();
		for (int i = 0;  i < mutation_count; i++) {

			mutationPosition = randInt(0, currentLength - 1 - mutation_count);
			mutations.add(mutationPosition);

			currentLength = getMutatedPosition(currentLength, mutation);
		}

		Collections.sort(mutations);

		int minIndex, maxIndex, length;
		int sequenceCounter;
		SequencePosition elementIndex;
		Element currentElement;
		mutationPosition = mutations.get(mutationCounter);

		while (mutationCounter < mutation_count) {
			System.out.println(mutation + " " + mutationCounter + "/" + mutation_count);
			sequenceCounter = 0;
			for (int i = 0; i < sequence.size(); i++) {
				currentElement = sequence.get(i);
				length = currentElement.getSequenceLength();
				minIndex = sequenceCounter;
				maxIndex = sequenceCounter + length - 1;

				if (mutationPosition >= minIndex && mutationPosition <= maxIndex) {
					int insideIndex = mutationPosition - sequenceCounter;
					elementIndex = new SequencePosition(i, insideIndex);

					if (mutation == Mutation.removal) {

						if (currentElement.getSequenceLength() == 1){
							sequence.remove(elementIndex.index);
							if (i != 0) i--;

						}
						else {
							currentElement.applyMutation(mutation, elementIndex.insideIndex);
						}


					}
					else if (mutation == Mutation.insertion) {
						if (currentElement.getType() == SeqType.simple_element){
							sequence.add(elementIndex.index, new SimpleElement());
							if (i != 0) i--;

						}
						else {
							currentElement.applyMutation(mutation, elementIndex.insideIndex);
						}

					}
					else if (mutation == Mutation.replacement)
						currentElement.applyMutation(mutation, elementIndex.insideIndex);

					sequenceCounter = getMutatedPosition(sequenceCounter, mutation);
					mutationCounter++;
					if (mutationCounter == mutation_count) {
						break;
					}
					mutationPosition = mutations.get(mutationCounter);
					if (i != 0) i--;
				}
				else {
					sequenceCounter += length;

				}

			}

		}


	}
	/*
	private static SequencePosition getIndexBySequenceIndex(ArrayList<Element> sequence,
			int searchSequenceIndex) {
		SequencePosition sp  = null;
		int sequenceCounter = 0;
		int length, minIndex, maxIndex;
		int i = 0;
		int seq_size = sequence.size();
		for (Element e : sequence) {
			length = e.getSequenceLength();
			minIndex = sequenceCounter;
			maxIndex = sequenceCounter + length - 1;

			if (searchSequenceIndex >= minIndex && searchSequenceIndex <= maxIndex) {
				int insideIndex = searchSequenceIndex - sequenceCounter;
				sp = new SequencePosition(i, insideIndex);
				break;
			}

			sequenceCounter += length;
			i++;
		}

		return sp;
	}
	 */
	private static int getMutatedPosition(int currentSequenceSize,
			Mutation mutation) {
		if (mutation == Mutation.insertion) return currentSequenceSize + 1;
		else if (mutation == Mutation.removal) return currentSequenceSize - 1;
		else return currentSequenceSize;
	}

	private static void insertTransposons(
			ArrayList<TransposonElement> transposons,
			ArrayList<Element> sequence, int currentLength, int maxDepth) {

		for (int i = 0; i < transposons.size(); i++) {
			int insertPosition = randInt(0, currentLength - 1);
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
					insertIndex = element_index;
					if (currentSequenceSize == (insertPosition - currentPosition) || currentSequenceSize - 1 == (insertPosition - currentPosition)){
						if (insertIndex != sequence.size() - 1) insertIndex++;
					}
					else {
						if (sequence.get(element_index).canInsert(maxDepth))
							//System.out.println("Transposon in transposon");
							insertIndex = insertIndex; 	//TODO consider depth

					}

					break;
				}

			}

			sequence.add(insertIndex, transposons.get(i));
			currentLength += transposons.get(i).getSequenceLength();
		}
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




	static class SequencePosition {

		int index;
		int insideIndex;

		SequencePosition(int index, int insideIndex) {
			this.index = index;
			this.insideIndex = insideIndex;
		};

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
