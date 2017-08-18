package com.akbros.wifiserial;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	String ip;
	String name;
	boolean secure=false;
	int port;
	Socket socket = null;
	boolean validated = false;
	boolean valid = false;
	BufferedReader input;
	ArrayList<CommPort> ports = new ArrayList<CommPort>();
	String password="";
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public boolean isSecure() {
		return secure;
	}
	
	public Server(String ip,String name,int port){
		this.ip = ip;
		this.name = name;
		this.port = port;
		validate();
	}
	
	public boolean validate(){
		validated = false;
		Thread t = new Thread(new ServerValidater());
		t.start();
		new WatchDog(t,1000);
		while(!validated){}
		return valid;
	}
	
	class ServerValidater implements Runnable {
		@Override
		public void run() {
			valid = false;
			try {
				socket = new Socket(ip, port);
				PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				out.println("cmd;init/");
				String line;
				while((line = input.readLine())==""){ }
				if(line.charAt(0)=='<')
				{
					if(line.equalsIgnoreCase("<?password/>"))
					{
						secure = true;
					}
					else
					{
						secure = false;
					}
				}
				else
				{
					throw(new Exception("Invalid data received"));
				}
				out.println("cmd;exit/");
				valid = true;
				socket.close();
				
			} catch (Exception e1) {e1.printStackTrace();}
			validated = true;
		}

	}
	
	class WatchDog implements Runnable{
		Thread t;
		int time;
		
		@Override
		public void run() {
			try{
				Thread.sleep(time);
				if(!validated)
				{
					t.interrupt();
					valid = false;
					validated = true;
				}
			}
			catch(Exception e){}
		}
		
		public WatchDog(Thread t,int timeout){
			this.t = t;
			this.time = timeout;
			(new Thread(this)).start();
		}
	}

	public void authenticate(String password) {
		this.password  =password;
	}
	
}
