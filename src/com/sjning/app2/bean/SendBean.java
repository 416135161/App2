package com.sjning.app2.bean;

import java.util.ArrayList;

public class SendBean {

	private String phone;
	private String recivephone;
	private ArrayList<ContentItem> contents;
	private String comdate;

	public SendBean() {
		// TODO Auto-generated constructor stub
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRecivephone() {
		return recivephone;
	}

	public void setRecivephone(String recivephone) {
		this.recivephone = recivephone;
	}

	public ArrayList<ContentItem> getContents() {
		return contents;
	}

	public void setContents(ArrayList<ContentItem> contents) {
		this.contents = contents;
	}

	public String getComdate() {
		return comdate;
	}

	public void setComdate(String comdate) {
		this.comdate = comdate;
	}

	public static class ContentItem {
		private String date;
		private String tag;

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

	}

}
