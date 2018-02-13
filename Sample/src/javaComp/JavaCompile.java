package javaComp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JavaCompile {

	  public static StringBuilder printLines(String name, InputStream ins) throws Exception {
	    String line = null;
	    StringBuilder compileMsg=new StringBuilder();
	    BufferedReader in = new BufferedReader(new InputStreamReader(ins));
	    while ((line = in.readLine()) != null) {
	    	compileMsg.append(name + " " + line+"\n");
	    }
	    return compileMsg;
	  }

	  public static StringBuilder runProcess(String command) throws Exception {
	    Process pro = Runtime.getRuntime().exec(command);
	    StringBuilder compileMsg=new StringBuilder();
	    //compileMsg.append(printLines(command + " stdout:", pro.getInputStream()));
	   // compileMsg.append(printLines(command + " stderr:", pro.getErrorStream()));
	    compileMsg.append(printLines("", pro.getInputStream()));
		compileMsg.append(printLines("", pro.getErrorStream()));
	    pro.waitFor();
	   // compileMsg.append(command + " exitValue() " + pro.exitValue());
	    return  compileMsg;
	  }

	}