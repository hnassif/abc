package player;

import java.util.ArrayList;
import java.util.List;

import auxiliary.MultiplicativeFactor;

public class Chord implements MusicInterface {



    private MultiplicativeFactor length;
    private List<Note> allNotes;

    /**
     * Constructs an instance of Chord
     */
    public Chord() {
        length = null;
        allNotes = new ArrayList<Note>();
    }

    @Override
    public <R> R accept(MusicalVisitor<R> visitor) {
        return visitor.on(this);
    }

    @Override
    public MultiplicativeFactor getLength() {
        if (length == null) {
            throw new RuntimeException("This chord is empty");
        }
        return length;
    }

    /**
     * Returns all Notes within a Chord
     * 
     * @return : allnotes , all the notes within a a Chord
     */
    public List<Note> getAllNotes() {
        return allNotes;
    }

    /**
     * Appends a Note to the Chord.
     * 
     * @param note
     *            : the note to be added
     */
    public void addNote(Note note) {
        if (!note.getLength().equals(length) && length != null)
            throw new RuntimeException(
                    "Notes within a chord cannot have diffferent lengths");
        else if (length == null) // first note to be added.
            length = note.getLength();
        allNotes.add(note);
    }

}
