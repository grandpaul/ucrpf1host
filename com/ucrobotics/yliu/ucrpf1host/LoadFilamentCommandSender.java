
package com.ucrobotics.yliu.ucrpf1host;

import java.util.logging.*;
import java.util.*;
import gnu.io.*;

/**
 * A thread to load filament.
 */

public class LoadFilamentCommandSender extends Thread {

    private PF1Device pf1Device = null;
    private boolean runningFlag = true;
    private ArrayList<javax.swing.text.JTextComponent> statusTextComponents = null;

    public LoadFilamentCommandSender(PF1Device pf1Device) {
	super();
	this.pf1Device = pf1Device;
	this.runningFlag = true;
	this.statusTextComponents = new ArrayList<javax.swing.text.JTextComponent>();
    }

    public void pleaseStop() {
	this.runningFlag=false;
    }

    public void addStatusJTextComponent(javax.swing.text.JTextComponent component) {
	this.statusTextComponents.add(component);
    }

    public void run() {
	pf1Device.sendCommand("G21");
	/* homing */
	for (javax.swing.text.JTextComponent jTextComponent: statusTextComponents) {
	    jTextComponent.setText("Homing");
	}
	if (!runningFlag) {
	    return;
	}
	pf1Device.sendCommand("G28 X0");
	if (!runningFlag) {
	    return;
	}
	pf1Device.sendCommand("G1 X50 F3600");
	if (!runningFlag) {
	    return;
	}
	pf1Device.sendCommand("G28 Y0");
	if (!runningFlag) {
	    return;
	}
	pf1Device.sendCommand("G1 Y50 F3600");
	if (!runningFlag) {
	    return;
	}
	pf1Device.sendCommand("G28 Z0");
	if (!runningFlag) {
	    return;
	}
	pf1Device.sendCommand("G1 Z50 F3000");
	/* heating */
	for (javax.swing.text.JTextComponent jTextComponent: statusTextComponents) {
	    jTextComponent.setText("Heating");
	}
	if (!runningFlag) {
	    return;
	}
	pf1Device.sendCommand("M104 S215");
	if (!runningFlag) {
	    return;
	}
	pf1Device.sendCommand("M109 S215");
	/* extruding */
	for (javax.swing.text.JTextComponent jTextComponent: statusTextComponents) {
	    jTextComponent.setText("Loading");
	}
	while (runningFlag) {
	    pf1Device.sendCommand("G92 E0");
	    pf1Device.sendCommand("G1 E5.0 F300");
	    pf1Device.sendCommand("G92 E0");
	}
    }
}
