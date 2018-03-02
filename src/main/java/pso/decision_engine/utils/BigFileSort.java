package pso.decision_engine.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import lombok.Data;

public class BigFileSort {
	
	@Data
	private static class SplitFileProgress {
		private int lineNumber;
		private int fileNumber;
		private ArrayList<String> lines=new ArrayList<>();
		private ArrayList<Path> files=new ArrayList<>();
	}
	
	public void sortAndRemoveDuplicates(final Path inputFile, final String outputFileName, final String outputFileExtension) throws IOException {
		Path tempDir=Paths.get(inputFile.getParent().toString(), "temp");
		tempDir.toFile().mkdirs();
		List<Path> files=split(inputFile, outputFileName, outputFileExtension);
		ArrayList<BufferedReader> readers=new ArrayList<>();
		try {
			for (Path f:files) {
				Files.newBufferedReader(f);
			}
			Path outputFile=Paths.get(inputFile.getParent().toString(), outputFileName+"."+outputFileExtension);
			ArrayList<String> lastLines=new ArrayList<>(); 
			try(BufferedWriter writer = Files.newBufferedWriter(outputFile, Charset.forName("UTF-8"))) {
				
			}
		} finally {
			for (BufferedReader r:readers) {
				try {r.close();} catch(Exception e) {;}
			}
		}
		
	}
	
	private List<Path> split(final Path inputFile, final String outputFileName, final String outputFileExtension) throws IOException {
		final SplitFileProgress sfp=new SplitFileProgress();
		try (Stream<String> stream = Files.lines(inputFile)) {
			stream.forEach(line -> {
				try {
					if (sfp.getLineNumber()>0 && sfp.getLineNumber()%100000==0) {
						Path outputFile=Paths.get(inputFile.getParent().toString(), "temp", outputFileName+"_"+sfp.getFileNumber()+"."+outputFileExtension);
						if (sfp.getFileNumber()==0) {
							outputFile.toFile().getParentFile().mkdirs();	
						}
						sfp.getFiles().add(outputFile);
						sortAndWriteLinesToFile(outputFile, sfp.getLines());
						sfp.setFileNumber(sfp.getFileNumber()+1);
						sfp.setLines(new ArrayList<>());
					}
					String l=line.trim();
					if (l.length()>0) {
						sfp.setLineNumber(sfp.getLineNumber()+1);
						sfp.getLines().add(line.trim());	
					}
				} catch (IOException ioe) {
					throw new RuntimeException(ioe);
				}
			});
		}
		Path outputFile=Paths.get(inputFile.getParent().toString(), "temp", outputFileName+"_"+sfp.getFileNumber()+"."+outputFileExtension);
		if (sfp.getFileNumber()==0) {
			outputFile.toFile().getParentFile().mkdirs();	
		}
		sfp.getFiles().add(outputFile);
		sortAndWriteLinesToFile(outputFile, sfp.getLines());
		return sfp.getFiles();
	}
	
	private void sortAndWriteLinesToFile(Path outputFile, List<String> lines) throws IOException {
		Collections.sort(lines);
		try(final BufferedWriter writer = Files.newBufferedWriter(outputFile, Charset.forName("UTF-8"))) {
			lines.stream().forEach(sortedline -> {
				try {
					writer.write(sortedline);
					writer.write("\r\n");
				} catch (IOException ioe) {
					throw new RuntimeException(ioe);
				}
			});
			writer.flush();
		}
	}
	
	
	static public void main(String[] args) throws IOException {
		BigFileSort bfs=new BigFileSort();
		Path inputFile=Paths.get("C:/temp/decision_engine/testbigsort/partsfile-1.txt");
		bfs.sortAndRemoveDuplicates(inputFile, "partsfile-1-output", "txt");
	}

}
