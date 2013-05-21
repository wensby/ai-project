/**
 * Debug class for easy prints.
 * @author Lukas J. Wensby
 * @version 2013-05-20
 */
public class Debug {
	public static boolean toggle = true;
	
	public static void p(Object o) {
		if (toggle) System.out.print(o);
	}
	
	public static void pl(Object o) {
		if (toggle) System.out.println(o);
	}
	
	/**
	 * Prints every object in the specified array.
	 */
	public static void pal(Object[] o) {
		if (toggle) {
			for (Object object : o) p(object.toString() + " \t");
		}
		pl("");
	}
}