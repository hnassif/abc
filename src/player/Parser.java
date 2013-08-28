package player;

import java.util.ArrayList;
import java.util.List;

import auxiliary.MultiplicativeFactor;

import sound.Pitch;

public class Parser {

    private final Lexer lexer;
    private boolean deleteToken; // for repeats

    /**
     * Constructs an instance of the parser
     * 
     * @param Lexer
     */
    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    /**
     * Parses the Header of the abc music file
     * 
     * @return : A Song with the specifications mentioned in the header
     */
    public Song parseFileHeader() {
        return new Song(lexer.getHeaderLexer().getHeaderMap());
    }

    /**
     * Helper method which returns the next Token from the body of the abc file
     * 
     * @return nextToken The next body Token
     */
    private BodyToken getNextBodyToken() {
        BodyToken nextToken = lexer.getNextToken();
        if (deleteToken) {
            lexer.deleteLastToken();
        }
        return nextToken;
    }

    /**
     * Parses an abc music file.
     * 
     * @return finalSong A Song that represents the content of the body of the
     *         abc file
     * @throws RuntimeException
     *             if the Token stream cannot be parsed
     */
    public Song parse() {
        Song finalSong = parseFileHeader();
        this.deleteToken = false;
        String presentVoice = null;
        boolean stop = false;

        Modifiers defaultMods = Modifiers.getStartMods(finalSong.getSongKey().trim());
        Modifiers currentMods = Modifiers.clone(defaultMods);

        if (finalSong.containsDefaultVoice()) 
            presentVoice = finalSong.Voice_Default;
        

        while (!stop) {
            BodyToken nextToken = getNextBodyToken();
            switch (nextToken.getBodyTokenType()) {

            case VOICE:
                // create a new Voice with declared name
                String extraVoice = nextToken.getBodyTokenText().substring(2);
                if (finalSong.containsDefaultVoice())
                    throw new RuntimeException(
                            "There cannot be a voice declared in the file and not declared in the header");
                else if (!finalSong.getVoiceMap().containsKey(extraVoice))
                    throw new RuntimeException(
                            "Encountered voice in body that was not declared in header of abc file");
                else
                    presentVoice = extraVoice;
                break;

            case ACCIDENTAL:
                Chord accentedChord = new Chord();
                Note accentedNote = processAccidental(
                        nextToken.getBodyTokenText(), presentVoice,
                        currentMods, finalSong);
                accentedChord.addNote(accentedNote);
                finalSong.addChord(presentVoice, accentedChord);
                break;

            case NOTE_REST:
                Chord newChord = new Chord();
                Note newNote = processNoteRest(nextToken.getBodyTokenText(),
                        null, presentVoice, currentMods, finalSong);
                newChord.addNote(newNote);
                finalSong.addChord(presentVoice, newChord);
                break;

            case END_CHORD:
                throw new RuntimeException(
                        "Found chord end before chord beginning");

            case BEGIN_CHORD:
                MultiplicativeFactor chordLength = null;
                Chord noteChord = new Chord();
                while (lexer.peekToken().getBodyTokenType() != BodyToken.Type.END_CHORD) {
                    Note followingNote = processAccentedNote(presentVoice,
                            currentMods, finalSong);
                    if (chordLength == null)
                        chordLength = followingNote.getLength();
                    else if (followingNote.getLength().compareTo(chordLength) == 1)
                        followingNote.setLength(chordLength);
                    // if a note in a chord is longer, we set the length of the
                    // note to be that of the chord
                    noteChord.addNote(followingNote);
                }

                getNextBodyToken(); // disregard end chord token
                finalSong.addChord(presentVoice, noteChord);
                break;

            case BEGIN_TUPLET:
                List<Note> tupletNotes = processTuplet(presentVoice,
                        currentMods, finalSong);
                for (Note tupletNote : tupletNotes) {
                    Chord tupletChord = new Chord();
                    finalSong.syncTicks(tupletNote.getLength().getDenom());
                    tupletChord.addNote(tupletNote);
                    finalSong.addChord(presentVoice, tupletChord);
                }
                break;

            case REPEAT_BEGIN:case SECTION_BEGIN:case SECTION_END:case BAR:
                currentMods = Modifiers.clone(defaultMods);
                break;

            case REPEAT_END:
                if (!this.deleteToken)
                    lexer.deleteLastToken();
                lexer.goBack();
                this.deleteToken = false;
                break;

            case EXTRA_REPEAT:
                if (nextToken.getBodyTokenText().charAt(1) == '1') {
                    lexer.deleteLastToken();
                    this.deleteToken = true;
                }
                break;

            case OCTAVE_MOD:
                throw new RuntimeException(
                        "Encountered an Octave modifier before a note or rest. Modifiers should preceed notes");
            case MULT_FACTOR:
                throw new RuntimeException(
                        "Encountered a length modifier before a note or rest. Modifiers should preceed notes");

            case EOF:
                stop = true;
                break;

            case COMMENT:
                break;

            default:
                throw new RuntimeException("Unrecognised token of type, "
                        + nextToken.getBodyTokenType().toString()
                        + "encountered");
            }
        }
        return finalSong;
    }

    /**
     * Parses a tuplet.
     * 
     * @param currentVoice
     * @param currentMods
     * @param finalSong
     * @return tupletNotes An ArrayList of Note objects representing the given
     *         tuplet
     */
    private List<Note> processTuplet(String currentVoice,
            Modifiers currentMods, Song finalSong) {
        BodyToken isInteger = getNextBodyToken();
        if (isInteger.getBodyTokenType() != BodyToken.Type.MULT_FACTOR)
            throw new RuntimeException(
                    "Tuplet declaration must be followed by an Integer Multiplicative Factor. Instead followed by: "
                            + isInteger.getBodyTokenText());

        String integerNumber = isInteger.getBodyTokenText();
        // Checks if the tuplet is formatted correctly
        if (!integerNumber.equals("2") && !integerNumber.equals("3")
                && !integerNumber.equals("4"))
            throw new RuntimeException(
                    "Tuplet declaration must always be followed by an Integer (2,3,4). Instead followed by: "
                            + integerNumber);

        MultiplicativeFactor lengthModifier = null;
        // Integer representation of a numerical char
        int numberOfNotes = integerNumber.charAt(0) - '0';

        List<Note> tupletNotes = new ArrayList<Note>();
        if (numberOfNotes == 4)
            lengthModifier = new MultiplicativeFactor(3, 4);
        else if (numberOfNotes == 3)
            lengthModifier = new MultiplicativeFactor(2, 3);
        else if (numberOfNotes == 2)
            lengthModifier = new MultiplicativeFactor(3, 2);

        for (int j = 0; j < numberOfNotes; j = j + 1) {
            Note accentedNote = processAccentedNote(currentVoice, currentMods,
                    finalSong);
            Note modifiedLengthNote = new Note(accentedNote.getPitch(),
                    accentedNote.getLength().product(lengthModifier));
            tupletNotes.add(modifiedLengthNote);
        }
        return tupletNotes;
    }


    /**
     * Parses an accidental, and returns the note following it.
     * 
     * @param accidentalString
     * @param currentVoice
     * @param currentMods
     * @param finalSong
     * @return n Note
     * @throws RuntimeException
     *             if the given accidental is not followed by a note or a rest
     */
    private Note processAccidental(String accidentalString,
            String currentVoice, Modifiers currentMods, Song finalSong) {
        BodyToken isNoteOrRest = lexer.peekToken();
        if (isNoteOrRest.getBodyTokenType() != BodyToken.Type.NOTE_REST)
            throw new RuntimeException(
                    "Accidental must be succeded by a note or a rest");
        BodyToken nextNoteOrRest = getNextBodyToken();
        Note n = processNoteRest(nextNoteOrRest.getBodyTokenText(),
                accidentalString, currentVoice, currentMods, finalSong);
        return n;
    }

    /**
     * Parses a note or a rest, applying multiplicative factors or octave
     * shifts.
     * 
     * @param value
     * @param accidental
     * @param presentVoice
     * @param currentMods
     * @param finalSong
     * @return Note an abstract representation of a note from the abc file
     *         including the effect of all of its modifiers
     */
    private Note processNoteRest(String value, String accidental,
            String presentVoice, Modifiers currentMods, Song finalSong) {
        String lengthMod = null;
        String octaveMod = null;
        if (lexer.peekToken().getBodyTokenType() == BodyToken.Type.OCTAVE_MOD) {
            octaveMod = getNextBodyToken().getBodyTokenText();
            if (lexer.peekToken().getBodyTokenType() == BodyToken.Type.MULT_FACTOR)
                lengthMod = getNextBodyToken().getBodyTokenText();
        } else if (lexer.peekToken().getBodyTokenType() == BodyToken.Type.MULT_FACTOR)
            lengthMod = getNextBodyToken().getBodyTokenText();

        // We change the pitch according to the modifiers or lower case letters
        // that appear
        Pitch pitch;
        if (value.charAt(0) == 'z')
            pitch = null;
        else
            pitch = new Pitch(Character.toUpperCase(value.charAt(0)));

        if (Character.isLowerCase(value.charAt(0)) && value.charAt(0) != 'z')
            pitch = pitch.octaveTranspose(1);

        if (pitch != null && octaveMod != null) {
            for (char octaveShift : octaveMod.toCharArray()) {
                switch (octaveShift) {
                case '\'':
                    pitch = pitch.octaveTranspose(1);
                    break;
                case ',':
                    pitch = pitch.octaveTranspose(-1);
                    break;
                default:
                    throw new RuntimeException("Octave Modifier: "
                            + octaveShift + " is not recognised");
                }
            }
        }
        
        if (pitch != null && accidental != null) {
            currentMods.applyMods(pitch, accidental);
        }
        
        MultiplicativeFactor noteDuration = new MultiplicativeFactor(1, 1);
        
        if (lengthMod != null)
            noteDuration = MultiplicativeFactor.multFactorFromString(lengthMod);
        finalSong.syncTicks(noteDuration.product(
                finalSong.getDefaultNoteLength()).getDenom());
        
        if (pitch != null)
            return new Note(currentMods.applyModsTo(pitch), finalSong
                    .getDefaultNoteLength().product(noteDuration));
        
        else
            return new Note(pitch, finalSong.getDefaultNoteLength().product(
                    noteDuration));
    }
    
    /**
     * Parses a note which has accidentals applied to it.
     * 
     * @param presentVoice
     * @param currentMods
     * @param finalSong
     * @return currentNote A Note
     */
    private Note processAccentedNote(String presentVoice,
            Modifiers currentMods, Song finalSong) {
        Note currentNote = null;
        if (lexer.peekToken().getBodyTokenType() == BodyToken.Type.ACCIDENTAL)
            currentNote = processAccidental(getNextBodyToken()
                    .getBodyTokenText(), presentVoice, currentMods, finalSong);
        else if (lexer.peekToken().getBodyTokenType() == BodyToken.Type.NOTE_REST)
            currentNote = processNoteRest(
                    getNextBodyToken().getBodyTokenText(), null, presentVoice,
                    currentMods, finalSong);
        else
            throw new RuntimeException(
                    "Expected to encounter Note. Instead encountered: "
                            + lexer.peekToken());
        return currentNote;
    }
}
