package com.dascom.netty.service;

public interface PrintStatisticsService {

	void printFailureStatistics(String number);

	void printSucceedStatistics(String number);
}
