package com.akbros.wifiserial;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void openServersList(View view){
		Intent intent = new Intent(this,ServersActivity.class);
		startActivityForResult(intent, 1);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == 1) {
	        if(resultCode == RESULT_OK){
	        	Intent i = new Intent(this,TerminalActivity.class);
	        	i.putExtra("IP", data.getStringExtra("ip"));
	        	i.putExtra("PORT", data.getIntExtra("port",255));
	        	i.putExtra("PASS", data.getStringExtra("password"));
	        	startActivity(i);
	        }
	        if (resultCode == RESULT_CANCELED) {}
	    }
	    if(requestCode==2){
	    	if(resultCode == RESULT_OK){
	    		Intent i = new Intent(this,SystemRemote.class);
	        	i.putExtra("IP", data.getStringExtra("ip"));
	        	i.putExtra("PORT", data.getIntExtra("port",255));
	        	i.putExtra("PASS", data.getStringExtra("password"));
	        	i.putExtra("NAME", "WMPlayer");
	    		startActivity(i);
	    	}
	    }
	}
	
	Dialog dialog,passDialog,errorDialog;
	public void manualConnect(View v){
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.ip_input_dialog);
		dialog.setTitle("Manual Connect");
		Button okB = (Button) dialog.findViewById(R.id.connect_dialog);
		okB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText e = ((EditText)dialog.findViewById(R.id.ip_editText));
				final Server s = new Server(e.getText().toString(),"Manual server",255);
				
				dialog.dismiss();
				if(s.valid)
				{
					if(s.secure)
					{
						passDialog = new Dialog(MainActivity.this);
						passDialog.setContentView(R.layout.password_input_dialog);
						passDialog.setTitle("Enter Password");
						passDialog.show();
						Button okB2 = (Button) passDialog.findViewById(R.id.ok_dialog);
						okB2.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								EditText e = (EditText)passDialog.findViewById(R.id.password_editText);
								passDialog.dismiss();
									Intent i = new Intent(MainActivity.this,TerminalActivity.class);
									i.putExtra("IP", s.getIp());
									i.putExtra("PORT", 255);
									i.putExtra("PASS", e.getText().toString());
									MainActivity.this.startActivity(i);
							}
						
						});
						Button cancelB2 = (Button) passDialog.findViewById(R.id.cancel_dialog);
						cancelB2.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								passDialog.dismiss();
							}
							
						});
					}
					else
					{
						Intent i = new Intent(MainActivity.this,TerminalActivity.class);
						i.putExtra("IP", s.getIp());
						i.putExtra("PORT", 255);
						MainActivity.this.startActivity(i);
					}
				}
				else
				{
					createPopup("Connection Failed.");
				}
				
				//Toast.makeText(ServersActivity.this, password, Toast.LENGTH_SHORT).show();
			}
		});
		
		Button cancelB = (Button) dialog.findViewById(R.id.cancel_connect_dialog);
		cancelB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
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
	
	public void startQuickButtons(View v){
		Intent i = new Intent(this,QuickButtonsActivity.class);
		this.startActivity(i);
	}
	
	public void exit(View v){
		finish();
	}
	
	public void startRemotes(View v){
		Intent intent = new Intent(this,ServersActivity.class);
		startActivityForResult(intent, 2);
	}
}
