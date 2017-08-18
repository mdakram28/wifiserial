package com.akbros.wifiserial;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClientThread implements Runnable {
	InetAddress serverAddr;
	Socket socket;
	private String serverIp;
	private int serverPort;
	ServerUser holder = null;
	BufferedReader input;
	PrintWriter out;
	String message="";
	String PortList[] = null;
	String info="";
	String error="";
	String password="";
	
	public ClientThread(String serverIp,int serverPort,ServerUser holder){
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.holder = holder;
	}
	public String getServerIp() {
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public int getServerPort() {
		return serverPort;
	}
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
	@Override
	public void run() {
		try {
			out.println("data;"+message);
			System.out.println(message);
		}
		catch (Exception e1) {
			holder.addMessage("Error : Data sending failed",3);
		}
	}
	
	public void send(String text){
		if(socket==null)
		{
			holder.addMessage("Error : Server Not initialized", 3);
			return;
		}
		this.message = text;
		(new Thread(this)).start();
	}
	boolean done = false;
	String err="";
	public void connect(String pass) throws Exception{
		password = pass;
			Thread receiver = new Thread(new Runnable(){
				@Override
				public void run() {
					try{
						init();
						err = "";
					}
					catch(Exception e)
					{
						err = e.getMessage();
					}
					finally{
						done = true;
					}
					while(true)
					{
						try{
							String line = input.readLine();
							if(line==null)
							{}
							else if(isError(line))
							{
								error = line;
								holder.addMessage("Error : "+line, 3);
								if(line.equalsIgnoreCase("<e : Server Shutting down/>"))
								{
									holder.exit("Server closed");
								}
							}
							else if(isInfo(line))
							{
								info = line;
								holder.addMessage(line, 2);
							}
							else
							{
								holder.addMessage(format(line), 1);
							}
						}
						catch(Exception e){}
					}
				}
				
				private boolean isInfo(String line) {
					try{
						return line.charAt(1)==':';
					}
					catch(Exception e){}
					return false;
				}

				public String format(String s)
				{
					String ret = "";
					int l = s.length();
					boolean cmd = false;
					String msg="";
					for(int i=0;i<l;i++)
					{
						char ch = s.charAt(i);
						if(cmd)
						{
							if(ch=='>')
							{
								cmd = false;
								ret+=getString(msg);
							}
							else
							{
								msg+=ch;
							}
						}
						else
						{
							if(ch=='<')
							{
								cmd = true;
								msg="";
							}
							else
							{
								ret+=ch;
							}
						}
					}
					return ret;
				}
				public String getString(String s){
					String ret="";
					if(s.equals("n"))
					{
						ret = "\n";
					}
					return ret;
				}
				public void init() throws Exception{
					socket = new Socket(serverIp,serverPort);
					out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
					input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out.println("cmd;init/");
					String res = input.readLine();
					if(res.equalsIgnoreCase("<?password/>"))
					{
							out.println(password);
							res = input.readLine();
							if(isError(res))
							{
								socket.close();
								throw(new Exception("Fatal Error : "+res));
							}
							else
							{
								holder.addMessage("Server : "+res, 2);
							}
					}
					else
					{
						holder.addMessage("Server : "+res, 2);
					}
					getPortList();
				}
			});
			receiver.start();
			
			while(!done){}
			if(!err.equals(""))
			{
				throw(new Exception(err));
			}
	}
	
	public boolean isError(String s)
	{
		boolean ret = false;
		try{
			ret = s.substring(0, 5).equalsIgnoreCase("<e : ");
		}
		catch(Exception w){}
		return ret;
	}

	public void exit(){
		out.println("cmd;dispatch/");
		out.println("cmd;exit/");
	}
	public void SendRemoteData(String Data){
		out.println("cmd;remotedata/"+Data);
	}
	public void startRemote(String RemName ){
		out.println("cmd;initremote/"+RemName);
		
	}
	public void getPortList() {
		ArrayList<String> temp = new ArrayList<String>();
		out.println("cmd;portlist/");
		System.out.println("Requesting portlist");
		String line="";
		
		try {
			line = input.readLine();
			while(true)
			{
				line = input.readLine();
				System.out.println("port : "+line);
				if(line.equalsIgnoreCase("</PortList>"))
				{
					break;
				}
				temp.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		PortList = new String[temp.size()];
		for(int i=0;i<temp.size();i++)
		{
			PortList[i] = temp.get(i);
		}
	}
	public boolean connectPort(String port) {
		info="";
		error="";
		out.println("cmd;connect/"+port);
		while(info=="" && error==""){}
		if(info.equalsIgnoreCase("<:PortStarted/>"))
		{
			return true;
		}
		else if(error.equalsIgnoreCase("<e : Unable to open serial port>"))
		{
			return false;
		}
		else
		{
			return false;
		}
	}
	public boolean disconnect(){
		out.println("cmd;dispatch/");
		return true;
	}
	public void settings(String baud, String parity, String stopbits,String handshake) {
		out.println("cmd;settings/"+baud+","+parity+","+stopbits+","+handshake);
	}
}