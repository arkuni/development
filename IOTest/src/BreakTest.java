
public class BreakTest {
	public static void main(String[] args) {
		outer:
			for(int x=0; x<3; x++) {
				inner:
					for(int y=0; y<3;y++) {
					if(x==y) {
						break inner;
						
					}
					System.out.println(x+" and "+y);
				}
				
			}
	}
}
