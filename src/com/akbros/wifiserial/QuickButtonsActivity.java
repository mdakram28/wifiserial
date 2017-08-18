package com.akbros.wifiserial;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class QuickButtonsActivity extends Activity implements OnItemClickListener {
	ListView list;
	String[] names;
	String selected = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quick_buttons);
		names = this.fileList();
		//populateRemoteList();
		list = (ListView)this.findViewById(R.id.quick_buttons_list);
		list.setAdapter(new QuickButtonsListAdapter(this,names,list));
	}

	private void populateRemoteList() {
		BufferedReader reader=null;
		
		try {
			
			/*FileInputStream fis = openFileInput("remotes.list");
			reader = new BufferedReader(new InputStreamReader(fis));
			String line="";
			while((line=reader.readLine())!=null)
			{
				if(line.equalsIgnoreCase("<Remote>"))
				{
					while(!(line=reader.readLine()).equalsIgnoreCase("</Remote>"))
					{
						String[] l = line.split(";");
						if(l[0].equalsIgnoreCase("name"))names.add(l[1]);
						if(l[0].equalsIgnoreCase("file"))fileNames.add(l[1]);
						if(l[0].equalsIgnoreCase("type"));
						if(l[0].equalsIgnoreCase("user"));
					}
				}
			}*/
			
			names = this.fileList();
			
		}
		catch(Exception e)
		{e.printStackTrace();}
		try {
			reader.close();
		} catch (IOException e) {}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quick_buttons, menu);
		return true;
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {}
	
	public void newRemote(View v){
		Intent i =new Intent(this,RemoteEditActivity.class);
		this.startActivityForResult(i, 1);
		
	}
	
    public void reload() {
	    //startActivity(getIntent());
	    //finish();
    }
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == 0) {
	        if(resultCode == RESULT_OK){
	    		//System.out.println("server position : "+pos);
	        		System.out.println("server position : "+selected);
	        		Intent in =new Intent(this,RemoteActivity.class);
	        		in.putExtra("IP", data.getStringExtra("ip"));
	        		in.putExtra("PORT", data.getIntExtra("port",255));
	        		in.putExtra("PASS", data.getStringExtra("password"));
	        		in.putExtra("FILE_NAME",selected);
	            	startActivity(in);
	        }
	        if (resultCode == RESULT_CANCELED) {}
	    }
	    else if(resultCode==1)reload();
	}
}
