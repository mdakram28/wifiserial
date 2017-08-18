package com.akbros.wifiserial;


public class Message {
	private String text="";
	private int type=1;
	private String date;
	public Message(String text,int type,String date){
		this.text = text;
		this.type = type;
		this.date = date;
	}
	
	public String getText() {
		return text;
	}

	public int getType() {
		return type;
	}

	public Message(String text){
		this.text = text;
	}

	public String getDate() {
		return date;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDateString() {
		return date;
	}
	
}
