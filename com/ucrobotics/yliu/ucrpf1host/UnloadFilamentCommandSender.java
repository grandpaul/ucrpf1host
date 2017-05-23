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

import java.util.logging.*;
import java.util.*;

/**
 * A thread to load filament.
 */

public class UnloadFilamentCommandSender extends Thread {

    private PF1Device pf1Device = null;
    private boolean runningFlag = true;
    private ArrayList<javax.swing.text.JTextComponent> statusTextComponents = null;

    public UnloadFilamentCommandSender(PF1Device pf1Device) {
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
	    jTextComponent.setText("Unloading");
	}
	while (runningFlag) {
	    pf1Device.sendCommand("G92 E0");
	    pf1Device.sendCommand("G1 E-5.0 F300");
	    pf1Device.sendCommand("G92 E0");
	}
    }
}
