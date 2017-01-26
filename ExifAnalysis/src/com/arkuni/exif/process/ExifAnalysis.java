package com.arkuni.exif.process;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;

import com.arkuni.exif.info.TagCodeInfo;

import common.util.HMException;
import common.util.HMException.ExceptionCD;
import common.util.HMTrans;

public class ExifAnalysis implements TagCodeInfo{
	static boolean isBigEndian = false;
	static int ifdAddr = 0;
	public static void main(String[] args) {
		BufferedInputStream bis = null;
		//BufferedWriter bos = null;
		boolean isSOI = false;
		boolean isEOI = false;
		boolean isAPP0 = false;
		boolean isIncludExifHeader = false;
		boolean isAPP1 = false;
		boolean checker = false;
		
		byte[] b = null;
		try {
			bis = new BufferedInputStream(new FileInputStream("D:\\picture\\tmp\\IMG_0739.JPG"));
			//bos = new BufferedWriter(new FileWriter("D:\\picture\\dica\\PA250093.dat"));
			int i=0;
			b = new byte[12];
			StringBuilder tmp= null;
			String hexNumber = "";
			tmp = new StringBuilder();
			i = bis.read(b);
			String isSOICheckString = "";
			String isAPP1CheckString = "";
			String exifHeaderString = "";
			String checkEndianString = "";
			int app1Size = 0;
			if ( i == -1) throw new HMException(ExceptionCD.FAIL_VALUE, "FAIL TO READ IMAGE FILE");
			
			isSOICheckString = byteToHexString(b[0]) + byteToHexString(b[1]);
			if (isSOICheckString.equals("ffd8")) isSOI = true;
			
			if (!isSOI) throw new HMException(ExceptionCD.WRONG_VALUE, "NOT IMAGE");
			
			System.out.println("it is Img");
			
			isAPP1CheckString = byteToHexString(b[2]) + byteToHexString(b[3]);
			if (isAPP1CheckString.equals("ffe0")) isAPP0 = true;
			else if (isAPP1CheckString.equals("ffe1")) isAPP1 = true;

			if (!isAPP1) throw new HMException(ExceptionCD.WRONG_VALUE, "NOT APP1 MARKER");
			
			System.out.println("it is APP1 tag");
			
			app1Size = ((b[4] & 0xff) << 8) + ((b[5] & 0xff) << 0);
			
			System.out.println("size : "+app1Size);
			
			exifHeaderString = 	((char)(b[6] & 0xff))+""+
					((char)(b[7] & 0xff))+""+
					((char)(b[8] & 0xff))+""+
					((char)(b[9] & 0xff))+""+
					((char)(b[10] & 0xff))+""+
					((char)(b[11] & 0xff));

			if ( !HMTrans.trim(exifHeaderString).equals("Exif")) throw new HMException(ExceptionCD.WRONG_VALUE, "NOT EXIF TAG");
			
			System.out.println(HMTrans.trim(exifHeaderString));
			
			b = new byte[1024];
			i = bis.read(b);
			
			if ( i == -1) throw new HMException(ExceptionCD.FAIL_VALUE, "FAIL TO READ IMAGE FILE");
			
			checkEndianString = ((char)(b[0] & 0xff))+""+((char)(b[1] & 0xff));
			
			if (checkEndianString.equals("II")) {
				isBigEndian = true;
			} else if (checkEndianString.equals("MM")) {
				isBigEndian = false;
			} else {
				throw new HMException(ExceptionCD.WRONG_VALUE, "ERROR READ ENDIAN INFO");
			}
			
			System.out.println("is big endian : "+ isBigEndian);
			

			
			byte [] checkbyte = new byte[4];
			for(i=0; i<4; i++) {
				checkbyte[i] = b[i+4];
			}
			checkbyte = changeToLittleEndian(checkbyte);
			
			
			/*String IFDPositionByteAddress = "";
			IFDPositionByteAddress = byteToHexString(checkbyte[0])+byteToHexString(checkbyte[1])+byteToHexString(checkbyte[2])+byteToHexString(checkbyte[3]);*/
			
			ifdAddr = ((checkbyte[0] & 0xff) << 24) + ((checkbyte[1] & 0xff) << 16) + ((checkbyte[2] & 0xff) << 8) + ((checkbyte[3] & 0xff) << 0);
			System.out.println("ifd address : "+ ifdAddr);
			
			checkbyte = new byte[2];
			for(i=0; i<2; i++) {
				checkbyte[i] = b[i+ifdAddr];
			}
			checkbyte = changeToLittleEndian(checkbyte);

			int IFDEntryCnt = 0;
			IFDEntryCnt = ((checkbyte[0] & 0xff) << 8) + ((checkbyte[1] & 0xff) << 0);
			System.out.println("ifd entry cnt : "+ IFDEntryCnt);
			ifdAddr = ifdAddr+2;
			
			for (int j=0; j<IFDEntryCnt; j++) {

				int tagCode = 0;
				tagCode = getIntValue(2, b,ifdAddr, true);
				System.out.println("tagCode : " + TiffTagCD.findTagNm(tagCode).name());
				
				if (tagCode==34665) processExifIfdTag(b);
				
				
				int dataType = 0;
				dataType = getIntValue(2, b,ifdAddr, true);
				DataTypeCd data = DataTypeCd.findTagNm(dataType);
				System.out.println("dataType : " + dataType + ", " +data.name());


				int datacnt = 0, datasize = 0;
				datacnt = getIntValue(4, b,ifdAddr, true);
				datasize = data.getByteVal()*datacnt;
				
				System.out.println("component cnt : " + datacnt + ", " + "data size : " + datasize);

				
				if (datasize > 4) {
					int dataAddr = 0;
					dataAddr = getIntValue(4, b,ifdAddr, true);
					
					if (data.equals(DataTypeCd.ASCII_STRING)) {
						String datavalue = "";
						datavalue = HMTrans.trim(getASCIIValue(datasize, b, dataAddr, false));
						System.out.println("datavalue : " + datavalue);
					} else if (data.equals(DataTypeCd.UNSIGNED_RATIONAL) || data.equals(DataTypeCd.SIGNED_RATIONAL)) {
						int datavalue = 0;
						datavalue = getIntValue(4, b, dataAddr, false) / getIntValue(4, b, dataAddr+4, false);
						System.out.println("datavalue : " + datavalue);
					} else {
						int datavalue = 0;
						datavalue = getIntValue(datasize, b, dataAddr, false);
						System.out.println("datavalue : " + datavalue);
					}
					
				} else {
					if (data.equals(DataTypeCd.ASCII_STRING)) {
						String datavalue = "";
						datavalue = HMTrans.trim(getASCIIValue(datasize, b,ifdAddr, true));
						System.out.println("datavalue : " + datavalue);
					} else {
						int datavalue = 0;
						datavalue = getIntValue(datasize, b, ifdAddr, true);
						System.out.println("datavalue : " + datavalue);
					}
					ifdAddr = ifdAddr+(4-datasize);
				}
			}
			
					
		} catch (IOException e) {
			e.printStackTrace();
		} catch (HMException e) {
			e.printStackTrace();
		} finally {
			if (bis !=null) {
				try{
					bis.close();
				} catch (IOException e) {}
			}
			/*if (bos !=null) {
				try{
					bos.close();
				} catch (IOException e) {}
			}*/
		}
	}
	
	public static String byteToHexString(byte val) {
		String rtn = Integer.toHexString(0xff & val);
		if (rtn.length() > 1) return rtn;
		return "0"+rtn;
	}
	
	public static byte[] changeToLittleEndian(byte[] val) {
		if (!isBigEndian) return val;
		int bytelen = val.length;
		byte [] rtn = new byte[bytelen];
		for (int i = 0; i<bytelen; i++) {
			rtn[i] = val[bytelen-(i+1)];
		}
		return rtn;
	}
	
	public static int getIntValue(int datasize, byte[] org, int offset, boolean isMoveAddr) {
		int rtn = 0;
		byte [] val = new byte[datasize];
		for(int i=0; i<datasize; i++) {
			val[i] = org[i+offset];
		}
		val = changeToLittleEndian(val);
		
		for(int i=0; i<datasize; i++) {
			rtn += ((val[i] & 0xff) << ((8*(datasize-1))-(i*8)));

		}
		if(isMoveAddr) ifdAddr = ifdAddr+datasize;
		return rtn;
	}
	
	public static String getASCIIValue(int datasize, byte[] org, int offset, boolean isMoveAddr) {
		String rtn = "";
		byte [] val = new byte[datasize];
		for(int i=0; i<datasize; i++) {
			val[i] = org[i+offset];
		}
		val = changeToLittleEndian(val);
		
		for(int i=0; i<datasize; i++) {
			rtn += ((char)(val[i] & 0xff))+"";

		}
		if(isMoveAddr) ifdAddr = ifdAddr+datasize;
		return rtn;
	}
	
	public static String getHexValue(byte[] val) {
		String rtn = "";
		return rtn;
	}
	
	public static void processExifIfdTag(byte[] org) {
		
	}
}
