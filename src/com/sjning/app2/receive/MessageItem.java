package com.sjning.app2.receive;

import java.util.List;

public class MessageItem implements java.io.Serializable

{
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private int type;
	
	private int protocol;
	
	private String phone;
	
	private String body;
	
	private String date;
	
	private String items;
	
	private List<String> childItems;
	
	public MessageItem()
	
	{
	}
	
	public int getId()
	
	{
		
		return id;
		
	}
	
	public void setId(int id)
	
	{
		
		this.id = id;
		
	}
	
	public int getType()
	
	{
		
		return type;
		
	}
	
	public void setType(int type)
	
	{
		
		this.type = type;
		
	}
	
	public int getProtocol()
	
	{
		
		return protocol;
		
	}
	
	public void setProtocol(int protocol)
	
	{
		
		this.protocol = protocol;
		
	}
	
	public String getPhone()
	
	{
		
		return phone;
		
	}
	
	public void setPhone(String phone)
	
	{
		
		this.phone = phone;
		
	}
	
	public String getBody()
	
	{
		
		return body;
		
	}
	
	public void setBody(String body)
	
	{
		
		this.body = body;
		
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}
	
	

	public List<String> getChildItems() {
		return childItems;
	}

	public void setChildItems(List<String> childItems) {
		this.childItems = childItems;
	}

	public String toString()
	
	{
		
		return
		
		"id = " + id + ";" +
		
		"type = " + type + ";" +
		
		"protocol = " + protocol + ";" +
		
		"phone = " + phone + ";" + "date = " + date + ";" + "body = " + body;
		
	}
	
}
