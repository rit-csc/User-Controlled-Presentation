package edu.rit.cs.csc.ucp;


public class Settings {
	
	//--------------------------------------------------------------------------
	// Debug options
	//--------------------------------------------------------------------------
	
	public static final boolean AlwaysOnTop = true;
	
	//--------------------------------------------------------------------------
	
	public static final String Version = "0.1";
	public static final String Title = "User Controlled Presentation v" + Version;
	
	public static String RunStatement = "java -jar csc-recorder.jar";
	//static {
	//	StackTraceElement[] stack = Thread.currentThread ().getStackTrace ();
	//	StackTraceElement main = stack[stack.length - 1];
	//	RunStatement = "java " + main.getClassName ();
	//}
	
	
	//--------------------------------------------------------------------------
	// Set by command line
	//--------------------------------------------------------------------------
	
	//TODO add error checking
	
	public static final String SlideFolderId = "i";
	public static final String PauseTimeId = "p";
	public static final String VisibleTimeId = "s";
	
	private static String slideFolder = "./";
	private static int pauseTime = 4;
	private static int slideTime = 2;
	
	public static boolean setOptionValue(String option, String value) {
		
		switch(option) {
			case SlideFolderId: return setSlideFolder(value);
			case PauseTimeId:   return setPauseTime(value);
			case VisibleTimeId: return setVisibleTime(value);
			case "h":
			case "v":
				return true;
			default:
				System.err.println("Option not handled: '" + option + "' with value: '" + value + "'");
				return false;
		}
	}
	
	private static boolean setSlideFolder(String directory) {
		Settings.slideFolder = directory;
		return true;
	}
	
	private static boolean setPauseTime(String seconds) {
		Settings.pauseTime = Integer.parseInt(seconds);
		return true;
	}
	
	private static boolean setVisibleTime(String seconds) {
		Settings.slideTime = Integer.parseInt(seconds);
		return true;
	}
	
	public static String getSlideFolder() {
		return slideFolder;
	}
	
	public static int getPauseTime() {
		return pauseTime;
	}
	
	public static int getVisibleTime() {
		return slideTime;
	}
	
	//--------------------------------------------------------------------------
	//
	//--------------------------------------------------------------------------
	
	
	
	
}
