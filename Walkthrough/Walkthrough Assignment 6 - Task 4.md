# Implementation Walkthrough: Task 4 Symbol Table Dump

This walkthrough documents the successful implementation of the symbol table dump feature for the MicroJava Compiler, meeting the requirements of Assignment 6, Task 4.

## Goal
The objective was to extend the MicroJava Compiler to output its symbol table using the `MJ.SymTab` package when a `--symdump` argument is supplied. Specifically, the compiler needed to display variables and constants accessible from the `main` method's scope just before its termination, printing details such as type, level, address, value, and class fields.

## Implementation Details

### 1. Adding the `--symdump` Argument
I modified the `main` method in `MJ/Compiler.java` to parse command-line arguments. The code now iterates through the arguments, setting a new static boolean flag `symdump` if the `--symdump` flag is found, and treats the last non-flag argument as the input source file.

> [!NOTE]
> `Compiler.symdump` is stored globally so other components of the compiler (such as the `Parser`) can easily access it to decide whether to output the symbol table.

### 2. Creating the `printSymTabPretty` Method
I added the `printSymTabPretty` method inside `MJ/SymTab/Tab.java`. This method:
- Retrieves the `main` method's local scope (`Tab.curScope`) and the global scope (`Tab.curScope.outer`).
- Iterates through the objects declared in these scopes.
- Filters and prints only `Obj.Var` and `Obj.Con` types.
- If an `Obj.Var` has `Struct.Class` as its type, I additionally looped through its fields and printed them indented.
- Uses a helper function `getTypeName` to ensure clean, padded output (e.g., `"Int  "`, `"Char "`, `"Class"`).

### 3. Hooking into the Parser
To execute `printSymTabPretty` precisely before the `main` method terminates, I injected a hook into `MJ/Parser.java`'s `MethodDecl()` method. Right before `Tab.closeScope()` is called at the end of a method declaration block, it checks if `Compiler.symdump` is true and if the currently parsed method is `"main"`. If so, it invokes `Tab.printSymTabPretty()`.

## Verification

I created a test file `test_symdump.mj` containing the exact example code from the assignment requirements:

```java
program P 
final char a = 'a'; 
int b; 
int[] c; 
class A { char[] f; } 
class B { int g; char h; } 
{ 
    void main() 
    A v; B x; 
    {   } 
    void p() 
    A q; 
    {   } 
}
```

I successfully compiled the modified source files using `build.bat` and ran the compiler with the new flag:
`java MJ.Compiler --symdump test_symdump.mj`

The output accurately matched the requested specification:
```text
Var: v Type: Class Level: 1  Address: 0
- Field:f Type: Arr   Level: 1  Address: 0
Var: x Type: Class Level: 1  Address: 1
- Field:g Type: Int   Level: 1  Address: 0
- Field:h Type: Char  Level: 1  Address: 1
Con: a Type: Char  Level: 0  Value: 97
Var: b Type: Int   Level: 0  Address: 0
Var: c Type: Arr   Level: 0  Address: 1
```

> [!SUCCESS]
> The requirements for Task 4 have been fully met, and the symbol table structures are dumping correctly at the end of the main method!
