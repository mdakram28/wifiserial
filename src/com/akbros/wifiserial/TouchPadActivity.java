package com.akbros.wifiserial;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RemoteViews.ActionException;

public class TouchPadActivity extends Activity implements ServerUser , OnTouchListener  {
	ClientThread sender;
	int startx,starty;
	View pad;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_touch_pad);
		Bundle bundle = getIntent().getExtras();
		
		sender = new ClientThread(bundle.getString("IP"),bundle.getInt("PORT", 255),this);
		
		try {
			sender.connect(bundle.getString("PASS"));
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getMessage().indexOf("Fatal Error")!=-1)
			{this.finish();}
		}
		sender.startRemote("TouchPad");
		(pad = (View)this.findViewById(R.id.TouchPad)).setOnTouchListener(this);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.touch_pad, menu);
		return true;
	}
	public void LeftButtonClicked(View v){
		sender.SendRemoteData("btn:l");
	}
	public void RightButtonClicked(View v){
		sender.SendRemoteData("btn:r");
	}
	@Override
	public void addMessage(String text, int type) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void exit(String string) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		int e = event.getAction();
		System.out.println(e);
		if(e==MotionEvent.ACTION_DOWN)
		{
			startx = (int) event.getX();
			starty = (int) event.getY();
		}
		else if(e==MotionEvent.ACTION_MOVE)
		{
			int x = (int) event.getX();
			int y = (int) event.getY();
			sender.SendRemoteData("tp:"+(x-startx)+":"+(y-starty));
			startx = x;
			starty = y;
		}
		return true;
	}
}
