
package com.ucrobotics.yliu.ucrpf1host;

import java.util.logging.*;
import java.util.*;
import gnu.io.*;

public class CommandData {
    private String command=null;
    private ArrayList<String> answer=null;

    public CommandData() {
	answer = new ArrayList<String>();
    }

    public void setCommand(String command) {
	this.command = command;
    }
    public String getCommand() {
	return command;
    }
}
