package txtShow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

public class TxtShow {

// String fileAddress = "d://tx.txt";
// 
// public TxtShow(String fileAdderss){
//
//	 this.fileAddress = fileAddress;
// }
 
 public void writeStr (String fileAddress,String str){

	 try{
		 FileOutputStream fos=new FileOutputStream(fileAddress,true);
		 fos.write(str.getBytes());
		 }catch (Exception e) {e.printStackTrace();}
		}
	 

	
	public static void main(String[] args) {
		String str1 = "≤‚ ‘"+"\r\n";
		String str2 = "2"+"\r\n";
		String str3 = "3"+"\r\n";
		
		TxtShow ts = new TxtShow();
		//String fileAddress = "d:/workspace/tx.txt";
		//String fileAddress = "txtInformation/txt.txt";
		String fileAddress = "txtInformation\\txt.txt";
		ts.writeStr(fileAddress,str1);
		ts.writeStr(fileAddress,str2);
		ts.writeStr(fileAddress,str3);
		
//		String test="≤‚ ‘";
//		try {
//			FileOutputStream fos=new FileOutputStream("d:/test.txt");
//			fos.write(test.getBytes());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	
	}
	}

