package com.akbros.wifiserial;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class WMPlayerActivity extends Activity implements ServerUser{
ClientThread sender = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wmplayer);
		
		Bundle bundle = getIntent().getExtras();
		
		sender = new ClientThread(bundle.getString("IP"),bundle.getInt("PORT", 255),this);
		
		try {
			sender.connect(bundle.getString("PASS"));
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getMessage().indexOf("Fatal Error")!=-1)
			{this.finish();}
		}
		sender.startRemote("WMControl");
		sender.SendRemoteData("init");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.wmplayer, menu);
		return true;
	}

	@Override
	public void addMessage(String text, int type) {}

	@Override
	public void exit(String string) {}

	public void playpauseClick(View v){
		sender.SendRemoteData("playtoggle");
	}
	public void stopClick(View v){
		sender.SendRemoteData("stop");
	}
	public void prevClick(View v){
		sender.SendRemoteData("prev");
	}
	public void nextClick(View v){
		sender.SendRemoteData("next");
	}
	public void volupClick(View v){
		sender.SendRemoteData("volup");
	}
	public void voldownClick(View v){
		sender.SendRemoteData("voldown");
	}
}
