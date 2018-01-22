package de.pangan.tstringviewer;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.dalsemi.onewire.OneWireAccessProvider;
import com.dalsemi.onewire.OneWireException;
import com.dalsemi.onewire.adapter.DSPortAdapter;
import com.dalsemi.onewire.container.MemoryBank;
import com.dalsemi.onewire.container.OneWireContainer;
import com.dalsemi.onewire.container.OneWireContainer2D;
import com.dalsemi.onewire.utils.Convert;

public class GUI extends JFrame {
	
	public final String ADAPTER_TYPE;
	public final String PORT;
	
	public GUI(String adapterType, String port){
		PORT = port;
		ADAPTER_TYPE = adapterType;
		
		this.setBounds(25, 25, 450, 600);
		this.setTitle("T-String-Viewer");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		ArrayList<JPanel> tStrings = getTStrings();
		if(tStrings !=  null){
			for(int i = 0; i < tStrings.size(); i++){
				tabbedPane.add(String.valueOf(i+1), tStrings.get(i));
			}
		}

		this.add(tabbedPane);
	}
	
	public ArrayList<JPanel> getTStrings(){
		int off = 0;
		int cols = 8;
		int len = 8;
		String[] adress = new String[7];
		JPanel jpnltmp;
		ArrayList<JPanel> returnJpnl = new ArrayList<JPanel>();
		try {
			DSPortAdapter adapter = OneWireAccessProvider.getAdapter(ADAPTER_TYPE, PORT);
			boolean hasMoreDevices = true;
			while(hasMoreDevices){
				OneWireContainer owc = adapter.getNextDeviceContainer();
				if(owc != null){
					if(owc.getName() == "DS1972"){		// check if device is 1024-Bit EEPROM
						adress[0] = owc.getAddressAsString();
						jpnltmp = new JPanel();
						jpnltmp.setLayout(new GridLayout(10, 10));
						OneWireContainer2D owc2d = (OneWireContainer2D) owc;
						Enumeration pages = owc2d.getMemoryBanks();
						// read adresses of the temperature sensors from eeprom
						while(pages.hasMoreElements()){
							MemoryBank mb = (MemoryBank) pages.nextElement();
							int size = mb.getSize();
							byte[] readBuf = new byte[size];
							mb.read(0,false,readBuf,0,size);
							for(int i = 1; i < 7 && mb.getSize() > 8; i++){
								int start = i*8;
								adress[i] = Convert.toHexString(readBuf, start, Math.min(8, mb.getSize()-start), "");
							}
						}
						// add images labeled with corresponding adresses
						for(int i = 0; i < adress.length; i++){
							if(i != adress.length - 1){
								ImageIcon icon = createImageIcon("midElement.png", "");
								jpnltmp.add(new JLabel(icon));
							}else{
								ImageIcon icon = createImageIcon("bottomElement.png", "");
								jpnltmp.add(new JLabel(icon));
							}
							jpnltmp.add(new JLabel(adress[i]));
						}
						returnJpnl.add(jpnltmp);
					}
				}else{
					hasMoreDevices = false;
				}
			}
			return returnJpnl;
		} catch (OneWireException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path, String description) {
	    java.net.URL imgURL = getClass().getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL, description);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	}

}
