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
    private ArrayList<javax.swing.JProgressBar> progressBars = null;
    private int numberOfLines = 0;
    private int currentLine = 0;
    private java.beans.PropertyChangeSupport mPcs = new java.beans.PropertyChangeSupport(this);
    

    public FileCommandSender(PF1Device pf1Device, File gcodeFile) {
	super();
	this.pf1Device = pf1Device;
	this.gcodeFile = gcodeFile;
	this.runningFlag = true;
	this.progressBars = new ArrayList<javax.swing.JProgressBar>();
	this.numberOfLines = getLines(this.gcodeFile);
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

    public void addProgressBar(javax.swing.JProgressBar progressBar) {
	this.progressBars.add(progressBar);
    }

    public void run() {
	currentLine = 0;

	if (this.numberOfLines <= 0) {
	    return;
	}
	for (javax.swing.JProgressBar progressBar : progressBars) {
	    progressBar.setIndeterminate(false);
	    progressBar.setMinimum(0);
	    progressBar.setMaximum(this.numberOfLines);
	    progressBar.setValue(0);
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
		for (javax.swing.JProgressBar progressBar : progressBars) {
		    progressBar.setValue(currentLine);
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
    
    public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	mPcs.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	mPcs.removePropertyChangeListener(listener);
    }
}
