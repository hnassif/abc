package auxiliary;

public class Mathematics {
	/**
	 * Calculates the greatest common divisor of two integers a, b
	 * 
	 * @param a the first integer (int)
	 * @param b the second integer (int)
	 * @return Greatest Common Divisor. That is, biggest d such that a % d == 0 && b % d == 0
	 * @throws ArithmeticException if both a == 0 and b == 0
	 */
	public static int gcd(int a, int b) {
		if (a == 0 && b == 0) {
			throw new ArithmeticException("gcd(0, 0) is undefined!");
		}
		if (a < 0) // fix negative values
			return gcd(-a, b);
		if (b < 0)
			return gcd(a, -b);
		while (a > 0 && b > 0) { // Euclid's algorithm
			if (a > b) {
				a %= b;
			} else {
				b %= a;
			}
		}
		return a + b;
	}

	/**
	 * Calculates the least common multiple of two integers.
	 * 
	 * @param a the first integer (int)
	 * @param b the second integer (int)
	 * @return smallest m such that m % a == 0 && m % b == 0
	 */
	public static int lcm(int a, int b) {
		return (a / gcd(a, b)) * b;
	}
}

