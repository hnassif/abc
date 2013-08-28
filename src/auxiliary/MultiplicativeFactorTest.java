package auxiliary;

import static org.junit.Assert.*;

import org.junit.Test;

public class MultiplicativeFactorTest {

    @Test
    public void testSlashOnly() {
        assertEquals(new MultiplicativeFactor(1, 2),
                MultiplicativeFactor.multFactorFromString("/"));
    }

    @Test
    public void testIntRatio() {
        assertEquals(new MultiplicativeFactor(840, 1),
                MultiplicativeFactor.multFactorFromString("840"));
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidSlashes() {
        assertEquals(new MultiplicativeFactor(0, 1),
                MultiplicativeFactor.multFactorFromString("//"));
    }

    @Test
    public void testSimplification() {
        assertEquals(new MultiplicativeFactor(9, 8),
                MultiplicativeFactor.multFactorFromString("18/16"));
    }

    @Test
    public void testMultiplication() {
        assertEquals(new MultiplicativeFactor(4, 7), new MultiplicativeFactor(
                40, 49).product(MultiplicativeFactor
                .multFactorFromString("7/10")));
    }

    @Test
    public void testSlashInteger() {
        assertEquals(new MultiplicativeFactor(-1, 97),
                MultiplicativeFactor.multFactorFromString("/-97"));
    }

}
