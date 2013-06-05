import java.util.HashMap;

/**
 * Debug class for easy prints.
 * @author Lukas J. Wensby
 * @version 2013-05-20
 */
public class Debug {
	public static boolean toggle = true;
	
	// Timer variables
	private static HashMap<String, Long> timers 	 = new HashMap<String, Long>(); 
	private static HashMap<String, Long> timeMarkers = new HashMap<String, Long>();
	
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
	
	public static void start(String timerName) {
		long now = System.nanoTime();
		timeMarkers.put(timerName, now);
	}
	
	public static void stop(String timerName) {
		long before = timeMarkers.get(timerName);
		long now = System.nanoTime();
		long interval = now - before;
		
		if (timers.containsKey(timerName)) timers.put(timerName, timers.get(timerName) + interval);
		else timers.put(timerName, interval);
		
		timeMarkers.put(timerName, 0l);
	}
	
	/**
	 * Prints the timer
	 */
	public static void pt(String timerName) {
		if (timeMarkers.get(timerName) != 0) {
			stop(timerName);
			Debug.pl("Timer: " + timerName + " \t " + (timers.get(timerName) / 1000000000.0f) + " seconds.");
			start(timerName);
		}
		else {
			Debug.pl("Timer: " + timerName + " \t " + (timers.get(timerName) / 1000000000.0f) + " seconds.");
		}
	}

    public static void reset(String timerName){
        timers.put(timerName,0l);
    }
}