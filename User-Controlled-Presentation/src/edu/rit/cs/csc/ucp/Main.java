package edu.rit.cs.csc.ucp;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import edu.rit.cs.csc.ucp.input.Audio;
import edu.rit.cs.csc.ucp.input.Timer;


public class Main {
	
	//TODO use a logger instead of print statements - yes so we can track how well the audio processing is working and how often users actually do things
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		//TODO move all these strings into Settings
		Option help = new Option("h", "Help", false, "Displays this help text and exits");
		Option version = new Option("v", "Version", false, "Displays version information and exits");
		
		Option images = new Option(Settings.SlideFolderId, "Images", true, "The directory where are the slide images are stored");
		Option pause = new Option(Settings.PauseTimeId, "Pause", true,     "The amonut of time the slideshow is paused after a user action (seconds)");
		Option visible = new Option(Settings.VisibleTimeId, "Slide", true, "The amount of time each slide is displayed (seconds)");
		//TODO add option for start in full screen mode.  Need to have a button to toggle between full/windowed mode.  
		//TODO add option for random or sequential display of slides/images
		//TODO handle display weather, clock, and nearby bus times.  What else would be helpful to students?
		//TODO option for how often to refresh slide list from folder - or how do we setup so OS tells us when files change?
		
		Options options = new Options();
		options.addOption(help);
		options.addOption(version);
		options.addOption(images);
		options.addOption(pause);
		options.addOption(visible);
		
		
		//read the parameters
		HelpFormatter helpFormat = new HelpFormatter();
		CommandLineParser parser = new PosixParser(); //PosixParser handles combining options: "ls -a -l" as "ls -al"
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch(ParseException e) {
			System.err.println("Error parsing command line options: " + e.getLocalizedMessage());
			helpFormat.printHelp(Settings.RunStatement, options, true);
			System.exit(1);
		}
		
		//handle the exit cases
		if(cmd == null) {
			throw new IllegalStateException("Command line parse is null");
		}
		if(cmd.hasOption(help.getOpt()) || cmd.hasOption(version.getOpt())) {
			if(cmd.hasOption(version.getOpt())) {
				System.out.println(Settings.Title);
			}
			if(cmd.hasOption(help.getOpt())) {
				helpFormat.printHelp(Settings.RunStatement, options, true);
			}
			
			System.exit(0);
		}
		
		//process the rest of the options
		for(Option o: cmd.getOptions()) {
			Settings.setOptionValue(o.getOpt(), o.getValue());
		}
		
		GUI gui = new GUI();
		Timer timer = new Timer();
		Audio audio = new Audio();
		
		//setup I/O connections
		audio.addListener(timer); //timer pauses after the user does something
		audio.addListener(gui);   //user tells gui to do things
		timer.addListener(gui);   //timer drives the main slideshow rate
		
		//start the slideshow
		Thread t = new Thread(timer);
		t.start();
		gui.display();
	}
	
}
