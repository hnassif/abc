package unitTests;

import static org.junit.Assert.*;

import org.junit.Test;

import player.BodyLexer;
import player.BodyToken;

public class BodyLexerTest {
    
    /**
     * Test that a tuplet is lexed correctly into its constituent tokens
     */
    @Test
    public void tupletTest() {
        BodyLexer lexer = new BodyLexer("(3CCC");
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.BEGIN_TUPLET, "("));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.MULT_FACTOR, "3"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "C"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "C"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "C"));
    }
    
    /**
     * Test that a chord is lexed correctly into its constituent tokens
     */
    @Test
    public void chordTest() {
        BodyLexer lexer = new BodyLexer("[FA]");
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.BEGIN_CHORD, "["));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "F"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "A"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.END_CHORD, "]"));    
    }
    
    /**
     * Test that repeats are lexed correctly into their constituent tokens
     */
    @Test
    public void repeatTest() {
        BodyLexer lexer = new BodyLexer("|: C D E |[1 G a b :|[2 f d C |");
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.REPEAT_BEGIN, "|:"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "C"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "D"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "E"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.BAR, "|"));
             
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.EXTRA_REPEAT, "[1"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "G"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "a"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "b"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.REPEAT_END, ":|"));
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.EXTRA_REPEAT, "[2"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "f"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "d"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "C"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.BAR, "|"));
    }
    
    /**
     * Test that voices are lexed correctly into their constituent tokens
     */
    @Test
    public void voiceTest() {
        BodyLexer lexer = new BodyLexer("V: upper\n z2 Gc V:middle \n E7 |]");
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.VOICE, "V: upper"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "z"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.MULT_FACTOR, "2"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "G"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "c"));
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.VOICE, "V:middle "));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "E"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.MULT_FACTOR, "7"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.SECTION_END, "|]")); 
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.EOF, ""));  
    }
    
    /**
     * Test that accidentals are lexed correctly into their constituent tokens
     */
    @Test
    public void accidentalTest() {
        BodyLexer lexer = new BodyLexer("_B =A ^G __b ^^g");
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.ACCIDENTAL, "_"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "B"));

        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.ACCIDENTAL, "="));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "A"));
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.ACCIDENTAL, "^"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "G"));
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.ACCIDENTAL, "__"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "b"));
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.ACCIDENTAL, "^^"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "g"));
    }
    
    /**
     * Test that note length factors are correctly into their constituent tokens
     */
    @Test
    public void noteLengthTest() {
        BodyLexer lexer = new BodyLexer("B/2 a3/4 g3 z/ z");
    
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "B"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.MULT_FACTOR, "/2"));
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "a"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.MULT_FACTOR, "3/4"));
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "g"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.MULT_FACTOR, "3"));
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "z"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.MULT_FACTOR, "/"));
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "z"));
    }
    
    /**
     * Test that octave shifts are lexed correctly into their constituent tokens
     */
    @Test
    public void octaveTest() {
        BodyLexer lexer = new BodyLexer("A,8 a,, C' c''/");
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "A"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.OCTAVE_MOD, ","));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.MULT_FACTOR, "8"));
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "a"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.OCTAVE_MOD, ",,"));
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "C"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.OCTAVE_MOD, "'"));
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "c"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.OCTAVE_MOD, "''"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.MULT_FACTOR, "/"));
    }
    
    /**
     * Test that peek() works as expected by finding the next matching sequence, 
     * without modifying the lexer's current location in the body string
     */
    @Test
    public void peekTest() {
        BodyLexer lexer = new BodyLexer("[| A");
        
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.SECTION_BEGIN, "[|"));
        assertEquals(lexer.peek(), new BodyToken(BodyToken.Type.NOTE_REST, "A"));
        assertEquals(lexer.getNextBodyToken(), new BodyToken(BodyToken.Type.NOTE_REST, "A"));
        assertEquals(lexer.peek(), new BodyToken(BodyToken.Type.EOF, "")); 
    }
}
    
    