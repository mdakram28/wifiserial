package com.akbros.wifiserial;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MessageListAdapter extends BaseAdapter {
ArrayList<Message> messages;
Context context;
ListView view;
AutoScroller scroller = new AutoScroller();
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return messages.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return messages.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View view, ViewGroup arg2) {
		if(view==null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.message_item, null);
		}
		
		Message message = (Message)getItem(arg0);
		
		TextView m = (TextView) view.findViewById(R.id.message);
		m.setText(message.getText());
		//System.out.println(message.getText());
		TextView t = (TextView) view.findViewById(R.id.time);
		t.setText(message.getDateString());
		ImageView i = (ImageView)view.findViewById(R.id.description_image);
		switch(message.getType())
		{
		case 1: i.setImageResource(R.drawable.message);
		m.setTextColor(Color.BLACK);
		break;
		case 2: i.setImageResource(R.drawable.notify);
		m.setTextColor(Color.BLUE);
		break;
		case 3: i.setImageResource(R.drawable.error);
		m.setTextColor(Color.RED);
		break;
		}
		
		return view;
	}

	public MessageListAdapter(Context context, ArrayList<Message> messages,ListView view){
		this.messages = messages;
		this.context = context;
		this.view = view;
		//(new Thread(scroller)).start();
	}
	
	public void addMessage(String message){
		if(getCount()>0)if(((Message)getItem(getCount()-1)).getType()==1)
		{
			((Message)getItem(getCount()-1)).setText(((Message)getItem(getCount()-1)).getText()+message);
			
			this.notifyDataSetChanged();
			scroller.scroll();
			return;
		}
		String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		Message temp = new Message(message,1,date);
		messages.add(temp);
		this.notifyDataSetChanged();
		scroller.scroll();
	}
	
	public void addNotificationMessage(String message){
		if(getCount()>0)if(((Message)getItem(getCount()-1)).getType()==2)
		{
			((Message)getItem(getCount()-1)).setText(((Message)getItem(getCount()-1)).getText()+"\n"+message);
			this.notifyDataSetChanged();
			scroller.scroll();
			return;
		}
		String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		Message temp = new Message(message,2,date);
		messages.add(temp);
		this.notifyDataSetChanged();
		scroller.scroll();
	}

	public void addErrorMessage(String message){
		if(getCount()>0)if(((Message)getItem(getCount()-1)).getType()==3)
		{
			((Message)getItem(getCount()-1)).setText(((Message)getItem(getCount()-1)).getText()+"\n"+message);
			this.notifyDataSetChanged();
			scroller.scroll();
			return;
		}
		String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		Message temp = new Message(message,3,date);
		messages.add(temp);
		this.notifyDataSetChanged();
		scroller.scroll();
	}
	
	class AutoScroller implements Runnable{

		boolean scrolling = true;
		@Override
		public void run(){
			while(true)
			while(scrolling){
				try{
					view.smoothScrollToPosition(10);
					scrolling = false;
				}
				catch(Exception e){}
			}
		}
		public void scroll(){
			scrolling = true;
		}
	}
}
