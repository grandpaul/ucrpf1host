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
import java.io.*;

/**
 * A thread to send files to the device.
 */

public class FileCommandSender extends Thread {

    private PF1Device pf1Device = null;
    private boolean runningFlag = true;
    private File gcodeFile = null;
    private int numberOfLines = 0;
    private int currentLine = 0;
    private java.beans.PropertyChangeSupport mPcs = new java.beans.PropertyChangeSupport(this);
    private java.time.LocalDateTime startTime = null;
    

    public FileCommandSender(PF1Device pf1Device, File gcodeFile) {
	super();
	this.pf1Device = pf1Device;
	this.gcodeFile = gcodeFile;
	this.runningFlag = true;
	this.numberOfLines = getLines(this.gcodeFile);
	this.startTime = java.time.LocalDateTime.now();
    }

    public void pleaseStop() {
	this.runningFlag=false;
    }

    public int getNumberOfLines() {
	return numberOfLines;
    }

    public int getCurrentLine() {
	return currentLine;
    }

    public java.time.LocalDateTime getStartTime() {
	return startTime;
    }

    /** 
     * Get the number of lines of a file
     */
    private int getLines(File file) {
	int ret=0;
	FileReader in = null;
	BufferedReader br = null;

	try {
	    in = new FileReader(file);
	} catch (FileNotFoundException e) {
	    return ret;
	}
	br = new BufferedReader(in);

	String line = null;
	try {
	    while ( (line = br.readLine()) != null) {
		ret = ret + 1;
	    }
	} catch (IOException e) {
	    return ret;
	}
	return ret;
    }

    public void run() {
	currentLine = 0;

	if (this.numberOfLines <= 0) {
	    return;
	}

	FileReader in = null;
	BufferedReader br = null;

	try {
	    in = new FileReader(gcodeFile);
	} catch (FileNotFoundException e) {
	    return;
	}
	br = new BufferedReader(in);

	String line = null;
	try {
	    while ( (line = br.readLine()) != null) {
		if (!runningFlag) {
		    break;
		}
		int oldCurrentLine = currentLine;
		currentLine = currentLine + 1;
		mPcs.firePropertyChange("currentLine", new Integer(oldCurrentLine), new Integer(currentLine));
		pf1Device.sendCommand(line);
	    }
	} catch (IOException e) {
	    return;
	}
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
