package player;

/**
 * This is the representation of a tuple 
 *
 */
public class AugmentedMIDI {
	
	private final int duration;
    private final int pitch;
    private final int start;
    
    /**
     * Constructs a MIDI note augmented with
     * its pitch, beginning and duration
     * @param pitch : the pitch of the note
     * @param beginning : the beginning of the note
     * @param duration : the duration of the note
     */
    public AugmentedMIDI(int pitch, int start, int duration) {
        this.start = start;
        this.duration = duration;
        this.pitch = pitch;
    }
    /**
     * Returns the pitch of the note
     * @return the pitch of the note
     */
    public int getPitch() {
        return pitch;
    }
    /**
     * Returns the start of the note 
     * @return: start
     */
    public int getStart() {
        return start;
    }
    /**
     * Returns the duration of the note 
     * @return: duration
     */
    public int getDuration() {
        return duration;
    }
}

