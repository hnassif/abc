package player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    private final HeadLexer headerLexer;
    private final BodyLexer bodyLexer;

    private static final Pattern SEPARATOR = Pattern.compile ("K:[^\\n]*"); // separation between header and body is the KEY field in the Header
    
    /**
     * Separates abc file into two strings: 
     * (1) the Header, which contains various metadata about the musical piece.
     * (2) the Body, which contains a sequence of notes that make up the musical piece.
     * @param fileString a String specifying the abc music file
     * @throws IllegalArgumentException if the Key field is missing from the abc file header
     */
    public Lexer (String fileString) {
        Matcher matcher = SEPARATOR.matcher(fileString);

        if(!matcher.find()) {
            throw new IllegalArgumentException("Key field missing from abc file");
        }
        else {
            this.headerLexer = new HeadLexer (fileString.substring(0, matcher.end())); // constructs the header map.
            this.bodyLexer = new BodyLexer (fileString.substring(matcher.end())); // proceeds to tokenize the body for parsing
        }
    }

    
    /**
     * Getter method for HeadLexer
     * @return headerLexer
     */
    public HeadLexer getHeaderLexer() {
        return headerLexer;
    }

    /**
     * Returns the next BodyToken encountered in the body. Mutates lexer.
     * @return BodyToken specifying the next encountered grouping in the body
     */
    public BodyToken getNextToken() { // since the parser only has access to the lexer, we have the method here to call getNextBodyToken in the bodyLexer
        return bodyLexer.getNextBodyToken();
    }

    /**
     * Returns the next BodyToken encountered in the body. Does not mutate lexer.
     * Has same functionality as Java Stack peek() method.
     * @return BodyToken specifying the next encountered grouping in the body
     */
    public BodyToken peekToken() {
        return bodyLexer.peek(); // don't remove the next token.
    }

    /**
     * Takes the BodyLexer back to where it last encountered majorSection
     * in the body before the current index. Mutates the state of the lexer.
     * 
     * @param majorSection - String to be matched
     * @return true if majorSection was found in the BodyLexer
     */
    public boolean goBackUntil(String majorSection) {
        return bodyLexer.goBackUntil(majorSection);
    }

    /**
     * Takes the BodyLexer back to to where it last encountered a begin Repeat
     *   delimiter before the current index in the body
     */
    public void goBack() {
        bodyLexer.goBack();
    }

    /**
     * Deletes the most recent returned BodyToken from the body
     */
    public boolean deleteLastToken() {
        return bodyLexer.deleteLastToken();
    }

}
