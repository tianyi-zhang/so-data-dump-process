package edu.ucla.cs.so.data.process.deeplearning;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;

public class RandomSample {
	public static void main(String[] args) throws IOException {
		int num = 100;
		String tsv_file_path = "output/dl4j-sorted.tsv";
		File tsv_file = new File(tsv_file_path);
		List<String> lines = FileUtils.readLines(tsv_file, Charset.defaultCharset());
		
		String sample_output_path = "output/dl4j-sample.tsv";
		File output_file = new File(sample_output_path);
		Random rand = new Random();
		int bound = lines.size();
		if(bound < num) {
			System.err.println("Do not have enough data to sample!"); 
		} else {
			int count = 0;
			HashSet<Integer> sampled = new HashSet<Integer>();
			while (count < num) {
				int i = rand.nextInt(bound);
				if(i < 50 || sampled.contains(i)) {
					continue;
				} else {
					FileUtils.writeStringToFile(output_file, 
							lines.get(i) + System.lineSeparator(), Charset.defaultCharset(), true);
					sampled.add(i);
					count++;
				}
			}
		}
	}
}
