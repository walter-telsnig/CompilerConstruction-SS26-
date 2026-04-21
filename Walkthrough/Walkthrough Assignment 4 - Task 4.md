# Error Recovery with Synchronization Anchors

This plan outlines the approach to extend the JSON parser with a panic-mode error recovery strategy using specific synchronization anchors. This enables the parser to recover from an error and continue parsing the rest of the file, allowing it to detect multiple syntax errors in a single scan.

## Proposed Changes

We will create a new Java file, `JsonParserRecovery.java`, which will be a duplicate of the existing `JsonParser.java` but significantly enhanced with error recovery.

### Overall Error Recovery Strategy

Error recovery with anchors (synchronization sets) involves the following steps:
1. We will introduce a generalized `recover(String msg, Token... anchors)` method.
2. When a syntax error is encountered (e.g. an unexpected token), the parser will call this `recover` method instead of abruptly terminating.
3. The `recover` method will:
    - Print the error message.
    - Enter a "panic mode" loop.
    - Skip input tokens until the `lookahead` token matches one of the expected `anchors` or `EOF`.
4. We will pass specific synchronization anchors dynamically depending on the parsing context:
    - **In `parseObject`:** We expect to either see `RBRACE` (end of object) or `COMMA` / `STRING` (next pair). If parsing a pair fails, we will synchronize on `COMMA`, `RBRACE`, `EOF`.
    - **In `parsePair`:** After failing to parse a string or a colon, we can synchronize at `COMMA` or `RBRACE`.
    - **In `parseValue`:** Values usually sync on `COMMA`, `RBRACE`, or `RBRACK`.
    - **In `parseArray`:** Arrays synchronize on `RBRACK`, `COMMA`, `EOF`.

### [Component Name] Implementation Files

#### [NEW] [JsonParserRecovery.java](file:///c:/Users/User/OneDrive%20-%20Alpen-Adria%20Universit%C3%A4t%20Klagenfurt/SS26/621.400%20Compiler%20Construction%20%28SS26%29/Compiler/Assignments/Assignment%204/JsonParserRecovery.java)
- A new standalone parser class.
- Will modify the underlying `check()` structure. Instead of halting `error()`, it will initiate recovery using a token synchronisation list.
- Will maintain an `errorCount` field to exit with a non-zero code if any errors occurred during the entire parse.

#### [NEW] [invalid_recover1.json](file:///c:/Users/User/OneDrive%20-%20Alpen-Adria%20Universit%C3%A4t%20Klagenfurt/SS26/621.400%20Compiler%20Construction%20%28SS26%29/Compiler/Assignments/Assignment%204/invalid_recover1.json)
- Designed to trigger an error early on (e.g., missing a quote on a key or missing a colon).
- Followed by valid JSON properties to demonstrate that the parser continues correctly.
- Followed by yet another syntax error further down to show multiple error detection.
- Example: Missing a colon in the first pair, but the second and third pairs parse successfully, but the fourth pair misses a value.

#### [NEW] [invalid_recover2.json](file:///c:/Users/User/OneDrive%20-%20Alpen-Adria%20Universit%C3%A4t%20Klagenfurt/SS26/621.400%20Compiler%20Construction%20%28SS26%29/Compiler/Assignments/Assignment%204/invalid_recover2.json)
- Focuses on breaking array contexts.
- Example: Putting an unquoted string or an invalid keyword inside an array, followed by valid elements, then catching another error later.

## Verification Plan

### Automated Tests
- We will compile `JsonParserRecovery.java`.
- We will run `java JsonParserRecovery invalid_recover1.json` and verify the standard error output confirms two distinct, unrelated syntax errors caught on different lines, followed by reaching the end of the file.
- We will do the same testing for `invalid_recover2.json`.
