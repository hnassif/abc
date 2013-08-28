package player;

/**
 * A class that establishes all different terminals for the grammar of an abc
 * file
 */
public class BodyToken {

    public static enum Type {

        VOICE, BAR, 
        SECTION_BEGIN, 
        SECTION_END, 
        COMMENT, 
        ACCIDENTAL, 
        NOTE_REST, 
        BEGIN_CHORD, 
        END_CHORD, 
        BEGIN_TUPLET, 
        MULT_FACTOR, 
        OCTAVE_MOD, 
        REPEAT_BEGIN, 
        REPEAT_END, 
        EXTRA_REPEAT, 
        EOF,

    }

    private final Type type; // type of BodyToken
    private final String text; // content of BodyToken

    /**
     * Constructs an BodyToken object
     * 
     * @param type
     *           the type of the Token
     * @param text
     *           the content of the Token
     */
    public BodyToken(Type type, String text) {
        this.type = type;
        this.text = text;
    }

    /**
     * Returns the text of a BodyToken object
     * 
     * @return : the text of a BodyToken object
     */
    public String getBodyTokenText() {
        return text;
    }

    /**
     * Returns the type of a BodyToken object
     * 
     * @return : the type of a BodyToken object
     */
    public Type getBodyTokenType() {
        return type;
    }

    /**
     * Checks whether two BodyTokens are equal
     * 
     * @param Obj
     *            : Object being compared
     * @return : Boolean indicating equality
     */
    @Override
    public boolean equals(Object Obj) {
        if (Obj instanceof BodyToken) {
            BodyToken CastObj = (BodyToken) Obj;
            return this.text.equals(CastObj.text)
                    && this.type.equals(CastObj.type);
        } else
            return false;
    }

    /**
     * Convert Token to a string in a custom format
     * 
     * @return String representing the Token and its value
     */
    @Override
    public String toString() {
        return "Token<" + type + ":" + text + ">";
    }
}
