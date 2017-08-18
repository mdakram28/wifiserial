package com.akbros.wifiserial;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

public class EditRemoteButton implements OnClickListener {

	boolean enabled = false;
	String label="";
	String text="";
	String offText="";
	boolean toggle = false;
	Button button = null;
	int row=0,column=0;
	int width=50;
	int height=50;
	RemoteEditActivity holder;
	int drawable=R.drawable.btn_default_normal;
	
	public EditRemoteButton(RemoteEditActivity holder)
	{
		this.holder = holder;
	}
	public Button getButton(Context context) {
			button = new Button(context);
			button.setBackgroundDrawable(context.getResources().getDrawable(drawable));
		button.setText(label);
		if(!enabled)
		{
			//button.setBackgroundResource(R.drawable.add);
			button.setTextSize(height*0.25f);
			button.setText("+");
			button.setGravity(Gravity.CENTER);
		}
			button.setWidth(width);
			button.setHeight(height);
			button.setOnClickListener(this);
		return button;
	}
	@Override
	public void onClick(View arg0) {
		holder.showButtonSettings(this);
	}
}
