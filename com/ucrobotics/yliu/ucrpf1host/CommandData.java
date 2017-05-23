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
import gnu.io.*;

public class CommandData {
    private String command=null;
    private ArrayList<ReceivedData> answer=null;
    private int lineNumber = 0;
    private long timestamp=0;
    private double prevExtruderX=0.0;
    private double prevExtruderY=0.0;
    private double prevExtruderZ=0.0;
    private double prevExtruderE=0.0;
    private double prevExtruderF=0.0;

    public CommandData() {
	answer = new ArrayList<ReceivedData>();
    }

    public void setCommand(String command) {
	this.command = command;
    }
    public String getCommand() {
	return command;
    }
    public String getStripedCommand() {
	String ret = command;
	int commentIndex = ret.indexOf(';');
	if (commentIndex >= 0) {
	    ret = ret.substring(0, commentIndex);
	}
	ret = ret.replaceAll("\\s+", "");
	return ret;
    }

    private int getChecksum(byte[] s) {
	int checksum=0;
	for (int i=0; i<s.length; i++) {
	    int v = (((int)s[i])&0x00ff);
	    checksum ^= v;
	}
	return checksum;
    }
    public byte[] getEncodedCommand() {
	return getEncodedCommand(getLineNumber());
    }
    
    public byte[] getEncodedCommand(int lineNumber) {
	String linedCommand = null;
	linedCommand = String.format("N%1$d%2$s",lineNumber, getStripedCommand());
	byte[] bLinedCommand = linedCommand.getBytes();
	int cs = getChecksum(bLinedCommand);
	byte[] bcs = String.format("*%1$d",cs).getBytes();
	byte[] ret = new byte[bLinedCommand.length + bcs.length];
	for (int i=0; i<bLinedCommand.length; i++) {
	    ret[i] = bLinedCommand[i];
	}
	for (int i=0; i<bcs.length; i++) {
	    ret[i+bLinedCommand.length] = bcs[i];
	}
	return ret;
    }

    public int getLineNumber() {
	return lineNumber;
    }
    public void setLineNumber(int lineNumber) {
	this.lineNumber = lineNumber;
    }

    public void updateTimestamp() {
	this.timestamp = Calendar.getInstance().getTimeInMillis();
    }
    public long getTimestamp() {
	return timestamp;
    }

    public void addAnswer(ReceivedData rd) {
	answer.add(rd);
    }

    /**
     * Get estimated timeout of this command in ms
     */
    public long getEstimatedTimeout() {
	long ret = 5000;   // default: 5 sec
	String data = getStripedCommand();
	if (data.startsWith("M104")) {
	    return 3000;
	} else if (data.startsWith("G28")) {
	    return 60000;
	} else if (data.startsWith("M109")) {
	    return 600000;
	} else if (data.startsWith("M105")) {
	    return 1500;
	}

	return ret;
    }

    /**
     * Store previous extruder position
     */
    public void setPrevExtruderPos(double X,double Y,double Z, double E, double F) {
	this.prevExtruderX=X;
	this.prevExtruderY=Y;
	this.prevExtruderZ=Z;
	this.prevExtruderE=E;
	this.prevExtruderF=F;
    }
}
