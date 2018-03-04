package pso.decision_engine.utils.bigfilesort;

import java.nio.file.Path;

import lombok.Data;

@Data
public class BigFileSortResult {
	private Path outputFile;
	private String headerLine;
}
