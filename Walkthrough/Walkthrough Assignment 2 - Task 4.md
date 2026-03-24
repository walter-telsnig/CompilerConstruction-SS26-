# Assignment 2, Task 4 - Approach Walkthrough

In this task, I tested the scanner implementation of the MicroJava compiler. The goal was to verify that the scanner correctly identifies all valid tokens and appropriately handles whitespace, comments, and invalid inputs.

## Approach

### 1. Test Program (`ScannerTest.java`)
I created a driver program that:
- Reads a source file from the command line.
- Initializes the `MJ.Scanner` with the file content.
- Repeatedly calls `Scanner.next()` to fetch tokens.
- Prints the `kind` (integer code) of each token to the standard output until the `eof` token is reached.

### 2. Valid Tokens Test Case (`valid.mj`)
I constructed a file that includes:
- **Keywords**: `program`, `class`, `final`, `if`, `else`, `while`, `break`, `return`, `read`, `print`, `new`, `void`.
- **Identifiers**: Mixed case names, names with underscores and digits.
- **Numbers**: Various integer values.
- **Character Constants**: Standard characters (e.g., `'a'`) and escape sequences (e.g., `'\n'`, `'\t'`, `'\r'`).
- **Operators and Punctuation**: `+`, `-`, `*`, `/`, `%`, `++`, `--`, `==`, `!=`, `<`, `<=`, `>`, `>=`, `&&`, `||`, `=`, `;`, `,`, `.`, `(`, `)`, `[`, `]`, `{`, `}`.
- **Whitespace and Comments**: Spaces, tabs, newlines, and line comments (`// ...`) to ensure they were correctly skipped.

### 3. Invalid Tokens Test Case (`invalid.mj`)
I constructed a file with erroneous inputs such as:
- Standalone `!` (should be part of `!=`).
- Standalone `&` (should be part of `&&`).
- Standalone `|` (should be part of `||`).
- Illegal characters (e.g., `@`, `$`, `#`).
- Malformed character constants (e.g., unclosed `'x`, or too many characters in `'abc'`).

## Execution Steps
1. **Analyzed** the current `Scanner.java` and `Token.java` to extract token codes.
2. **Implemented** `ScannerTest.java`.
3. **Drafted** the test files.
4. **Compiled and Executed** the test program against the test files.

## Verification Results

### Valid Tokens Test (`valid.mj`)
The scanner correctly identified all 41 valid token types, including keywords, identifiers, numbers, and operators. Whitespace and line comments were appropriately skipped.

### Invalid Tokens Test (`invalid.mj`)
The scanner correctly returned the `none` token (code 0) for illegal characters (e.g., `@`, `#`) and incomplete operators (e.g., standalone `!`, `&`, `|`).

## Final Status
The test driver `MJ.ScannerTest` is fully implemented and verified. All required test cases have been created and successfully executed. The code and test files have been pushed to the GitHub repository.

## Usage

To compile the `Scanner` and `ScannerTest` classes:
```bash
javac MJ/*.java
```

To execute the test driver and print the token codes to the console:
```bash
java MJ.ScannerTest tests/valid.mj
java MJ.ScannerTest tests/invalid.mj
```
