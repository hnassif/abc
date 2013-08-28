package auxiliary;

public class MultiplicativeFactor {

	private int num;
	private int denom;

	/**
	 * Constructs an instance of a fractional number
	 * @param num : an integer (int) representing the numerator
	 * @param denom : an integer (int) representing the denominator
	 */
	public MultiplicativeFactor(int num, int denom) {
		if (denom < 0) {
			denom = (-1)*denom;
		}
		if (num < 0) {
			num = (-1)*num;
		}
		this.num = num / Mathematics.gcd(num, denom);
		this.denom = denom / Mathematics.gcd(num, denom);
	}

	/**
	 * Return the numerator of the fraction
	 * @return : an integer (int) representing the numerator 
	 */
	public int getNum() {
		return num;
	}
	
	/**
	 * Return the denominator of the fraction
	 * @return : and integer (int) representing the denominator
	 */
	public int getDenom() {
		return denom;
	}
	
	/**
	 * Calculates the product of two rational numbers in its simplest form
	 * @param other: the fraction being multiplied
	 * @return : a MultiplicativeFactor representing the product
	 */
	public MultiplicativeFactor product(MultiplicativeFactor other) {
		int productNum= this.num * other.getNum();
		int productDenom= this.denom* other.getDenom();
		return new MultiplicativeFactor(productNum, productDenom);
	}

	/**
	 * Calculates the sum of two rational numbers in its simplest form
	 * @param other: the fraction being added
	 * @return : a MultiplicativeFactor representing the sum
	 */
	public MultiplicativeFactor sum(MultiplicativeFactor other) {
		int commonDenom = this.denom * other.getDenom();
		return new MultiplicativeFactor((commonDenom / this.denom) * this.num
				+ (commonDenom / other.getDenom()) * other.getNum(), commonDenom);
	}

	/**
	 * Calculates the subtraction of two rational numbers in its simplest form
	 * @param other: the fraction being subtracted
	 * @return : a MultiplicativeFactor representing the difference
	 */

	public MultiplicativeFactor subtract(MultiplicativeFactor other) {
		return sum(new MultiplicativeFactor(-other.getNum(), other.getDenom()));
	}

	/**
	 * Build a Multiplicative Factor from a String input s
	 * Requires: s != null and s not empty. 
	 * s CAN be / , "/<int>" , "<int>/" . Read below for their mappings.
	 * Peculiar Mappings: "/"      -> "1/2"    ,   
	 * 					  "/<int>" -> "1/<int>",  
	 * 					  "<int>/" -> "<int>/1"
	 * 
	 * @param s the string representation of a Ratio to be converted into a Multiplicative Factor
	 * @return a MultiplicativeFactor that corresponds to the given string representation,
	 * @throws an RuntimeException if s is null or empty.
	 */
	public static MultiplicativeFactor multFactorFromString(String stringIn) {
		if (stringIn.equals("/")) 
			return new MultiplicativeFactor(1, 2);
		else if (stringIn == null || stringIn.equals("")) 
			throw new IllegalArgumentException("Can't create a multiplicativeFactor from an empty or null string");


		StringBuilder stringFract = new StringBuilder(stringIn);
		if (!stringIn.contains("/"))
			stringFract.append("/1");
		if (stringIn.charAt(0) == '/')
			stringFract.insert(0,"1");
		if (stringIn.charAt(stringIn.length()-1) == '/')
			stringFract.append("1");

		String[] components = stringFract.toString().split("/");

		if (components.length!=2) {
			throw new RuntimeException("Invalid rational Number");
		} 
		else {
			try {
				int num = Integer.parseInt(components[0]);
				int denom = Integer.parseInt(components[1]);
				return new MultiplicativeFactor(num, denom);
			} 
			catch (NumberFormatException ex) 
			{
				throw new RuntimeException(stringIn + " is not valid");
			}
		}
	}

	@Override // usefull for testing
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (!(other instanceof MultiplicativeFactor))
			return false;
		MultiplicativeFactor that = (MultiplicativeFactor) other;
		return this.num == that.num && this.denom == that.denom;
	}

	@Override // usefull for display purposes
	public String toString() {
		return String.valueOf(this.num) + "/" + String.valueOf(this.denom);
	}

	public int compareTo(MultiplicativeFactor to) {
		int num = this.subtract(to).getNum();
		if (num < 0)
			return -1;
		else if (num > 0)
			return 1;
		else
			return 0;
	}

}
