package com.dascom.entity;

import java.util.LinkedList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "collection_status_log")
public class PrinterStatusAlterEntity {

	
	@Id
	private String _id;//number +data
	
	@Indexed(unique = true)
	private String id;//number +data
	
	private String number;  //number
	
	private LinkedList<PrinterStatus> events;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public LinkedList<PrinterStatus> getEvents() {
		return events;
	}

	public void setEvents(LinkedList<PrinterStatus> events) {
		this.events = events;
	}


	
	
	
}
