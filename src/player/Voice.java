package player;

import java.util.List;
import java.util.ArrayList;
import auxiliary.MultiplicativeFactor;


public class Voice implements MusicInterface {

	private final String VoiceName;
	private MultiplicativeFactor length;
	private List<Chord> Allchords;

	/**
	 * Constructs a voice, given its name
	 * @param name : name of the voice
	 */
	public Voice(String name) {
		this.VoiceName = name;
		this.length = new MultiplicativeFactor(0, 1);
		this.Allchords = new ArrayList<Chord>();
	}

	@Override
	public <R> R accept(MusicalVisitor<R> visitor) {
		return visitor.on(this);
	}

	@Override
	public MultiplicativeFactor getLength() {
		return length;
	}
	
	/**
	 * Return the name of the voice
	 * @return the name of the voice
	 */
	public String getVoiceName() {
		return VoiceName;
	}
	
	/**
	 * Return the list of chords in the voice
	 * @return : List of chords in the voice 
	 */
	public List<Chord> getAllChords(){
		return Allchords;
	}

	/**
	 * Append a Chord to the end of the Chords list
	 * @param chord the chord to be added
	 */
	public void addChord(Chord chord) {
		Allchords.add(chord);
		length = length.sum(chord.getLength());
	}
}
