
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

public class MyApplet extends JApplet {

    private JPanel cards = null;
    private java.util.logging.Logger logger = null;
    public static String loggerName = "MainLogger";

    public void init() {
	Container cp = getContentPane();
	JPanel mainPanel = null;
	JPanel filamentPanel = null;
	JPanel connectPanel = null;

	this.logger = java.util.logging.Logger.getLogger(MyApplet.loggerName);
	this.logger.setLevel(java.util.logging.Level.INFO);

	cp.setLayout(new GridLayout(1,1));

	cards = new JPanel(new CardLayout());
	cp.add(cards);

	mainPanel = new JPanel();
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

	filamentPanel = new JPanel();
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

	connectPanel = new JPanel();
	connectPanel.setLayout(new FlowLayout());

	ArrayList<String> devices = PF1Device.listDevices();
	JComboBox deviceComboBox = new JComboBox();
	for (String dev : devices) {
	    deviceComboBox.addItem(dev);
	}
	JButton connectButton = new JButton("Connect");
	connectButton.addActionListener(new ConnectButtonActionListener());
	
	connectPanel.add(deviceComboBox);
	connectPanel.add(connectButton);

	cards.add(mainPanel, "MainPanel");
	cards.add(filamentPanel, "FilamentPanel");
	cards.add(connectPanel, "ConnectPanel");


	/* default card */
	((CardLayout)cards.getLayout()).show(cards, "ConnectPanel");

    }

    class PrintButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Print");
	}
    }
    class FilamentButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Filament");
	    CardLayout cl = (CardLayout)cards.getLayout();
	    cl.show(cards, "FilamentPanel");
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
	}
    }
    class UnloadFilamentButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Unload Filamente");
	}
    }
    class BackToMainButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Unload Filamente");
	    CardLayout cl = (CardLayout)cards.getLayout();
	    cl.show(cards, "MainPanel");
	}
    }
    class ConnectButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Connect");
	    ((CardLayout)cards.getLayout()).show(cards, "MainPanel");
	}
    }
    
}
