package com.akbros.wifiserial;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ServersActivity extends Activity implements OnItemClickListener {
ListView serversListView = null;
ServersListAdapter servers = null;
ArrayList<Server> serversList = new ArrayList<Server>();
int defaultPort = 255;
boolean scanning = false;
TextView status;
Server selected ;
String fileName="";
boolean remote=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_servers);
		// Show the Up button in the action bar.
		setupActionBar();
		ProgressBar p = (ProgressBar)ServersActivity.this.findViewById(R.id.progressBar1);
		status = (TextView)this.findViewById(R.id.status_textview);
		p.setVisibility(View.INVISIBLE);
		servers = new ServersListAdapter(this,serversList,serversListView);
		
		serversListView = (ListView)this.findViewById(R.id.ServersListView);
		serversListView.setAdapter(servers);
		serversListView.setOnItemClickListener(this);
		try{
			Bundle bundle = getIntent().getExtras();
			fileName = bundle.getString("FILE_NAME");
			if(fileName!="")remote = true;
		}
		catch(Exception e){}
		
		refreshServersList(null);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.servers, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void refreshServersList(View v){
		if(!scanning)
		{
			new ScanOperation().execute("");
			setStatus("Scanning LAN");
		}
	}
	String st;
	public void setStatus(String s){
		st=s;
		ServersActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
            	status.setText("STATUS : "+st);
            }
        });
		
	}
	private class ScanOperation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
			scanning = true;
        	getInterfaces();
			scanning = false;
        	return null;
        }
        
        @Override
        protected void onPostExecute(String result) {
			p.setVisibility(View.INVISIBLE);
        }
        
        ProgressBar p=null;
        @Override
        protected void onPreExecute() {
			//Toast.makeText(ServersActivity.this, "Scanning in Progress ...", Toast.LENGTH_SHORT).show();
			p = (ProgressBar)ServersActivity.this.findViewById(R.id.progressBar1);
			p.setVisibility(View.VISIBLE);
			//setStatus("Searching for Servers");
			servers.clear();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
			//Toast.makeText(ServersActivity.this, "Scanning finished", Toast.LENGTH_SHORT).show();
			//setStatus(servers.getCount()==0?"Search finished , No Servers found":"Search finished");
        }
    }
	
	public void  getInterfaces (){
	      try {
	         Enumeration<?> e = NetworkInterface.getNetworkInterfaces();
	 
	         while(e.hasMoreElements()) {
	            NetworkInterface ni = (NetworkInterface) e.nextElement();
	            System.out.println("Net interface: "+ni.getName());
	            
	            Enumeration<?> e2 = ni.getInetAddresses();
	 
	            while (e2.hasMoreElements()){
	            	try{
		               InetAddress ip = (InetAddress) e2.nextElement();
		               System.out.println("IP address: "+ ip.toString());
					    Server temp = new Server(ip.getHostAddress(),ip.getHostName(),defaultPort);
					    if(!temp.valid)continue;
					    
					    servers.addServer(temp);
					    changed();
	            	}
				    catch(Exception ex){
				    	ex.printStackTrace();
				    }
	            }
	         }
	         
             //Socket s = new Socket("10.0.2.2",defaultPort);
			    Server temp = new Server("10.0.2.2","virtual device host server",defaultPort);
			    if(temp.valid)
			    {
			    	servers.addServer(temp);
			    	changed();
			    }
			    temp = new Server("192.168.1.2","virtual device host server",defaultPort);
			    if(temp.valid)
			    {
			    	servers.addServer(temp);
			    	changed();
			    }
			    //s.close();
	      }
	      catch (Exception e) {
	         e.printStackTrace();
	      }
	   }
	
	public void validateAll(){
		for(int i=0;i<servers.getCount();i++)
		{
			servers.servers.get(i).validate();
		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		Server server = (Server)servers.getItem(pos);
		if(server.valid)
		{
			if(server.secure)
			{
				selected = server;
				createPasswordPopup();
			}
			else
			{
				end(server.getIp(),defaultPort,"");
			}
		}
		else
		{
			createPopup("Server Inactive");
		}
	}

	Dialog dialog;
	private void createPopup(String text) {
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.server_inactive_dialog);
		dialog.setTitle("Error");
		TextView t = (TextView)dialog.findViewById(R.id.error_textView);
		t.setText(text);
		Button okB = (Button) dialog.findViewById(R.id.ok_server);
		okB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
	
	String password = "";
	boolean passwordEntered = false;
	private void createPasswordPopup() {
		passwordEntered = false;
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.password_input_dialog);
		dialog.setTitle("Enter Password To Server");
		Button okB = (Button) dialog.findViewById(R.id.ok_dialog);
		okB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText e = ((EditText)dialog.findViewById(R.id.password_editText));
				password = e.getText().toString();
				passwordEntered = true;
				dialog.dismiss();
				Next();
				//Toast.makeText(ServersActivity.this, password, Toast.LENGTH_SHORT).show();
			}
		});
		
		Button cancelB = (Button) dialog.findViewById(R.id.cancel_dialog);
		cancelB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	public void Next(){
		ServersActivity.this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				if(passwordEntered)
				{
					end(selected.getIp(),defaultPort,password);
				}
			}});
	}
	public void changed(){
	    ServersActivity.this.runOnUiThread(new Runnable() {
	    	@Override
           public void run() {
           	servers.notifyDataSetChanged();
           	setStatus(servers.servers.size()+" AKBros server(s) found");
        }});
	}
	
	public void end(String ip,int port,String password)
	{
		Intent returnIntent = new Intent();
		returnIntent.putExtra("ip",ip);
		returnIntent.putExtra("port",port);
		returnIntent.putExtra("password",password);
		setResult(RESULT_OK,returnIntent);
		finish();
	}
	
	Dialog ipDialog;
	EditText input;
	public void manualIP(View v){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enter IP");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   final Server server = new Server(input.getText().toString(),input.getText().toString(), defaultPort);
                	   if(server.valid)
               			{
               				if(server.secure)
               				{
               					selected = server;
               					createPasswordPopup();
               				}
               				else
               				{
               					end(server.getIp(),defaultPort,"");
               				}
               			}
               			else
               			{
               				AlertDialog.Builder builder = new AlertDialog.Builder(input.getContext());
               		        builder.setMessage("Server not running on specified IP. Continue ?");
               		        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									end(server.getIp(),defaultPort,"");
								}
							});
               		        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							});
               		        builder.create().show();
               			}
                   }
               });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                   }
               });
        
        input = new EditText(this);
        builder.setView(input);
        
        ipDialog = builder.create();
        ipDialog.show();
	}
}
