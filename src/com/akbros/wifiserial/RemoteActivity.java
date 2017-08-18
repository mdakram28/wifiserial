package com.akbros.wifiserial;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

public class RemoteActivity extends Activity implements ServerUser {
ButtonsGridAdapter gridAdapter = null;
RemoteButton[] buttons = null;
GridView gridView = null;
String name="";
String title="";
String portSettings="";
ClientThread sender = null;
int textSize=20,rows=0,columns=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote);
		// Show the Up button in the action bar.
		setupActionBar();
		Bundle bundle = getIntent().getExtras();
		name = bundle.getString("FILE_NAME");
		System.out.println(name);

		sender = new ClientThread(bundle.getString("IP"),bundle.getInt("PORT"),this);
		String password = bundle.getString("PASS");
		try {
			sender.connect(password);
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getMessage().indexOf("Fatal Error")!=-1)
			{this.finish();}
		}
		
		prepareRemoteButtons();
		this.setTitle(title);
		gridAdapter = new ButtonsGridAdapter(this,buttons);
		gridView = ((GridView)this.findViewById(R.id.buttons_grid));
		gridView.setNumColumns(columns);
		
		gridView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			boolean resized = false;
			@Override
			public void onGlobalLayout() {
				if(resized)return;
				System.out.println(gridView.getWidth());
				System.out.println(gridView.getHeight());
				for(int i=0;i<rows;i++)
				{
					for(int j=0;j<columns;j++)
					{
						int pos = i*columns+j;
						buttons[pos].width = gridView.getWidth()/columns;
						buttons[pos].height = gridView.getHeight()/rows;
					}
				}
				gridView.setAdapter(gridAdapter);
				resized = true;
			}
		});
		connectPortDialog();
	}

	private void prepareRemoteButtons() {
		
		ArrayList<RemoteButton> buttonList = new ArrayList<RemoteButton>();
		BufferedReader reader=null;
		try {
			FileInputStream fis = openFileInput(name+".remote");
			reader = new BufferedReader(new InputStreamReader(fis));
			
			String line="";
			while((line=reader.readLine())!=null)
			{
				if(line.equalsIgnoreCase("<Settings>"))
				{
					while(!(line=reader.readLine()).equalsIgnoreCase("</Settings>"))
					{
						String l[] = line.split(";");
						if(l[0].equalsIgnoreCase("title"))
						{
							title = l[1];
						}
						if(l[0].equalsIgnoreCase("portSettings"))
						{
							portSettings = l[1];
						}
						if(l[0].equalsIgnoreCase("textSize"))
						{
							textSize = Integer.parseInt(l[1]);
						}
						if(l[0].equalsIgnoreCase("rows"))
						{
							rows = Integer.parseInt(l[1]);
						}
						if(l[0].equalsIgnoreCase("columns"))
						{
							columns = Integer.parseInt(l[1]);
						}
					}
					buttons = new RemoteButton[rows*columns];
				}
				if(line.equalsIgnoreCase("<Button>"))
				{
					RemoteButton b = new RemoteButton();
					while(!(line=reader.readLine()).equalsIgnoreCase("</Button>"))
					{
						String l[] = line.split(";");
						if(l.length<=1){
							String t[] = new String[2];
							t[0] = l[0];
							t[1] = "";
							l=t;
						}
						if(l[0].equalsIgnoreCase("enabled"))b.enabled = l[1].equals("true");
						else if(l[0].equalsIgnoreCase("label"))b.label = l[1];
						else if(l[0].equalsIgnoreCase("toggle"))b.toggle = l[1].equals("true");
						else if(l[0].equalsIgnoreCase("text"))b.text = l[1].replaceAll("<n>", "\n");
						else if(l[0].equalsIgnoreCase("offText"))b.offText = l[1].replaceAll("<n>", "\n");
						else if(l[0].equalsIgnoreCase("row"))b.row = Integer.parseInt(l[1]);
						else if(l[0].equalsIgnoreCase("column"))b.column = Integer.parseInt(l[1]);
						else if(l[0].equalsIgnoreCase("drawable"))b.drawable = Integer.parseInt(l[1]);
					}
					buttons[b.row*columns+b.column] = b;
					b.sender = sender;
					System.out.println(b.enabled);
				}
			}
		} catch (Exception e) {e.printStackTrace();}
			try {
				reader.close();
			} catch (IOException e) {}
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<columns;j++)
			{
				int pos = i*columns+j;
					if(buttons[pos]==null)
					{
						buttons[pos] = new RemoteButton();
						buttons[pos].enabled=false;
					}
			}
		}
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
		(new MenuInflater(this)).inflate(R.menu.remote, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void addMessage(String text, int type) {
		if(type==1)
		{
			notify(text);
		}
	}
	
	Dialog PortListDialog = null;
	public void connectPortDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Select Serial Port")
	           .setItems(sender.PortList, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int pos) {
	            	   if(sender.connectPort(sender.PortList[pos]))
	            	   {
	            		   PortListDialog.dismiss();
	            	   }
	            	   else
	            	   {
	            		   RemoteActivity.this.notify("Unable to open Port");
	            	   }
	           }
	    });
	    builder.setCancelable(false);
	    PortListDialog = builder.create();
	    PortListDialog.show();
	}

	@Override
	public void exit(String string) {}
	
	public void notify(String text){
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	
	@Override
	public void onStop(){
		super.onStop();
		sender.exit();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		sender.exit();
	}
	
	public boolean editRemote(MenuItem item)
	{
		sender.disconnect();
		Intent i =new Intent(this,RemoteEditActivity.class);
		i.putExtra("FILE_NAME", name);
		startActivity(i);
		this.finish();
		return true;
	}
	public boolean openPortSettings(MenuItem item){
		settingsDialog(null);
		return true;
	}
	
	View settingsView = null;
	Dialog settingsDialog = null;
	public void settingsDialog(View v){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		
		builder.setTitle("Serial Port Settings");
		settingsView = inflater.inflate(R.layout.settings_dialog,null);
		builder.setView(settingsView);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Resources res = getResources();
				
				String baud = (res.getStringArray(R.array.baudrate_array))[((Spinner)settingsDialog.findViewById(R.id.baudrate_list)).getSelectedItemPosition()];
				String parity = (res.getStringArray(R.array.parity_array))[((Spinner)settingsDialog.findViewById(R.id.parity_list)).getSelectedItemPosition()];
				String stopbits = (res.getStringArray(R.array.stopbits_array))[((Spinner)settingsDialog.findViewById(R.id.stopbits_list)).getSelectedItemPosition()];
				String handshake = (res.getStringArray(R.array.handshake_array))[((Spinner)settingsDialog.findViewById(R.id.handshake_list)).getSelectedItemPosition()];
				sender.settings(baud,parity,stopbits,handshake);
				settingsDialog.dismiss();
			}
			
		});
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				settingsDialog.dismiss();
			}
			
		});
		
		settingsDialog = builder.create();
		settingsDialog.show();
		populateSpinner(R.id.baudrate_list,R.array.baudrate_array);
		populateSpinner(R.id.parity_list,R.array.parity_array);
		populateSpinner(R.id.stopbits_list,R.array.stopbits_array);
		populateSpinner(R.id.handshake_list,R.array.handshake_array);
	}

	
	public void populateSpinner(int spinnerId,int arrayId){
		Spinner spinner = (Spinner) settingsDialog.findViewById(spinnerId);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(settingsDialog.getContext(),arrayId, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}
}
