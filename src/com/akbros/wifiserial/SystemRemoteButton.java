package com.akbros.wifiserial;

import java.io.IOException;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class SystemRemoteButton implements OnClickListener {
	String icon="btn.jpg";
	String color="#FF530D";
	String text;
	boolean enabled = false;
	LinearLayout.LayoutParams params;
	ImageButton b;
	String pressed="#225378";
	String focused = "#1695A3";
	ClientThread sender;
	
	public ImageButton getButton(Context c) {
		b = new ImageButton(c);
		params =	new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,1f);
		params.topMargin=5;
		params.bottomMargin=5;
		params.leftMargin=5;
		params.rightMargin=5;
		b.setLayoutParams(params);
		try {
		
			b.setImageDrawable(Drawable.createFromStream(c.getAssets().open(icon), null));
		StateListDrawable states = new StateListDrawable();
		states.addState(new int[]{android.R.attr.state_pressed},new ColorDrawable(Color.parseColor(pressed)));
		states.addState(new int[]{android.R.attr.state_focused},new ColorDrawable(Color.parseColor(focused)));
		states.addState(new int[]{ },new ColorDrawable(Color.parseColor(color)));
		
		b.setBackgroundDrawable(states);
		b.setOnClickListener(this);
		} catch (IOException e) {e.printStackTrace();}
		
		return b;
	}
	public void setWeight(float w) {
		params.weight = w;
		b.setLayoutParams(params);
	}
	@Override
	public void onClick(View arg0) {
		sender.SendRemoteData(text);
	}
}
