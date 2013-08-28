package player;

import auxiliary.MultiplicativeFactor;
import sound.Pitch;


public class Note implements MusicInterface {
	
	    private final Pitch pitch;
	    private MultiplicativeFactor length; //not final for

	    /**Constructs a Note
	     * 
	     * @param pitch : pitch of the note
	     * @param length : length of the note
	     */
	    public Note(Pitch pitch, MultiplicativeFactor  length) {
	        this.pitch = pitch;
	        this.length = length;
	    }
	    
	    @Override
	    public <R> R accept(MusicalVisitor<R> visitor) {
	        return visitor.on(this);
	    }

	    @Override
	    public MultiplicativeFactor  getLength() {
	        return length;
	    }
	    
	    /**
	     * Returns the Note's pitch
	     * @return the pitch of the note
	     */
	    public Pitch getPitch() {
	        return pitch;
	    }

		public void setLength(MultiplicativeFactor length) {
			this.length = length;
			
		}
	}

