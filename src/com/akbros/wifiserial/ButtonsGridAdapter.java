package com.akbros.wifiserial;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class ButtonsGridAdapter extends BaseAdapter {

	RemoteButton[] buttonGrid = null;
	Context context;
	
	public ButtonsGridAdapter(Context context,RemoteButton[] buttonGrid){
		this.context = context;
		this.buttonGrid = buttonGrid;
	}
	
	@Override
	public int getCount() {
		return buttonGrid.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return buttonGrid[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int pos, View view, ViewGroup parentView) {
		Button ret;
		
		if (view == null) {  // if it's not recycled, initialize some attributes
            ret = buttonGrid[pos].getButton(context);
            System.out.println(pos);
        } else {
            ret = (Button) view;
        }
        
		return ret;
	}

}
