package player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import auxiliary.MultiplicativeFactor;


import sound.SequencePlayer;

/**
 * Main entry point of your application.
 */
public class Main {

	/**
	 * Plays the input file using Java MIDI API and displays
	 * header information to the standard output stream.
	 * 
	 * (Your code should not exit the application abnormally using
	 * System.exit().)
	 * 
	 * @param file the name of input abc file
	 */
	public static void play(String file) {
		
		String inputFile = readFileIntoString(file);
		Lexer lexer = new Lexer(inputFile);
		Parser parser = new Parser(lexer);

		Song piece = parser.parse();
		piece.displaySongInfo();

		List<AugmentedMIDI> musicNotes = MusicVisitor.getMIDINotes(piece, 0, (int) piece.getTicksForNote());

		SequencePlayer player;
		try {
			//Default: 2 ticks per quarter note
			// default tempo 120 / min
			MultiplicativeFactor initialLength = piece.getDefaultNoteLength(); // default length of single note
			int initialTempo = piece.getSongTempo(); // the starting tempo
	
			//tempo expressed in quarters: Num(quarter/minute) = Num(defaultNotes/minute) * Num(fullNote/defaultNote) * Num(quarter/fullNote)
			int tempoExpressedAsQuarters = ((4 * initialTempo * initialLength.getNum()) / initialLength.getDenom());

			// ticks for each quarter = Num(ticks/fullNote) / 4
			player = new SequencePlayer(tempoExpressedAsQuarters,(int) piece.getTicksForNote()/4);

			for(AugmentedMIDI midi : musicNotes) {
				player.addNote(midi.getPitch(), midi.getStart(), midi.getDuration());
			}

			System.out.println(player);
			player.play();

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Helper method that converts the content of an abc music file into a string.  
     * @param filePath a String specifying a unique the location of an abc music file
     * @return String representing the content of an abc music file 
     * @throws RuntimeException if the file is not found or if IO exception is encountered.
     */
    public static String readFileIntoString (String filePath){

        String readLine = null; // line to append
        StringBuffer fileString = new StringBuffer();
        try {
            File file = new File(filePath);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((readLine = reader.readLine()) != null)
                // make sure we know where the line ended
                fileString.append(readLine + "\n");
            reader.close();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("File Not Found");
        } catch (IOException ex) {
            throw new RuntimeException("IO exception encountered while reading file");
        }
        return fileString.toString();
    }

	public static void main(String[] args) {
		 //play("sample_abc/fur_elise.abc");
		 //play("sample_abc/invention.abc");
		 //play("sample_abc/little_night_music.abc");
		 play("sample_abc/paddy.abc");
		// play("sample_abc/piece1.abc");
		 //play("sample_abc/piece2.abc");
		 //play("sample_abc/prelude.abc");
		 //play("sample_abc/scale.abc");
	}

}
