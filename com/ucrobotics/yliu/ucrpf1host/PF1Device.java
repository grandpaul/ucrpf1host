
package com.ucrobotics.yliu.ucrpf1host;

import java.util.logging.*;
import java.util.*;
import gnu.io.*;

/**
 * Panowin F1 Device class
 */
public class PF1Device {
    private java.util.logging.Logger logger = null;
    public static String loggerName = "PF1DeviceLogger";
    private SerialPort serialPort = null;
    private Thread pf1DeviceServerThread = null;

    /**
     * Constructor for PF1Device
     *
     * @devName the device name of the printer
     */
    public PF1Device(String devName) throws java.io.FileNotFoundException, PortInUseException, UnsupportedCommOperationException {
	this.logger = java.util.logging.Logger.getLogger(PF1Device.loggerName);
	this.logger.setLevel(java.util.logging.Level.INFO);

	CommPortIdentifier portID = getCommPortIdentifierByName(devName);
	if (portID == null) {
	    throw new java.io.FileNotFoundException(devName);
	}

	CommPort commPort = null;

	commPort = portID.open("ucrpf1host", 10);
	if (commPort == null) {
	    throw new java.io.FileNotFoundException(devName);
	}
	
	serialPort = (SerialPort)commPort;
	serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

	pf1DeviceServerThread = new Thread(new PF1DeviceServer(serialPort));
	pf1DeviceServerThread.start();
    }

    /**
     * get the CommPortIdentifier by device name
     *
     * @devName the device name
     * @return null if not found
     */
    private CommPortIdentifier getCommPortIdentifierByName(String devName) {
	Enumeration ports = CommPortIdentifier.getPortIdentifiers();
	while (ports.hasMoreElements()) {
	    CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
	    if (port.getPortType() != CommPortIdentifier.PORT_SERIAL) {
		continue;
	    }
	    if (port.getName().compareTo(devName) == 0) {
		return port;
	    }
	}
	return null;
    }

    /**
     * List all serial devices
     *
     * @return the List of all devices
     */
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
