package com.dascom.mongodb.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.dascom.entity.PrintStatistics;
import com.dascom.mongodb.PrintStatisticsDao;
@Repository
public class PrintStatisticsDaoImpl implements PrintStatisticsDao {

	@Qualifier(value="printMongoTemplate")
	@Autowired
	private MongoTemplate printMongoTemplate;
	
	
	public PrintStatistics findOne(String id) {
		Query query = new Query(Criteria.where("id").is(id));
		PrintStatistics pss = printMongoTemplate.findOne(query, PrintStatistics.class);
		return pss;
	}

	public void insert(PrintStatistics devicePrintStatistics) {
		printMongoTemplate.insert(devicePrintStatistics);
	}

//	public void addSucceed_page(String id) {
//		Criteria criteria = Criteria.where("id").is(id);
//		Query query = new Query(criteria);
//		PrintStatistics pss = printMongoTemplate.findOne(query, PrintStatistics.class);
//		Integer succeed_page=pss.getSucceed_page()+1;
//		Update update = Update.update("succeed_page",succeed_page);
//		printMongoTemplate.updateMulti(query, update, PrintStatistics.class);
//		
//	}

	public void update(PrintStatistics ps) {
		Criteria criteria = Criteria.where("id").is(ps.getId());
		Query query = new Query(criteria);
		Update update = Update.update("failure_page",ps.getFailure_page()).set("succeed_page",ps.getSucceed_page());
		printMongoTemplate.updateMulti(query, update, PrintStatistics.class);
	}

}
