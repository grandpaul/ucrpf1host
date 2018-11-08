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

/**
 * Panowin F1 Device class
 */
public class PF1Device {
    private java.util.logging.Logger logger = null;
    public static String loggerName = "PF1DeviceLogger";
    private SerialPort serialPort = null;
    private PF1DeviceServer pf1DeviceServer = null;
    private Thread pf1DeviceServerThread = null;
    private double extruderX = 0.0;
    private double extruderY = 0.0;
    private double extruderZ = 0.0;
    private double extruderE = 0.0;
    private double extruderF = 0.0;
    private double extruderTemperature = 0.0;
    private double extruderTargetTemperature = 0.0;
    private java.beans.PropertyChangeSupport mPcs = new java.beans.PropertyChangeSupport(this);

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

	pf1DeviceServer = new PF1DeviceServer(this);
	pf1DeviceServerThread = new Thread(pf1DeviceServer);
	pf1DeviceServerThread.start();
    }

    /**
     * get the Serial port of the PF1Device
     */
    public SerialPort getSerialPort() {
	return serialPort;
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
	    if (port == null) {
		continue;
	    }
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
	java.util.logging.Logger.getLogger(PF1Device.loggerName).info(String.format("Start listing devices"));
	Enumeration ports = CommPortIdentifier.getPortIdentifiers();
	while (ports != null && ports.hasMoreElements()) {
	    CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
	    java.util.logging.Logger.getLogger(PF1Device.loggerName).info(String.format("Found device name: %1$s", port.getName()));
	    if (port.getPortType() != CommPortIdentifier.PORT_SERIAL) {
		java.util.logging.Logger.getLogger(PF1Device.loggerName).info(String.format("Device: %1$s is not a serial port", port.getName()));
		continue;
	    }
	    java.util.logging.Logger.getLogger(PF1Device.loggerName).info(String.format("Name: %1$s", port.getName()));
	    ret.add(port.getName());
	}
	return ret;
    }

    public double getExtruderX() {
	return extruderX;
    }
    public void setExtruderX(double extruderX) {
	double oldExtruderX = this.extruderX;
	this.extruderX = extruderX;
	java.util.logging.Logger.getLogger(PF1Device.loggerName).finer(String.format("ExtruderX: %1$f", extruderX));
	if (oldExtruderX != extruderX) {
	    mPcs.firePropertyChange("extruderX", Double.valueOf(oldExtruderX), Double.valueOf(extruderX));
	}
    }
    public double getExtruderY() {
	return extruderY;
    }
    public void setExtruderY(double extruderY) {
	double oldExtruderY = this.extruderY;
	this.extruderY = extruderY;
	java.util.logging.Logger.getLogger(PF1Device.loggerName).finer(String.format("ExtruderY: %1$f", extruderY));
	if (oldExtruderY != extruderY) {
	    mPcs.firePropertyChange("extruderY", Double.valueOf(oldExtruderY), Double.valueOf(extruderY));
	}
    }
    public double getExtruderZ() {
	return extruderZ;
    }
    public void setExtruderZ(double extruderZ) {
	double oldExtruderZ = this.extruderZ;
	this.extruderZ = extruderZ;
	if (oldExtruderZ != extruderZ) {
	    mPcs.firePropertyChange("extruderZ", Double.valueOf(oldExtruderZ), Double.valueOf(extruderZ));
	}
    }
    public double getExtruderE() {
	return extruderE;
    }
    public void setExtruderE(double extruderE) {
	double oldExtruderE = this.extruderE;
	this.extruderE = extruderE;
	if (oldExtruderE != extruderE) {
	    mPcs.firePropertyChange("extruderE", Double.valueOf(oldExtruderE), Double.valueOf(extruderE));
	}
    }
    public double getExtruderF() {
	return extruderF;
    }
    public void setExtruderF(double extruderF) {
	double oldExtruderF = this.extruderF;
	this.extruderF = extruderF;
	if (oldExtruderF != extruderF) {
	    mPcs.firePropertyChange("extruderF", Double.valueOf(oldExtruderF), Double.valueOf(extruderF));
	}
    }

    public void setExtruderTemperature(double extruderTemperature) {
	double oldTemperature = this.extruderTemperature;
	this.extruderTemperature = extruderTemperature;
	if (oldTemperature != extruderTemperature) {
	    mPcs.firePropertyChange("extruderTemperature", Double.valueOf(oldTemperature), Double.valueOf(extruderTemperature));
	}
    }
    public double getExtruderTargetTemperature() {
	return extruderTargetTemperature;
    }
    public void setExtruderTargetTemperature(double extruderTargetTemperature) {
	double oldTargetTemperature = this.extruderTargetTemperature;
	this.extruderTargetTemperature = extruderTargetTemperature;
	if (oldTargetTemperature != extruderTargetTemperature) {
	    mPcs.firePropertyChange("extruderTargetTemperature", Double.valueOf(oldTargetTemperature), Double.valueOf(extruderTargetTemperature));
	}
    }
    public double getExtruderTemperature() {
	return extruderTemperature;
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

    /**
     * Put a gcode to the commandQueue of the PF1DeviceServer.
     * May block until the queue is empty.
     */
    public void sendCommand(String gcode) {
	pf1DeviceServer.sendCommand(gcode);
    }

    public void close(long millis) {
	if (pf1DeviceServer != null && pf1DeviceServerThread != null) {
	    pf1DeviceServer.pleaseStop();
	    try {
		if (millis<0) {
		    pf1DeviceServerThread.join();
		} else {
		    pf1DeviceServerThread.join(millis);
		}
	    } catch (InterruptedException e) {
	    }
	    pf1DeviceServer = null;
	    pf1DeviceServerThread = null;
	}
	if (serialPort != null) {
	    serialPort.close();
	    serialPort = null;
	}
    }
}
