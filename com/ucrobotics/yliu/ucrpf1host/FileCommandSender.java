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

    public FileCommandSender(PF1Device pf1Device, File gcodeFile) {
	super();
	this.pf1Device = pf1Device;
	this.gcodeFile = gcodeFile;
	this.runningFlag = true;
	this.progressBars = new ArrayList<javax.swing.JProgressBar>();
    }

    public void pleaseStop() {
	this.runningFlag=false;
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
	int N = getLines(gcodeFile);
	int c = 0;

	if (N<=0) {
	    return;
	}
	for (javax.swing.JProgressBar progressBar : progressBars) {
	    progressBar.setIndeterminate(false);
	    progressBar.setMinimum(0);
	    progressBar.setMaximum(N);
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
		for (javax.swing.JProgressBar progressBar : progressBars) {
		    progressBar.setValue(c);
		}
		c = c + 1;
		pf1Device.sendCommand(line);
	    }
	} catch (IOException e) {
	    return;
	}
    }
}
