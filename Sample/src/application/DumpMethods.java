package application;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;

public class DumpMethods {
   public static void main(String[] a)
   {
	   URL url = String.class.getResource("String.class");
	   System.out.println(url);
	   for (String cpElement : System.getProperty("java.class.path").split(File.pathSeparator)) {
		    File cpFile = new File(cpElement);
		    if (!cpFile.exists()) {
		         continue;
		    }
		    if (cpFile.getName().endsWith("rt.jar")) {
		    	
		    	
		    } 		}
   }
}