package com.dascom.service;


import com.dascom.common.RequestEntity;
import com.dascom.common.ResultVO;

public interface UserService {
	/**
	 * 设置用户信息
	 * @param number
	 * @param id
	 * @param requestEntity
	 * @return ResultVO<Object>
	 * @throws Exception
	 */
	ResultVO<Object> writeUserMessage(String number,String id, RequestEntity requestEntity) throws Exception;
	
//	ResultVO<Object> readUserMessage(String number,String id) throws  Exception;

}
