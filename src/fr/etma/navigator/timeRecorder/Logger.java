package fr.etma.navigator.timeRecorder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Logger {

	protected static FileWriter outFile = null;
	protected static FileWriter outFileXML = null;
	private static Logger instance = null;
	
	public static Logger getInstance() {
		if (instance == null) {
			instance = new Logger();
		}
		return instance;	
	}
	
	private Logger() {
		String date = new Date(System.currentTimeMillis()).toString();
		date = date.replace(' ', '_').replace(':', '_');
		String fileName = "Exp_Intersemestre_" + date + ".txt";
		String xmlfileName = "Exp_Intersemestre_" + date + ".xml";

		try {
			outFile = new FileWriter(fileName, true);
			outFile.write("started" + '\n');
			outFile.flush();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			outFileXML = new FileWriter(xmlfileName, true);
			outFileXML.write("<?xml version=\"1.0\" encoding=\"ASCII\" standalone=\"yes\"?>");
			outFileXML.write("<test date='" + date + "'>");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void close() {
		try {
		outFile.close();
		outFileXML.write("</test>");
		outFileXML.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	@Deprecated
	public void record(String pname,  double time0, double lgh, double rt, double pre ) {
		try {
			outFile.write("step " + pname + "\n");
			outFile.write("Duration : "
					+ ((System.currentTimeMillis() - time0) / 1000) + '\n');
			outFile.write("Length : " + lgh + '\n');
			outFile.write("Rotation : " + rt + '\n');
			outFile.write("Precision : " + pre + '\n');
			outFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void recordXML(String pname,  double time0, double lgh, double rt, double pre ) {
		try {
			outFileXML.write("\n<step> ");
			outFileXML.write("<name>" + pname + "</name>");
			outFileXML.write("<duration>"
					+ ((System.currentTimeMillis() - time0) / 1000) + "</duration>");
			outFileXML.write("<length>" + lgh + "</length>");
			outFileXML.write("<rotation> " + rt + "</rotation>");
			outFileXML.write("<precision>" + pre + "</precision>");
			outFileXML.write("</step> ");
			outFileXML.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void recordXML(int id, boolean success, double time0, double lgh, double rt, double pre ) {
		try {
			outFileXML.write('\n');
			outFileXML.write("<step> ");
			outFileXML.write("<id>" + id + "</id>");
			
			outFileXML.write("<status>");
			if (success) 
				outFileXML.write("RIGHT");
			else
				outFileXML.write("WRONG");
			outFileXML.write("</status>");
			outFileXML.write("<duration>"
					+ ((System.currentTimeMillis() - time0) / 1000) + "</duration>");
			outFileXML.write("<length>" + lgh + "</length>");
			outFileXML.write("<rotation> " + rt + "</rotation>");
			outFileXML.write("<precision>" + pre + "</precision>");
			outFileXML.write("</step> ");
			outFileXML.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
