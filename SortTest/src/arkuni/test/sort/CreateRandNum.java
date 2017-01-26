package arkuni.test.sort;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CreateRandNum {
	public static void main(String[] args) {
		
		try {
			System.out.println("start");
			BufferedWriter out = new BufferedWriter(new FileWriter("D:\\out.txt"));
			
			for (int i = 0; i < 30000000; i++) out.write(((int)(Math.random()*3000000)+1)+"\n");

			out.close();
			System.out.println("created.");
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
	}
}

