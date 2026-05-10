# Assignment 7 - Task 2

In this task, I was asked to design the changes necessary to support the type checking of floating-point numbers in MicroJava, allowing both integer and floating-point operands while explicitly preventing mixed expressions. No actual code implementation is required yet. Here is what I plan to change in `MJ.SymTab` and `MJ.Parser` before proceeding with any implementation:

## Planned Changes in `MJ.SymTab`

To support the `float` data type, I need to introduce it into the compiler's symbol table and type system.

1.  **`Struct.java`**
    *   I will add a new type constant to represent floating-point numbers: `public static final int Float = 5;`.
    *   No changes are required for the `assignableTo()` or `compatibleWith()` methods! Since they both rely on the `equals()` method (which checks if the structure `kind` matches), they will naturally allow `float` to `float` assignment and comparisons, and automatically reject mixed types (like `int` to `float`).

2.  **`Tab.java`**
    *   I will add a new predefined type variable: `public static Struct floatType;`.
    *   In the initialization block (`static { ... }`), I will initialize this new type: `floatType = new Struct(Struct.Float);`.
    *   I will insert the `float` type into the universe scope so it can be recognized as a valid type identifier in programs: `insert(Obj.Type, "float", floatType);`.
    *   I will update `dumpStruct()` and `getTypeName()` to handle `Struct.Float` for correct symbol table dumping and debugging output.

## Planned Changes in `MJ.Parser`

The syntax analyzer needs to be updated to type-check arithmetic and assignments correctly with the new type.

1.  **Expressions and Terms (`Expr()` and `Term()`)**
    *   Currently, arithmetic operations enforce integer operands: `if (x.type != Tab.intType || y.type != Tab.intType) error(...)`.
    *   I plan to change this logic to verify that both operands are of the same numeric type (either both `int` or both `float`). The check will look something like this: `if (x.type != y.type || (x.type != Tab.intType && x.type != Tab.floatType)) error("operands must be of the same numeric type");`.

2.  **Unary Minus (`Expr()`)**
    *   The unary minus currently requires an integer. I will update it to allow both integer and float: `if (x.type != Tab.intType && x.type != Tab.floatType) error("numeric operand required");`.

3.  **Method Parameters (`ActPars()`)**
    *   The logic in `ActPars()` checks `if (!ap.type.assignableTo(fp.type))`. Since `assignableTo()` correctly requires identical types for primitive values, this will automatically work for `float` parameters. No changes are needed here.

4.  **Assignments (`Statement()`)**
    *   Assignments check `if (y.type.assignableTo(x.type))`. Similar to method parameters, this will automatically succeed for `float = float` and fail for `float = int` or `int = float`. Thus, no changes are required to enforce the "no mixed expressions" rule in assignments.

5.  **Built-in Functions (`read` and `print`)**
    *   In `Statement()`, the `read` and `print` routines currently restrict arguments to `Tab.intType` or `Tab.charType`. I will expand these checks to also accept `Tab.floatType`.

By implementing these specific modifications, the compiler will be able to parse and type-check floating-point variables and expressions precisely according to the requirements, ensuring no implicit mixing of types occurs.
