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

public class GlobalSettings {
    private double extruderPreheatTemperature = 215.0;
    private double bedWidth = 120;
    private double bedHeight = 120;
    private double maxZ = 120;

    private java.beans.PropertyChangeSupport mPcs = new java.beans.PropertyChangeSupport(this);
    private static GlobalSettings instance = null;

    private GlobalSettings() {
    }

    public static GlobalSettings getInstance() {
	if (instance == null) {
	    instance = new GlobalSettings();
	}
	return instance;
    }

    public double getExtruderPreheatTemperature() {
	return extruderPreheatTemperature;
    }
    public void setExtruderPreheatTemperature(double extruderPreheatTemperature) {
	double oldTemperature = this.extruderPreheatTemperature;
	this.extruderPreheatTemperature = extruderPreheatTemperature;
	if (oldTemperature != extruderPreheatTemperature) {
	    mPcs.firePropertyChange("extruderPreheatTemperature", new Double(oldTemperature), new Double(extruderPreheatTemperature));
	}
    }

    public double getBedWidth() {
	return bedWidth;
    }
    public void setBedWidth(double bedWidth) {
	double oldBedWidth = this.bedWidth;
	this.bedWidth = bedWidth;
	if (oldBedWidth != bedWidth) {
	    mPcs.firePropertyChange("bedWidth", new Double(oldBedWidth), new Double(bedWidth));
	}
    }

    public double getBedHeight() {
	return bedHeight;
    }
    public void setBedHeight(double bedHeight) {
	double oldBedHeight = this.bedHeight;
	this.bedHeight = bedHeight;
	if (oldBedHeight != bedHeight) {
	    mPcs.firePropertyChange("bedHeight", new Double(oldBedHeight), new Double(bedHeight));
	}
    }

    public double getMaxZ() {
	return maxZ;
    }
    public void setMaxZ(double maxZ) {
	double oldMaxZ = this.maxZ;
	this.maxZ = maxZ;
	if (oldMaxZ != maxZ) {
	    mPcs.firePropertyChange("maxZ", new Double(oldMaxZ), new Double(maxZ));
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

