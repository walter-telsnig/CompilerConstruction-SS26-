import java.io.*;

public class JsonParser {

    // --- Scanner ---
    public enum Token {
        LBRACE, RBRACE, LBRACK, RBRACK, COMMA, COLON, STRING, NULL_LIT, EOF, ERROR
    }

    private static Reader reader;
    private static int currChar;
    
    // Lookahead token
    private static Token la;
    
    // For error reporting
    private static int line = 1;
    private static int col = 0;

    private static void nextChar() {
        try {
            currChar = reader.read();
            col++;
            if (currChar == '\n') {
                line++;
                col = 0;
            }
        } catch (IOException e) {
            currChar = -1;
        }
    }

    private static void initScanner(String filePath) throws FileNotFoundException {
        reader = new FileReader(filePath);
        line = 1;
        col = 0;
        nextChar();
        scan(); // prepopulate lookahead
    }

    private static void scan() {
        // Skip whitespace
        while (currChar != -1 && Character.isWhitespace(currChar)) {
            nextChar();
        }

        if (currChar == -1) {
            la = Token.EOF;
            return;
        }

        switch (currChar) {
            case '{':
                la = Token.LBRACE;
                nextChar();
                break;
            case '}':
                la = Token.RBRACE;
                nextChar();
                break;
            case '[':
                la = Token.LBRACK;
                nextChar();
                break;
            case ']':
                la = Token.RBRACK;
                nextChar();
                break;
            case ',':
                la = Token.COMMA;
                nextChar();
                break;
            case ':':
                la = Token.COLON;
                nextChar();
                break;
            case '"':
                readString();
                break;
            default:
                if (Character.isLetter(currChar)) {
                    readIdentifier();
                } else {
                    la = Token.ERROR;
                    nextChar();
                }
        }
    }

    private static void readString() {
        nextChar(); // skip opening quote
        while (currChar != -1 && currChar != '"') {
            nextChar();
        }
        if (currChar == '"') {
            nextChar(); // skip closing quote
            la = Token.STRING;
        } else {
            la = Token.ERROR; // Unclosed string
        }
    }

    private static void readIdentifier() {
        StringBuilder sb = new StringBuilder();
        while (currChar != -1 && Character.isLetter(currChar)) {
            sb.append((char) currChar);
            nextChar();
        }
        if (sb.toString().equals("null")) {
            la = Token.NULL_LIT;
        } else {
            la = Token.ERROR; // Only "null" is allowed as an identifier here
        }
    }

    // --- Parser ---
    
    private static void check(Token expected) {
        if (la == expected) {
            scan();
        } else {
            error("Expected " + expected + ", but found " + la);
        }
    }

    private static void error(String msg) {
        System.err.println("Syntax Error at line " + line + ", col " + col + ": " + msg);
        throw new RuntimeException("Parse failed");
    }

    // Object = "{" [ Pair { "," Pair } ] "}".
    private static void parseObject() {
        check(Token.LBRACE);
        if (la == Token.STRING) {
            parsePair();
            while (la == Token.COMMA) {
                scan(); // consume ','
                parsePair();
            }
        }
        check(Token.RBRACE);
    }

    // Pair = string ":" Value.
    private static void parsePair() {
        check(Token.STRING);
        check(Token.COLON);
        parseValue();
    }

    // Value = string | "null" | Object | Array.
    private static void parseValue() {
        if (la == Token.STRING) {
            scan();
        } else if (la == Token.NULL_LIT) {
            scan();
        } else if (la == Token.LBRACE) {
            parseObject();
        } else if (la == Token.LBRACK) {
            parseArray();
        } else {
            error("Invalid start of Value: " + la);
        }
    }

    // Array = "[" [ Value { "," Value } ] "]".
    private static void parseArray() {
        check(Token.LBRACK);
        if (la == Token.STRING || la == Token.NULL_LIT || la == Token.LBRACE || la == Token.LBRACK) {
            parseValue();
            while (la == Token.COMMA) {
                scan();
                parseValue();
            }
        }
        check(Token.RBRACK);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java JsonParser <input.json>");
            return;
        }
        
        try {
            initScanner(args[0]);
            parseObject(); 
            if (la != Token.EOF) {
                error("Extra tokens found after object: " + la);
            }
            System.out.println("File '" + args[0] + "' parsed successfully!");
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + args[0]);
        } catch (RuntimeException e) {
            // Error already printed, exit with non-zero code to indicate failure
            System.exit(1);
        }
    }
}
