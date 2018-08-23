package com.dascom.common;

public class RequestEntity {
	
	private byte[] data;
	
//	private String id;
	
	private  byte[] message;
	
	private boolean mark=false;
	
	private boolean endMark=false;

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

//	public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}


	public boolean isMark() {
		return mark;
	}


	public byte[] getMessage() {
		return message;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}

	public void setMark(boolean mark) {
		this.mark = mark;
	}

	public boolean isEndMark() {
		return endMark;
	}

	public void setEndMark(boolean endMark) {
		this.endMark = endMark;
	}
	
}
