package com.dascom.common.utils;

import com.dascom.common.ResultVO;

public class ResultVOUtil {
	
	public static ResultVO<Object> success(Object object) {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		resultVO.setCode(0);
		resultVO.setData(object);
		return resultVO;
	}
	public static ResultVO<Object> success() {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		resultVO.setCode(0);
		return resultVO;
	}
	public static ResultVO<Object> success(String id) {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		resultVO.setCode(0);
		resultVO.setId(id);
		return resultVO;
	}
	public static ResultVO<Object> success(String id,Object object) {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		resultVO.setCode(0);
		resultVO.setData(object);
		resultVO.setId(id);
		return resultVO;
	}

	
	public static ResultVO<Object> error(Integer code,Object message) {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		resultVO.setCode(code);
		resultVO.setData(message);
		return resultVO;
	}
	public static ResultVO<Object> error(Integer code) {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		resultVO.setCode(code);
		return resultVO;
	}
	public static ResultVO<Object> error(Integer code,String id) {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		resultVO.setCode(code);
		resultVO.setId(id);
		return resultVO;
	}
	public static ResultVO<Object> error(Integer code,String id,String data) {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		resultVO.setCode(code);
		resultVO.setId(id);
		resultVO.setData(data);
		return resultVO;
	}
}
