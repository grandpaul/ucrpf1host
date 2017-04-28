
package com.ucrobotics.yliu.ucrpf1host;

import java.util.logging.*;
import java.util.*;
import gnu.io.*;

/**
 * A server for the PanoWwin F1 Printer.
 * This class provides a queue which receive commands.
 * When the command received, it will try to send it to the printer and
 * attach the results to command
 */
public class PF1DeviceServer implements Runnable {
    private java.util.logging.Logger logger = null;
    public static String loggerName = "PF1DeviceServerLogger";
    private SerialPort serialPort = null;
    private int commandCounter = 0;
    private java.util.concurrent.BlockingQueue<CommandData> commandQueue = null;

    public PF1DeviceServer(SerialPort serialPort) {
        this.logger = java.util.logging.Logger.getLogger(PF1DeviceServer.loggerName);
	this.logger.setLevel(java.util.logging.Level.INFO);
	
	this.serialPort = serialPort;
	commandCounter = 0;
	commandQueue = new java.util.concurrent.SynchronousQueue<CommandData>(true);
	
    }

    public void run() {
	java.io.InputStream in = null;
	try {
	    in = serialPort.getInputStream();
	} catch (java.io.IOException e) {
	    logger.info("getInputStream() Error");
	    return;
	}
	java.io.OutputStream out = null;
	try {
	    out = serialPort.getOutputStream();
	} catch (java.io.IOException e) {
	    logger.info("getOutputStream() Error");
	    return;
	}
	
	while (true) {
	    CommandData cmdData = null;
	    try {
		commandQueue.poll(3, java.util.concurrent.TimeUnit.SECONDS);
	    } catch (java.lang.InterruptedException e) {
		logger.info("java.lang.InterruptedException");
		continue;
	    }
	    if (cmdData == null) {
		/* status update */
		logger.info("Status update");
	    } else {
		/* execute command */
		logger.info(String.format("Execute %1$s",cmdData.getCommand()));
	    }
	}
    }
}
