// File Name ClientClass.java
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ClientClass extends Thread {
	private Socket client;
	private DataOutputStream out;
	private DataInputStream in;
	private static DatagramSocket udpsoc;


	//Receivefile function takes filename as input and receives file sent from other side
	public void receivefile(String filename) throws IOException
	{
		DataInputStream din = new DataInputStream(client.getInputStream());
	    FileOutputStream fout = new FileOutputStream("Output1.txt");
	    byte[] buffer = new byte[4096];

	    long file_sz = din.readLong();
	    long recv_sz=0;
	    long size;

	    while(recv_sz < file_sz)
	    {
	    	recv_sz += 4096;
	    	size = din.read(buffer);
	    	if(size < 0)
	    	{
	    		break;
	    	}
	    	fout.write(buffer, 0, (int)size);
	    }
	    din.close();
	    fout.close();
	}

	//sendfile function takes filename as input and send file to other end.
	public void sendfile(String filename, String method, int port) throws IOException
	{

	     FileInputStream fin = new FileInputStream(filename);
	     byte[] buffer = new byte[4096];
	     long size,total=0;
	     File f=null;
	     f = new File(filename);
	     long total1 = f.length();

	     out.writeUTF(filename);
	     out.writeLong(total1);
	     InetAddress host = InetAddress.getByName("localhost");

	     while( (size = fin.read(buffer)) > 0 )
	     {
	    	 if(method.equals("UDP"))
	    	 {
	    		 out.write(buffer,0,(int)size);
	    	 }
	    	 else
	    	 {
	    		 out.write(buffer,0,(int)size);
	    	 }
	    	 total = total + size;
	    	 printprogressBar(total1, total);
	     }
	     System.out.printf("\n");
	     fin.close();

	}


	//This function prints progress bar while sending and receiving.
	public void printprogressBar(long total, long sent)
	{
		double per = sent*10/total,ind=0.0;
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


	//This function establishes connection and read input from terminal.
   public ClientClass(String serverName, int port, int port2) {
	   while(true)
	   {
		   try {
		         System.out.println("Connecting to " + serverName + " on port " + port);
		         String[] words;
		         while(true)
		         {
		        	 try{
		        		 client = new Socket(serverName, port);
		        		 break;
		        	 }
		        	 catch(Exception e){

		        	 }
		         }
		         String str;
		         Scanner sc = new Scanner(System.in);

		         System.out.println("Just connected to " + client.getRemoteSocketAddress());
		         OutputStream outToServer = client.getOutputStream();

		         out = new DataOutputStream(outToServer);

		         InputStream inFromServer = client.getInputStream();
		         in = new DataInputStream(inFromServer);

		         while(true){
		           System.out.printf(">> ");
		           str = sc.nextLine();
		           if(str.equals("Goodbye"))
		           {
		        	   break;
		           }
		           words = str.split("\\s");
		           out.writeUTF(str);
		           if(words[0].equals("Send"))
		           {
		        	   if(words.length!=3)
		        	   {
		        		   System.out.println("Give method and File Name...");
		        	   }
		        	   else
		        	   {
		        		   sendfile(words[1], words[2], port2);
		        	   }
		           }

		         }

		         break;
		      }catch(IOException e) {
		         e.printStackTrace();
		      }
	   }
   }

   public static void main(String [] args){
	   int port1 = 9999;
		int port2 = 10000;
		int port3 = 10002;
		String serverName = "localhost";
		try {
			   Thread server_thread = new ServerClass(port2,1);
			   server_thread.start();
		   }
		   catch(Exception e){

		   }

	   try {
		   Thread client_thread = new ClientClass("localhost",port1, port3);
		   client_thread.start();
	   }
	   catch(Exception e){
		   System.out.println("sfjkadskfkajdf");
	   }

   }
}
