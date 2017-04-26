
package com.ucrobotics.yliu.ucrpf1host;

import java.util.logging.*;
import java.util.*;
import gnu.io.*;

public class PF1Device {
    private java.util.logging.Logger logger = null;
    public static String loggerName = "PF1DeviceLogger";

    public PF1Device() {
	this.logger = java.util.logging.Logger.getLogger(PF1Device.loggerName);
	this.logger.setLevel(java.util.logging.Level.INFO);

    }

    public static ArrayList<String> listDevices() {
	ArrayList<String> ret = new ArrayList<String>();
	Enumeration ports = CommPortIdentifier.getPortIdentifiers();
	while (ports.hasMoreElements()) {
	    CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
	    if (port.getPortType() != CommPortIdentifier.PORT_SERIAL) {
		continue;
	    }
	    java.util.logging.Logger.getLogger(PF1Device.loggerName).info(String.format("Name: %1$s", port.getName()));
	    ret.add(port.getName());
	}
	return ret;
    }
}
