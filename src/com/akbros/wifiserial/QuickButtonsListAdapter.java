package com.akbros.wifiserial;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class QuickButtonsListAdapter extends BaseAdapter {
	Context context;
	ListView view;
	String[] names;

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return names.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return names[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
Dialog deleteDialog = null;
	@Override
	public View getView(int arg0, View view, ViewGroup arg2) {
		if (view == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.quick_button_item, null);
		}

		String name = (String) getItem(arg0);

		TextView m = (TextView) view.findViewById(R.id.quick_button_label);
		m.setText(name.substring(0, name.lastIndexOf(".")));
		
		((Button)view.findViewById(R.id.delete_remote_button)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View btn) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
		        builder.setMessage("Are you sure you want to delete this remote?");
		        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String fName = ((TextView)(((View)btn.getParent()).findViewById(R.id.quick_button_label))).getText().toString()+".remote";
						context.deleteFile(fName);
						deleteDialog.dismiss();
						((QuickButtonsActivity)context).reload();
					}
		        });
		        
		        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteDialog.dismiss();
					}
		        	
		        });
		        deleteDialog=builder.create();
		        deleteDialog.show();
			}
			
		});
		
		((Button)view.findViewById(R.id.run_remote_button)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View btn) {
				String fName = ((TextView)(((View)btn.getParent()).findViewById(R.id.quick_button_label))).getText().toString();

				Intent i = new Intent(context,ServersActivity.class);
				((QuickButtonsActivity)context).selected = fName;
				((Activity)context).startActivityForResult(i,0);
			}
			
		});
		
		((ImageButton)view.findViewById(R.id.edit_remote_button)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View btn) {
				String fName = ((TextView)(((View)btn.getParent()).findViewById(R.id.quick_button_label))).getText().toString();

				Intent i = new Intent(context,RemoteEditActivity.class);
				i.putExtra("FILE_NAME", fName);
				context.startActivity(i);
			}
			
		});
		
		return view;
	}

	public QuickButtonsListAdapter(Context context, String[] names,
			ListView view) {
		this.names = names;
		this.context = context;
		this.view = view;
	}

	
}
