import java.io.*;
import java.util.EnumSet;
import java.util.Arrays;

public class JsonParserRecovery {

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
    
    // Error tracking
    private static int errorCount = 0;

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
            case '{':  la = Token.LBRACE; nextChar(); break;
            case '}':  la = Token.RBRACE; nextChar(); break;
            case '[':  la = Token.LBRACK; nextChar(); break;
            case ']':  la = Token.RBRACK; nextChar(); break;
            case ',':  la = Token.COMMA; nextChar(); break;
            case ':':  la = Token.COLON; nextChar(); break;
            case '"':  readString(); break;
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
            la = Token.ERROR; // Only "null" is allowed
        }
    }

    // --- Parser with Panic-Mode Error Recovery ---
    
    private static void error(String msg) {
        System.err.println("Syntax Error at line " + line + ", col " + col + ": " + msg);
        errorCount++;
    }

    private static void recover(String msg, Token... anchors) {
        error(msg);
        EnumSet<Token> syncSet = EnumSet.noneOf(Token.class);
        syncSet.addAll(Arrays.asList(anchors));
        syncSet.add(Token.EOF); // Fallback to avoid infinite loops
        
        while (!syncSet.contains(la)) {
            scan();
        }
    }

    private static boolean check(Token expected, Token... anchors) {
        if (la == expected) {
            scan();
            return true;
        } else {
            recover("Expected " + expected + ", but found " + la, anchors);
            return false;
        }
    }

    // Object = "{" [ Pair { "," Pair } ] "}".
    private static void parseObject(Token... anchors) {
        check(Token.LBRACE, Token.STRING, Token.RBRACE, Token.EOF);
        
        if (la == Token.STRING || la == Token.ERROR || la == Token.COMMA) {
            parsePair(Token.COMMA, Token.RBRACE);
            while (la == Token.COMMA || la == Token.STRING || la == Token.ERROR) {
                if (la == Token.COMMA) {
                    scan(); 
                } else {
                    recover("Expected COMMA between pairs, but found " + la, Token.STRING, Token.RBRACE);
                    if (la == Token.RBRACE) break; // If we synced on RBRACE, stop trying to parse pairs
                }
                
                if (la != Token.RBRACE) {
                    parsePair(Token.COMMA, Token.RBRACE);
                }
            }
        }
        
        EnumSet<Token> anchorSet = EnumSet.noneOf(Token.class);
        anchorSet.addAll(Arrays.asList(anchors));
        anchorSet.add(Token.EOF);
        check(Token.RBRACE, anchorSet.toArray(new Token[0]));
    }

    // Pair = string ":" Value.
    private static void parsePair(Token... anchors) {
        check(Token.STRING, Token.COLON, Token.COMMA, Token.RBRACE);
        
        if (la == Token.COLON) {
            scan();
        } else {
            // Panic mode: missing colon, synchronize up to start of Value or anchors
            EnumSet<Token> valueStarts = EnumSet.of(Token.STRING, Token.NULL_LIT, Token.LBRACE, Token.LBRACK);
            valueStarts.addAll(Arrays.asList(anchors));
            recover("Expected COLON between key and value, but found " + la, valueStarts.toArray(new Token[0]));
        }
        
        parseValue(anchors);
    }

    // Value = string | "null" | Object | Array.
    private static void parseValue(Token... anchors) {
        if (la == Token.STRING) {
            scan();
        } else if (la == Token.NULL_LIT) {
            scan();
        } else if (la == Token.LBRACE) {
            parseObject(anchors);
        } else if (la == Token.LBRACK) {
            parseArray(anchors);
        } else {
            recover("Invalid start of Value: " + la, anchors);
        }
    }

    // Array = "[" [ Value { "," Value } ] "]".
    private static void parseArray(Token... anchors) {
        check(Token.LBRACK, Token.STRING, Token.NULL_LIT, Token.LBRACE, Token.LBRACK, Token.RBRACK, Token.EOF);
        
        if (la == Token.STRING || la == Token.NULL_LIT || la == Token.LBRACE || la == Token.LBRACK || la == Token.ERROR || la == Token.COMMA) {
            parseValue(Token.COMMA, Token.RBRACK);
            while (la == Token.COMMA || la == Token.STRING || la == Token.NULL_LIT || la == Token.LBRACE || la == Token.LBRACK || la == Token.ERROR) {
                if (la == Token.COMMA) {
                    scan();
                } else {
                    recover("Expected COMMA between array elements, but found " + la, Token.STRING, Token.NULL_LIT, Token.LBRACE, Token.LBRACK, Token.RBRACK);
                    if (la == Token.RBRACK) break;
                }
                
                if (la != Token.RBRACK) {
                    parseValue(Token.COMMA, Token.RBRACK);
                }
            }
        }
        
        EnumSet<Token> anchorSet = EnumSet.noneOf(Token.class);
        anchorSet.addAll(Arrays.asList(anchors));
        anchorSet.add(Token.EOF);
        check(Token.RBRACK, anchorSet.toArray(new Token[0]));
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java JsonParserRecovery <input.json>");
            return;
        }
        
        try {
            initScanner(args[0]);
            parseObject(Token.EOF); 
            if (la != Token.EOF) {
                recover("Extra tokens found after object: " + la, Token.EOF);
            }
            if (errorCount == 0) {
                System.out.println("File '" + args[0] + "' parsed successfully without errors.");
            } else {
                System.err.println("File '" + args[0] + "' parsed with " + errorCount + " syntax error(s).");
                System.exit(1);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + args[0]);
        }
    }
}
