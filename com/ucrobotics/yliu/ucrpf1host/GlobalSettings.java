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
	    mPcs.firePropertyChange("extruderPreheatTemperature", Double.valueOf(oldTemperature), Double.valueOf(extruderPreheatTemperature));
	}
    }

    public double getBedWidth() {
	return bedWidth;
    }
    public void setBedWidth(double bedWidth) {
	double oldBedWidth = this.bedWidth;
	this.bedWidth = bedWidth;
	if (oldBedWidth != bedWidth) {
	    mPcs.firePropertyChange("bedWidth", Double.valueOf(oldBedWidth), Double.valueOf(bedWidth));
	}
    }

    public double getBedHeight() {
	return bedHeight;
    }
    public void setBedHeight(double bedHeight) {
	double oldBedHeight = this.bedHeight;
	this.bedHeight = bedHeight;
	if (oldBedHeight != bedHeight) {
	    mPcs.firePropertyChange("bedHeight", Double.valueOf(oldBedHeight), Double.valueOf(bedHeight));
	}
    }

    public double getMaxZ() {
	return maxZ;
    }
    public void setMaxZ(double maxZ) {
	double oldMaxZ = this.maxZ;
	this.maxZ = maxZ;
	if (oldMaxZ != maxZ) {
	    mPcs.firePropertyChange("maxZ", Double.valueOf(oldMaxZ), Double.valueOf(maxZ));
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

