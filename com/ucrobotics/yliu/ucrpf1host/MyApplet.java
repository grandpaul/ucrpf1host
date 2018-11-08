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
public class MyApplet extends Panel {

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
    private JTextField infoTemperatureJTextField = null;
    private PointPanel utilitiesExtruderPositionPanel = null;
    private JSlider utilitiesExtruderHeightPanel = null;

    private ResourceBundle resources = ResourceBundle.getBundle("ucrpf1host");

    public MyApplet() {
	super();
	init();
    }
    
    /**
     * init the UI layout and connect the ActionListeners
     */
    public void init() {
	//super.init();
	Container cp = this;

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
	
	cards.add(createSettingsPanel(), "SettingsPanel");
	
	cards.add(createInfoPanel(), "InfoPanel");

	cards.add(createUtilitiesPanel(), "UtilitiesPanel");

	/* default card */
	goToCard("ConnectPanel");
    }

    /**
     * Create "PrintingInfoPanel".
     * This function should be only called once in 
     * init().
     *
     * @return a JPanel for "PrintingInfoPanel"
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
     * @return a JPanel for "ConnectPanel"
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
     * @return a JPanel for "FilamentPanel"
     */
    private JPanel createFilamentPanel() {
	JPanel filamentPanel = new JPanel();
	filamentPanel.setLayout(new BorderLayout());

	JButton loadFilamentButton = new JButton(resources.getString("Load_Filament"), loadIcon("/images/loadfilament.png", 200));
	loadFilamentButton.addActionListener(new LoadFilamentButtonActionListener());
	
	JButton unloadFilamentButton = new JButton(resources.getString("Unload_Filament"), loadIcon("/images/unloadfilament.png", 200));
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
     * @return a JPanel for "FilamentPanel"
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
     * Create "SettingsPanel".
     * This function should be only called once in 
     * init().
     *
     * @return a JPanel for "SettingsPanel"
     */
    private JPanel createSettingsPanel() {
	JPanel settingsPanel = new JPanel();
	settingsPanel.setLayout(new BorderLayout());
	
	JButton backToMainButton = new JButton(resources.getString("Back_to_Main"));
	backToMainButton.addActionListener(new BackToMainButtonActionListener());
	settingsPanel.add(backToMainButton, BorderLayout.SOUTH);

	JPanel centerPanel = new JPanel();

	centerPanel.setLayout(new GridBagLayout());

	JLabel preheatTemperatureLabel = new JLabel(resources.getString("Preheat_Temperature"));
	GridBagConstraints preheatTemperatureLabelGC = new GridBagConstraints();
	preheatTemperatureLabelGC.gridx = 1;
	preheatTemperatureLabelGC.gridy = 1;
	preheatTemperatureLabelGC.gridwidth = 1;
	preheatTemperatureLabelGC.gridheight = 1;
	preheatTemperatureLabelGC.weightx = 1;
	preheatTemperatureLabelGC.weighty = 0;
	preheatTemperatureLabelGC.fill = GridBagConstraints.NONE;
	preheatTemperatureLabelGC.anchor = GridBagConstraints.EAST;
	centerPanel.add(preheatTemperatureLabel, preheatTemperatureLabelGC);

	JSpinner preheatTemperature = new JSpinner (new SpinnerNumberModel(Integer.valueOf((int)Math.round(GlobalSettings.getInstance().getExtruderPreheatTemperature())), Integer.valueOf(100), Integer.valueOf(260), Integer.valueOf(1)));
	GridBagConstraints preheatTemperatureGC = new GridBagConstraints();
	preheatTemperatureGC.gridx = 2;
	preheatTemperatureGC.gridy = 1;
	preheatTemperatureGC.gridwidth = 3;
	preheatTemperatureGC.gridheight = 1;
	preheatTemperatureGC.weightx = 1;
	preheatTemperatureGC.weighty = 0;
	preheatTemperatureGC.fill = GridBagConstraints.BOTH;
	preheatTemperatureGC.anchor = GridBagConstraints.WEST;
	centerPanel.add(preheatTemperature, preheatTemperatureGC);
	preheatTemperature.addChangeListener(new SettingsExtruderPreheatTemperatureChangeListener());

	settingsPanel.add(centerPanel, BorderLayout.CENTER);
	return settingsPanel;
    }

    /**
     * Create "InfoPanel".
     * This function should be only called once in 
     * init().
     *
     * @return a JPanel for "InfoPanel"
     */
    private JPanel createInfoPanel() {
	JPanel infoPanel = new JPanel();
	infoPanel.setLayout(new BorderLayout());
	
	JButton backToMainButton = new JButton(resources.getString("Back_to_Main"));
	backToMainButton.addActionListener(new BackToMainButtonActionListener());
	infoPanel.add(backToMainButton, BorderLayout.SOUTH);


	JPanel centerPanel = new JPanel();
	centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

	JScrollPane centerPanelScroll = new JScrollPane(centerPanel);

	JPanel panel1 = new JPanel();
	panel1.setLayout(new GridLayout(1,2));
	
	JLabel extruderTemperatureLabel = new JLabel(resources.getString("Temperature_COLON"));
	panel1.add(extruderTemperatureLabel);
	JTextField temperatureField = new JTextField();
	panel1.add(temperatureField);
	this.infoTemperatureJTextField = temperatureField;

	centerPanel.add(panel1);

	infoPanel.add(centerPanel, BorderLayout.CENTER);
	return infoPanel;
    }

    /**
     * Create "UtilitiesPanel".
     * This function should be only called once in 
     * init().
     *
     * @return a JPanel for "UtilitiesPanel"
     */
    private JPanel createUtilitiesPanel() {
	JPanel utilitiesPanel = new JPanel();
	utilitiesPanel.setLayout(new BorderLayout());
	
	JButton backToMainButton = new JButton(resources.getString("Back_to_Main"));
	backToMainButton.addActionListener(new BackToMainButtonActionListener());
	utilitiesPanel.add(backToMainButton, BorderLayout.SOUTH);


	JPanel centerPanel = new JPanel();
	centerPanel.setLayout(new GridBagLayout());

	JButton homeButton = new JButton(resources.getString("Home"));
	GridBagConstraints homeButtonGC = new GridBagConstraints();
	homeButtonGC.gridx = 1;
	homeButtonGC.gridy = 1;
	homeButtonGC.gridwidth = 2;
	homeButtonGC.gridheight = 2;
	homeButtonGC.weightx = 0;
	homeButtonGC.weighty = 0;
	homeButtonGC.fill = GridBagConstraints.BOTH;
	homeButtonGC.anchor = GridBagConstraints.WEST;
	homeButton.addActionListener(new UtilitiesHomeButtonActionListener());
	centerPanel.add(homeButton, homeButtonGC);

	JLabel extruderPositionLabel = new JLabel(resources.getString("Extruder_Position"));
	GridBagConstraints extruderPositionLabelGC = new GridBagConstraints();
	extruderPositionLabelGC.gridx = 3;
	extruderPositionLabelGC.gridy = 3;
	extruderPositionLabelGC.gridwidth = 1;
	extruderPositionLabelGC.gridheight = 1;
	extruderPositionLabelGC.weightx = 1;
	extruderPositionLabelGC.weighty = 0;
	extruderPositionLabelGC.fill = GridBagConstraints.NONE;
	extruderPositionLabelGC.anchor = GridBagConstraints.EAST;
	centerPanel.add(extruderPositionLabel, extruderPositionLabelGC);

	PointPanel extruderPositionPanel = new PointPanel();
	extruderPositionPanel.setPreferredSize(new Dimension(100,100));
	GridBagConstraints extruderPositionPanelGC = new GridBagConstraints();
	extruderPositionPanelGC.gridx = 4;
	extruderPositionPanelGC.gridy = 1;
	extruderPositionPanelGC.gridwidth = 3;
	extruderPositionPanelGC.gridheight = 3;
	extruderPositionPanelGC.weightx = 0;
	extruderPositionPanelGC.weighty = 0;
	extruderPositionPanelGC.fill = GridBagConstraints.NONE;
	extruderPositionPanelGC.anchor = GridBagConstraints.WEST;
	extruderPositionPanel.addMouseListener(new ExtruderPositionPanelMouseListener());
	centerPanel.add(extruderPositionPanel, extruderPositionPanelGC);
	this.utilitiesExtruderPositionPanel = extruderPositionPanel;

	JLabel extruderHeightLabel = new JLabel(resources.getString("Extruder_Height"));
	GridBagConstraints extruderHeightLabelGC = new GridBagConstraints();
	extruderHeightLabelGC.gridx = 9;
	extruderHeightLabelGC.gridy = 3;
	extruderHeightLabelGC.gridwidth = 1;
	extruderHeightLabelGC.gridheight = 1;
	extruderHeightLabelGC.weightx = 1;
	extruderHeightLabelGC.weighty = 0;
	extruderHeightLabelGC.fill = GridBagConstraints.NONE;
	extruderHeightLabelGC.anchor = GridBagConstraints.EAST;
	centerPanel.add(extruderHeightLabel, extruderHeightLabelGC);
	
	JSlider extruderHeightPanel = new JSlider(JSlider.VERTICAL,0,100000,0);
	GridBagConstraints extruderHeightPanelGC = new GridBagConstraints();
	extruderHeightPanelGC.gridx = 10;
	extruderHeightPanelGC.gridy = 1;
	extruderHeightPanelGC.gridwidth = 2;
	extruderHeightPanelGC.gridheight = 3;
	extruderHeightPanelGC.weightx = 0;
	extruderHeightPanelGC.weighty = 0;
	extruderHeightPanelGC.fill = GridBagConstraints.NONE;
	extruderHeightPanelGC.anchor = GridBagConstraints.WEST;
	extruderHeightPanel.addMouseListener(new ExtruderPositionPanelMouseListener());
	extruderHeightPanel.addChangeListener(new ExtruderHeightPanelChangeListener());
	centerPanel.add(extruderHeightPanel, extruderHeightPanelGC);
	this.utilitiesExtruderHeightPanel = extruderHeightPanel;

	JLabel gCodeLabel = new JLabel(resources.getString("GCode"));
	GridBagConstraints gCodeLabelGC = new GridBagConstraints();
	gCodeLabelGC.gridx = 1;
	gCodeLabelGC.gridy = 4;
	gCodeLabelGC.gridwidth = 1;
	gCodeLabelGC.gridheight = 1;
	gCodeLabelGC.weightx = 1;
	gCodeLabelGC.weighty = 0;
	gCodeLabelGC.fill = GridBagConstraints.NONE;
	gCodeLabelGC.anchor = GridBagConstraints.EAST;
	centerPanel.add(gCodeLabel, gCodeLabelGC);

	JTextField gCodeTextField = new JTextField();
	GridBagConstraints gCodeTextFieldGC = new GridBagConstraints();
	gCodeTextFieldGC.gridx = 2;
	gCodeTextFieldGC.gridy = 4;
	gCodeTextFieldGC.gridwidth = 3;
	gCodeTextFieldGC.gridheight = 1;
	gCodeTextFieldGC.weightx = 1;
	gCodeTextFieldGC.weighty = 0;
	gCodeTextFieldGC.fill = GridBagConstraints.BOTH;
	gCodeTextFieldGC.anchor = GridBagConstraints.EAST;
	centerPanel.add(gCodeTextField, gCodeTextFieldGC);

	JButton gCodeTextButton = new JButton(resources.getString("Send"));
	GridBagConstraints gCodeTextButtonGC = new GridBagConstraints();
	gCodeTextButtonGC.gridx = 5;
	gCodeTextButtonGC.gridy = 4;
	gCodeTextButtonGC.gridwidth = 1;
	gCodeTextButtonGC.gridheight = 1;
	gCodeTextButtonGC.weightx = 1;
	gCodeTextButtonGC.weighty = 0;
	gCodeTextButtonGC.fill = GridBagConstraints.BOTH;
	gCodeTextButtonGC.anchor = GridBagConstraints.EAST;
	centerPanel.add(gCodeTextButton, gCodeTextButtonGC);
	gCodeTextButton.addActionListener(new UtilitiesGCodeSendButtonActionListener(gCodeTextField));
	

	utilitiesPanel.add(centerPanel, BorderLayout.CENTER);
	return utilitiesPanel;
    }

    
    /**
     * Create "MainPanel".
     * This function should be only called once in 
     * init().
     *
     * @return a JPanel for "MainPanel"
     */
    private JPanel createMainPanel() {
	JPanel mainPanel = new JPanel();
	mainPanel.setLayout(new GridLayout(2,3));

	JButton printButton = new JButton(resources.getString("Print"), loadIcon("/images/print.png", 70));
	printButton.addActionListener(new PrintButtonActionListener());

	JButton filamentButton = new JButton(resources.getString("Filament"), loadIcon("/images/filament.png", 70));
	filamentButton.addActionListener(new FilamentButtonActionListener());

	JToggleButton preheatButton = new JToggleButton(resources.getString("Preheat"), loadIcon("/images/preheat.png", 70));
	preheatButton.addItemListener(new PreheatButtonActionListener());

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
     * @name the name of the card
     */
    public void goToCard(String name) {
	((CardLayout)cards.getLayout()).show(cards, name);
    }

    /**
     * Load an icon from jar and resize the maximum side to iconSize
     *
     * @fileName the filename of the icon in jar
     * @iconSize resize the icon to iconSize
     * @return an ImageIcon
     */
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

    /**
     * Stop the applet. Close the serial connections.
     * 
     */
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
	//super.stop();
    }

    /**
     * The ActionListener for PrintButton in MainPanel
     */
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

    /**
     * The ActionListener for FilamentButton in MainPanel
     */
    class FilamentButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Filament");
	    goToCard("FilamentPanel");
	}
    }

    /**
     * The ItemListener for PreheatButton in MainPanel
     */
    class PreheatButtonActionListener implements ActionListener, ItemListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Preheat");
	}
	public void itemStateChanged(java.awt.event.ItemEvent e1) {
	    if (e1.getStateChange() == ItemEvent.SELECTED) {
		logger.info("Preheat selected");
		if (pf1Device != null) {
		    pf1Device.sendCommand(String.format("M104 S%1$d", Math.round(GlobalSettings.getInstance().getExtruderPreheatTemperature())));
		}
	    } else if (e1.getStateChange() == ItemEvent.DESELECTED) {
		logger.info("Preheat deselected");
		if (pf1Device != null) {
		    pf1Device.sendCommand("M104 S0");
		}
	    }
	}
    }

    /**
     * The ActionListener for UtilitiesButton in MainPanel
     */
    class UtilitiesButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Utilities");
	    goToCard("UtilitiesPanel");
	}
    }

    /**
     * The ActionListener for SettingsButton in MainPanel
     */
    class SettingsButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Settings");
	    goToCard("SettingsPanel");
	}
    }

    /**
     * The ActionListener for InfoButton in MainPanel
     */
    class InfoButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Info");
	    goToCard("InfoPanel");
	}
    }

    /**
     * The ActionListener for LoadFilamentButton in FilamentPanel
     */
    class LoadFilamentButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Load Filamente");
	    loadFilamentThread = new LoadFilamentCommandSender(pf1Device);
	    loadFilamentThread.addPropertyChangeListener("status", new MyAppletPropertyChangeListener(loadFilamentStatusJTextField));
	    loadFilamentThread.start();
	    goToCard("LoadFilamentPanel");
	}
    }

    /**
     * The ActionListener for UnloadFilamentButton in FilamentPanel
     */
    class UnloadFilamentButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Unload Filamente");
	    unloadFilamentThread = new UnloadFilamentCommandSender(pf1Device);
	    unloadFilamentThread.addPropertyChangeListener("status", new MyAppletPropertyChangeListener(loadFilamentStatusJTextField));
	    unloadFilamentThread.start();
	    goToCard("LoadFilamentPanel");
	}
    }

    /**
     * The ActionListener for StopLoadFilamentButton in LoadFilamentPanel
     */
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

    /**
     * The ActionListener for BackToMainButton. Can be used in several cards.
     */
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

    /**
     * The ActionListener for ConnectButton in ConnectPanel
     */
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
		pf1Device.addPropertyChangeListener("extruderTemperature", new MyAppletPropertyChangeListener(infoTemperatureJTextField));
		pf1Device.addPropertyChangeListener(new ExtruderPositionPanelUpdater(utilitiesExtruderPositionPanel, utilitiesExtruderHeightPanel));
		goToCard("MainPanel");
	    }
	}
    }

    /**
     * The PropertyChangeListener for JLabel, JTextField
     */
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

    /**
     * This class listens to the progess from FileCommandSender class and
     * updates the JProgressBar, time estimation and the related stuff.
     */
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

    class SettingsExtruderPreheatTemperatureChangeListener implements javax.swing.event.ChangeListener {
	public void stateChanged(ChangeEvent e) {
	    Object sourceO = e.getSource();
	    if (sourceO instanceof JSpinner) {
		JSpinner jsp1 = (JSpinner)sourceO;
		int temp1 = Integer.parseInt( jsp1.getValue().toString() );
		GlobalSettings.getInstance().setExtruderPreheatTemperature((double)temp1);
	    }
	}
    }
    
    class ExtruderPositionPanelMouseListener implements MouseListener {
	public void mouseClicked(MouseEvent e) {
	    int mx = e.getX();
	    int my = e.getY();
	    Object jPanelO = e.getSource();
	    PointPanel jPanel = null;
	    if (jPanelO instanceof PointPanel) {
		jPanel = (PointPanel)jPanelO;
	    }
	    if (jPanel == null) {
		return;
	    }
	    if (e.getButton() == MouseEvent.BUTTON1) {
		double x = ((double)mx)/((double)jPanel.getWidth());
		double y = ((double)my)/((double)jPanel.getHeight());
		if (x<=0.0) {
		    x=0.0;
		}
		if (x>=1.0) {
		    x=1.0;
		}
		if (y<=0.0) {
		    y=0.0;
		}
		if (y>=1.0) {
		    y=1.0;
		}
		y=1.0-y;
		if (pf1Device != null) {
		    pf1Device.sendCommand(String.format("G1 X%1$.2f Y%2$.2f F3600", x*GlobalSettings.getInstance().getBedWidth(), y*GlobalSettings.getInstance().getBedHeight()));
		}
	    }
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
    }

    class ExtruderPositionPanelUpdater implements java.beans.PropertyChangeListener {
	PointPanel jPanel = null;
	JSlider jSlider = null;
	public ExtruderPositionPanelUpdater(PointPanel jPanel, JSlider jSlider) {
	    this.jPanel = jPanel;
	    this.jSlider= jSlider;
	}
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
	    if (evt.getPropertyName().compareTo("extruderX")==0) {
		double x = ((Double)(evt.getNewValue())).doubleValue();
		double x1 = x / GlobalSettings.getInstance().getBedWidth();
		if (x1 >= 1.0) {
		    x1 = 1.0;
		}
		if (x1 <= 0.0) {
		    x1 = 0.0;
		}
		this.jPanel.setPointX(x1);
		this.jPanel.repaint();
	    } else if (evt.getPropertyName().compareTo("extruderY")==0) {
		double y = ((Double)(evt.getNewValue())).doubleValue();
		double y1 = y / GlobalSettings.getInstance().getBedHeight();
		if (y1 >= 1.0) {
		    y1 = 1.0;
		}
		if (y1 <= 0.0) {
		    y1 = 0.0;
		}
		y1 = 1.0-y1;
		this.jPanel.setPointY(y1);
		this.jPanel.repaint();
	    } else if (evt.getPropertyName().compareTo("extruderZ")==0) {
		double z = ((Double)(evt.getNewValue())).doubleValue();
		double z1 = z / GlobalSettings.getInstance().getMaxZ();
		if (z1 < 0.0) {
		    z1 = 0.0;
		}
		if (z1 >= 1.0) {
		    z1 = 1.0;
		}
		int z2 = (int) Math.round(z1*((double)jSlider.getMaximum()));
		if (z2 != jSlider.getValue()) {
		    ChangeListener[] listeners = jSlider.getChangeListeners();
		    for (int i1=0; i1<listeners.length; i1++) {
			if (listeners[i1] instanceof ExtruderHeightPanelChangeListener) {
			    ExtruderHeightPanelChangeListener lis1 = (ExtruderHeightPanelChangeListener)  listeners[i1];
			    lis1.setActive(false);
			}
		    }
		    jSlider.setValue(z2);
		    for (int i1=0; i1<listeners.length; i1++) {
			if (listeners[i1] instanceof ExtruderHeightPanelChangeListener) {
			    ExtruderHeightPanelChangeListener lis1 = (ExtruderHeightPanelChangeListener)  listeners[i1];
			    lis1.setActive(true);
			}
		    }
		}
	    }
	}
    }

    class ExtruderHeightPanelChangeListener implements ChangeListener {
	private boolean valueChanged=false;
	private boolean active=true;
	public void setActive(boolean active) {
	    this.active = active;
	}
	public boolean getActive() {
	    return this.active;
	}
	public void stateChanged(ChangeEvent e) {
	    if (!active) {
		return;
	    }
	    Object o1 = e.getSource();
	    JSlider jSlider = null;
	    if (o1 instanceof JSlider) {
		jSlider = (JSlider)(o1);
	    }
	    if (jSlider != null) {
		if (!jSlider.getValueIsAdjusting()) {
		    logger.info(String.format("Extruder Height: %1$d", jSlider.getValue()));
		    double h1 = ((double)(jSlider.getValue())) / ((double)(jSlider.getMaximum()));
		    if (h1 < 0.0) {
			h1 = 0.0;
		    }
		    if (h1 >= 1.0) {
			h1 = 1.0;
		    }
		    if (pf1Device != null) {
			if (valueChanged) {
			    pf1Device.sendCommand(String.format("G1 Z%1$.5f F3000", h1*GlobalSettings.getInstance().getMaxZ()));
			}
		    }
		    valueChanged=false;
		    
		} else {
		    valueChanged=true;
		}
	    }
	}
    }

    class UtilitiesGCodeSendButtonActionListener implements ActionListener {
	private JTextField jTextField=null;
	public UtilitiesGCodeSendButtonActionListener(JTextField jTextField) {
	    this.jTextField = jTextField;
	}
	public void actionPerformed(ActionEvent e) {
	    logger.info(String.format("Utilities -> Send Button pressed. Cmd=%1$s",jTextField.getText()));
	    if (pf1Device != null) {
		pf1Device.sendCommand(jTextField.getText());
	    }
	    jTextField.setText("");
	}
    }

    class UtilitiesHomeButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info(String.format("Utilities -> Home Button pressed."));
	    if (pf1Device != null) {
		pf1Device.sendCommand("G28");
	    }
	}
    }

}
