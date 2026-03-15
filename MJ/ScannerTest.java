package MJ;

import java.io.*;

/**
 * Assignment 2 - Task 5
 * Extended ScannerTest to support an optional ground-truth file for automatic verification.
 * The second command-line parameter contains the expected sequence of token codes.
 */
public class ScannerTest {
	public static void main(String[] args) {
		if (args.length > 0) {
			String sourceFileName = args[0];
			String groundTruthFileName = args.length > 1 ? args[1] : null;
			java.util.Scanner groundTruthScanner = null;
			
			try {
				if (groundTruthFileName != null) {
					groundTruthScanner = new java.util.Scanner(new File(groundTruthFileName));
				}
				
				MJ.Scanner.init(new InputStreamReader(new FileInputStream(sourceFileName)));
				Token t = MJ.Scanner.next();
				boolean testPassed = true;
				
				while (t.kind != 41) { // 41 is eof
					// Task 5 additions: check ground truth if provided
					if (groundTruthScanner != null) {
						if (!groundTruthScanner.hasNextInt()) {
							System.out.println("Error: ground truth file has fewer tokens than expected.");
							testPassed = false;
							break;
						}
						int expectedKind = groundTruthScanner.nextInt();
						if (t.kind != expectedKind) {
							System.out.println("Error at Line " + t.line + ", Col " + t.col + 
								": Expected token " + expectedKind + ", but got " + t.kind + 
								(t.val != null ? " (" + t.val + ")" : ""));
							testPassed = false;
						}
					} else {
						// Original Task 4 behavior
						System.out.println("Line " + t.line + ", Col " + t.col + ": " + t.kind + (t.val != null ? " (" + t.val + ")" : ""));
					}
					t = MJ.Scanner.next();
				}
				
				// Handle eof token
				if (groundTruthScanner != null && testPassed) {
					if (!groundTruthScanner.hasNextInt()) {
						System.out.println("Error: missing eof token in ground truth file.");
						testPassed = false;
					} else {
						int expectedKind = groundTruthScanner.nextInt();
						if (t.kind != expectedKind) {
							System.out.println("Error at Line " + t.line + ", Col " + t.col + 
								": Expected token " + expectedKind + ", but got " + t.kind + " (eof)");
							testPassed = false;
						}
					}
					
					if (testPassed && groundTruthScanner.hasNextInt()) {
						System.out.println("Error: ground truth file has more tokens than actual input.");
						testPassed = false;
					}
					
					if (testPassed) {
						System.out.println("All tests passed!");
					}
				} else if (groundTruthScanner == null) {
					System.out.println("Line " + t.line + ", Col " + t.col + ": " + t.kind + " (eof)");
				}
				
				if (groundTruthScanner != null) {
					groundTruthScanner.close();
				}
			} catch (IOException e) {
				System.out.println("-- cannot open file: " + e.getMessage());
			}
		} else {
			System.out.println("-- synopsis: java MJ.ScannerTest <sourceFileName> [groundTruthFile]");
		}
	}
}
