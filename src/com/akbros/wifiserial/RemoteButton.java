package com.akbros.wifiserial;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

public class RemoteButton implements View.OnTouchListener {

	boolean enabled = false;
	String label="";
	String text="";
	String offText="";
	boolean toggle = false;
	Button button = null;
	int row=0,column=0;
	int width=0;
	int height=0;
	ClientThread sender = null;
	int drawable=R.drawable.btn_default_normal_blue;
	String background="";
	Context context = null;
	boolean toggled = false;
	
	public Button getButton(Context context) {
		this.context = context;
		if(button!=null){return button;}
		
			button = new Button(context);
			button.setBackgroundDrawable(context.getResources().getDrawable(drawable));
			
		button.setText(label);
		if(!enabled)button.setVisibility(View.INVISIBLE);
		
		button.setWidth(width);
		button.setHeight(height);
		
		button.setOnTouchListener(this);
		return button;
	}
	@Override
	public boolean onTouch(View v,MotionEvent m) {
		int e = m.getAction();
        if (e==MotionEvent.ACTION_DOWN)
        {
        	if(toggle)
        	{
        		if(toggled)
        		{
        			sender.send(offText.replaceAll("\n","<n>"));
        			button.setBackgroundDrawable(context.getResources().getDrawable(drawable));
        		}
        		else
        		{
        			sender.send(text.replaceAll("\n","<n>"));
        			button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_default_selected));
        		}
        		toggled = !toggled;
        	}
        	else
        	{
        		sender.send(text.replaceAll("\n","<n>"));

    			button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_default_pressed));
        	}
        }
        else if(e==MotionEvent.ACTION_UP)
        {
        	if(!toggle)
        	{
        		sender.send(offText.replaceAll("\n","<n>"));

    			button.setBackgroundDrawable(context.getResources().getDrawable(drawable));
        	}
        	
        }
        return true;
	}
}
