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
 * Class to test PointPanel
 */

public class TestPointPanel extends junit.framework.TestCase {

    private class MyPropertyChangeListener implements java.beans.PropertyChangeListener {
	private double data;
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
	    data = ((Double)(evt.getNewValue())).doubleValue();
	}
	public double getData() {
	    return data;
	}
    }

    @Override
    public void setUp() {
    }

    @Override
    public void tearDown() {
    }

    public void testPointPanel() {
	com.ucrobotics.yliu.ucrpf1host.PointPanel pointPanel = new com.ucrobotics.yliu.ucrpf1host.PointPanel();

	MyPropertyChangeListener xListener = new MyPropertyChangeListener();
	MyPropertyChangeListener yListener = new MyPropertyChangeListener();
	pointPanel.addPropertyChangeListener("pointX", xListener);
	pointPanel.addPropertyChangeListener("pointY", yListener);
	
	pointPanel.setPointX(10.0);
	assertEquals(pointPanel.getPointX(), 10.0);
	assertEquals(xListener.getData(), 10.0);
	pointPanel.setPointX(11.0);
	assertEquals(pointPanel.getPointX(), 11.0);
	assertEquals(xListener.getData(), 11.0);
	pointPanel.setPointY(8.0);
	assertEquals(pointPanel.getPointY(), 8.0);
	assertEquals(yListener.getData(), 8.0);
	pointPanel.setPointY(9.0);
	assertEquals(pointPanel.getPointY(), 9.0);
	assertEquals(yListener.getData(), 9.0);
	pointPanel.setPointXY(5.0,6.0);
	assertEquals(pointPanel.getPointX(), 5.0);
	assertEquals(xListener.getData(), 5.0);
	assertEquals(pointPanel.getPointY(), 6.0);
	assertEquals(yListener.getData(), 6.0);
    }

    
}
