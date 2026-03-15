---
description: 
---

# Walkthrough Assignment 2 - Task 6

In this task, we extended the MicroJava scanner implementation `MJ.Scanner` to adequately handle non-nested block comments enclosed by `/*` and `*/`.

## What was modified


### `MJ/Scanner.java`
- Inside the scanner's main lexical parsing loop (`public static Token next()`), we targeted the switch case handling forward slashes (`case '/'`).
- **Initial Implementation:** The scanner initially only had branching to handle single line comments if a sequential slash followed (`//`).
- **New Block Comment Logic (Visible locally inside `Scanner.java`):** We added an `if (ch == '*')` condition immediately upon discovering the first `/`. 
  Our new code spins a loop checking `while (ch != eofCh)`. Inside this loop, it consumes characters freely until it finds an asterisk (`*`) immediately adjoined by a slash (`/`).
  Once closed, `break` escapes the block comment reading loop, and `t = next()` is automatically invoked to advance the scanner past the commentary entirely to fetch the next real Token in the program.

### Tests
- **`tests/valid.mj`**: We appended a multi-line block comment spanning multiple linebreaks, immediately followed by the identifier `identAfterBlockComment`.
- **`tests/valid.mj.txt`**: We updated the ground truth integer matching file to reflect these additions properly. The block comment is correctly skipped and only the final `identAfterBlockComment` (Token integer `1`) is added to the log.

## Verification / Running

No new setup logic is needed. You can verify it accurately passes via our testing architecture built in Task 5:

```bash
# Compile
javac MJ/Scanner.java
javac MJ/ScannerTest.java

# Using standard testing behavior
java MJ.ScannerTest tests/valid.mj

# Compare automatically against ground truth
java MJ.ScannerTest tests/valid.mj tests/valid.mj.txt
```

When evaluated against the testing logs automatically, it states exactly what we desired:
> All tests passed!
