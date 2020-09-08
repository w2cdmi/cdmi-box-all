package pw.cdmi.box.disk.test;

public class TestT { 
	 public static void main (String[] args)
	 { 
		 TestT a=new TestT(); 
	  a.method(8); 
	  a.method(1.2f); 
	  
	  Thread t = new Thread()
	  { 
	      public void run()
	      {
	          while(true)
	          {
	          }
	      }
	  }; 
	  t.start();
	  t = null;
	 } 
	 void method(float i) 
	 { 
	  System.out.println("float: "+i); 
	 } 
	 void method(long i) 
	 { 
	 System.out.println("long: "+i); 
	 } 
	}