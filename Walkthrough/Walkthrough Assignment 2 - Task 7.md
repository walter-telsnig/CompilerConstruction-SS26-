# Walkthrough: Implementing Power Operator (**) in MicroJava Scanner

This document explains the steps I took to extend the MicroJava Scanner to support the `**` token for power operations.

## 1. Defining the Token Constant
To recognize the new `**` token, I first needed to define an explicit representation for it in the scanner token enumerations. In `MJ/Scanner.java` at line 55, I appended `power = 42;`:
```diff
-		eof       = 41; // end-of-file token
+		eof       = 41, // end-of-file token
+		power     = 42; // **
```

## 2. Extending the Scanner Logic
MicroJava already processes `*` as the `times` operation token. To introduce `**`, I modified the scanning logic for `*` inside the `next()` method of `Scanner.java` (around line 134). By extending `case '*'`, the scanner looks ahead by one character using `nextCh()`: if the subsequent character is another `*`, the scanner consumes it and sets the kind to `power`. Otherwise, it resolves to `times`. I also added descriptive comments at the site of this code change per the assignment instructions.
```diff
-			case '*':
-				t.kind = times; nextCh();
-				break;
+			case '*':
+				nextCh();
+				// Assignment extension: Check for power token (**)
+				if (ch == '*') { t.kind = power; nextCh(); }
+				else t.kind = times;
+				break;
```

## 3. Extending the Test Cases
To test my implementation without removing any existing `times` tests, I extended `tests/valid.mj` to include `x ** y` on a new line at the end of the file.
```diff
+x ** y
```
This string yields three specific tokens: `ident` (token 1), `power` (token 42), and `ident` (token 1). I mirrored these expected tokens by updating `tests/valid.mj.txt` to inject them right before the concluding `eof` token (41).

## 4. Verification
The changes were successfully evaluated, and the output matched seamlessly, yielding: `All tests passed!`.

## Usage

To compile the updated Scanner:
```bash
javac MJ/*.java
```

To run the test and compare against the expected tokens:
```bash
java MJ.ScannerTest tests/valid.mj tests/valid.mj.txt
```
