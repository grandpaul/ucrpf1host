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
 * Class to test ReceivedData
 */

public class TestReceivedData extends junit.framework.TestCase {

    @Override
    public void setUp() {
    }

    @Override
    public void tearDown() {
    }

    public void testReceivedDataGetData() {
	com.ucrobotics.yliu.ucrpf1host.ReceivedData receivedData = null;

	String data = "; Test 123";

	receivedData = new com.ucrobotics.yliu.ucrpf1host.ReceivedData(data);
	assertEquals(receivedData.getData().compareTo(data), 0);
    }

    public void testReceivedDataGetTimeStamp() {
	com.ucrobotics.yliu.ucrpf1host.ReceivedData receivedData = null;
	long startTimeStamp;
	long endTimeStamp;

	String data = "; Test 4556";

	startTimeStamp = java.util.Calendar.getInstance().getTimeInMillis();
	receivedData = new com.ucrobotics.yliu.ucrpf1host.ReceivedData(data);
	endTimeStamp = java.util.Calendar.getInstance().getTimeInMillis();
	assertEquals(receivedData.getData().compareTo(data), 0);
	assertTrue(startTimeStamp <= receivedData.getTimestamp());
	assertTrue(endTimeStamp >= receivedData.getTimestamp());
    }

    public void testReceivedDataIsOK() {
	com.ucrobotics.yliu.ucrpf1host.ReceivedData receivedData = null;

	String data = "OK";

	receivedData = new com.ucrobotics.yliu.ucrpf1host.ReceivedData(data);
	assertEquals(receivedData.getData().compareTo(data), 0);
	assertTrue(receivedData.isOK());

	data = ";Bad command";
	receivedData = new com.ucrobotics.yliu.ucrpf1host.ReceivedData(data);
	assertEquals(receivedData.getData().compareTo(data), 0);
	assertTrue(!receivedData.isOK());
    }

    public void testReceivedDataGetResend() {
	com.ucrobotics.yliu.ucrpf1host.ReceivedData receivedData = null;

	String data = "Resend: 12354";

	receivedData = new com.ucrobotics.yliu.ucrpf1host.ReceivedData(data);
	assertEquals(receivedData.getData().compareTo(data), 0);
	assertEquals(receivedData.getResend(), 12354);
	assertTrue(!receivedData.isOK());

	data = "OK";
	receivedData = new com.ucrobotics.yliu.ucrpf1host.ReceivedData(data);
	assertEquals(receivedData.getData().compareTo(data), 0);
	assertEquals(receivedData.getResend(), -1);
	assertTrue(receivedData.isOK());

	data = "; Bad Data";
	receivedData = new com.ucrobotics.yliu.ucrpf1host.ReceivedData(data);
	assertEquals(receivedData.getData().compareTo(data), 0);
	assertEquals(receivedData.getResend(), -1);
	assertTrue(!receivedData.isOK());

    }
    
    public void testReceivedDataGetExtruderTemperature() {
	com.ucrobotics.yliu.ucrpf1host.ReceivedData receivedData = null;

	String data = "Resend: 12354";

	receivedData = new com.ucrobotics.yliu.ucrpf1host.ReceivedData(data);
	assertEquals(receivedData.getData().compareTo(data), 0);
	assertEquals(receivedData.getResend(), 12354);
	assertTrue(!receivedData.isOK());
	assertTrue(Double.isNaN(receivedData.getExtruderTemperature()));
	assertTrue(Double.isNaN(receivedData.getExtruderTargetTemperature()));
	
	data = "OK";
	receivedData = new com.ucrobotics.yliu.ucrpf1host.ReceivedData(data);
	assertEquals(receivedData.getData().compareTo(data), 0);
	assertEquals(receivedData.getResend(), -1);
	assertTrue(receivedData.isOK());
	assertTrue(Double.isNaN(receivedData.getExtruderTemperature()));
	assertTrue(Double.isNaN(receivedData.getExtruderTargetTemperature()));

	data = "; Bad Data";
	receivedData = new com.ucrobotics.yliu.ucrpf1host.ReceivedData(data);
	assertEquals(receivedData.getData().compareTo(data), 0);
	assertEquals(receivedData.getResend(), -1);
	assertTrue(!receivedData.isOK());
	assertTrue(Double.isNaN(receivedData.getExtruderTemperature()));
	assertTrue(Double.isNaN(receivedData.getExtruderTargetTemperature()));

	data = "[VALUE] T:45.0/180.0 B:0.0/0.0 S:0 F:'' P:-1 I:0 D:100 E:100";
	receivedData = new com.ucrobotics.yliu.ucrpf1host.ReceivedData(data);
	assertEquals(receivedData.getData().compareTo(data), 0);
	assertEquals(receivedData.getResend(), -1);
	assertTrue(!receivedData.isOK());
	assertTrue(!Double.isNaN(receivedData.getExtruderTemperature()));
	assertTrue(!Double.isNaN(receivedData.getExtruderTargetTemperature()));
	assertEquals(receivedData.getExtruderTemperature(), 45.0);
	assertEquals(receivedData.getExtruderTargetTemperature(), 180.0);

	data = "[VALUE] T:-10.3/180.0 B:0.0/0.0 S:0 F:'' P:-1 I:0 D:100 E:100";
	receivedData = new com.ucrobotics.yliu.ucrpf1host.ReceivedData(data);
	assertEquals(receivedData.getData().compareTo(data), 0);
	assertEquals(receivedData.getResend(), -1);
	assertTrue(!receivedData.isOK());
	assertTrue(!Double.isNaN(receivedData.getExtruderTemperature()));
	assertTrue(!Double.isNaN(receivedData.getExtruderTargetTemperature()));
	assertEquals(receivedData.getExtruderTemperature(), -10.3);
	assertEquals(receivedData.getExtruderTargetTemperature(), 180.0);

    }

    
}
