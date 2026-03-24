# Walkthrough Assignment 2 - Task 5

In this task, I extended the implementation of `MJ.ScannerTest` from Task 4 to automatically verify the correct behavior of the scanner against "ground truth" files. By comparing the output token stream against expected token codes, I could confidently confirm if the scanner processed tokens as intended without manual inspection.

## What was added in Task 5

1. **Javadocs and Comments**: I added comments and Javadoc to `MJ.ScannerTest.java` marking `Assignment 2 - Task 5`.
2. **Ground Truth Files Generation**: 
   - I generated `tests/valid.mj.txt` and `tests/invalid.mj.txt`. 
   - Each file contains a simple vertical list of the expected (correct) integer token codes corresponding to the test files created in Task 4.
3. **Optional Command-Line Parameter**: 
   - I modified `MJ.ScannerTest.java` to accept an optional second argument specifically for the ground truth file: `java MJ.ScannerTest <sourceFileName> [groundTruthFile]`.
4. **Verification Logic**: 
   - The test program scans the target file with `MJ.Scanner` and iterates over each `Token`.
   - If a ground-truth file route is supplied, it reads the next integer from the text file using `java.util.Scanner` and cross-matches it with the generated `Token.kind`.
   - The program strictly verifies everything matches completely, including the `eof` token (`kind 41`).
   - If there are any discrepancies, an error message points to the exact Line, Column, and Token mismatch.
   - Upon a complete matching pass of all tokens, the program concludes with `All tests passed!`.
   - If no second parameter is provided, `MJ.ScannerTest` behaves exactly as it did in Task 4 (printing out all tokens visibly to the console).

## How to execute tests

Make sure to compile the Java file first:
```bash
javac MJ/ScannerTest.java
```

### 1. Simple Run (Task 4 Behavior - Printing Tokens)
If you just want to see the tokens parsed by the scanner:
```bash
java MJ.ScannerTest tests/valid.mj
```

### 2. Automatic Verification (Task 5 Behavior - Ground Truth Verification)
To test whether the valid source file matches its expected output:
```bash
java MJ.ScannerTest tests/valid.mj tests/valid.mj.txt
```

To test the invalid/error robustness test cases against its expected output:
```bash
java MJ.ScannerTest tests/invalid.mj tests/invalid.mj.txt
```

If all tests are successful, the command line outputs:
> All tests passed!
