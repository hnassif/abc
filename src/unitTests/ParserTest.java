package unitTests;

import junit.framework.Assert;

import org.junit.Test;

import player.Lexer;
import player.Parser;
import player.Song;

import sound.Pitch;
import auxiliary.MultiplicativeFactor;

public class ParserTest {
    @Test
    public void testParserHeaderSingleNamedVoice() {
        Lexer lexer = new Lexer("X:9\nT:Interesting\nV:cool\nK:Am\nV:cool\nD");
        Parser parser = new Parser(lexer);

        Song song = parser.parse();
        Assert.assertEquals("Interesting", song.getSongTitle());
        Assert.assertEquals("9", song.getSongTrackNumber());
        Assert.assertEquals(song.getSongKey(), "Am");
        Assert.assertEquals(1, song.getVoiceMap().size()); // only one voice
        Assert.assertTrue(song.getVoiceMap().containsKey("cool")); // called cool
        
        Assert.assertEquals(new MultiplicativeFactor(1, 8), 
                song.getVoiceMap().get("cool").getAllChords().get(0).getAllNotes().get(0).getLength());
        Assert.assertEquals(new Pitch('D'), song.getVoiceMap().get("cool").getAllChords().get(0).getAllNotes().get(0).getPitch());
        
        Assert.assertEquals(1, song.getVoiceMap().get("cool").getAllChords().size());// we confirm the number of chords and number of elements in each
        Assert.assertEquals(1, song.getVoiceMap().get("cool").getAllChords().get(0).getAllNotes().size());

    }
    
    @Test
    public void testParserOctaveModifier() {
        Lexer lexer = new Lexer("X:0\nT:title\nK:C\nF,,,\n");
        Parser parser = new Parser(lexer);

        Song song = parser.parse();
        Assert.assertEquals("title", song.getSongTitle());
        Assert.assertEquals("0", song.getSongTrackNumber());
        Assert.assertEquals(song.getSongKey(), "C");
        Assert.assertEquals(1, song.getVoiceMap().size()); // single default voice
        Assert.assertTrue(song.getVoiceMap().containsKey("default Voice")); 
        
        Assert.assertEquals(new MultiplicativeFactor(1, 8), 
                song.getVoiceMap().get("default Voice").getAllChords().get(0).getAllNotes().get(0).getLength());
        Assert.assertEquals(new Pitch('F').octaveTranspose(-3), 
                song.getVoiceMap().get("default Voice").getAllChords().get(0).getAllNotes().get(0).getPitch());
        
        Assert.assertEquals(1, song.getVoiceMap().get("default Voice").getAllChords().size()); // we confirm the number of chords and number of elements in each
        Assert.assertEquals(1, song.getVoiceMap().get("default Voice").getAllChords().get(0).getAllNotes().size());

    }
    
    @Test
    public void testParserHigherOctave() {
        Lexer lexer = new Lexer("X:0\nT:Yay\nK:C\nc\n");
        Parser parser = new Parser(lexer);

        Song song = parser.parse();
        Assert.assertEquals("Yay", song.getSongTitle());
        Assert.assertEquals("0", song.getSongTrackNumber());
        Assert.assertEquals(song.getSongKey(), "C");
        Assert.assertEquals(1, song.getVoiceMap().size()); // Single default voice
        Assert.assertTrue(song.getVoiceMap().containsKey("default Voice")); 
        
        Assert.assertEquals(new MultiplicativeFactor(1, 8), 
                song.getVoiceMap().get("default Voice").getAllChords().get(0).getAllNotes().get(0).getLength());
        Assert.assertEquals(new Pitch('C').octaveTranspose(1), 
                song.getVoiceMap().get("default Voice").getAllChords().get(0).getAllNotes().get(0).getPitch());
        
        Assert.assertEquals(1, song.getVoiceMap().get("default Voice").getAllChords().size());// we confirm the number of chords and number of elements in each
        Assert.assertEquals(1, song.getVoiceMap().get("default Voice").getAllChords().get(0).getAllNotes().size());

    }
    

    
    @Test
    public void testParserMultipleModifiers() {
        Lexer lexer = new Lexer("X:0\nT:different\nK:Am\n^^b\n");
        Parser parser = new Parser(lexer);

        Song song = parser.parse();
        Assert.assertEquals("different", song.getSongTitle());
        Assert.assertEquals("0", song.getSongTrackNumber());
        Assert.assertEquals(song.getSongKey(), "Am");
        Assert.assertEquals(1, song.getVoiceMap().size()); // single default Voice
        Assert.assertTrue(song.getVoiceMap().containsKey("default Voice"));
        
        Assert.assertEquals(new MultiplicativeFactor(1, 8), 
                song.getVoiceMap().get("default Voice").getAllChords().get(0).getAllNotes().get(0).getLength());
        // 2double sharp
        Assert.assertEquals(new Pitch('B').octaveTranspose(1).accidentalTranspose(2), 
                song.getVoiceMap().get("default Voice").getAllChords().get(0).getAllNotes().get(0).getPitch());
        
        Assert.assertEquals(1, song.getVoiceMap().get("default Voice").getAllChords().size());// we confirm the number of chords and number of elements in each
        Assert.assertEquals(1, song.getVoiceMap().get("default Voice").getAllChords().get(0).getAllNotes().size());
    }
    
    @Test
    public void testParseDifferentRepeats() {
        Lexer lexer = new Lexer("X:8\nT:Repetition\nK:C\n|: A |[1 B :|[2 C |");
        Parser parser = new Parser(lexer);

        Song song = parser.parse();
        Assert.assertEquals("Repetition", song.getSongTitle());
        Assert.assertEquals("8", song.getSongTrackNumber());
        Assert.assertEquals(song.getSongKey(), "C");
        Assert.assertEquals(1, song.getVoiceMap().size()); // single default voice
        Assert.assertTrue(song.getVoiceMap().containsKey("default Voice")); 
        Assert.assertEquals(4, song.getVoiceMap().get("default Voice").getAllChords().size());
        
        Assert.assertEquals(1, song.getVoiceMap().get("default Voice").getAllChords().get(0).getAllNotes().size());
        Assert.assertEquals(1, song.getVoiceMap().get("default Voice").getAllChords().get(1).getAllNotes().size());
        Assert.assertEquals(1, song.getVoiceMap().get("default Voice").getAllChords().get(2).getAllNotes().size());
        Assert.assertEquals(1, song.getVoiceMap().get("default Voice").getAllChords().get(3).getAllNotes().size());

        Assert.assertEquals(new Pitch('A'), 
                song.getVoiceMap().get("default Voice").getAllChords().get(0).getAllNotes().get(0).getPitch());
        Assert.assertEquals(new MultiplicativeFactor(1, 8),
                song.getVoiceMap().get("default Voice").getAllChords().get(0).getAllNotes().get(0).getLength());

        Assert.assertEquals(new Pitch('B'), 
                song.getVoiceMap().get("default Voice").getAllChords().get(1).getAllNotes().get(0).getPitch());
        Assert.assertEquals(new MultiplicativeFactor(1, 8),
                song.getVoiceMap().get("default Voice").getAllChords().get(1).getAllNotes().get(0).getLength());

        Assert.assertEquals(new Pitch('A'), 
                song.getVoiceMap().get("default Voice").getAllChords().get(2).getAllNotes().get(0).getPitch());
        Assert.assertEquals(new MultiplicativeFactor(1, 8),
                song.getVoiceMap().get("default Voice").getAllChords().get(2).getAllNotes().get(0).getLength());
        
        Assert.assertEquals(new Pitch('C'), 
                song.getVoiceMap().get("default Voice").getAllChords().get(3).getAllNotes().get(0).getPitch());
        Assert.assertEquals(new MultiplicativeFactor(1, 8),
                song.getVoiceMap().get("default Voice").getAllChords().get(3).getAllNotes().get(0).getLength());
    }
    
    @Test
    public void testParserAccidentalsInSingleBar() {
        Lexer lexer = new Lexer("X:1984\nT:Tribute\nK:C\n^aa_a=a2\n");
        Parser parser = new Parser(lexer);

        Song song = parser.parse();
        Assert.assertEquals("Tribute", song.getSongTitle());
        Assert.assertEquals("1984", song.getSongTrackNumber());
        Assert.assertEquals(song.getSongKey(), "C");
        Assert.assertEquals(1, song.getVoiceMap().size()); // single default voice
        Assert.assertTrue(song.getVoiceMap().containsKey("default Voice"));
        
        //We have 4 "chords" (each holding a single note) made up of 1 note each
        Assert.assertEquals(4, song.getVoiceMap().get("default Voice").getAllChords().size());// we confirm the number of chords and number of elements in each
        Assert.assertEquals(1, song.getVoiceMap().get("default Voice").getAllChords().get(0).getAllNotes().size());
        Assert.assertEquals(1, song.getVoiceMap().get("default Voice").getAllChords().get(1).getAllNotes().size());
        Assert.assertEquals(1, song.getVoiceMap().get("default Voice").getAllChords().get(2).getAllNotes().size());
        Assert.assertEquals(1, song.getVoiceMap().get("default Voice").getAllChords().get(3).getAllNotes().size());
        
        // Sharp applied to first note
        Assert.assertEquals(new MultiplicativeFactor(1, 8), 
                song.getVoiceMap().get("default Voice").getAllChords().get(0).getAllNotes().get(0).getLength());
        Assert.assertEquals(new Pitch('A').octaveTranspose(1).accidentalTranspose(1),
                song.getVoiceMap().get("default Voice").getAllChords().get(0).getAllNotes().get(0).getPitch());

        // We check that the sharp still applies in this bar
        Assert.assertEquals(new MultiplicativeFactor(1, 8),
                song.getVoiceMap().get("default Voice").getAllChords().get(1).getAllNotes().get(0).getLength());
        Assert.assertEquals(new Pitch('A').octaveTranspose(1).accidentalTranspose(1),
                song.getVoiceMap().get("default Voice").getAllChords().get(1).getAllNotes().get(0).getPitch());

        // When encountering a flat we remove the sharp
        Assert.assertEquals(new MultiplicativeFactor(1, 8),
                song.getVoiceMap().get("default Voice").getAllChords().get(2).getAllNotes().get(0).getLength());
        Assert.assertEquals(new Pitch('A').octaveTranspose(1).accidentalTranspose(-1),
                song.getVoiceMap().get("default Voice").getAllChords().get(2).getAllNotes().get(0).getPitch());

        // a is neutralised (or terminated if you are a fan of the Governator!)
        Assert.assertEquals(new MultiplicativeFactor(1, 4),
                song.getVoiceMap().get("default Voice").getAllChords().get(3).getAllNotes().get(0).getLength());
        Assert.assertEquals(new Pitch('A').octaveTranspose(1),
                song.getVoiceMap().get("default Voice").getAllChords().get(3).getAllNotes().get(0).getPitch());
    }
    
    @Test
    public void testParseHalfNote() {
        Lexer lexer = new Lexer("X:666\nT:NumberOfTheBeast\nK:C\nb/2\n");
        Parser parser = new Parser(lexer);

        Song song = parser.parse();
        Assert.assertEquals("NumberOfTheBeast", song.getSongTitle());
        Assert.assertEquals("666", song.getSongTrackNumber());
        Assert.assertEquals(song.getSongKey(), "C");
        Assert.assertEquals(1, song.getVoiceMap().size()); // single default voice
        Assert.assertTrue(song.getVoiceMap().containsKey("default Voice"));
        
        //default length is 1/8, so half note is 1/16
        Assert.assertEquals(new MultiplicativeFactor(1, 16), 
                song.getVoiceMap().get("default Voice").getAllChords().get(0).getAllNotes().get(0).getLength());
        Assert.assertEquals(new Pitch('B').octaveTranspose(1), 
                song.getVoiceMap().get("default Voice").getAllChords().get(0).getAllNotes().get(0).getPitch());
        
        Assert.assertEquals(1, song.getVoiceMap().get("default Voice").getAllChords().size());// we confirm the number of chords and number of elements in each
        Assert.assertEquals(1, song.getVoiceMap().get("default Voice").getAllChords().get(0).getAllNotes().size());

    }

}
