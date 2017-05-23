
package com.ucrobotics.yliu.ucrpf1host;

import java.util.logging.*;
import java.util.*;
import gnu.io.*;

/**
 * A thread to load filament.
 */

public class UnloadFilamentCommandSender extends Thread {

    private PF1Device pf1Device = null;
    private boolean runningFlag = true;

    public UnloadFilamentCommandSender(PF1Device pf1Device) {
	super();
	this.pf1Device = pf1Device;
	this.runningFlag = true;
    }

    public void pleaseStop() {
	this.runningFlag=false;
    }

    public void run() {
	pf1Device.sendCommand("G21");
	/* homing */
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
	if (!runningFlag) {
	    return;
	}
	pf1Device.sendCommand("M104 S215");
	if (!runningFlag) {
	    return;
	}
	pf1Device.sendCommand("M109 S215");
	/* extruding */
	while (runningFlag) {
	    pf1Device.sendCommand("G92 E0");
	    pf1Device.sendCommand("G1 E-5.0 F300");
	    pf1Device.sendCommand("G92 E0");
	}
    }
}
