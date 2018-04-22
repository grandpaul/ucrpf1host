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
    private java.io.InputStream in = null;
    private java.io.OutputStream out = null;
    private PF1Device pf1Device = null;
    private boolean runningFlag = true;

    public PF1DeviceServer(PF1Device pf1Device) {
        this.logger = java.util.logging.Logger.getLogger(PF1DeviceServer.loggerName);
	this.logger.setLevel(java.util.logging.Level.INFO);

	this.pf1Device = pf1Device;
	this.serialPort = pf1Device.getSerialPort();
	commandCounter = 0;
	commandQueue = new java.util.concurrent.SynchronousQueue<CommandData>(true);
	historyQueue = new LinkedList<CommandData>();
    }

    private LinkedList<Byte> inBuffer=null;
    private java.util.concurrent.BlockingQueue<ReceivedData> inQueue=null;
    class SerialPortEventListener1 implements SerialPortEventListener {
	public void serialEvent(SerialPortEvent ev) {
	    if (in == null) {
		return;
	    }
	    byte[] buf = new byte[1024];
	    boolean isNewLineFlag=false;
	    try {
		while (in.available() > 0) {
		    int len = in.read(buf);
		    if (len <= 0) {
			break;
		    }
		    for (int i=0; i<buf.length && i<len; i++) {
			if (buf[i] == '\n') {
			    if (inBuffer.size() > 0) {
				byte[] mybuf = new byte[inBuffer.size()];
				for (int j=0; !inBuffer.isEmpty() && j<mybuf.length; j++) {
				    mybuf[j] = inBuffer.pollFirst().byteValue();
				}
				String data = new String(mybuf);
				ReceivedData rd = new ReceivedData(data);
				logger.info(String.format("Received %1$s", rd.getData()));
				try {
				    inQueue.put(rd);
				} catch (InterruptedException e) {
				    logger.severe("Bad. Cannot put to queue due to interrupt");
				}
			    }
			    inBuffer.clear();
			} else if (buf[i] == '\r') {
			} else {
			    inBuffer.add(Byte.valueOf(buf[i]));
			}
		    }
		}
	    } catch (java.io.IOException e) {
		return;
	    }
	}
    }

    private void updateExtruderPosition(CommandData command) {
	String cmd = command.getStripedCommand();
	if (command.getStripedCommand().compareTo("G28")==0) {
	    /* reset all axes */
	    pf1Device.setExtruderX(0.0);
	    pf1Device.setExtruderY(0.0);
	    pf1Device.setExtruderZ(0.0);
	    pf1Device.setExtruderE(0.0);
	    return;
	}
	java.util.regex.Pattern g28Pattern = java.util.regex.Pattern.compile("^G28(.+)");
	java.util.regex.Matcher g28Matcher = g28Pattern.matcher(cmd);
	if (g28Matcher.matches()) {
	    /* reset some axis */
	    String axis = g28Matcher.group(1);
	    if (axis.indexOf("X") >= 0 || axis.indexOf("x") >= 0) {
		pf1Device.setExtruderX(0.0);
	    }
	    if (axis.indexOf("Y") >= 0 || axis.indexOf("y") >= 0) {
		pf1Device.setExtruderY(0.0);
	    }
	    if (axis.indexOf("Z") >= 0 || axis.indexOf("z") >= 0) {
		pf1Device.setExtruderZ(0.0);
	    }
	    if (axis.indexOf("E") >= 0 || axis.indexOf("e") >= 0) {
		pf1Device.setExtruderE(0.0);
	    }
	    return;
	}

	java.util.regex.Pattern g1Pattern = java.util.regex.Pattern.compile("^G1([A-Z][0-9.\\-]*)*");
	java.util.regex.Matcher g1Matcher = g1Pattern.matcher(cmd);
	if (g1Matcher.matches()) {
	    /* reset some axis */
	    String axis = cmd.substring(2);
	    java.util.regex.Pattern axPattern = java.util.regex.Pattern.compile("[A-Z][0-9.\\-]*");
	    java.util.regex.Matcher axMatcher = axPattern.matcher(axis);
	    while (axMatcher.find()) {
		String a1 = axMatcher.group();
		if (a1.charAt(0) == 'X') {
		    pf1Device.setExtruderX(Double.parseDouble(a1.substring(1)));
		} else if (a1.charAt(0) == 'Y') {
		    pf1Device.setExtruderY(Double.parseDouble(a1.substring(1)));
		} else if (a1.charAt(0) == 'Z') {
		    pf1Device.setExtruderZ(Double.parseDouble(a1.substring(1)));
		} else if (a1.charAt(0) == 'E') {
		    pf1Device.setExtruderE(Double.parseDouble(a1.substring(1)));
		} else if (a1.charAt(0) == 'F') {
		    pf1Device.setExtruderF(Double.parseDouble(a1.substring(1)));
		}
	    }
	    return;
	}

	java.util.regex.Pattern g92Pattern = java.util.regex.Pattern.compile("^G92([A-Z][0-9.\\-]*)*");
	java.util.regex.Matcher g92Matcher = g92Pattern.matcher(cmd);
	if (g92Matcher.matches()) {
	    /* reset some axis */
	    String axis = cmd.substring(2);
	    java.util.regex.Pattern axPattern = java.util.regex.Pattern.compile("[A-Z][0-9.\\-]*");
	    java.util.regex.Matcher axMatcher = axPattern.matcher(axis);
	    while (axMatcher.find()) {
		String a1 = axMatcher.group();
		if (a1.charAt(0) == 'X') {
		    pf1Device.setExtruderX(Double.parseDouble(a1.substring(1)));
		} else if (a1.charAt(0) == 'Y') {
		    pf1Device.setExtruderY(Double.parseDouble(a1.substring(1)));
		} else if (a1.charAt(0) == 'Z') {
		    pf1Device.setExtruderZ(Double.parseDouble(a1.substring(1)));
		} else if (a1.charAt(0) == 'E') {
		    pf1Device.setExtruderE(Double.parseDouble(a1.substring(1)));
		} else if (a1.charAt(0) == 'F') {
		    pf1Device.setExtruderF(Double.parseDouble(a1.substring(1)));
		}
	    }
	    return;
	}
    }

    /**
     * Send the command to the historyQueue and then send it to the printer.
     *
     * This function will gurantee the command is being put to the historyQueue
     * and will be sent to the printer. Of course it doesn't know if the 
     * printer will receive it or execute it now. But since it is in the
     * historyQueue that means the printer will execute it for sure later.
     *
     * @cmdData the command
     */
    private void doSend(CommandData cmdData) {
	if (cmdData.getStripedCommand().length()<=0) {
	    return;
	}

	cmdData.setLineNumber(commandCounter);
	commandCounter++;
	cmdData.setPrevExtruderPos(pf1Device.getExtruderX(),
				   pf1Device.getExtruderY(),
				   pf1Device.getExtruderZ(),
				   pf1Device.getExtruderE(),
				   pf1Device.getExtruderF());
	historyQueue.add(cmdData);
	while (historyQueue.size() >= 1500) {
	    historyQueue.remove();
	}
	updateExtruderPosition(cmdData);
	doResend(commandCounter-1);
    }
    
    /**
     * Resend a command from historyQueue
     *
     * It will resent all of the command in historyQueue whose line number is 
     * larger than the parameter.
     *
     * @lineNumber the lineNumber that needs to be resent.
     */
    private void doResend(int lineNumber) {
	boolean resendAgain = true;
	while (resendAgain) {
	    resendAgain=false;
	
	    for (CommandData command : historyQueue) {
		if (command.getLineNumber() < lineNumber) {
		    continue;
		}
	    
		if (command.getLineNumber() == lineNumber) {
		    int ret;
		    ret = sendCommandToPrinter(command);
		    if (ret == -1) {
			lineNumber++;
			continue;
		    } else {
			lineNumber = ret;
			resendAgain=true;
			break;
		    }
		}
	    }
	}
    }

    private LinkedList<CommandData> historyQueue = null;

    /**
     * Send a command to the printer.
     *
     * This command will try to send a command to a printer.
     * If it gets a Resend message. It will return the line number indicate
     * by the resend message. If it gets an OK then it will return -1.
     *
     * But each commands has its own timeout. After the timeout we assume
     * it is ok and will also return -1. Don't worry about this because
     * someone will send the next command soon and we can know if the timeout
     * command needs a resend or not.
     *
     * @command the command to send to the printer
     * @return -1 if success. Otherwise it is the line number that needs to
     * be resent.
     */
    private int sendCommandToPrinter(CommandData command) {
	String data = command.getStripedCommand();
	if (data.length()<=0) {
	    return -1;
	}
	byte[] encodedData = command.getEncodedCommand();
	byte[] encodedDataNL = Arrays.copyOf(encodedData, encodedData.length+1);
	encodedDataNL[encodedData.length] = '\n';

	/* clean the reading queue */
	try {
	    while (!inQueue.isEmpty()) {
		ReceivedData rd = null;
		rd = inQueue.poll(1, java.util.concurrent.TimeUnit.NANOSECONDS);
		if (rd == null) {
		    break;
		}
		if (rd.getResend() >= 0) {
		    /* need resend */
		    return rd.getResend();
		}
	    }
	} catch (Exception e) {
	}

	/* send out commands */
	command.updateTimestamp();
	try {
	    out.write(encodedDataNL);
	    out.flush();
	} catch (java.io.IOException e) {
	    logger.severe("Write to printer error");
	}

	/* reading inQueue */
	long commandTimeout=command.getEstimatedTimeout();
	while (commandTimeout == -1 || command.getTimestamp() + commandTimeout >= Calendar.getInstance().getTimeInMillis()) {
	    ReceivedData rd = null;
	    if (commandTimeout == -1) {
		try {
		    rd = inQueue.take();
		} catch (java.lang.InterruptedException e) {
		    rd = null;
		}
	    } else {
		try {
		    rd = inQueue.poll(commandTimeout, java.util.concurrent.TimeUnit.MILLISECONDS);
		} catch (java.lang.InterruptedException e) {
		    rd = null;
		}
	    }
	    
	    if (rd != null) {
		command.addAnswer(rd);
		if (!Double.isNaN(rd.getExtruderTemperature())) {
		    pf1Device.setExtruderTemperature(rd.getExtruderTemperature());
		}
		if (!Double.isNaN(rd.getExtruderTargetTemperature())) {
		    pf1Device.setExtruderTargetTemperature(rd.getExtruderTargetTemperature());
		}		    
		if (rd.isOK()) {
		    return -1;
		} else if (rd.getResend()>0) {
		    return rd.getResend();
		}
	    }
	}
	return -1;
    }
    
    public void run() {
	in = null;
	try {
	    in = serialPort.getInputStream();
	} catch (java.io.IOException e) {
	    logger.info("getInputStream() Error");
	    return;
	}

	inBuffer = new LinkedList<Byte> ();
	inQueue = new java.util.concurrent.LinkedBlockingQueue<ReceivedData>();
	try {
	    serialPort.addEventListener(new SerialPortEventListener1());
	    serialPort.notifyOnDataAvailable(true);
	} catch (TooManyListenersException e) {
	    logger.info("TooManyListeners Error");
	    return;
	}
	
	try {
	    out = serialPort.getOutputStream();
	} catch (java.io.IOException e) {
	    logger.info("getOutputStream() Error");
	    return;
	}
	
	while (runningFlag) {
	    CommandData cmdData = null;
	    try {
		cmdData = commandQueue.poll(3, java.util.concurrent.TimeUnit.SECONDS);
	    } catch (java.lang.InterruptedException e) {
		logger.info("java.lang.InterruptedException");
		continue;
	    }
	    if (cmdData == null) {
		/* status update */
		logger.info("Status update");
		CommandData M105 = new CommandData("M105");
		doSend(M105);
	    } else {
		/* execute command */
		logger.info(String.format("Execute %1$s",cmdData.getCommand()));
		doSend(cmdData);
	    }
	}
    }

    /**
     * Put a gcode to the commandQUeue. May block until the queue is empty.
     *
     * @gcode the gcode string.
     */
    public void sendCommand(String gcode) {
	CommandData cmdData = new CommandData(gcode);
	while (true) {
	    try {
		commandQueue.put(cmdData);
	    } catch (InterruptedException e) {
		continue;
	    }
	    break;
	}
    }

    /**
     * Please stop the thread
     */
    public void pleaseStop() {
	this.runningFlag=false;
    }
}
