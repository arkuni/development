package arkuni.test.sort;

import common.util.CryptUtil;

public class CryptStringTest {
	public static void main(String[] args) {
		String scratchno = "3124697918234444";
		String encryptScratchNo = "";
		try {
			encryptScratchNo = CryptUtil.aesEncrypt("happymoneyno1234567", scratchno);
			System.out.println("encrypt: " + encryptScratchNo);
			System.out.println("decrypt: " + CryptUtil.aesDecrypt("happymoneyno1234567",encryptScratchNo));
			
			encryptScratchNo = CryptUtil.tripleDesEncrypt("happymoneyno1234567", scratchno);
			System.out.println("encrypt: " + encryptScratchNo);
			System.out.println("decrypt: " + CryptUtil.tripleDesDecrypt("happymoneyno1234567",encryptScratchNo));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}