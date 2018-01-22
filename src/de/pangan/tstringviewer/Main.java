package de.pangan.tstringviewer;

import de.pangan.tstringviewer.gui;

public class Main {
	
	public static final String ADAPTER_TYPE = "DS9097U";
	public static final String PORT = "COM1";

	public static void main(String[] args) {
		GUI gui = null;
		if(args.length == 2){
			gui = new GUI(args[0], args[1]);
		}else{
			gui = new GUI(ADAPTER_TYPE, PORT);
		}
		gui.setVisible(true);
	}
}
