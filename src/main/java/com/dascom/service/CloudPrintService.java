package com.dascom.service;

import com.dascom.common.ResultVO;
import com.dascom.entity.WifiConfig;

/**
 * 云打印的服务接口
 * @author hqw
 *
 */
public interface CloudPrintService {

//	ResultVO<Object> print(String number, String id, byte[] data) throws Exception;
//
//	ResultVO<Object> update(String number, String id, byte[] updateData) throws Exception;

	ResultVO<Object> print(String number,String id,String data) throws Exception;

	ResultVO<Object> update(String number,String id,String data)throws Exception;

	ResultVO<Object> review(String number, String id,String data) throws Exception ;

	ResultVO<Object> control(String number, String id, String data,String controlType)throws Exception ;

	ResultVO<Object> readWifiConfig(String number, String id) throws Exception;

	ResultVO<Object> updataWifiConfig(String number, String id, WifiConfig wifiConfig) throws Exception;
}
