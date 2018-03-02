package pso.decision_engine.utils.bigfilesort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

/**
 * Merge Sort using files
 * @author Peter Somers
 *
 */
public class BigFileSort {
	
	static public Path sortAndRemoveDuplicates(BigFileSortCommand c) throws IOException {
		Path tempDir=Paths.get(c.getInputFile().getParent().toString(), "temp");
		tempDir.toFile().mkdirs();
		List<Path> files=split(c);
		ArrayList<BufferedReader> readers=new ArrayList<>();
		ArrayList<LastLineStatus> lastLines=new ArrayList<>();
		try {
			for (Path f:files) {
				BufferedReader reader=Files.newBufferedReader(f);
				readers.add(reader);
				LastLineStatus lls=new LastLineStatus();
				lls.setReader(reader);
				lls.setNeedNewLine(true);
				lastLines.add(lls);
			}
			Path outputFile=Paths.get(c.getInputFile().getParent().toString(), c.getOutputFileName()+"."+c.getOutputFileExtension());
			try(BufferedWriter writer = Files.newBufferedWriter(outputFile, Charset.forName("UTF-8"))) {
				String lastText=null;
				MAINLOOP: while (true) {
					getLastLines(lastLines);
					
					int lowestIndex=-1;
					String lowestString=null;
					int i=-1;
					CHECKALL: for (LastLineStatus ll:lastLines) {
						i++;
						if (ll.endOfFile) continue CHECKALL;
						if (lowestString==null || lowestString.compareTo(ll.getLine())>0) {
							lowestString=ll.getLine();
							lowestIndex=i;
						}
					}
					if (lowestIndex==-1) {
						//finished
						break MAINLOOP;
					}
					lastLines.get(lowestIndex).setNeedNewLine(true);
					if (lastText==null || !lastText.equals(lowestString)) {
						// remove duplicates
						writer.write(lowestString);
						writer.write("\r\n");
					}
					lastText=lowestString;
				}
			}
			return outputFile;
		} finally {
			for (BufferedReader r:readers) {
				try {r.close();} catch(Exception e) {;}
			}
		}
		
	}
	
	@Data
	private static class SplitFileProgress {
		private int lineNumber;
		private int fileNumber;
		private ArrayList<String> lines=new ArrayList<>();
		private ArrayList<Path> files=new ArrayList<>();
	}
	
	@Data
	private static class LastLineStatus {
		private BufferedReader reader;
		private boolean needNewLine=true;
		private boolean endOfFile=false;
		private String line=null;
	}
	
	static private void getLastLines(ArrayList<LastLineStatus> lastLines) throws IOException {
		for (LastLineStatus ll:lastLines) {
			if (ll.isNeedNewLine() && !ll.endOfFile) {
				ll.setNeedNewLine(false);
				String line=ll.getReader().readLine();
				ll.setLine(line);
				if (line==null) ll.setEndOfFile(true);
			}
 		}
	}
	
	static private List<Path> split(BigFileSortCommand c) throws IOException {
		final SplitFileProgress sfp=new SplitFileProgress();
		try (Stream<String> stream = Files.lines(c.getInputFile())) {
			stream.forEach(line -> {
				try {
					if (sfp.getLineNumber()>0 && sfp.getLineNumber()%100000==0) {
						Path outputFile=Paths.get(c.getInputFile().getParent().toString(), "temp", c.getOutputFileName()+"_"+sfp.getFileNumber()+"."+c.getOutputFileExtension());
						if (sfp.getFileNumber()==0) {
							outputFile.toFile().getParentFile().mkdirs();	
						}
						sfp.getFiles().add(outputFile);
						sortAndWriteLinesToFile(outputFile, sfp.getLines());
						sfp.setFileNumber(sfp.getFileNumber()+1);
						sfp.setLines(new ArrayList<>());
					}
					String l=line;
					if (c.isRemoveTabs()) {
						int tabIndex=line.indexOf('\t');
						if (tabIndex>=0) {
							l=l.substring(0, tabIndex);
						}
					}
					l=l.trim();
					if (l.length()>0 || !c.isRemoveEmptyLines()) {
						sfp.setLineNumber(sfp.getLineNumber()+1);
						sfp.getLines().add(l);	
					}
				} catch (IOException ioe) {
					throw new RuntimeException(ioe);
				}
			});
		}
		Path outputFile=Paths.get(c.getInputFile().getParent().toString(), "temp", c.getOutputFileName()+"_"+sfp.getFileNumber()+"."+c.getOutputFileExtension());
		if (sfp.getFileNumber()==0) {
			outputFile.toFile().getParentFile().mkdirs();	
		}
		sfp.getFiles().add(outputFile);
		sortAndWriteLinesToFile(outputFile, sfp.getLines());
		return sfp.getFiles();
	}
	
	static private void sortAndWriteLinesToFile(Path outputFile, List<String> lines) throws IOException {
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
		Path inputFile=Paths.get("C:/temp/decision_engine/testbigsort/partsfile-1.txt");
		BigFileSortCommand c=new BigFileSortCommand();
		c.setInputFile(inputFile);
		c.setOutputFileName("partsfile-1-output");
		c.setOutputFileExtension("txt");
		c.setKeepFirstLine(false);
		c.setOnlySortOnFirstTab(false);
		c.setRemoveTabs(false);
		c.setRemoveEmptyLines(true);
		BigFileSort.sortAndRemoveDuplicates(c);
	}

}