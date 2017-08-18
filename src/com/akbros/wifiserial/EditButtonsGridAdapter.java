package com.akbros.wifiserial;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class EditButtonsGridAdapter extends BaseAdapter {

	ArrayList<EditRemoteButton> buttonGrid = new ArrayList<EditRemoteButton>();
	Context context;
	
	public EditButtonsGridAdapter(Context context,EditRemoteButton[] buttonGrid){
		this.context = context;
		this.buttonGrid.clear();
		for(int i=0;i<buttonGrid.length;i++)
		{
			this.buttonGrid.add(buttonGrid[i]);
		}
	}
	
	@Override
	public int getCount() {
		return buttonGrid.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return buttonGrid.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public void addAll(EditRemoteButton[] buttonGrid){
		this.buttonGrid.clear();
		for(int i=0;i<buttonGrid.length;i++)
		{
			this.buttonGrid.add(buttonGrid[i]);
		}
	}
	@Override
	public View getView(int pos, View view, ViewGroup parentView) {
		Button ret;
		
		if (view == null) {  // if it's not recycled, initialize some attributes
            ret = buttonGrid.get(pos).getButton(context);
            System.out.println(pos);
        } else {
            ret = (Button) view;
        }
        
		return ret;
	}

}
