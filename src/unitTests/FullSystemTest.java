package unitTests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.junit.Test;

import auxiliary.MultiplicativeFactor;

import player.AugmentedMIDI;
import player.Lexer;
import player.Main;
import player.MusicVisitor;
import player.Parser;
import player.Song;
import sound.SequencePlayer;
import sound.Pitch;

/**
 * These tests are designed to see if a file can propagate through the 
 * entire project and yield the correct output in terms of AugmentedMIDI note sequences
 *
 */
public class FullSystemTest {
	
	/**@category no_didit
	 * Method that replicates main. Reads, lexes, and parses file as call from main would.
	 * Returns output produced by AST without calling play() to play the music
	 * 
	 * @param filePath, String indicating the file path to the location of the test abc file
	 * @return instance of SequencePlayer representing the AugmentedMIDI notes in the abc file
	 */

	private SequencePlayer getSequencePlayer(String filePath){

		String in = Main.readFileIntoString(filePath);	   
		Lexer lex = new Lexer(in);
		Parser parser = new Parser(lex);

		Song song = parser.parse();        
		List<AugmentedMIDI> notes = MusicVisitor.getMIDINotes(song, 0, song.getTicksForNote());

		SequencePlayer seqPlayer;
		try {
			int initTempo = song.getSongTempo(); 
			MultiplicativeFactor initLength = song.getDefaultNoteLength();
			int quarterTempo = (4 * initTempo * initLength.getNum()) / initLength.getDenom();

			seqPlayer = new SequencePlayer(quarterTempo,(int) song.getTicksForNote()/4);

			for(AugmentedMIDI note : notes) {
				seqPlayer.addNote(note.getPitch(), note.getStart(), note.getDuration());
			}            
			return seqPlayer;

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Player Not initialised. Invalid data given");

	}
	
	@Test
	/**
	 * @category no_didit
	 */
	public void noteTest() {
		SequencePlayer seqPlayer;
		try {
			seqPlayer = new SequencePlayer(50, 2);

			// The note in the file is C
			seqPlayer.addNote(new Pitch('C').toMidiNote(), 0, 1);

			assertEquals(getSequencePlayer("sample_abc/noteTest.abc").toString(), seqPlayer.toString());
			return;

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Player Not initialised. Invalid data given");
	}

	@Test
	/**
	 * @category no_didit
	 */
	public void chordTest() {
		SequencePlayer seqPlayer;
		try {
			seqPlayer = new SequencePlayer(50, 2);

			// Chord in file: [CGb]
			seqPlayer.addNote(new Pitch('C').toMidiNote(), 0, 1); // C
			seqPlayer.addNote(new Pitch('G').toMidiNote(), 0, 1); // G
			seqPlayer.addNote(new Pitch('B').octaveTranspose(1).toMidiNote(), 0, 1); // b

			assertEquals(getSequencePlayer("sample_abc/chordTest.abc").toString(), seqPlayer.toString());
			return;

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

		throw new RuntimeException("Player Not initialised. Invalid data given");
	}
	
	@Test
	/**
	 * @category no_didit
	 */
	public void tupletTest() {
		SequencePlayer seqPlayer;
		try {
			seqPlayer = new SequencePlayer(50, 3);

			// Notes in File: (3GCA
			seqPlayer.addNote(new Pitch('G').toMidiNote(), 0, 2); //tuplets play 2 in 3 when we have 3 notes
			seqPlayer.addNote(new Pitch('C').toMidiNote(), 2, 2); 
			seqPlayer.addNote(new Pitch('A').toMidiNote(), 4, 2); 

			assertEquals(getSequencePlayer("sample_abc/tupletTest.abc").toString(), seqPlayer.toString());
			return;

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

		throw new RuntimeException("Player Not initialised. Invalid data given");
	}
	
	@Test
	/**
	 * @category no_didit
	 */
	public void sharpModifierTest() {
		
		SequencePlayer seqPlayer;
		try {
			seqPlayer = new SequencePlayer(50, 2);

			// Note in file: ^B
			seqPlayer.addNote(new Pitch('B').accidentalTranspose(1).toMidiNote(), 0, 1);
			
			assertEquals(getSequencePlayer("sample_abc/sharpModifierTest.abc").toString(), seqPlayer.toString());
			return;
			
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Player Not initialised. Invalid data given");
	}

	@Test
	/**
	 * @category no_didit
	 */
	public void flatModifierTest() {
		SequencePlayer seqPlayer;
		try {
			seqPlayer = new SequencePlayer(50, 2);

			// Note in File _F
			seqPlayer.addNote(new Pitch('F').accidentalTranspose(-1).toMidiNote(), 0, 1);

			assertEquals(getSequencePlayer("sample_abc/flatModifierTest.abc").toString(), seqPlayer.toString());
			return;

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Player Not initialised. Invalid data give");
	}

	@Test
	/**
	 * @category no_didit
	 */
	public void naturalModifierTest() {
		SequencePlayer player;
		try {
			player = new SequencePlayer(50, 2);

			// Note in File: =C
			player.addNote(new Pitch('C').toMidiNote(), 0, 1);

			assertEquals(getSequencePlayer("sample_abc/naturalModifierTest.abc").toString(), player.toString());
			return;

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

		throw new RuntimeException("Player Not initialised. Invalid data given");
	}
	
	@Test
	/**
	 * @category no_didit
	 */
	public void doubleModifierTest() {
		SequencePlayer seqPlayer;
		try {
			seqPlayer = new SequencePlayer(50, 2);

			//Note in File:  ^^A
			seqPlayer.addNote(new Pitch('A').accidentalTranspose(2).toMidiNote(), 0, 1);

			assertEquals(getSequencePlayer("sample_abc/doubleModifierTest.abc").toString(), seqPlayer.toString());
			return;

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Player Not initialised. Invalid data given");
	}

	@Test
	/**
	 * @category no_didit
	 */
	public void higherOctaveTest() {
		SequencePlayer seqPlayer;
		try {
			seqPlayer = new SequencePlayer(50, 2);

			// Note in File: e
			seqPlayer.addNote(new Pitch('E').octaveTranspose(1).toMidiNote(), 0, 1);

			assertEquals(getSequencePlayer("sample_abc/higherOctaveTest.abc").toString(), seqPlayer.toString());
			return;

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

		throw new RuntimeException("Player Not initialised. Invalid data given");
	}
	
	@Test
	/**
	 * @category no_didit
	 */
	public void doubleOctaveModifierTest() {
		SequencePlayer seqPlayer;
		try {
			seqPlayer = new SequencePlayer(50, 2);

			// Note in File: g'
			seqPlayer.addNote(new Pitch('G').octaveTranspose(2).toMidiNote(), 0, 1);

			assertEquals(getSequencePlayer("sample_abc/doubleOctaveModifierTest.abc").toString(), seqPlayer.toString());
			return;

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

		throw new RuntimeException("Player Not initialised. Invalid data given");
	}

	@Test
	/**
	 * @category no_didit
	 */
	public void lengthModifierTest() {
		SequencePlayer seqPlayer;
		try {
			seqPlayer = new SequencePlayer(100, 2);

			// Notes In File: L:1/4, B/2
			seqPlayer.addNote(new Pitch('B').toMidiNote(), 0, 1);

			assertEquals(getSequencePlayer("sample_abc/lengthModifierTest.abc").toString(), seqPlayer.toString());
			return;

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

		throw new RuntimeException("Player Not initialised. Invalid data given");
	}
	
	@Test
	/**
	 * @category no_didit
	 */
	public void repeatTest() {
		SequencePlayer player;
		try {
			player = new SequencePlayer(50, 2);

			// |: B :|
			player.addNote(new Pitch('B').toMidiNote(), 0, 1);
			player.addNote(new Pitch('B').toMidiNote(), 1, 1); 

			assertEquals(getSequencePlayer("sample_abc/repeatTest.abc").toString(), player.toString());
			return;

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

		throw new RuntimeException("Player Not initialised. Invalid data given");
	}
	
	@Test
	/**
	 * @category no_didit
	 */
	public void missingRepeatSignTest() {
		SequencePlayer seqPlayer;
		try {
			seqPlayer = new SequencePlayer(50, 2);

			// Notes in File: L:1/4, A B C D :|
			seqPlayer.addNote(new Pitch('A').toMidiNote(), 0, 1); // A
			seqPlayer.addNote(new Pitch('B').toMidiNote(), 1, 1); // B
			seqPlayer.addNote(new Pitch('C').toMidiNote(), 2, 1); // C
			seqPlayer.addNote(new Pitch('D').toMidiNote(), 3, 1); // D

			seqPlayer.addNote(new Pitch('A').toMidiNote(), 4, 1); // A
			seqPlayer.addNote(new Pitch('B').toMidiNote(), 5, 1); // B
			seqPlayer.addNote(new Pitch('C').toMidiNote(), 6, 1); // C
			seqPlayer.addNote(new Pitch('D').toMidiNote(), 7, 1); // D

			assertEquals(getSequencePlayer("sample_abc/missingRepeatSignTest.abc").toString(), seqPlayer.toString());
			return;

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

		throw new RuntimeException("Player Not initialised. Invalid data given");
	}
	
	@Test
	/**
	 * @category no_didit
	 */
	public void differentRepeatTest() {
		SequencePlayer seqPlayer;
		try {
			seqPlayer = new SequencePlayer(50, 2);

			// Notes in File: |: a |[1 B :|[2 C, |
			seqPlayer.addNote(new Pitch('A').octaveTranspose(1).toMidiNote(), 0, 1); //a
			seqPlayer.addNote(new Pitch('B').toMidiNote(), 1, 1); // B
			seqPlayer.addNote(new Pitch('A').octaveTranspose(1).toMidiNote(), 2, 1); // a
			seqPlayer.addNote(new Pitch('C').octaveTranspose(-1).toMidiNote(), 3, 1); // C,

			assertEquals(getSequencePlayer("sample_abc/differentRepeatTest.abc").toString(), seqPlayer.toString());
			return;

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Player Not initialised. Invalid data given");
	}

}



