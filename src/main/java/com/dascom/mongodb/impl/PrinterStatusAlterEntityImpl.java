package com.dascom.mongodb.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.dascom.entity.PrinterStatusAlterEntity;
import com.dascom.mongodb.PrinterStatusAlterEntityDao;
@Primary
@Repository
public class PrinterStatusAlterEntityImpl implements PrinterStatusAlterEntityDao {

	@Autowired
	private MongoTemplate deviceMongoTemplate;
	
	
	public void updeta(PrinterStatusAlterEntity printerStatusAlterEntity) {
		Criteria criteria = Criteria.where("id").is(printerStatusAlterEntity.getId());
		Query query = new Query(criteria);
		Update update = Update.update("events", printerStatusAlterEntity.getEvents());
		deviceMongoTemplate.updateMulti(query, update, PrinterStatusAlterEntity.class);
	}

	public PrinterStatusAlterEntity findOne(String id) {
		Criteria criteria = Criteria.where("id").is(id);
		Query query = new Query(criteria);
		return deviceMongoTemplate.findOne(query, PrinterStatusAlterEntity.class);
	}

	public void insert(PrinterStatusAlterEntity printerStatusAlterEntity) {
		deviceMongoTemplate.insert(printerStatusAlterEntity);
	}


}
