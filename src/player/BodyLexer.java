package player;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.List;
import java.util.regex.Pattern;

public class BodyLexer {

    private Matcher regexExpressionMatcher;// expression matcher
    private String body; // string representing the body
    private int currentLocationIndex = 0; // current index being visited
    private List<Integer> visitedLocations; // save visited locations

    // Regular expression representing all possible tokens patterns in the abc
    // file body
    private final static String regex = "(V:[^\\n]+)|" + // VOICE
            "(%[^\\n]*)|" + // COMMENT
            "(\\[[12])|" + // EXTRA_REPEAT
            "([\\^_=]+)|" + // ACCIDENTAL
            "([a-gA-Gz])|" + // NOTE_REST
            "([1-9]*/[1-9]*|[1-9]+)|" + // MULT_FACTOR
            "([\',]+)|" + // OCTAVE_MOD
            "(\\[\\|)|" + // SECTION_BEGIN
            "(\\|])|" + // SECTION_END
            "(\\|:)|" + // REPEAT_BEGIN
            "(:\\|)|" + // REPEAT_END
            "(\\[)|" + // BEGIN_CHORD
            "(])|" + // END_CHORD
            "(\\()|" + // BEGIN_TUPLET
            "(\\|)"; // BAR

    private static final Pattern REGEX_Body_Tokens = Pattern.compile(regex);

    // Array of all possible body token types
    private static final BodyToken.Type[] Body_Tokens_Types = {
            BodyToken.Type.VOICE, BodyToken.Type.COMMENT,
            BodyToken.Type.EXTRA_REPEAT, BodyToken.Type.ACCIDENTAL,
            BodyToken.Type.NOTE_REST, BodyToken.Type.MULT_FACTOR,
            BodyToken.Type.OCTAVE_MOD, BodyToken.Type.SECTION_BEGIN,
            BodyToken.Type.SECTION_END, BodyToken.Type.REPEAT_BEGIN,
            BodyToken.Type.REPEAT_END, BodyToken.Type.BEGIN_CHORD,
            BodyToken.Type.END_CHORD, BodyToken.Type.BEGIN_TUPLET,
            BodyToken.Type.BAR, };

    /**
     * Constructs a BodyLexer from a body string Requires a valid body string
     * according to abc specifications
     * 
     * @param body
     *            : The body of the abc file represented as a string
     */
    public BodyLexer(String body) {
        this.body = body;
        this.regexExpressionMatcher = REGEX_Body_Tokens.matcher(body);
        this.visitedLocations = new ArrayList<Integer>();
    }

    /**
     * Returns the next body Token (relative to the current location the the
     * body string, which represts the content of the body of the given abc
     * file). If no content left to tokenize, returns the Token representing EOF
     * (end of file). Requires that this body is a string representation of
     * valid abc body content.
     * 
     * @return An instance of BodyToken representing the next Token in the body
     *         string or the EOF Token if no tokens left to tokenize
     */
    public BodyToken getNextBodyToken() {

        if (currentLocationIndex >= body.length()) {
            // The end of the body string has been reached
            return new BodyToken(BodyToken.Type.EOF, "");
        }
        
        // Progresses through the body string skipping all unmatched sequences,
        // until it finds a sequence that matches a token pattern
        else if (regexExpressionMatcher.find(currentLocationIndex)) {
            String matchedString;
            visitedLocations.add(currentLocationIndex);
            // Update current location in the body file
            currentLocationIndex = regexExpressionMatcher.end();
            matchedString = regexExpressionMatcher.group();
            // Finds the regex group that matched
            for (int i = 1; i <= 15; i++) {
                if (regexExpressionMatcher.group(i) != null
                        && regexExpressionMatcher.group(i)
                                .equals(matchedString)) {
                    return new BodyToken(Body_Tokens_Types[i - 1],
                            matchedString);
                }
            }
        }
        // No matching tokens left
        return new BodyToken(BodyToken.Type.EOF, "");
    }

    /**
     * Rewinds the BodyLexer to the most recent occurrence of the given string.
     * The string must not be null or ""
     * 
     * @param wanted
     *            String to which to rewind
     * @return true if the desired string was found Otherwise, returns false.
     */
    public boolean goBackUntil(String wanted) {
        int lastOccurence = body.lastIndexOf(wanted, currentLocationIndex);
        if (lastOccurence != -1) {
            currentLocationIndex = lastOccurence;
            return true;
        }
        return false;
    }

    /**
     * Rewinds back to the beginning of a sequence to be repeated. If not found,
     * sets current location index to the start of the body string.
     */
    public void goBack() {
        int startLocation = currentLocationIndex;

        while (startLocation >= 0) {
            String bodySegment = body.substring(startLocation,
                    currentLocationIndex);
            if (bodySegment.startsWith("V:") || bodySegment.startsWith("|:")
                    || bodySegment.startsWith("[|")) {
                currentLocationIndex = startLocation;
                return;
            }
            startLocation--;
        }

        currentLocationIndex = 0;
        return;

    }

    /**
     * Deletes the last BodyToken and modifies the body String to reflect the
     * removal of the given token.
     * 
     * @return true if operation carried out successfully, false otherwise
     */
    public boolean deleteLastToken() {
        if (visitedLocations.size() != 0) {
            int StartOfPreviousToken = visitedLocations.remove(visitedLocations
                    .size() - 1);
            body = body.substring(0, StartOfPreviousToken)
                    + body.substring(currentLocationIndex);
            regexExpressionMatcher = REGEX_Body_Tokens.matcher(body);
            currentLocationIndex = StartOfPreviousToken;

            return true;
        }

        return false;
    }

    /**
     * Finds the next matching Token, without modifying the lexer's current
     * location in the body String.
     * 
     * @return next BodyToken or BodyToken of type EOF
     */
    public BodyToken peek() {
        if (currentLocationIndex >= body.length()) {
            // The end of the body string has been reached
            return new BodyToken(BodyToken.Type.EOF, "");
        }
        // Peeks through the body string skipping all unmatched sequences,
        // until it finds a sequence that matches a token pattern
        if (regexExpressionMatcher.find(currentLocationIndex)) {
            String value = regexExpressionMatcher.group();
            for (int i = 1; i <= 15; i++) {
                if (regexExpressionMatcher.group(i) != null
                        && regexExpressionMatcher.group(i).equals(value)) {
                    return new BodyToken(Body_Tokens_Types[i - 1], value);
                }
            }
        }
        // No matching tokens left
        return new BodyToken(BodyToken.Type.EOF, "");
    }
}
