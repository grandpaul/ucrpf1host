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

public class PointPanel extends JPanel {
    private double pointX = 0.0;
    private double pointY = 0.0;
    private java.beans.PropertyChangeSupport mPcs = new java.beans.PropertyChangeSupport(this);

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

    public PointPanel() {
	super();
	this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
	//this.addMouseListener(new MyMouseListener());
    }

    private void myDrawOval(Graphics g, int x, int y, double r) {
	for (double angle=0.0; angle<360.0; angle+=3.0) {
	    double x1 = ((double)x)+r*Math.sin(Math.toRadians(angle));
	    double y1 = ((double)y)+r*Math.cos(Math.toRadians(angle));
	    double x2 = ((double)x)+r*Math.sin(Math.toRadians(angle+3.0));
	    double y2 = ((double)y)+r*Math.cos(Math.toRadians(angle+3.0));
	    g.drawLine((int)Math.round(x1), (int)Math.round(y1), (int)Math.round(x2), (int)Math.round(y2));
	}
    }

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
	
	if (px-5 < 0 || py-5 < 0) {
	    myDrawOval(g,px,py,2.5);
	} else {
	    g.drawOval(px-5, py-5, 5, 5);
	}
	g.setColor(currentColor);
    }

    public void setPointX(double pointX) {
	double oldX = this.pointX;
	this.pointX = pointX;
	if (oldX != pointX) {
	    mPcs.firePropertyChange("pointX", new Double(oldX), new Double(pointX));
	}
    }
    public double getPointX() {
	return pointX;
    }
    public void setPointY(double pointY) {
	double oldY = this.pointY;
	this.pointY = pointY;
	if (oldY != pointY) {
	    mPcs.firePropertyChange("pointY", new Double(oldY), new Double(pointY));
	}
    }
    public double getPointY() {
	return pointY;
    }
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

    
    public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	mPcs.addPropertyChangeListener(listener);
    }
    public void addPropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener) {
	mPcs.addPropertyChangeListener(propertyName, listener);
    }
    public void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	mPcs.removePropertyChangeListener(listener);
    }
    
}
