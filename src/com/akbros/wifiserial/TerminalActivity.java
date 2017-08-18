package com.akbros.wifiserial;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import android.widget.TextView.OnEditorActionListener;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;

public class TerminalActivity extends Activity implements ServerUser {

	ArrayList<Message> messageList = new ArrayList<Message>();
	ListView messageListView ;
	MessageListAdapter messages = new MessageListAdapter(this,messageList,messageListView);
	EditText input = null;
	ClientThread sender = null;
	String password="";
	Spinner portList = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terminal);
		// Show the Up button in the action bar.
		setupActionBar();
		messageListView = (ListView)this.findViewById(R.id.messageListView);
		messageListView.setAdapter(messages);
		input = (EditText) findViewById(R.id.message_input);
		
		
		input.setOnEditorActionListener(new OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        boolean handled = false;
		        if (actionId == EditorInfo.IME_ACTION_SEND) {
		        	sendMessage(null);
		            handled = true;
		        }
		        return handled;
		    }
		});
		Bundle bundle = getIntent().getExtras();
		sender = new ClientThread(bundle.getString("IP"),bundle.getInt("PORT"),this);
		password = bundle.getString("PASS");
		try {
			sender.connect(password);
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getMessage().indexOf("Fatal Error")!=-1)
			{
				createPopup(e.getMessage());
				this.finish();
			}
			else
			{
				addMessage(e.getMessage(), 3);
			}
		}
		
		portList = (Spinner)this.findViewById(R.id.ports_listview);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sender.PortList); //selected item will look like a spinner set from XML
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		portList.setAdapter(spinnerArrayAdapter);
	}
	Dialog errorDialog;
	
	public void exit(final String text){
		TerminalActivity.this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				createPopup(text);
				TerminalActivity.this.finish();
			}
			
		});
	}
	private void createPopup(String text) {
		errorDialog = new Dialog(this);
		errorDialog.setContentView(R.layout.server_inactive_dialog);
		errorDialog.setTitle("Error");
		TextView t = (TextView)errorDialog.findViewById(R.id.error_textView);
		t.setText(text);
		Button okB = (Button) errorDialog.findViewById(R.id.ok_server);
		okB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				errorDialog.dismiss();
			}
		});
		
		errorDialog.show();
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.terminal, menu);
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
	public void sendMessage(View view){
    	if(sender!=null)
    	{
    		sender.send(input.getText().toString()+"<n>");
    	}
    	else
    	{
    		messages.addErrorMessage("ERROR : Server Not initialized");
    	}
    	input.setText("");
    	input.requestFocus();
	}
	
	public void addMessage(final String message,final int type)
	{
		TerminalActivity.this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				switch(type)
				{
				case 1 : 
					messages.addMessage(message);
					break;
				case 2 :
					messages.addNotificationMessage(message);
					break;
				case 3 :
					messages.addErrorMessage(message);
					break;
				default :
					messages.addMessage(message);
				}
			}
		});
	}
	
	public void connect(View v)
	{
		if(((Button)this.findViewById(R.id.connect_button)).getText().toString().equalsIgnoreCase("close"))
		{
			((Button)this.findViewById(R.id.connect_button)).setText("Open");
			portList.setEnabled(true);
			sender.disconnect();
		}
		else
		{
			String choice = sender.PortList[portList.getSelectedItemPosition()];
			if(sender.connectPort(choice))
			{
				((Button)this.findViewById(R.id.connect_button)).setText("Close");
				portList.setEnabled(false);
			}
		}
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
