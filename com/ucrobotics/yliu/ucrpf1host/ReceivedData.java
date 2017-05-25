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
import java.util.regex.*;
import gnu.io.*;

public class ReceivedData {
    private String data=null;
    private long timestamp=0;
    private Pattern resendPattern = null;
    private Pattern temperaturePattern = null;

    public ReceivedData(String data) {
	timestamp = Calendar.getInstance().getTimeInMillis();
	this.data = data;
	resendPattern = Pattern.compile("[Rr][Ee][Ss][Ee][Nn][Dd][:]\\s*(\\d+)");
	temperaturePattern = Pattern.compile(".*T:([0-9.]+)/([0-9.]+)\\s+B:([0-9.]+)/([0-9.]+).*");
    }

    public String getData() {
	return data;
    }
    public long getTimestamp() {
	return timestamp;
    }

    /**
     * Is the received message an OK
     *
     * @return: true - yes. false - no
     */
    public boolean isOK() {
	if (data.trim().compareToIgnoreCase("OK")==0) {
	    return true;
	}
	if (data.trim().compareToIgnoreCase("!!")==0) {
	    return true;
	}
	return false;
    }

    /**
     * Is the received message an Resend?
     *
     * @return: -1: not a resend. other number is the line to resend
     */
    public int getResend() {
	Matcher m = resendPattern.matcher(data.trim());
	if (m.matches()) {
	    int ret = -1;
	    try {
		ret = Integer.parseInt(m.group(1));
	    } catch (java.lang.NumberFormatException e) {
		ret = -1;
	    }
	    return ret;
	}
	return -1;
    }

    /**
     * Is the received message an temperature value?
     *
     * @return: -1.0: not a resend. other number is the temperature
     */
    public double getExtruderTemperature() {
	Matcher m = temperaturePattern.matcher(data.trim());
	if (m.matches()) {
	    double ret = -1.0;
	    try {
		ret = Double.parseDouble(m.group(1));
	    } catch (java.lang.NumberFormatException e) {
		ret = -1.0;
	    }
	    return ret;
	}
	return -1.0;
    }
}
