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

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.awt.datatransfer.*;
import java.awt.image.*;
import java.applet.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * A empty panel with a point on it.
 */
public class PointPanel extends JPanel {
    private double pointX = 0.0;
    private double pointY = 0.0;
    private java.beans.PropertyChangeSupport mPcs = new java.beans.PropertyChangeSupport(this);

    /**
     * An example of the mouseListener for this PointPanel.
     */
    class MyMouseListener implements MouseListener {
	public void mouseClicked(MouseEvent e) {
	    int mx = e.getX();
	    int my = e.getY();
	    if (e.getButton() == MouseEvent.BUTTON1) {
		double x = ((double)mx)/((double)getWidth());
		double y = ((double)my)/((double)getHeight());
		setPointXY(x,y);
		repaint();
	    }
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
    }

    /**
     * Constructs a PointPanel. There's no preferredSize of this panel.
     * It is able to use setPreferredSize() to set a preferredSize.
     */
    public PointPanel() {
	super();
	this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    }

    /**
     * draw an oval
     *
     * @x center of the oval in x-axis
     * @y center of the oval in y-axis
     * @r the radius of the oval
     */
    private void myDrawOval(Graphics g, int x, int y, double r) {
	for (double angle=0.0; angle<360.0; angle+=3.0) {
	    double x1 = ((double)x)+r*Math.sin(Math.toRadians(angle));
	    double y1 = ((double)y)+r*Math.cos(Math.toRadians(angle));
	    double x2 = ((double)x)+r*Math.sin(Math.toRadians(angle+3.0));
	    double y2 = ((double)y)+r*Math.cos(Math.toRadians(angle+3.0));
	    g.drawLine((int)Math.round(x1), (int)Math.round(y1), (int)Math.round(x2), (int)Math.round(y2));
	}
    }

    /**
     * paint the component
     *
     * @g the graphics
     */
    @Override
    public void paintComponent(Graphics g) {
	super.paintComponent(g);
	if (getWidth() <= 5 || getHeight() <= 5) {
	    return;
	}
	Color currentColor = g.getColor();
	g.setColor(Color.RED);
	int px = (int)Math.round(getPointX()*((double)getWidth()));
	int py = (int)Math.round(getPointY()*((double)getHeight()));

	/* use myDrawOval() if the coordinate is negatove for drawOval() */
	if (px-5 < 0 || py-5 < 0) {
	    myDrawOval(g,px,py,2.5);
	} else {
	    g.drawOval(px-5, py-5, 5, 5);
	}
	g.setColor(currentColor);
    }

    /**
     * set the x-coordinate of the point
     *
     * @pointX the x-coordinate of the point. range: 0.0 to 1.0
     */
    public void setPointX(double pointX) {
	double oldX = this.pointX;
	this.pointX = pointX;
	if (oldX != pointX) {
	    mPcs.firePropertyChange("pointX", new Double(oldX), new Double(pointX));
	}
    }

    /**
     * get the x-coordinate of the point
     *
     * @return the x-coordinate of the point. range: 0.0 to 1.0
     */
    public double getPointX() {
	return pointX;
    }
    
    /**
     * set the y-coordinate of the point
     *
     * @pointY the y-coordinate of the point. range: 0.0 to 1.0
     */
    public void setPointY(double pointY) {
	double oldY = this.pointY;
	this.pointY = pointY;
	if (oldY != pointY) {
	    mPcs.firePropertyChange("pointY", new Double(oldY), new Double(pointY));
	}
    }

    /**
     * get the y-coordinate of the point
     *
     * @return the y-coordinate of the point. range: 0.0 to 1.0
     */
    public double getPointY() {
	return pointY;
    }
    
    /**
     * set the coordinate of the point
     *
     * @pointX the x-coordinate of the point. range: 0.0 to 1.0
     * @pointY the y-coordinate of the point. range: 0.0 to 1.0
     */
    public void setPointXY(double pointX, double pointY) {
	double oldX = this.pointX;
	this.pointX = pointX;
	double oldY = this.pointY;
	this.pointY = pointY;
	if (oldX != pointX) {
	    mPcs.firePropertyChange("pointX", new Double(oldX), new Double(pointX));
	}
	if (oldY != pointY) {
	    mPcs.firePropertyChange("pointY", new Double(oldY), new Double(pointY));
	}
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
    
}
