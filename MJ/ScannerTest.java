package MJ;

import java.io.*;

public class ScannerTest {
	public static void main(String[] args) {
		if (args.length > 0) {
			String sourceFileName = args[0];
			try {
				Scanner.init(new InputStreamReader(new FileInputStream(sourceFileName)));
				Token t = Scanner.next();
				while (t.kind != 41) { // 41 is eof
					System.out.println("Line " + t.line + ", Col " + t.col + ": " + t.kind + (t.val != null ? " (" + t.val + ")" : ""));
					t = Scanner.next();
				}
				System.out.println("Line " + t.line + ", Col " + t.col + ": " + t.kind + " (eof)");
			} catch (IOException e) {
				System.out.println("-- cannot open input file " + sourceFileName);
			}
		} else {
			System.out.println("-- synopsis: java MJ.ScannerTest <sourceFileName>");
		}
	}
}
