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

