package com.akbros.wifiserial;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;

public class RemoteEditActivity extends Activity {
EditRemoteButton[] buttons;
EditButtonsGridAdapter gridAdapter;
GridView gridView;
int rows=4,columns=3;
String fileName="";
boolean newRemote = true;

String name="New custom remote";
String title = "Serial remote";
String portSettings="";
int textSize=20;

boolean resized = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote_edit);
		
		Bundle bundle = getIntent().getExtras();
		try{
			fileName = bundle.getString("FILE_NAME");
			if(fileName!="")newRemote = false;
		}
		catch(Exception e){}

		
		if(!newRemote)
		{
			prepareRemoteButtons();
			name = fileName;
		}
		else
		{
			populateButtons();
			showSettingsDialog();
		}
		
		gridAdapter = new EditButtonsGridAdapter(this,buttons);
		gridView = ((GridView)this.findViewById(R.id.edit_buttons_grid));
		gridView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if(resized)return;
				System.out.println(gridView.getWidth());
				System.out.println(gridView.getHeight());
				for(int i=0;i<rows;i++)
				{
					for(int j=0;j<columns;j++)
					{
						int pos = i*columns+j;
						buttons[pos].width = gridView.getWidth()/columns;
						buttons[pos].height = gridView.getHeight()/rows;
					}
				}
				gridView.setNumColumns(columns);
				gridView.setAdapter(gridAdapter);
				resized = true;
			}
		});
	}
	
	private void prepareRemoteButtons() {
		
		ArrayList<RemoteButton> buttonList = new ArrayList<RemoteButton>();
		BufferedReader reader=null;
		try {
			InputStream fis = openFileInput(fileName+".remote");
			reader = new BufferedReader(new InputStreamReader(fis));
			
			String line="";
			while((line=reader.readLine())!=null)
			{
				if(line.equalsIgnoreCase("<Settings>"))
				{
					while(!(line=reader.readLine()).equalsIgnoreCase("</Settings>"))
					{
						String l[] = line.split(";");
						if(l[0].equalsIgnoreCase("title"))title = l[1];
						if(l[0].equalsIgnoreCase("portSettings"))portSettings = l[1];
						if(l[0].equalsIgnoreCase("textSize"))textSize = Integer.parseInt(l[1]);
						if(l[0].equalsIgnoreCase("rows"))rows = Integer.parseInt(l[1]);
						if(l[0].equalsIgnoreCase("columns"))columns = Integer.parseInt(l[1]);
					}
					buttons = new EditRemoteButton[rows*columns];
				}
				if(line.equalsIgnoreCase("<Button>"))
				{
					EditRemoteButton b = new EditRemoteButton(this);
					while(!(line=reader.readLine()).equalsIgnoreCase("</Button>"))
					{
						String l[] = line.split(";");
						if(l.length<=1){
							String t[] = new String[2];
							t[0] = l[0];
							t[1] = "";
							l=t;
						}
						if(l[0].equalsIgnoreCase("enabled"))b.enabled = l[1].equals("true");
						else if(l[0].equalsIgnoreCase("label"))b.label = l[1];
						else if(l[0].equalsIgnoreCase("toggle"))b.toggle = l[1].equals("true");
						else if(l[0].equalsIgnoreCase("text"))b.text = l[1].replaceAll("<n>", "\n");
						else if(l[0].equalsIgnoreCase("offText"))b.offText = l[1].replaceAll("<n>", "\n");
						else if(l[0].equalsIgnoreCase("row"))b.row = Integer.parseInt(l[1]);
						else if(l[0].equalsIgnoreCase("column"))b.column = Integer.parseInt(l[1]);
						else if(l[0].equalsIgnoreCase("drawable"))b.drawable = Integer.parseInt(l[1]);
					}
					buttons[b.row*columns+b.column] = b;
				}
			}
		} catch (Exception e) {}
		try {
			reader.close();
		} catch (IOException e) {}
		
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<columns;j++)
			{
				int pos = i*columns+j;
					if(buttons[pos]==null)
					{
						buttons[pos] = new EditRemoteButton(this);
						buttons[pos].enabled=false;
						buttons[pos].row = i;
						buttons[pos].column = j;
					}
			}
		}
	}

	private void populateButtons() {
		buttons = new EditRemoteButton[rows*columns];
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<columns;j++)
			{
				int pos=i*columns+j;
				buttons[pos] = new EditRemoteButton(this);
				buttons[pos].row = i;
				buttons[pos].column = j;
				buttons[pos].enabled = false;
			}
		}
	}
	
Dialog settingsDialog = null;
	private void showSettingsDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Remote Settings");
		LayoutInflater inflater = getLayoutInflater();
		View v = inflater.inflate(R.layout.edit_remote_settings, null);
		builder.setView(v);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				name = ((EditText)settingsDialog.findViewById(R.id.remote_settings_name)).getText().toString();
				title = ((EditText)settingsDialog.findViewById(R.id.remote_settings_title)).getText().toString();
				int nrows = Integer.parseInt(((EditText)settingsDialog.findViewById(R.id.remote_settings_rows)).getText().toString());
				int ncolumns = Integer.parseInt(((EditText)settingsDialog.findViewById(R.id.remote_settings_columns)).getText().toString());
				updateGrid(nrows,ncolumns);
				settingsDialog.dismiss();
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				settingsDialog.dismiss();
			}
		});
		settingsDialog = builder.create();
		((EditText)v.findViewById(R.id.remote_settings_name)).setText(name);
		((EditText)v.findViewById(R.id.remote_settings_title)).setText(title);
		((EditText)v.findViewById(R.id.remote_settings_columns)).setText(columns+"");
		((EditText)v.findViewById(R.id.remote_settings_rows)).setText(rows+"");
		settingsDialog.show();
	}

	protected void updateGrid(int nrows,int ncolumns) {
		if(nrows!=rows || ncolumns!=columns)
		{
			rows = nrows;
			columns=ncolumns;
			populateButtons();
			gridAdapter.addAll(buttons);
			RemoteEditActivity.this.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					resized = false;
					gridAdapter.notifyDataSetChanged();
					gridView.setAdapter(gridAdapter);
				}
				
			});
		}
	}
	public void settingsClick(MenuItem item){
		showSettingsDialog();
	}
	public void saveClick(MenuItem item){
		save();
	}
	private void save() {
		try {
			
			FileOutputStream a = openFileOutput(name+".remote",Context.MODE_PRIVATE);
			
			String s = ("<Settings>\nname;"+name+"\ntitle;"+title+"\nrows;"+rows+"\ncolumns;"+columns+"\n</Settings>\n");
			a.write(s.getBytes());
			for(int i=0;i<rows;i++)
			{
				for(int j=0;j<columns;j++)
				{
					int pos = i*columns+j;
					s = ("<Button>\nlabel;"+buttons[pos].label+"\ntext;"+buttons[pos].text.replaceAll("\n", "<n>")+"\noffText;"+buttons[pos].offText.replaceAll("\n", "<n>")+"\nenabled;"+buttons[pos].enabled+"\ntoggle;"+buttons[pos].toggle+"\nrow;"+buttons[pos].row+"\ncolumn;"+buttons[pos].column+"\ndrawable;"+buttons[pos].drawable+"\n</Button>\n");
					a.write(s.getBytes());
				}
			}
			a.close();
			System.out.println("write Successfull");
		} catch (Exception e) {e.printStackTrace();}
		
	}

	public void finishClick(MenuItem item){
		save();
		finish();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		(new MenuInflater(this)).inflate(R.menu.remote_edit, menu);
		return true;
	}
Dialog buttonSettingsDialog = null;
	public void showButtonSettings(final EditRemoteButton b) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Remote Settings");
		LayoutInflater inflater = getLayoutInflater();
		View v = inflater.inflate(R.layout.remote_button_settings, null);
		builder.setView(v);

		final String[] colors = {"DEFAULT","BLUE","BROWN","GREEN","LIGHT BLUE","PINK","RED","YELLOW"};
		final int[] colorId = {R.drawable.btn_default_normal,R.drawable.btn_default_normal_blue,R.drawable.btn_default_normal_brown,R.drawable.btn_default_normal_green,R.drawable.btn_default_normal_lblue,R.drawable.btn_default_normal_pink,R.drawable.btn_default_normal_red,R.drawable.btn_default_normal_yellow};
		buttonSettingsDialog = builder.create();
		
		((EditText)v.findViewById(R.id.label_edittext)).setText(b.label);
		((CheckBox)v.findViewById(R.id.toggle_enable_checkbox)).setChecked(b.toggle);
		if(!b.toggle)
		{
			((View)v.findViewById(R.id.toggle_settings_box)).setVisibility(View.GONE);
		}
		else
		{
			((View)v.findViewById(R.id.button_settings_box)).setVisibility(View.GONE);
		}
		((EditText)v.findViewById(R.id.toggle_on_data)).setText(b.text);
		((EditText)v.findViewById(R.id.toggle_off_data)).setText(b.offText);
		((EditText)v.findViewById(R.id.press_data)).setText(b.text);
		((EditText)v.findViewById(R.id.release_data)).setText(b.offText);
		for(int i=0;i<colorId.length;i++){if(colorId[i]==b.drawable){((Spinner)v.findViewById(R.id.color_list)).setSelection(i);break;}}
		
		
		((CheckBox)v.findViewById(R.id.toggle_enable_checkbox)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				boolean te = ((CheckBox)buttonSettingsDialog.findViewById(R.id.toggle_enable_checkbox)).isChecked();
				if(te)
				{
					((View)buttonSettingsDialog.findViewById(R.id.toggle_settings_box)).setVisibility(View.VISIBLE);
					((View)buttonSettingsDialog.findViewById(R.id.button_settings_box)).setVisibility(View.GONE);
				}
				else
				{
					((View)buttonSettingsDialog.findViewById(R.id.toggle_settings_box)).setVisibility(View.GONE);
					((View)buttonSettingsDialog.findViewById(R.id.button_settings_box)).setVisibility(View.VISIBLE);
				}
			}
			
		});
		
		((Button)v.findViewById(R.id.add_button_settings_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				b.label = ((EditText)buttonSettingsDialog.findViewById(R.id.label_edittext)).getText().toString();
				b.toggle = ((CheckBox)buttonSettingsDialog.findViewById(R.id.toggle_enable_checkbox)).isChecked();
				if(b.toggle)
				{
					b.text = ((EditText)buttonSettingsDialog.findViewById(R.id.toggle_on_data)).getText().toString();
					b.offText = ((EditText)buttonSettingsDialog.findViewById(R.id.toggle_off_data)).getText().toString();
				}
				else
				{
					b.text = ((EditText)buttonSettingsDialog.findViewById(R.id.press_data)).getText().toString();
					b.offText = ((EditText)buttonSettingsDialog.findViewById(R.id.release_data)).getText().toString();
				}
				b.enabled = true;
				b.drawable = colorId[((Spinner)buttonSettingsDialog.findViewById(R.id.color_list)).getSelectedItemPosition()];
				RemoteEditActivity.this.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						resized = false;
						gridAdapter.notifyDataSetChanged();
						gridView.setAdapter(gridAdapter);
					}
					
				});
				buttonSettingsDialog.dismiss();
			}
		});
		
		((Button)v.findViewById(R.id.cancel_button_settings_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buttonSettingsDialog.dismiss();
			}
		});
		
		((Button)v.findViewById(R.id.remove_button_settings_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				b.label = ((EditText)buttonSettingsDialog.findViewById(R.id.label_edittext)).getText().toString();
				b.toggle = ((CheckBox)buttonSettingsDialog.findViewById(R.id.toggle_enable_checkbox)).isChecked();
				if(b.toggle)
				{
					b.text = ((EditText)buttonSettingsDialog.findViewById(R.id.toggle_on_data)).getText().toString();
					b.offText = ((EditText)buttonSettingsDialog.findViewById(R.id.toggle_off_data)).getText().toString();
				}
				else
				{
					b.text = ((EditText)buttonSettingsDialog.findViewById(R.id.press_data)).getText().toString();
					b.offText = ((EditText)buttonSettingsDialog.findViewById(R.id.release_data)).getText().toString();
				}
				b.enabled = false;

				b.drawable = colorId[((Spinner)buttonSettingsDialog.findViewById(R.id.color_list)).getSelectedItemPosition()];
				RemoteEditActivity.this.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						resized = false;
						gridAdapter.notifyDataSetChanged();
						gridView.setAdapter(gridAdapter);
					}
					
				});
				buttonSettingsDialog.dismiss();
			}
		});

		Spinner colorList = (Spinner)v.findViewById(R.id.color_list);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,colors); //selected item will look like a spinner set from XML
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		colorList.setAdapter(spinnerArrayAdapter);
		
		buttonSettingsDialog.show();
	}

}
