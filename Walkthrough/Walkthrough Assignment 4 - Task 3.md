# Implementation Plan: Standalone JSON Parser

This plan outlines the design and implementation approach for building an executable Java program that contains both a scanner and a recursive-descent parser for a specified JSON-like grammar.

## Goal Description

The goal is to parse a simplified JSON grammar strictly from source text without relying on external libraries or the existing `MJ.Scanner` / `MJ.Parser` infrastructure. The implemented program must be able to lex text into basic tokens and structurally validate the sequence through a set of mutually recursive Java methods mapping to the grammar rules.

### Target Grammar
- `Object = "{" [ Pair { "," Pair } ] "}".`
- `Pair = string ":" Value.`
- `Value = string | "null" | Object | Array.`
- `Array = "[" [ Value { "," Value } ] "]".`

## Proposed Changes

We will create a single executable Java file, `JsonParser.java`, which encapsulates state and logic for both lexical analysis (scanning) and syntactic analysis (parsing).

### [Component Name] Implementation Strategy

#### [NEW] [JsonParser.java](file:///c:/Users/User/OneDrive%20-%20Alpen-Adria%20Universit%C3%A4t%20Klagenfurt/SS26/621.400%20Compiler%20Construction%20%28SS26%29/Compiler/Assignments/Assignment%204/JsonParser.java)

**1. The Scanner**
- An internal `Token` enum will define token types: `LBRACE`, `RBRACE`, `LBRACK`, `RBRACK`, `COMMA`, `COLON`, `STRING`, `NULL_LIT`, `EOF`, and `ERROR`.
- `currChar` will trace the current byte from the `FileReader`.
- A method `scan()` will skip whitespace, map specific punctuation characters to their token representations, consume quote-enclosed strings, and identify `null` literals. 
- Any unrecognized characters or unclosed strings actuate an `ERROR` state.
- Line and column numbers will be tracked for diagnostic reporting.

**2. The Parser**
- Built on top of a "lookahead" architecture where `la` (lookahead token) informs path branching.
- A generic `check(Token expected)` method will enforce token sequence expectations. If expectation matches lookahead, the token is consumed. Otherwise, a `RuntimeException` via `error(msg)` halts execution immediately (standard fail-fast approach without advanced recovery).
- Four specific parser methods will be implemented sequentially according to standard compiler construction principles:
  - `parseObject()`: Enforces curly braces and handles bounded loops for zero-to-many `Pair`s separated by commas.
  - `parsePair()`: Demands a string, a colon, and delegates to value evaluation.
  - `parseValue()`: Evaluates the initial token type to conditionally jump to terminating variables (string/null) or further structural blocks (objects/arrays).
  - `parseArray()`: Enforces square brackets and handles arbitrary length iterations of internal `Value`s separated by commas.

**3. Execution & Testing Files**
- A series of valid mapping files (`valid1.json`, `valid2.json`) and broken mapping files (`invalid1.json`, `invalid2.json`, `invalid3.json`) must be placed in the target assignment folder to formally prove execution capabilities across edge cases.

## Verification Plan

### Automated Tests
- Command execution mapping:
  - `javac JsonParser.java`
  - `java JsonParser valid1.json` -> Expected output: Success.
  - `java JsonParser invalid1.json` -> Expected output: Syntax Error (mismatched terminal token).
- Manual confirmation ensuring that the parser triggers line-accurate errors specifically when expected punctuation (such as `COLON` in `Pair` or string inside `Array`) is fundamentally broken.
