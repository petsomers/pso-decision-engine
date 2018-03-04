package pso.decision_engine.utils.bigfilesort;

import java.nio.file.Path;

import lombok.Data;

@Data
public class BigFileSortCommand {
	Path inputFile;
	String outputFileName; 
	String outputFileExtension;
	boolean extractHeaderLine;
	boolean removeTabs;
	boolean removeEmptyLines=true;
	boolean keepFirstTabUnique;
}
