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

package com.ucrobotics.yliu.ucrpf1host.test;

/**
 * Class to test CommandData
 */

public class TestCommandData extends junit.framework.TestCase {

    @Override
    public void setUp() {
    }

    @Override
    public void tearDown() {
    }

    public void testCommandDataGetCommand() {
	com.ucrobotics.yliu.ucrpf1host.CommandData commandData = null;

	String data = "G1 X10 Y20 Z30 F1000 ; Test 123";

	commandData = new com.ucrobotics.yliu.ucrpf1host.CommandData(data);
	assertEquals(commandData.getCommand().compareTo(data), 0);
    }

    public void testCommandDataGetTimeStamp() {
	com.ucrobotics.yliu.ucrpf1host.CommandData commandData = null;
	long startTimeStamp;
	long endTimeStamp;

	String data = "G1 X10 Y20 Z30 F1000 ; Test 123";

	startTimeStamp = java.util.Calendar.getInstance().getTimeInMillis();
	commandData = new com.ucrobotics.yliu.ucrpf1host.CommandData(data);
	endTimeStamp = java.util.Calendar.getInstance().getTimeInMillis();
	assertEquals(commandData.getCommand().compareTo(data), 0);

	startTimeStamp = java.util.Calendar.getInstance().getTimeInMillis();
	commandData.updateTimestamp();
	endTimeStamp = java.util.Calendar.getInstance().getTimeInMillis();
	assertTrue(startTimeStamp <= commandData.getTimestamp());
	assertTrue(endTimeStamp >= commandData.getTimestamp());
    }

    public void testCommandDataLineNumber() {
	com.ucrobotics.yliu.ucrpf1host.CommandData commandData = null;

	String data = "M105";
	int lineNumber = 10;

	commandData = new com.ucrobotics.yliu.ucrpf1host.CommandData(data);
	assertEquals(commandData.getCommand().compareTo(data), 0);

	commandData.setLineNumber(lineNumber);
	assertEquals(commandData.getLineNumber(), lineNumber);
	
    }

    public void testCommandDataGetStripedCommand() {
	com.ucrobotics.yliu.ucrpf1host.CommandData commandData = null;

	String data = "G1 X10 Y20 Z30 F1000 ; Test 123";

	commandData = new com.ucrobotics.yliu.ucrpf1host.CommandData(data);
	assertEquals(commandData.getCommand().compareTo(data), 0);
	assertEquals(commandData.getStripedCommand(), "G1X10Y20Z30F1000");
	
    }
    
    public void testCommandDataGetEncodedCommand() {
	com.ucrobotics.yliu.ucrpf1host.CommandData commandData = null;

	String data = "G1 X10 Y20 Z30 F1000 ; Test 123";
	int lineNumber = 1;

	commandData = new com.ucrobotics.yliu.ucrpf1host.CommandData(data);
	assertEquals(commandData.getCommand().compareTo(data), 0);
	assertEquals(commandData.getStripedCommand(), "G1X10Y20Z30F1000");

	commandData.setLineNumber(lineNumber);
	byte[] encodedBytes = commandData.getEncodedCommand();
	String encodedCommand = new String(encodedBytes);
	assertEquals(encodedCommand, String.format("N%1$d%2$s*%3$d", lineNumber, commandData.getStripedCommand(), 21));
	
    }

    
}
