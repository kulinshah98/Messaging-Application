// File Name ServerClass.java
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ServerClass extends Thread {
   private ServerSocket serverSocket;
   private Socket server;
   private DataInputStream in;
   private DataOutputStream out;
   private static DatagramSocket udpsoc;
   private static DatagramSocket udpsoc2;


   public ServerClass(int port, int flag) throws IOException {
      serverSocket = new ServerSocket(port);
     if(flag==0)
     {
    	 udpsoc = new DatagramSocket(10002);
    	 udpsoc2 = null;
     }
     else
     {
    	 udpsoc = null;
    	 udpsoc2 = new DatagramSocket(10003);
     }
      serverSocket.setSoTimeout(1000000);
   }

   //sendfile function takes filename as input and send file to other end.
	public void sendfile(String filename) throws IOException
	{
	     FileInputStream fin = new FileInputStream(filename);
	     byte[] buffer = new byte[4096];
	     long size,total=0;
	     File f=null;
	     f = new File(filename);
	     long total1 = f.length();
	     out.writeLong(total1);
	     while( (size = fin.read(buffer)) > 0 )
	     {
	    	 total = total + size;
	    	 out.write(buffer,0,(int)size);
	     }
	     fin.close();
	}

   public void receivefile(String filename, String method)  throws IOException
	{
	   filename = in.readUTF();
	   filename = "a"+filename;
	    FileOutputStream fout = new FileOutputStream(filename);
	    byte[] buffer = new byte[4096];

	    long file_sz = in.readLong();
	    long recv_sz=0;
	    long size;

	    byte[] recv = new byte[4096];
	    DatagramSocket udps=null;
	    if(udpsoc==null)
	    {
	    	udps = udpsoc2;
	    }
	    else if(udpsoc2==null)
	    {
	    	udps = udpsoc;
	    }
	    DatagramPacket datap = new DatagramPacket(recv, recv.length);

	    while(recv_sz < file_sz)
	    {
	    	if(method.equals("UDP"))
	    	{
	    		size = in.read(buffer);
	    		if(size<0)
	    		{
	    			udpsoc.receive(datap);
		    		buffer = datap.getData();
	    		}
	    	}
	    	else
	    	{
	    		size = in.read(buffer);
	    	}
	    	if(size < 0)
	    	{
	    		break;
	    	}
	    	recv_sz += size;
	    	printprogressBar(file_sz, recv_sz);
	    	fout.write(buffer, 0, (int)size);
	    }
	    System.out.printf("\n");
	    fout.close();
	}

   public void printprogressBar(long total, long recv)
   {
	   double per = recv*10/total,ind=0.0;
		System.out.printf("\r[");
		while(ind<per)
		{
			System.out.printf("=");
			ind = ind + 1;
		}
		while(ind<10.0)
		{
			System.out.printf(" ");
			ind = ind+1;
		}
		System.out.printf("] "+per);
   }

   public void run() {
		   try {
	            System.out.println("Waiting for client on port " +
	               serverSocket.getLocalPort() + "...");
	            server = serverSocket.accept();
	            Scanner p = new Scanner(System.in);
	            String str;
	            String[] words;

	            in = new DataInputStream(server.getInputStream());
	            out = new DataOutputStream(server.getOutputStream());

	            System.out.println("Just connected to " + server.getRemoteSocketAddress());

	            while(true){
	            	str = in.readUTF();
	            	if(str.equals("Goodbye"))
	            	{
	            		break;
	            	}
	            	System.out.println("\r"+str);
	              words = str.split("\\s");
	              if(words[0].equals("Send"))
	              {
	                receivefile(words[1], words[2]);
	              }
	              System.out.printf(">> ");
	            }

	            server.close();

	         }catch(SocketTimeoutException s) {
	            System.out.println("Socket timed out!");
	         }catch(IOException e) {
	            e.printStackTrace();
	         }
   }

   public static void main(String [] args) {
      int port1 = 9999;
      int port2 = 10000;
      int port3 = 10003;

      try {
          Thread server_thread = new ServerClass(port1,0);
          server_thread.start();
       }catch(Exception e) {

       }

      try{
    	  Thread client_thread = new ClientClass("localhost", port2, port3);
          client_thread.start();
      }
      catch(Exception e){

      }

   }
}
