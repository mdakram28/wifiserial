package com.akbros.wifiserial;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ServersListAdapter extends BaseAdapter {
ArrayList<Server> servers;
ListView view;
Context context;
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return servers.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return servers.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int p, View v, ViewGroup arg2) {
		if(view==null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = layoutInflater.inflate(R.layout.server_item, null);
		}
		
		Server server = (Server)getItem(p);
		
		TextView ip = (TextView) v.findViewById(R.id.server_ip_textview);
		ip.setText(server.getIp());
		TextView name = (TextView) v.findViewById(R.id.server_name_textview);
		name.setText(server.getName());
		ImageView i = (ImageView)v.findViewById(R.id.secure_image);
		i.setImageResource(server.secure?R.drawable.locked:R.drawable.unlocked);
		return v;
	}
	
	public ServersListAdapter(Context context,ArrayList<Server> servers,ListView view){
		this.context = context;
		this.servers = servers;
		this.view = view;
	}
	public void addServer(Server server){
		servers.add(server);
	}
	
	public void clear(){
		servers.clear();
		this.notifyDataSetChanged();
	}
}
