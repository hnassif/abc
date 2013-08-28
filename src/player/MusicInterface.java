package player;

import auxiliary.MultiplicativeFactor;

/**
 * Interface implementing the visitor design pattern
 * 
 */
public interface MusicInterface {
	
	public interface MusicalVisitor<R>{
		public R on(Note note);

		public R on(Chord chord);
		
		public R on(Voice voice);
		
		public R on(Song song);
	}
	
	public <R> R accept(MusicalVisitor<R> visitor);
	public MultiplicativeFactor getLength();

}
