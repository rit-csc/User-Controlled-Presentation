package edu.rit.cs.csc.ucp;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class GUI implements Actions {
	
	private final JFrame frame;
	
	public GUI() {
		//TODO run borderless
		
		frame = new JFrame();
		frame.setTitle(Settings.Title);
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setAlwaysOnTop(Settings.AlwaysOnTop);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		findImageFiles();
	}
	
	public void display() {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				frame.setVisible(true);
			}
		});
	}
	
	//--------------------------------------------------------------------------
	// Image handling and caching
	//--------------------------------------------------------------------------
	
	private LinkedList<File> imageFiles = new LinkedList<File>();
	private HashMap<File, BufferedImage> imageCache = new HashMap<File, BufferedImage>();
	private ListIterator<File> imageIterator = null;
	
	private void findImageFiles() {
		File folder = new File(Settings.getSlideFolder());
		System.out.println("Looking for images in: " + folder.getAbsolutePath());
		File[] kids = folder.listFiles();
		if(kids == null) {
			kids = new File[0];
		}
		
		for(File f: kids) {
			if(!f.canRead()) {
				System.out.println("Skipping an unreadable file: " + f.getAbsolutePath());
				continue;
			} else if(!f.isFile()) {
				System.out.println("Skipping a non-file: " + f.getAbsolutePath());
				continue;
			} else if(f.isHidden()) {
				System.out.println("Skipping a hidden file: " + f.getAbsolutePath());
				continue;
			}
			
			imageFiles.add(f);
		}
		
		Collections.sort(imageFiles);
	}
	
	private void cacheNewErrorImage(File key, String msg) {
		int width = frame.getWidth();
		int height = frame.getHeight();
		
		BufferedImage bi = new BufferedImage(width, height, Image.SCALE_DEFAULT);
		bi.createGraphics().drawString(msg, width/3, height/2);
		
		imageCache.put(key, bi);
	}
	
	private BufferedImage checkNextEmpty() {
		//we're not at the end yet
		if(imageIterator != null && imageIterator.hasNext()) {
			return null;
		}
		
		//loop around to the beginning
		imageIterator = imageFiles.listIterator();
		if(imageIterator.hasNext()) {
			return null;
		}
		
		//no images, so use an error image
		if(imageCache.containsKey(null)) {
			return imageCache.get(null);
		}
		
		cacheNewErrorImage(null, "No slides found (>__<)");
		return imageCache.get(null);
	}
	
	private BufferedImage checkPrevEmpty() {
		//we're not at the beginning yet
		if(imageIterator != null && imageIterator.hasPrevious()) {
			return null;
		}
		
		//loop around to the end
		imageIterator = imageFiles.listIterator(imageFiles.size());
		if(imageIterator.hasPrevious()) {
			return null;
		}
		
		//no images, so use an error image
		if(imageCache.containsKey(null)) {
			return imageCache.get(null);
		}
		
		//cache a new error image
		cacheNewErrorImage(null, "No slides found (>__<)");
		return imageCache.get(null);
	}
	
	private BufferedImage findImage(File file) {
		//check cache
		if(imageCache.containsKey(file)) {
			return imageCache.get(file);
		}
		
		//image not cached, so we need to load it
		BufferedImage bi = null;
		try {
			if(file != null) {
				System.out.println("Loading image: " + file.getAbsolutePath());
				bi = ImageIO.read(file);
			} else {
				System.out.println("Loading image: (file is null)");
			}
		} catch (IOException e) {
			
		} finally {
			//cache a new error image if we couldn't load the file
			if(bi == null) {
				int width = frame.getWidth();
				int height = frame.getHeight();
				String name = "[null]";
				if(file != null) {
					name = file.getName();
				}
				
				bi = new BufferedImage(width, height, Image.SCALE_DEFAULT);
				bi.createGraphics().drawString("Invalid image: " + name, width/3, height/2);
			}
		}
		imageCache.put(file, bi);
		
		return imageCache.get(file);
	}
	
	private BufferedImage prevImage() {
		BufferedImage bi = checkPrevEmpty();
		if(bi != null) {
			return bi;
		}
		
		File file = null;
		try {
			file = imageIterator.next();
		} catch(Exception e) {
			imageIterator = null; //clear the iterator so it's reset next time
			System.err.println("Can't get the previous image: " + e.getMessage());
			e.printStackTrace();
		}
		
		return findImage(file);
	}
	
	private BufferedImage nextImage() {
		BufferedImage bi = checkNextEmpty();
		if(bi != null) {
			return bi;
		}
		
		File file = null;
		try {
			file = imageIterator.next();
		} catch(Exception e) {
			imageIterator = null; //clear the iterator so it's reset next time
			System.err.println("Can't get the next image: " + e.getMessage());
			e.printStackTrace();
		}
		
		return findImage(file);
		
	}
	
	//--------------------------------------------------------------------------
	// Methods from Actions
	//--------------------------------------------------------------------------
	
	@Override
	public void nextSlide(boolean userAction) {
		System.out.println("Next slide.  (user action: " + userAction + ")");
		
		BufferedImage bi = nextImage();
		
		frame.getContentPane().prepareImage(bi, null);
		Graphics g = frame.getContentPane().getGraphics();
		g.drawImage(bi, 0, 0, null);
	}
	
	@Override
	public void previousSlide(boolean userAction) {
		System.out.println("Prev slide.  (user action: " + userAction + ")");
		
		BufferedImage bi = prevImage();
		
		frame.getContentPane().prepareImage(bi, null);
		Graphics g = frame.getContentPane().getGraphics();
		g.drawImage(bi, 0, 0, null);
	}
	
}
