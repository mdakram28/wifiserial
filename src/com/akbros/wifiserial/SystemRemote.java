package com.akbros.wifiserial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class SystemRemote extends Activity implements ServerUser {
String name;
String initCmd="";
String focusColor="#1695A3";
String clickColor="#225378";
String remoteCode="";
String label="Remote";
ClientThread sender;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_remote);
		Bundle bundle = getIntent().getExtras();
		name = bundle.getString("NAME");
		sender = new ClientThread(bundle.getString("IP"),bundle.getInt("PORT"),this);
		
		try{
			sender.connect(bundle.getString("PASS"));
		}
		catch(Exception e){}
		populateView((LinearLayout)findViewById(R.id.system_remotes_root_view),name);
		sender.startRemote(remoteCode);
		sender.SendRemoteData(initCmd);
	}

	private void populateView(LinearLayout v,String path) {
		InputStream input=null;
		try {
			input = getAssets().open(name+".systemRemote");
			BufferedReader r = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			String line="";
			while((line=r.readLine())!=null)
			{
				System.out.println(line);
				if(line.equalsIgnoreCase("<settings>"))
				{
					while(!(line=r.readLine()).equalsIgnoreCase("</settings>"))
					{
						String[] d = line.split("=");
						if(d[0].equalsIgnoreCase("remoteCode"))remoteCode=d[1];
						else if(d[0].equalsIgnoreCase("initCmd"))initCmd=d[1];
						else if(d[0].equalsIgnoreCase("focusColor"))focusColor=d[1];
						else if(d[0].equalsIgnoreCase("clickColor"))clickColor=d[1];
					}
				}
				if(line.equalsIgnoreCase("<row>"))
				{
					LinearLayout ll = new LinearLayout(this);
					ll.setOrientation(LinearLayout.HORIZONTAL);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1f);
					ll.setLayoutParams(params);

					while(!(line=r.readLine()).equalsIgnoreCase("</row>"))
					{
						String[] l=line.split("=");
						if(l[0].equalsIgnoreCase("weight"))
						{
							params.weight=Integer.parseInt(l[1]);
							ll.setLayoutParams(params);
						}
						else if(line.equalsIgnoreCase("<button>"))
						{
							SystemRemoteButton b = new SystemRemoteButton();
							while(!(line=r.readLine()).equalsIgnoreCase("</button>"))
							{
								System.out.println(line);
								String[] d = line.split("=");
								if(d[0].equalsIgnoreCase("icon"))b.icon = d[1];
								else if(d[0].equalsIgnoreCase("text"))b.text = d[1];
								else if(d[0].equalsIgnoreCase("enabled"))b.enabled = d[1].equalsIgnoreCase("true");
								else if(d[0].equalsIgnoreCase("weight"))b.setWeight(Integer.parseInt(d[1]));
							}
							b.focused = focusColor;
							b.pressed = clickColor;
							b.sender = sender;
							ImageButton ib = b.getButton(this);
							ll.addView(ib);
						}
					}
					((ViewGroup) v).addView(ll);
				}
			}
			input.close();
		} catch (IOException e) {e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.system_remote, menu);
		return true;
	}

	@Override
	public void addMessage(String text, int type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exit(String string) {
		// TODO Auto-generated method stub
		
	}
}