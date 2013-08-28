package unitTests;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import player.HeadLexer;



public class HeadLexerTest {

    /**
     * 'X' not the first field
     */
    @Test(expected = RuntimeException.class)
    public void test0() {
        assertEquals(new RuntimeException(), new HeadLexer("T:Title\nX:1\nK:C"));
    }
    
    /**
     * 'T' not the second field
     */
    @Test(expected = RuntimeException.class)
    public void test1() {
        assertEquals(new RuntimeException(), new HeadLexer("X:1\nC:Composer\nT:Title\nK:C"));
    }
    
    /**
     * 'K' not the last field
     */
    @Test(expected = RuntimeException.class)
    public void test2() {
        assertEquals(new RuntimeException(), new HeadLexer("X:1\nT:Title\nK:C\nC:Composer"));
    }
    
    /**
     * Multiple Voices
     */
    @Test
    public void test3() {
        HeadLexer h = new HeadLexer("X:1\nT:Title\nC:Composer\nV:upper\nV:middle\nV:lower\nK:C");
        Map<Character,List<String>> m = h.getHeaderMap();
        List<String> l = m.get('V');
        assertEquals(3, l.size());
    }
    
    /**
     * Duplicate instances of a field other than Voice
     */
    @Test(expected = RuntimeException.class)
    public void test4() {
        assertEquals(new RuntimeException(), new HeadLexer(
                "X:1\nT:Title\nC:Composer1\nV:voice\nC:Composer2\nK:C"));
    }
    
    /**
     * Missing field
     */
    @Test(expected = IllegalArgumentException.class)
    public void test5() {
        assertEquals(new IllegalArgumentException(), new HeadLexer(
                "X:1\nT:Title\n:Composer\nV:voice\nK:C"));
    }
    
    /**
     * Unrecognized field
     */
    @Test(expected = IllegalArgumentException.class)
    public void test6() {
        assertEquals(new IllegalArgumentException(), new HeadLexer(
                "X:1\nT:Title\nC:Composer\nB:voice\nK:C"));
    }
}
