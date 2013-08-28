package player;

import java.util.List;
import java.util.ArrayList;
import player.MusicInterface.MusicalVisitor;

public class MusicVisitor implements MusicalVisitor<List<AugmentedMIDI>> {

	private int start;
	private final int numTicks;
	
	/**
	 * Creates an instance of MusicVisitor
	 * @param start
	 * @param numTicks
	 */
	public MusicVisitor(int start, int numTicks) {
		this.start = start;
		this.numTicks=numTicks;
	}
	
	@Override
	public List<AugmentedMIDI> on(Note n) {
		List<AugmentedMIDI> fullNote = new ArrayList<AugmentedMIDI>();

		int duration = getDuration(n, this.numTicks);

		if(n.getPitch() == null)
			return fullNote; //this denotes rests. We don't add them
		else
			fullNote.add(new AugmentedMIDI(n.getPitch().toMidiNote(), this.start, duration));
		return fullNote;
	}

	@Override
	public List<AugmentedMIDI> on(Chord c) {
		List<AugmentedMIDI> fullChord = new ArrayList<AugmentedMIDI>();

		for(Note note: c.getAllNotes()) {
			fullChord.addAll(getMIDINotes(note, this.start,this.numTicks));
		}
		return fullChord;
	}

	@Override
	public List<AugmentedMIDI> on(Voice v) {
		List<AugmentedMIDI> fullVoice = new ArrayList<AugmentedMIDI>();

		for(Chord chord: v.getAllChords())  {
			fullVoice.addAll(getMIDINotes(chord,this.start,this.numTicks));
			this.start += getDuration(chord, this.numTicks);
		}
		return fullVoice;
	}

	@Override
	public List<AugmentedMIDI> on(Song s) {
		List<AugmentedMIDI> fullSong = new ArrayList<AugmentedMIDI>();

		for(Voice voice: s.getVoiceMap().values()) {
			fullSong.addAll(getMIDINotes(voice,this.start,this.numTicks));
		}
		return fullSong;
	}

	/**
	 * 
	 * @param m
	 * @param numTicks
	 * @return
	 */
	private int getDuration(MusicInterface m, int numTicks) {
		int num = m.getLength().getNum();
		int denom = m.getLength().getDenom();

		return num*numTicks/denom; // returns an integer number of ticks for the duration of a note
	}
	
	/**
	 * 
	 * @param music
	 * @param start
	 * @param numTicks
	 * @return
	 */
	public static List<AugmentedMIDI> getMIDINotes(MusicInterface music, int start, int numTicks) {
		return music.accept(new MusicVisitor(start, numTicks));
	}

}
