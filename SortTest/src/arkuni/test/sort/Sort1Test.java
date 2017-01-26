package arkuni.test.sort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class Sort1Test {
	private static int [] randData = new int[30000000];
	public static void main(String[] args) {
		BufferedReader br = null;
		BufferedWriter out = null;
		try {
			
			br = new BufferedReader(new FileReader("D:\\out.txt"));
			String numdata = "";
			int i =0;
			while ((numdata = br.readLine()) != null) {
				if (!numdata.equals("")) {
					randData[i++] = Integer.parseInt(numdata);
				}
			}
			System.out.println("start : ");
			long starttime = System.currentTimeMillis();
			quicksort(0, randData.length-1, randData);
			long endtime = System.currentTimeMillis();
			System.out.println("sorted. " + (endtime - starttime));
			
			out = new BufferedWriter(new FileWriter("D:\\sorted1.txt"));
			for ( int j=0; j<randData.length; j++) {
				out.write("" + randData[j]+"\n");
			}
			
			
			
		} catch (Exception e) {

		} finally {
			try {
				br.close();
				out.close();
			} catch (Exception e) {
			}
			
			
		}

	}
	
	private static void quicksort(int l, int u, int[] x) {
		if(u <= l)
			return;
		
		int m = l;
		for(int i = l + 1; i <= u; ++i) 
			if(x[l] > x[i])
				swap(++m, i, x);
		swap(m, l, x);
		quicksort(l, m-1, x);
		quicksort(m+1, u, x);
	}

	private static void swap(int a, int b, int[] x) {
		int temp = x[a];
		x[a] = x[b];
		x[b] = temp;
	}
}
