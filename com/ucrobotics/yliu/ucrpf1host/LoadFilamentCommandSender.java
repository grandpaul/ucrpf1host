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

public class LoadFilamentCommandSender extends Thread {

    private PF1Device pf1Device = null;
    private boolean runningFlag = true;
    private java.beans.PropertyChangeSupport mPcs = new java.beans.PropertyChangeSupport(this);

    public LoadFilamentCommandSender(PF1Device pf1Device) {
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
	mPcs.firePropertyChange("status", "", "Homing");
	if (!runningFlag) {
	    return;
	}
	pf1Device.sendCommand("G28 X0");
	if (!runningFlag) {
	    return;
	}
	pf1Device.sendCommand(String.format("G1 X%1$.2f F3600", GlobalSettings.getInstance().getBedWidth()/2.0));
	if (!runningFlag) {
	    return;
	}
	pf1Device.sendCommand("G28 Y0");
	if (!runningFlag) {
	    return;
	}
	pf1Device.sendCommand(String.format("G1 Y%1$.2f F3600", GlobalSettings.getInstance().getBedHeight()/2.0));
	if (!runningFlag) {
	    return;
	}
	pf1Device.sendCommand("G28 Z0");
	if (!runningFlag) {
	    return;
	}
	pf1Device.sendCommand(String.format("G1 Z%1$.2f F3000", GlobalSettings.getInstance().getMaxZ()/2.0));
	/* heating */
	mPcs.firePropertyChange("status", "Homing", "Heating");
	if (!runningFlag) {
	    return;
	}
	pf1Device.sendCommand(String.format("M104 S%1$d", Math.round(GlobalSettings.getInstance().getExtruderPreheatTemperature())));
	if (!runningFlag) {
	    return;
	}
	pf1Device.sendCommand(String.format("M109 S%1$d", Math.round(GlobalSettings.getInstance().getExtruderPreheatTemperature())));
	while (runningFlag) {
	    double t1 = pf1Device.getExtruderTemperature();
	    if (t1 + 0.5 >= GlobalSettings.getInstance().getExtruderPreheatTemperature()) {
		break;
	    }
	    try {
		Thread.sleep(3000);
	    } catch (InterruptedException e1) {
	    }
	}
	/* extruding */
	mPcs.firePropertyChange("status", "Heating", "Loading");
	while (runningFlag) {
	    pf1Device.sendCommand("G92 E0");
	    pf1Device.sendCommand("G1 E5.0 F300");
	    pf1Device.sendCommand("G92 E0");
	}
	mPcs.firePropertyChange("status", "Loading", "");
    }

    /**
     * Add a PropertyChangeListener to the listener list. 
     *
     * The listener is registered for all properties.
     * The same listener object may be added more than once, and will be
     * called as many times as it is added. If listener is null, no exception
     * is thrown and no action is taken.
     *
     * @listener The PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	mPcs.addPropertyChangeListener(listener);
    }

    /**
     * Add a PropertyChangeListener for a specific property.
     *
     * The listener will be invoked only when a call on firePropertyChange
     * names that specific property. The same listener object may be added
     * more than once. For each property, the listener will be invoked the
     * number of times it was added for that property. If propertyName or
     * listener is null, no exception is thrown and no action is taken.
     *
     * @propertyName The name of the property to listen on.
     * @listener The PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener) {
	mPcs.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Remove a PropertyChangeListener from the listener list.
     *
     * This removes a PropertyChangeListener that was registered for all
     * properties. If listener was added more than once to the same event
     * source, it will be notified one less time after being removed. If
     * listener is null, or was never added, no exception is thrown and no
     * action is taken.
     *
     * @listener The PropertyChangeListener to be removed
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	mPcs.removePropertyChangeListener(listener);
    }

}
