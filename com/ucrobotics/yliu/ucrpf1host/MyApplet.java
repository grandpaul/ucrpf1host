
package com.ucrobotics.yliu.ucrpf1host;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.awt.datatransfer.*;
import java.awt.image.*;
import java.applet.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;

/**
 * This class is for the GUI.
 * Please implement all the UI logics here
 *
 * @author Paul Liu
 */
public class MyApplet extends JApplet {

    private JPanel cards = null;
    private java.util.logging.Logger logger = null;
    public static String loggerName = "MainLogger";
    private PF1Device pf1Device = null;
    private LoadFilamentCommandSender loadFilamentThread = null;
    private UnloadFilamentCommandSender unloadFilamentThread = null;

    /**
     * init the UI layout and connect the ActionListeners
     */
    public void init() {
	Container cp = getContentPane();

	this.logger = java.util.logging.Logger.getLogger(MyApplet.loggerName);
	this.logger.setLevel(java.util.logging.Level.INFO);

	cp.setLayout(new BorderLayout());

	cards = new JPanel(new CardLayout());
	cp.add(cards, BorderLayout.CENTER);

	JPanel mainPanel = createMainPanel();
	cards.add(mainPanel, "MainPanel");
	
	JPanel filamentPanel = createFilamentPanel();
	cards.add(filamentPanel, "FilamentPanel");

	JPanel connectPanel = createConnectPanel();
	cards.add(connectPanel, "ConnectPanel");

	JPanel printingInfoPanel = createPrintingInfoPanel();
	cards.add(printingInfoPanel, "PrintingInfoPanel");

	cards.add(createLoadFilamentPanel(), "LoadFilamentPanel");
	
	/* default card */
	goToCard("ConnectPanel");
    }

    /**
     * Create "PrintingInfoPanel".
     * This function should be only called once in 
     * init().
     *
     * @return: a JPanel for "PrintingInfoPanel"
     */
    private JPanel createPrintingInfoPanel() {
	JPanel printingInfoPanel = new JPanel();
	printingInfoPanel.setLayout(new BorderLayout());
	
	JButton backToMainButton = new JButton("Back to Main");
	backToMainButton.addActionListener(new BackToMainButtonActionListener());
	printingInfoPanel.add(backToMainButton, BorderLayout.SOUTH);

	JPanel progressPanel = new JPanel();
	progressPanel.setLayout(new GridBagLayout());
	JProgressBar progressBar = new JProgressBar();
	GridBagConstraints progressBarGC = new GridBagConstraints();
	progressBar.setIndeterminate(true);
	progressBarGC.gridx = 2;
	progressBarGC.gridy = 2;
	progressBarGC.gridwidth = 1;
	progressBarGC.gridheight = 1;
	progressBarGC.weightx = 0;
	progressBarGC.weighty = 0;
	progressBarGC.fill = GridBagConstraints.BOTH;
	progressBarGC.anchor = GridBagConstraints.CENTER;
	progressPanel.add(progressBar, progressBarGC);

	JLabel progressLabel = new JLabel("Progress:");
	GridBagConstraints progressLabelGC = new GridBagConstraints();
	progressLabelGC.gridx = 1;
	progressLabelGC.gridy = 2;
	progressLabelGC.gridwidth = 1;
	progressLabelGC.gridheight = 1;
	progressLabelGC.weightx = 0;
	progressLabelGC.weighty = 0;
	progressLabelGC.fill = GridBagConstraints.BOTH;
	progressLabelGC.anchor = GridBagConstraints.CENTER;
	progressPanel.add(progressLabel, progressLabelGC);
	
	printingInfoPanel.add(progressPanel, BorderLayout.CENTER);
	
	return printingInfoPanel;
    }

    /**
     * Create "ConnectPanel".
     * This function should be only called once in 
     * init().
     *
     * @return: a JPanel for "ConnectPanel"
     */
    private JPanel createConnectPanel() {
	JPanel connectPanel = new JPanel();
	connectPanel.setLayout(new FlowLayout());

	ArrayList<String> devices = PF1Device.listDevices();
	JComboBox<String> deviceComboBox = new JComboBox<String>();
	for (String dev : devices) {
	    deviceComboBox.addItem(dev);
	}
	//deviceComboBox.setEditable(true);
	
	JButton connectButton = new JButton("Connect");
	connectButton.addActionListener(new ConnectButtonActionListener(deviceComboBox));
	
	connectPanel.add(deviceComboBox);
	connectPanel.add(connectButton);
	return connectPanel;
    }
    
    /**
     * Create "FilamentPanel".
     * This function should be only called once in 
     * init().
     *
     * @return: a JPanel for "FilamentPanel"
     */
    private JPanel createFilamentPanel() {
	JPanel filamentPanel = new JPanel();
	filamentPanel.setLayout(new BorderLayout());

	JButton loadFilamentButton = new JButton("Load Filament");
	loadFilamentButton.addActionListener(new LoadFilamentButtonActionListener());
	
	JButton unloadFilamentButton = new JButton("Unload Filament");
	unloadFilamentButton.addActionListener(new UnloadFilamentButtonActionListener());

	JPanel filamentPanelBox1 = new JPanel();
	filamentPanelBox1.setLayout(new GridLayout(1,2));
	
	JButton backToMainButton = new JButton("Back to Main");
	backToMainButton.addActionListener(new BackToMainButtonActionListener());

	filamentPanelBox1.add(loadFilamentButton);
	filamentPanelBox1.add(unloadFilamentButton);
	filamentPanel.add(filamentPanelBox1, BorderLayout.CENTER);
	filamentPanel.add(backToMainButton, BorderLayout.SOUTH);
	return filamentPanel;
    }

    /**
     * Create "LoadFilamentPanel".
     * This function should be only called once in 
     * init().
     *
     * @return: a JPanel for "FilamentPanel"
     */
    private JPanel createLoadFilamentPanel() {
	JPanel loadFilamentPanel = new JPanel();
	loadFilamentPanel.setLayout(new BorderLayout());

	JLabel label1 = new JLabel("Temperature: ");
	JTextField textField1 = new JTextField();
	JLabel label2 = new JLabel("Status: ");
	JTextField textField2 = new JTextField();
	

	JPanel loadFilamentPanelBox1 = new JPanel();
	loadFilamentPanelBox1.setLayout(new GridLayout(2,2));

	
	
	JButton stopButton = new JButton("Stop Loading");
	stopButton.addActionListener(new StopLoadFilamentButtonActionListener());

	loadFilamentPanelBox1.add(label1);
	loadFilamentPanelBox1.add(textField1);
	loadFilamentPanelBox1.add(label2);
	loadFilamentPanelBox1.add(textField2);
	loadFilamentPanel.add(loadFilamentPanelBox1, BorderLayout.CENTER);
	loadFilamentPanel.add(stopButton, BorderLayout.SOUTH);
	return loadFilamentPanel;
    }

    
    /**
     * Create "MainPanel".
     * This function should be only called once in 
     * init().
     *
     * @return: a JPanel for "MainPanel"
     */
    private JPanel createMainPanel() {
	JPanel mainPanel = new JPanel();
	mainPanel.setLayout(new GridLayout(2,3));

	JButton printButton = new JButton("Print");
	printButton.addActionListener(new PrintButtonActionListener());

	JButton filamentButton = new JButton("Filament");
	filamentButton.addActionListener(new FilamentButtonActionListener());

	JButton preheatButton = new JButton("Preheat");
	preheatButton.addActionListener(new PreheatButtonActionListener());

	JButton utilitiesButton = new JButton("Utilities");
	utilitiesButton.addActionListener(new UtilitiesButtonActionListener());

	JButton settingsButton = new JButton("Settings");
	settingsButton.addActionListener(new SettingsButtonActionListener());

	JButton infoButton = new JButton("Info");
	infoButton.addActionListener(new InfoButtonActionListener());
	
	mainPanel.add(printButton);
	mainPanel.add(filamentButton);
	mainPanel.add(preheatButton);
	mainPanel.add(utilitiesButton);
	mainPanel.add(settingsButton);
	mainPanel.add(infoButton);

	return mainPanel;
    }

    /**
     * Go to the card registered by name
     *
     * @name: the name of the card
     */
    public void goToCard(String name) {
	((CardLayout)cards.getLayout()).show(cards, name);
    }

    class PrintButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Print");
	    JFileChooser chooser = new JFileChooser();
	    javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("GCode files", "gcode");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(MyApplet.this);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
		logger.info("You chose to open this file: " +
			    chooser.getSelectedFile().getName());
		
		goToCard("PrintingInfoPanel");
	    }
	}
    }

    class FilamentButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Filament");
	    goToCard("FilamentPanel");
	}
    }

    class PreheatButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Preheat");
	}
    }

    class UtilitiesButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Utilities");
	}
    }

    class SettingsButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Settings");
	}
    }

    class InfoButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Info");
	}
    }

    class LoadFilamentButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Load Filamente");
	    loadFilamentThread = new LoadFilamentCommandSender(pf1Device);
	    loadFilamentThread.start();
	    goToCard("LoadFilamentPanel");
	}
    }

    class UnloadFilamentButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Unload Filamente");
	    unloadFilamentThread = new UnloadFilamentCommandSender(pf1Device);
	    unloadFilamentThread.start();
	    goToCard("LoadFilamentPanel");
	}
    }

    class StopLoadFilamentButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Stop Load Filamente");
	    if (loadFilamentThread != null) {
		loadFilamentThread.pleaseStop();
		try {
		    loadFilamentThread.join();
		} catch (InterruptedException e1) {
		    e1.printStackTrace();
		}
		loadFilamentThread = null;
	    }
	    if (unloadFilamentThread != null) {
		unloadFilamentThread.pleaseStop();
		try {
		    unloadFilamentThread.join();
		} catch (InterruptedException e1) {
		    e1.printStackTrace();
		}
		unloadFilamentThread = null;
	    }
	    goToCard("FilamentPanel");
	}
    }

    class BackToMainButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Unload Filamente");
	    goToCard("MainPanel");
	}
    }

    class ConnectButtonActionListener implements ActionListener {
	private JComboBox deviceComboBox = null;
	public ConnectButtonActionListener(JComboBox deviceComboBox) {
	    this.deviceComboBox = deviceComboBox;
	}
	public void actionPerformed(ActionEvent e) {
	    logger.info("Connect");
	    goToCard("MainPanel");
	    try {
		pf1Device = new PF1Device((String)deviceComboBox.getSelectedItem());
	    } catch (java.io.FileNotFoundException e1) {
		logger.info("Get FileNotFoundException when creating PF1Device");
		pf1Device = null;
	    } catch (gnu.io.PortInUseException e1) {
		logger.info("Get PortInUseException when creating PF1Device");
		pf1Device = null;
	    } catch (gnu.io.UnsupportedCommOperationException e1) {
		logger.info("Get UnsupportedCommOperationException when creating PF1Device");
		pf1Device = null;
	    }		
	}
    }
    
}
