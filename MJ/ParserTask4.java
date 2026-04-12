package MJ;

import java.io.*;

public class ParserTask4 {

    // ---------------------------------------------------------
    // NEW ADDITIONS (Not in the original MJ.Parser)
    // ---------------------------------------------------------
    
    // We had to manually redefine these token codes from MJ.Scanner.
    // In MJ.Scanner, these are "private static final int", which means 
    // we cannot access them outside the Scanner class using simply Scanner.ident.
    private static final int none = 0;
    private static final int ident = 1;
    private static final int number = 2;
    private static final int charCon = 3;
    private static final int lbrack = 21;
    private static final int rbrack = 22;
    private static final int lbrace = 23;
    private static final int rbrace = 24;
    private static final int assign = 25;
    private static final int semicolon = 26;
    private static final int comma = 27;
    private static final int class_ = 30;
    private static final int final_ = 32;
    private static final int program_ = 36;
    private static final int eof = 41;

    // ---------------------------------------------------------
    // COPIED / ADAPTED FROM original MJ.Parser
    // ---------------------------------------------------------
    
    private static Token la; // lookahead token

    // Adapted from MJ.Parser: reads the next token from the Scanner.
    // Notice that we omitted 'sym' as we just work with 'la.kind' directly.
    private static void scan() {
        la = Scanner.next();
    }

    // Adapted from MJ.Parser: checks if the lookahead matches the expected terminal.
    private static void check(int expected) {
        if (la.kind == expected) {
            scan();
        } else {
            error("expected token " + expected + ", but found " + la.kind);
        }
    }

    // NEW ADDITION: Panic-mode error handling.
    // The original MJ.Parser uses 'errDist' for error recovery (skips tokens until safe).
    // The instruction for Task 4 only requires panic mode, so we just print the error and exit.
    private static void error(String msg) {
        System.out.println("Syntax error at line " + la.line + ", col " + la.col + ": " + msg);
        System.exit(1); // panic mode: halt immediately on first error
    }

    // ---------------------------------------------------------
    // PARSING METHODS 
    // These structural checks trace their logical origins from the original 
    // MJ.Parser, but were STRIPPED of all semantic analysis (e.g. Tab.insert, 
    // Object generation, Code generation) to satisfy only pure grammar checking.
    // ---------------------------------------------------------

    // Program = "program" ident DeclList "{" "}".
    private static void Program() {
        check(program_);
        check(ident);
        DeclList();
        check(lbrace);
        check(rbrace);
    }

    // DeclList = {ConstDecl | VarDecl | ClassDecl} .
    private static void DeclList() {
        // FIRST(ConstDecl) = final_
        // FIRST(VarDecl) = FIRST(Type) = ident
        // FIRST(ClassDecl) = class_
        while (la.kind == final_ || la.kind == ident || la.kind == class_) {
            if (la.kind == final_) {
                ConstDecl();
            } else if (la.kind == ident) {
                VarDecl();
            } else if (la.kind == class_) {
                ClassDecl();
            }
        }
    }

    // ConstDecl = "final" Type ident "=" Literal ";" .
    private static void ConstDecl() {
        check(final_);
        Type();
        check(ident);
        check(assign);
        Literal();
        check(semicolon);
    }

    // Literal = number | charConst .
    private static void Literal() {
        if (la.kind == number) {
            scan();
        } else if (la.kind == charCon) {
            scan();
        } else {
            error("expected number or charConst, but found " + la.kind);
        }
    }

    // Type = ident [ "[" "]" ] .
    private static void Type() {
        check(ident);
        if (la.kind == lbrack) {
            scan();
            check(rbrack);
        }
    }

    // VarDecl = Type ident { "," ident} ";" .
    private static void VarDecl() {
        Type();
        check(ident);
        while (la.kind == comma) {
            scan();
            check(ident);
        }
        check(semicolon);
    }

    // ClassDecl = "class" ident "{" { VarDecl } "}" .
    private static void ClassDecl() {
        check(class_);
        check(ident);
        check(lbrace);
        while (la.kind == ident) { // FIRST(VarDecl) = ident
            VarDecl();
        }
        check(rbrace);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MJ.ParserTask4 <source_file.mj>");
            return;
        }

        try {
            Scanner.init(new FileReader(args[0]));
            scan(); // Initialize lookahead
            Program();
            if (la.kind != eof) {
                error("code found after end of program");
            }
            System.out.println("OK");
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + args[0]);
        }
    }
}
