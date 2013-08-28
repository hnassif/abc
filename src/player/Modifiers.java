package player;
import java.util.HashMap; 
import java.util.Map;

import sound.Pitch;

public class Modifiers {
	
	private Map<Pitch, Pitch> modifiers;
	/**
	 * Constructs an instance of Modifiers
	 * @param startMap : The initial Map of modifiers
	 */
	public Modifiers(Map<Pitch, Pitch> startMap) {
		modifiers = new HashMap<Pitch, Pitch>();
		for (Pitch pitch : startMap.keySet()) {
			modifiers.put(pitch, startMap.get(pitch));
		}
	}
	
	/**
	 * Constructs an instance of Modifiers
	 */
	public Modifiers() {
		modifiers = new HashMap<Pitch, Pitch>();
	}
	
	/**
	 * Creates a copy of a modifiers map
	 * @return : a copy of the modifiers map
	 */
	public static Modifiers clone(Modifiers other) {
		Modifiers clonedModifiers = new Modifiers(other.getAccidentals());
		return clonedModifiers;
	}
	
	/**
	 * Modifies the original Pitch given the string accidental
	 * @param prevPitch : the original Pitch to be modified
	 * @param accidental : The accidental to be applied
	 */
	public void applyMods(Pitch prevPitch, String accidental) {
		Pitch newPitch = prevPitch;
		for (int location = 0; location < accidental.length(); ++location) {
			switch (accidental.charAt(location)) {
			case '=': // neutral
				newPitch = prevPitch;
				break;
				
			case '^': // sharp
				newPitch = newPitch.accidentalTranspose(1);
				break;

			case '_': // flat
				newPitch = newPitch.accidentalTranspose(-1);
				break;
				
			default:
				throw new RuntimeException("Accidental Type not recognized" + accidental.charAt(location));
			}
		}
		modifiers.put(prevPitch, newPitch);
	}

	/**
	 * Modifies the Modifiers map by applying the accidental
	 * @return : a new Modifiers map that accounts for this accidental
	 */
	private Modifiers extendMods(Pitch prevPitch, String accidental) {
		Modifiers newMods  = new Modifiers(this.getAccidentals());
		newMods.applyMods(prevPitch, accidental);
		return newMods;
	}

	/**
	 * Returns a copy of the Modifiers Map
	 * @return : A copy of the modifiers map
	 */
	private Map<Pitch, Pitch> getAccidentals() {
		Map<Pitch, Pitch> copyOfMods = new HashMap<Pitch, Pitch>();
		for (Pitch pitch : modifiers.keySet()) {
			copyOfMods.put(pitch, modifiers.get(pitch));
		}
		return copyOfMods;
	}

	/** Returns modified pitch given an original pitch
	 * If pitch isn't modified, returns original pitch
	 * @param originalPitch : unmodified pitch
	 * @return : the modified pitch , if accidentals are applied or the original pitch
	 */
	public Pitch applyModsTo(Pitch originalPitch) {
		if (!modifiers.containsKey(originalPitch))
			return originalPitch;
		else
			return modifiers.get(originalPitch);
	}
	/** Converts the Map of modifiers into a string
	 * @return : A string representing the modifiers map 
	 */
	@Override
	public String toString() {
		return modifiers.toString();
	}
	/**
	 * Returns the Starting Modifiers map for a key
	 * @return : the starting Modifiers map for 
	 */
	public static Modifiers getStartMods(String key) {
		Map<String, Modifiers> startMods = new HashMap<String, Modifiers>();
		startMods.put("Am", new Modifiers());
		startMods.put("C", new Modifiers()); 

		// source http://en.wikipedia.org/wiki/Key_signature
		char[] addedFlat = new char[]{' ', 'B', 'E', 'A', 'D', 'G', 'C', 'F'};
		final String[] majorFlats = new String[]{"C", "F", "Bb", "Eb", "Ab", "Db", "Gb", "Cb"};
		final String[] minorFlats = new String[]{"Am", "Dm", "Gm", "Cm", "Fm", "Bbm", "Ebm", "Abm"};
		

		for (int numFlats = 1; numFlats < majorFlats.length; numFlats=numFlats+1) {
			startMods.put(majorFlats[numFlats], startMods.get(majorFlats[numFlats - 1]).extendMods(new Pitch(addedFlat[numFlats]), "_"));
			startMods.put(minorFlats[numFlats], startMods.get(minorFlats[numFlats - 1]).extendMods(new Pitch(addedFlat[numFlats]), "_"));
		}
		
		char[] addedSharp = new char[]{' ', 'F', 'C', 'G', 'D', 'A', 'E', 'B'};
		final String[] majorSharps = new String[]{"C", "G", "D", "A", "E", "B", "F#", "C#"};
		final String[] minorSharps = new String[]{"Am", "Em", "Bm", "F#m", "C#m", "G#m", "D#m", "A#m"};
		

		for (int numSharps = 1; numSharps < majorSharps.length; numSharps=numSharps+1) {
			startMods.put(majorSharps[numSharps], startMods.get(majorSharps[numSharps - 1]).extendMods(new Pitch(addedSharp[numSharps]), "^"));
			startMods.put(minorSharps[numSharps],startMods.get(minorSharps[numSharps - 1]).extendMods(new Pitch(addedSharp[numSharps]), "^"));
		}
		if (!startMods.containsKey(key))
			throw new RuntimeException("Key Signature: " + key + "  is not recognized");
		
		return startMods.get(key);
	}
}
