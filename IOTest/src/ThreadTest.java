
public class ThreadTest {
    static B b; 
    public static void main(String args[]){ 
        b=new B(); 
        b.start(); 
        try{
            Thread.sleep(3000); 
        }catch(Exception e){} 
        b._stop();
    } 
}
class B extends Thread{ 
    public B(){}
    
    void _stop(){ 
     System.out.println("_stop 시작");
     System.out.println(Thread.currentThread().hashCode());
        Thread.currentThread().interrupt();
        System.out.println("_stop 끝");
    } 
    
    public void run(){ 
     System.out.println(Thread.currentThread().hashCode());
        System.out.println("웨이트로 들어간다"); 
        try{
         synchronized(this){
          wait();
         }
        }
        catch(InterruptedException ie){
         System.out.println("이래야지 ㅡㅡ");
        }
        catch(Exception e){ 
            System.out.println("ㅋㅋㅋ"); 
        }
    } 
}
