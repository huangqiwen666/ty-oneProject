package com.dascom.mongodb.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.dascom.entity.PrinterEntity;
import com.dascom.entity.PrinterInfo;
import com.dascom.entity.PrinterStatus;
import com.dascom.mongodb.PrinterDao;
@Primary
@Repository
public class PrinterDaoImpl implements PrinterDao{
		
	
	@Autowired
	private MongoTemplate deviceMongoTemplate;
	
	public void update(String number, PrinterInfo info, Date date) {
		Criteria criteria = Criteria.where("number").is(number);
		Query query = new Query(criteria);
		Update update = Update.update("login_date", date).set("info", info);
		deviceMongoTemplate.updateMulti(query, update, PrinterEntity.class);
	}

	public void insert(PrinterEntity printer) {
		deviceMongoTemplate.insert(printer);
	}

	public PrinterEntity findByNumber(String number) {
		Criteria criteria = Criteria.where("number").is(number);
		Query query = new Query(criteria);
		return deviceMongoTemplate.findOne(query, PrinterEntity.class);
	}

	public void updateAlive(String number, boolean alive) {
		Criteria criteria = Criteria.where("number").is(number);
		Query query = new Query(criteria);
		Update update = Update.update("alive", alive);
		deviceMongoTemplate.updateMulti(query, update, PrinterEntity.class);
	}

	public List<PrinterEntity> findAllPrinterEntity() {
		return deviceMongoTemplate.findAll(PrinterEntity.class);
	}

	public List<PrinterEntity> findAllDevice(String owner) {
		return null;
	}

	public List<PrinterEntity> findAllByAlive(Boolean alive) {
		Criteria criteria = Criteria.where("alive").is(alive);
		Query query = new Query(criteria);
		return deviceMongoTemplate.find(query, PrinterEntity.class);
	}

	@Override
	public List<PrinterEntity> findAllCondition(String number) {
		
		Query query = new Query();
		if (number!=null) {
			query.addCriteria(Criteria.where("number").regex(".*?"+number+".*?"));
		}
//		if (status!=null) {
//			query.addCriteria(Criteria.where("status.main").is(status));
//		}
		return deviceMongoTemplate.find(query, PrinterEntity.class);
	}

//	@Override
//	public PrinterEntity updateStatus(String number, PrinterStatus printerStatus) {
//		Criteria criteria = Criteria.where("number").is(number);
//		Query query = new Query(criteria);
//		Update update = Update.update("status",printerStatus);
//		FindAndModifyOptions options = new FindAndModifyOptions();
//		options.returnNew(false);// 获得更改前(false)或者更改后(true)的数据，
//		return deviceMongoTemplate.findAndModify(query, update, options, PrinterEntity.class);// 修改并返回结果(返回修改前后与options有关);
//	}

	@Override
	public void updateAliveAll() {
		List<PrinterEntity> findAll = deviceMongoTemplate.findAll(PrinterEntity.class);
		for (PrinterEntity printerEntity : findAll) {
			PrinterStatus status = printerEntity.getStatus();
			if (status!=null&&!"dead".equals(status.getMain())) {
				status.setMain("dead");
				Criteria criteria = Criteria.where("number").is(printerEntity.getNumber());
				Query query = new Query(criteria);
				Update update = Update.update("status",status);
				deviceMongoTemplate.findAndModify(query, update,PrinterEntity.class);
			}
		}
		
		
	}


}
