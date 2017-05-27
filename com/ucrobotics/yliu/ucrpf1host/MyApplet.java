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
    private JLabel printProgressCurrentLineJLabel = null;
    private JLabel printProgressTotalLineJLabel = null;
    private JLabel printProgressEstimateTimeJLabel = null;
    private JTextField loadFilamentTemperatureJTextField = null;

    private ResourceBundle resources = ResourceBundle.getBundle("ucrpf1host");
    
    /**
     * init the UI layout and connect the ActionListeners
     */
    public void init() {
	super.init();
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
	progressBar.setStringPainted(true);
	progressBarGC.gridx = 2;
	progressBarGC.gridy = 1;
	progressBarGC.gridwidth = 3;
	progressBarGC.gridheight = 1;
	progressBarGC.weightx = 1;
	progressBarGC.weighty = 0;
	progressBarGC.fill = GridBagConstraints.BOTH;
	progressBarGC.anchor = GridBagConstraints.WEST;
	progressPanel.add(progressBar, progressBarGC);

	JLabel progressLabel = new JLabel(resources.getString("Progress_COLON"));
	GridBagConstraints progressLabelGC = new GridBagConstraints();
	progressLabelGC.gridx = 1;
	progressLabelGC.gridy = 1;
	progressLabelGC.gridwidth = 1;
	progressLabelGC.gridheight = 1;
	progressLabelGC.weightx = 1;
	progressLabelGC.weighty = 0;
	progressLabelGC.fill = GridBagConstraints.NONE;
	progressLabelGC.anchor = GridBagConstraints.EAST;
	progressPanel.add(progressLabel, progressLabelGC);

	JLabel linesLabel = new JLabel(resources.getString("Lines_COLON"));
	GridBagConstraints linesLabelGC = new GridBagConstraints();
	linesLabelGC.gridx = 1;
	linesLabelGC.gridy = 2;
	linesLabelGC.gridwidth = 1;
	linesLabelGC.gridheight = 1;
	linesLabelGC.weightx = 1;
	linesLabelGC.weighty = 0;
	linesLabelGC.fill = GridBagConstraints.NONE;
	linesLabelGC.anchor = GridBagConstraints.EAST;
	progressPanel.add(linesLabel, linesLabelGC);
	
	JLabel currentLineLabel = new JLabel();
	GridBagConstraints currentLineLabelGC = new GridBagConstraints();
	currentLineLabelGC.gridx = 2;
	currentLineLabelGC.gridy = 2;
	currentLineLabelGC.gridwidth = 1;
	currentLineLabelGC.gridheight = 1;
	currentLineLabelGC.weightx = 1;
	currentLineLabelGC.weighty = 0;
	currentLineLabelGC.fill = GridBagConstraints.NONE;
	currentLineLabelGC.anchor = GridBagConstraints.CENTER;
	progressPanel.add(currentLineLabel, currentLineLabelGC);
	this.printProgressCurrentLineJLabel = currentLineLabel;

	JLabel slashLabel1 = new JLabel("/");
	GridBagConstraints slashLabel1GC = new GridBagConstraints();
	slashLabel1GC.gridx = 3;
	slashLabel1GC.gridy = 2;
	slashLabel1GC.gridwidth = 1;
	slashLabel1GC.gridheight = 1;
	slashLabel1GC.weightx = 1;
	slashLabel1GC.weighty = 0;
	slashLabel1GC.fill = GridBagConstraints.NONE;
	slashLabel1GC.anchor = GridBagConstraints.CENTER;
	progressPanel.add(slashLabel1, slashLabel1GC);

	JLabel totalLineLabel = new JLabel();
	GridBagConstraints totalLineLabelGC = new GridBagConstraints();
	totalLineLabelGC.gridx = 4;
	totalLineLabelGC.gridy = 2;
	totalLineLabelGC.gridwidth = 1;
	totalLineLabelGC.gridheight = 1;
	totalLineLabelGC.weightx = 1;
	totalLineLabelGC.weighty = 0;
	totalLineLabelGC.fill = GridBagConstraints.NONE;
	totalLineLabelGC.anchor = GridBagConstraints.CENTER;
	progressPanel.add(totalLineLabel, totalLineLabelGC);
	this.printProgressTotalLineJLabel = totalLineLabel;
	
	JLabel estimateTimeHeadLabel = new JLabel(resources.getString("Estimate_Time_COLON"));
	GridBagConstraints estimateTimeHeadLabelGC = new GridBagConstraints();
	estimateTimeHeadLabelGC.gridx = 1;
	estimateTimeHeadLabelGC.gridy = 3;
	estimateTimeHeadLabelGC.gridwidth = 1;
	estimateTimeHeadLabelGC.gridheight = 1;
	estimateTimeHeadLabelGC.weightx = 1;
	estimateTimeHeadLabelGC.weighty = 0;
	estimateTimeHeadLabelGC.fill = GridBagConstraints.NONE;
	estimateTimeHeadLabelGC.anchor = GridBagConstraints.EAST;
	progressPanel.add(estimateTimeHeadLabel, estimateTimeHeadLabelGC);
	
	JLabel estimateTimeLabel = new JLabel();
	GridBagConstraints estimateTimeLabelGC = new GridBagConstraints();
	estimateTimeLabelGC.gridx = 2;
	estimateTimeLabelGC.gridy = 3;
	estimateTimeLabelGC.gridwidth = 3;
	estimateTimeLabelGC.gridheight = 1;
	estimateTimeLabelGC.weightx = 1;
	estimateTimeLabelGC.weighty = 0;
	estimateTimeLabelGC.fill = GridBagConstraints.NONE;
	estimateTimeLabelGC.anchor = GridBagConstraints.CENTER;
	progressPanel.add(estimateTimeLabel, estimateTimeLabelGC);
	this.printProgressEstimateTimeJLabel = estimateTimeLabel;

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

	this.loadFilamentTemperatureJTextField = textField1;
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

	JButton printButton = new JButton(resources.getString("Print"), loadIcon("/images/print.png", 70));
	printButton.addActionListener(new PrintButtonActionListener());

	JButton filamentButton = new JButton(resources.getString("Filament"), loadIcon("/images/filament.png", 70));
	filamentButton.addActionListener(new FilamentButtonActionListener());

	JButton preheatButton = new JButton(resources.getString("Preheat"), loadIcon("/images/preheat.png", 70));
	preheatButton.addActionListener(new PreheatButtonActionListener());

	JButton utilitiesButton = new JButton(resources.getString("Utilities"), loadIcon("/images/utilities.png", 70));
	utilitiesButton.addActionListener(new UtilitiesButtonActionListener());

	JButton settingsButton = new JButton(resources.getString("Settings"), loadIcon("/images/settings.png", 70));
	settingsButton.addActionListener(new SettingsButtonActionListener());

	JButton infoButton = new JButton(resources.getString("Info"), loadIcon("/images/info.png", 70));
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

    private ImageIcon loadIcon(String fileName, int iconSize){
	ImageIcon ret = null;
	ImageIcon orig = null;
	orig = new javax.swing.ImageIcon(getClass().getResource(fileName));
	if (orig != null) {
	    Image im = orig.getImage();
	    Image imScaled = null;
	    if (im.getWidth(null) > im.getHeight(null)) {
		imScaled = im.getScaledInstance(iconSize,-1,Image.SCALE_SMOOTH);
	    } else {
		imScaled = im.getScaledInstance(-1,iconSize,Image.SCALE_SMOOTH);
	    }
	    ret = new javax.swing.ImageIcon(imScaled);
	}
	return ret;
    }

    public void stop() {
	if (loadFilamentThread != null) {
	    loadFilamentThread.pleaseStop();
	    try {
		loadFilamentThread.join(2000);
	    } catch (InterruptedException e1) {
		e1.printStackTrace();
	    }
	    loadFilamentThread = null;
	}
	if (unloadFilamentThread != null) {
	    unloadFilamentThread.pleaseStop();
	    try {
		unloadFilamentThread.join(2000);
	    } catch (InterruptedException e1) {
		e1.printStackTrace();
	    }
	    unloadFilamentThread = null;
	}
	if (fileCommandSenderThread != null) {
	    fileCommandSenderThread.pleaseStop();
	    try {
		fileCommandSenderThread.join(2000);
	    } catch (InterruptedException e1) {
		e1.printStackTrace();
	    }
	    fileCommandSenderThread = null;
	    printProgressJProgressBar.setIndeterminate(true);
	}
	if (pf1Device != null) {
	    pf1Device.close(5000);
	    pf1Device = null;
	}
	super.stop();
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
		fileCommandSenderThread.addPropertyChangeListener("currentLine", new FileCommandSenderProgressChangeListener(printProgressJProgressBar, printProgressCurrentLineJLabel, printProgressTotalLineJLabel, printProgressEstimateTimeJLabel));

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
	    loadFilamentThread.addPropertyChangeListener("status", new MyAppletPropertyChangeListener(loadFilamentStatusJTextField));
	    loadFilamentThread.start();
	    goToCard("LoadFilamentPanel");
	}
    }

    class UnloadFilamentButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Unload Filamente");
	    unloadFilamentThread = new UnloadFilamentCommandSender(pf1Device);
	    unloadFilamentThread.addPropertyChangeListener("status", new MyAppletPropertyChangeListener(loadFilamentStatusJTextField));
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
	    if (pf1Device != null) {
		pf1Device.addPropertyChangeListener("extruderTemperature", new MyAppletPropertyChangeListener(loadFilamentTemperatureJTextField));
		goToCard("MainPanel");
	    }
	}
    }

    class MyAppletPropertyChangeListener implements java.beans.PropertyChangeListener {
	private javax.swing.text.JTextComponent jTextComponent = null;
	private JLabel jLabel = null;
	public MyAppletPropertyChangeListener(javax.swing.text.JTextComponent jTextComponent) {
	    this.jTextComponent = jTextComponent;
	}
	public MyAppletPropertyChangeListener(JLabel jLabel) {
	    this.jLabel = jLabel;
	}
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
	    if (this.jTextComponent != null) {
		this.jTextComponent.setText(evt.getNewValue().toString());
	    }
	    if (this.jLabel != null) {
		this.jLabel.setText(evt.getNewValue().toString());
	    }
	}
    }

    class FileCommandSenderProgressChangeListener implements java.beans.PropertyChangeListener {
	JProgressBar jProgressBar = null;
	JLabel jCurrentLine = null;
	JLabel jTotalLine = null;
	JLabel jEstimateTime = null;
	public FileCommandSenderProgressChangeListener(JProgressBar jProgressBar, JLabel jCurrentLine, JLabel jTotalLine, JLabel jEstimateTime) {
	    this.jProgressBar = jProgressBar;
	    this.jCurrentLine = jCurrentLine;
	    this.jTotalLine = jTotalLine;
	    this.jEstimateTime = jEstimateTime;
	}
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
	    Object sourceO = evt.getSource();
	    FileCommandSender fcs = null;
	    if (sourceO instanceof FileCommandSender) {
		fcs = (FileCommandSender)sourceO;
	    }
	    if (fcs == null) {
		if (this.jProgressBar != null) {
		    this.jProgressBar.setIndeterminate(true);
		}
		return;
	    }
	    if (this.jProgressBar != null) {
		this.jProgressBar.setIndeterminate(false);
		this.jProgressBar.setMinimum(0);
		this.jProgressBar.setMaximum(fcs.getNumberOfLines());
		this.jProgressBar.setValue(fcs.getCurrentLine());
	    }
	    if (this.jCurrentLine != null) {
		this.jCurrentLine.setText(String.format("%1$d", fcs.getCurrentLine()));
	    }
	    if (this.jTotalLine != null) {
		this.jTotalLine.setText(String.format("%1$d", fcs.getNumberOfLines()));
	    }
	    java.time.LocalDateTime startTime = fcs.getStartTime();
	    if (startTime != null && fcs.getNumberOfLines() > 0 && fcs.getCurrentLine() > 0) {
		java.time.LocalDateTime currentTime = java.time.LocalDateTime.now();
		long diffInSeconds = java.time.Duration.between(startTime, currentTime).getSeconds();
		logger.finest(String.format("diffInSeconds: %1$d", diffInSeconds));

		long estimatedSeconds = diffInSeconds * fcs.getNumberOfLines() / fcs.getCurrentLine();
		logger.finest(String.format("estimatedSeconds: %1$d", estimatedSeconds));
		java.time.LocalDateTime estimatedTime = startTime.plusSeconds(estimatedSeconds);
		logger.finest(String.format("estimatedTime: %1$s", estimatedTime.toString()));
		java.time.Duration estimatedDuration = java.time.Duration.between(startTime, estimatedTime);
		if (this.jEstimateTime != null) {
		    String t1 = estimatedTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		    this.jEstimateTime.setText(t1);
		}
	    }
	}
    }
}
