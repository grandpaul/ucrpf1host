/*
    Copyright (C) 2017  Ying-Chun Liu (PaulLiu) <paulliu@debian.org>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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
    private FileCommandSender fileCommandSenderThread = null;

    private JTextField loadFilamentStatusJTextField = null;
    private JProgressBar printProgressJProgressBar = null;

    private ResourceBundle resources = ResourceBundle.getBundle("ucrpf1host");
    
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
	
	JButton backToMainButton = new JButton(resources.getString("Back_to_Main"));
	backToMainButton.addActionListener(new BackToMainButtonActionListener());
	printingInfoPanel.add(backToMainButton, BorderLayout.SOUTH);

	JPanel progressPanel = new JPanel();
	progressPanel.setLayout(new GridBagLayout());
	JProgressBar progressBar = new JProgressBar();
	this.printProgressJProgressBar = progressBar;
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

	JLabel progressLabel = new JLabel(resources.getString("Progress_COLON"));
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
	
	JButton connectButton = new JButton(resources.getString("Connect"));
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

	JButton loadFilamentButton = new JButton(resources.getString("Load_Filament"));
	loadFilamentButton.addActionListener(new LoadFilamentButtonActionListener());
	
	JButton unloadFilamentButton = new JButton(resources.getString("Unload_Filament"));
	unloadFilamentButton.addActionListener(new UnloadFilamentButtonActionListener());

	JPanel filamentPanelBox1 = new JPanel();
	filamentPanelBox1.setLayout(new GridLayout(1,2));
	
	JButton backToMainButton = new JButton(resources.getString("Back_to_Main"));
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

	JLabel label1 = new JLabel(resources.getString("Temperature_COLON"));
	JTextField textField1 = new JTextField();
	JLabel label2 = new JLabel(resources.getString("Status_COLON"));
	JTextField textField2 = new JTextField();

	this.loadFilamentStatusJTextField = textField2;

	JPanel loadFilamentPanelBox1 = new JPanel();
	loadFilamentPanelBox1.setLayout(new GridLayout(2,2));

	
	
	JButton stopButton = new JButton(resources.getString("Stop_Loading"));
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

	JButton printButton = new JButton(resources.getString("Print"));
	printButton.addActionListener(new PrintButtonActionListener());

	JButton filamentButton = new JButton(resources.getString("Filament"));
	filamentButton.addActionListener(new FilamentButtonActionListener());

	JButton preheatButton = new JButton(resources.getString("Preheat"));
	preheatButton.addActionListener(new PreheatButtonActionListener());

	JButton utilitiesButton = new JButton(resources.getString("Utilities"));
	utilitiesButton.addActionListener(new UtilitiesButtonActionListener());

	JButton settingsButton = new JButton(resources.getString("Settings"));
	settingsButton.addActionListener(new SettingsButtonActionListener());

	JButton infoButton = new JButton(resources.getString("Info"));
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
		fileCommandSenderThread = new FileCommandSender(pf1Device, chooser.getSelectedFile());
		
		fileCommandSenderThread.addProgressBar(printProgressJProgressBar);
		fileCommandSenderThread.start();
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
	    loadFilamentThread.addPropertyChangeListener(new LoadFilamentCommandSenderPropertyChangeListener(loadFilamentStatusJTextField));
	    loadFilamentThread.start();
	    goToCard("LoadFilamentPanel");
	}
    }

    class UnloadFilamentButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Unload Filamente");
	    unloadFilamentThread = new UnloadFilamentCommandSender(pf1Device);
	    unloadFilamentThread.addPropertyChangeListener(new LoadFilamentCommandSenderPropertyChangeListener(loadFilamentStatusJTextField));
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
	    if (fileCommandSenderThread != null) {
		fileCommandSenderThread.pleaseStop();
		try {
		    fileCommandSenderThread.join();
		} catch (InterruptedException e1) {
		    e1.printStackTrace();
		}
		fileCommandSenderThread = null;
		printProgressJProgressBar.setIndeterminate(true);
	    }
	    goToCard("FilamentPanel");
	}
    }

    class BackToMainButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("BackToMainButton");
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
	    if (fileCommandSenderThread != null) {
		fileCommandSenderThread.pleaseStop();
		try {
		    fileCommandSenderThread.join();
		} catch (InterruptedException e1) {
		    e1.printStackTrace();
		}
		fileCommandSenderThread = null;
		printProgressJProgressBar.setIndeterminate(true);
	    }
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

    class LoadFilamentCommandSenderPropertyChangeListener implements java.beans.PropertyChangeListener {
	private javax.swing.text.JTextComponent jTextComponent = null;
	private JLabel jLabel = null;
	public LoadFilamentCommandSenderPropertyChangeListener(javax.swing.text.JTextComponent jTextComponent) {
	    this.jTextComponent = jTextComponent;
	}
	public LoadFilamentCommandSenderPropertyChangeListener(JLabel jLabel) {
	    this.jLabel = jLabel;
	}
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
	    if (evt.getPropertyName().compareTo("status")==0) {
		if (this.jTextComponent != null) {
		    this.jTextComponent.setText((String)evt.getNewValue());
		}
		if (this.jLabel != null) {
		    this.jLabel.setText((String)evt.getNewValue());
		}
	    }
	}
    }
}
