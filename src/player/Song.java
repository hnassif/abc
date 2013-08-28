package player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import auxiliary.Mathematics;
import auxiliary.MultiplicativeFactor;


public class Song implements MusicInterface {

	//default values for Header Fields
	public final int Tempo_Default = 100;
	public final MultiplicativeFactor Length_Default = new MultiplicativeFactor(1,8);

	public final String Composer_Name_Default = "Unknown Piece composer";
	public final String Title_Default = "Unknown Piece Title";
	public final String Meter_Default = "4/4";
	public final String Voice_Default = "default Voice";

	// Values to initialise if different than defaults
	private String songTitle;
	private String composerName;
	private String trackNumber;
	private String key;
	private String meter;
	private boolean defaultVoice;

	private MultiplicativeFactor defaultNoteLength;
	private int ticksForNote;
	private int tempo;

	private Map<String,Voice> voiceMap;
	private MultiplicativeFactor length;


	public Song(Map<Character, List<String>> headerMap) {

		this.defaultNoteLength = Length_Default;
		this.meter = Meter_Default;
		this.tempo = Tempo_Default;
		this.composerName = Composer_Name_Default;

		this.ticksForNote = 4;//standard quarter note
		this.defaultVoice=false;

		this.voiceMap = new HashMap<String,Voice>();

		for(Map.Entry<Character, List<String>> entry : headerMap.entrySet()) {
		    // if the header field is not V parse appropriately
			if (!entry.getKey().equals('V')){

				switch(entry.getKey()) {
				case 'L':
					this.defaultNoteLength = MultiplicativeFactor.multFactorFromString(entry.getValue().get(0).trim());
					break;

				case 'M':
					this.meter = entry.getValue().get(0).trim(); 
					break;

				case 'Q':
					this.tempo = Integer.valueOf(entry.getValue().get(0).trim());
					break;

				case 'X': 
					this.trackNumber = entry.getValue().get(0).trim();
					break;

				case 'T': 
					this.songTitle = entry.getValue().get(0).trim();
					break;

				case 'C':
					this.composerName = entry.getValue().get(0).trim();
					break;

				case 'K':
					this.key = entry.getValue().get(0).trim();
					break;

				default:
					throw new RuntimeException("Invalid field in Header  : " + entry.getKey());
				}
			}
			
			else // if the header field is V,

				for(String voice : entry.getValue()){
					if (this.voiceMap.containsKey(voice)) // check if this voice has already been listed in the header
						throw new IllegalArgumentException("A duplicate voice has been listed in the header of the abc file");
					else
						voiceMap.put(voice, new Voice(voice)); // if it has not then added as a new list.
				}

		}
		
		if(voiceMap.isEmpty()){ // create a default voice if no voice is specified

			voiceMap.put(Voice_Default, new Voice(Voice_Default));
			this.defaultVoice=true;
		}
		this.length= new MultiplicativeFactor(0,1);
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
	 * Method to display all song info as specified in the header of abc File.
	 * Prints information to screen
	 */
	public void displaySongInfo() {
		System.out.println("Track number: " + this.trackNumber + "\n");
		System.out.println("Title: " + this.songTitle + "\n");
		System.out.println("Composer_Name: " + this.composerName + "\n");
		System.out.println("Voices: " + this.voiceMap.keySet() + "\n");
		System.out.println("Meter: " + this.meter + "\n");
		System.out.println("Default note length: " + this.Length_Default + "\n");
		System.out.println("Tempo: " + this.tempo + "\n");
		System.out.println("Key signature: " + this.key + "\n");
	}
	
	/**
	 * Method to synchronise the base ticks for each note based on the input noteLength
	 * given. Ensures all notes have proportional amounts of ticks to each other
	 * 
	 * @param noteLength, the note Length with which to synchronise
	 */
	public void syncTicks(int noteLength){
		this.ticksForNote = Mathematics.lcm(noteLength,this.ticksForNote);  
	}
	
	/**
	 * Sets the ticks for each note explicitly.
	 * 
	 * @param ticks, the duration in ticks to which to set the ticks for each note
	 */
	public void setTicksForNote(int ticks) {
		this.ticksForNote = ticks;
	}

	public int getTicksForNote(){
		return ticksForNote;
	}

	public boolean containsDefaultVoice(){
		return defaultVoice;
	}

	public String getSongTitle() {
		return songTitle;
	}

	public String getSongTrackNumber() {
		return trackNumber;
	}
	
	public String getSongKey() {
		return key;
	}

	public String getSongComposer() {
		return composerName;
	}

	public MultiplicativeFactor getDefaultNoteLength() {
		return defaultNoteLength;
	}

	public int getSongTempo() {
		return tempo;
	}
	
	public Map<String,Voice> getVoiceMap() {
		return voiceMap; 
	}
	
	/**
	 * Adds specified chord to the specified voice, if this voice exists.
	 * If the voice has not been specified, throws IllegalArgumentException
	 * @param voice
	 * @param chord
	 */
	public void addChord(String voice, Chord chord) {
		if(voiceMap.containsKey(voice))
		{
			voiceMap.get(voice).addChord(chord);
			if (length.compareTo(voiceMap.get(voice).getLength()) < 0) {
				length = voiceMap.get(voice).getLength(); //song length is as long as the longest voice
			}
		}
		else {
			throw new IllegalArgumentException("Voice: " + voice +",  does not exist in header");
		}
	}
}

