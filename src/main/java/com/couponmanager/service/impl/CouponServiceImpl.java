package com.couponmanager.service.impl;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.couponmanager.dao.ICouponDao;
import com.couponmanager.model.ApiModel;
import com.couponmanager.model.CouponModel;
import com.couponmanager.service.ICouponService;
import com.couponmanager.util.ErrorCodeType;

import net.sf.json.JSONObject;

/**
 * CouponService 실제 로직 부분
 * 
 * Status 
 * 0: 만들어지기만 한 상태 - 사용자에게 발급도 안 된 경우
 * 1: 사용자에게 발급 된 경우 / 취소
 * 2: 사용
 * 3: 만료
 * 
 * @author s160010
 *
 */

@Service
public class CouponServiceImpl implements ICouponService {
	
	@Autowired
	private Properties config;
	
	@Autowired
	private ICouponDao couponDao;
	
	@Override
	public int createCoupon(int count) throws Exception{
		int resultStatus = ErrorCodeType.Success;
		
		//갯수 나눔 기준
		int divInt = 10000;
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		//String[] randCodes = new String[count];
		String expiredMonth = config.getProperty("expiredMonth");
		
		//properties에 입력하지 않은 경우 기본값 1
		if(StringUtils.isBlank(expiredMonth)) {
			expiredMonth = "1";
		}
		
		//오늘 날짜
		String createDate = getSimpleDate(0);
	    
		//만료 날짜
	    String expiredDate = getSimpleDate(Integer.parseInt(expiredMonth));
		
		//기준보다 Count가 많은 경우(10000개 이상일 경우)
	    if (count > divInt) {
			createMassiveCouponDevider(count, map , divInt , createDate , expiredDate);
		} else {
			//기준보다 Count 적은 경우 - 코드값 만드는 곳
			resultStatus = couponCreator(count, map , createDate, expiredDate);
		}
		
		return resultStatus; 
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public CouponModel giveCoupon() throws Exception {
		//업데이트 될 날짜
		String updateDate = getSimpleDate(0);
		HashMap<String, Object> map = new HashMap<String, Object>();

		//가져 올 것 중에서 만료된 것 제외
		map.put("STATUS_EXCLUDE", CouponStatus.EXPIRED.value());
		
		map.put("STATUS", 0);
		map.put("LIMIT", 1);
		
		CouponModel coupon = new CouponModel();
		
		//생성만 된 쿠폰 중 가장 옛 것 return
		List<CouponModel> couponList = couponDao.getCoupon(map);
		
		//갯수가 0 보다 커야 발급 가능
		if(couponList.size() != 0) {
			coupon = couponList.get(0);
			updateCouponStatus(map, coupon.getId(), CouponStatus.GAVE.value(), updateDate);
		}
		
		return coupon;
	}

	@Override
	public JSONObject selectGivenCoupon() throws Exception {
		HashMap<String, Object> map = new HashMap<String, Object>();
		JSONObject jsonResult = new JSONObject();
		String[] couponArr = null;
		map.put("STATUS", CouponStatus.GAVE.value());
		
		//발급만 된 쿠폰 List
		List<CouponModel> couponList = couponDao.getCoupon(map);
		
		//List check
		jsonResult = jsonParserForCouponArr(jsonResult, couponList, couponArr);
		
		return jsonResult;
	}
	
	@Override
	public ApiModel doCoupon(String code, int status, ApiModel apiModel) throws Exception {
		CouponModel coupon = new CouponModel();
		HashMap<String, Object> map = new HashMap<String, Object>();
		//업데이트 될 날짜
		String updateDate = getSimpleDate(0);
		
		map.put("CODE", code);
		
		//쿠폰 정보 가져오기
		List<CouponModel> couponList = couponDao.getCoupon(map);
		
		if(couponList.size() != 0) {
			//쿠폰은 하나만 있어야함
			if(couponList.size() == 1) {
				coupon = couponList.get(0);
				
				switch(coupon.getStatus()) {
					
					//사용자에게 발급도 안 된 경우
					case 0 : 
						apiModel.setResult(ErrorCodeType.InvalidResult);
						apiModel.setMessage("You need to give this coupon first!");
					//사용자에게 발급 된 경우
					case 1 :
						if(status == CouponStatus.USED.value()) {
							//사용 : 사용 상태로 업데이트
							int count = updateCouponStatus(map, coupon.getId(), status, updateDate);
							
							if(count > 0) {
								apiModel.setResult(ErrorCodeType.Success);
								apiModel.setMessage("Update Success! : " + coupon.getCode() + " is used!");
							}else {
								apiModel.setResult(ErrorCodeType.DBError);
								apiModel.setMessage("Update Fail!");
							}
							
						}else {
							apiModel.setResult(ErrorCodeType.InvalidParameter);
							apiModel.setMessage("You need to give this coupon first!");
						}
					//이미 사용한 경우
					case 2 :
						if(status == CouponStatus.GAVE.value()) {
							//사용 취소 : 발급 상태로 업데이트
							int count = updateCouponStatus(map, coupon.getId(), status, updateDate);
							
							if(count > 0) {
								apiModel.setResult(ErrorCodeType.Success);
								apiModel.setMessage("Update Success! : " + coupon.getCode() + " has been canceled!");
							}else {
								apiModel.setResult(ErrorCodeType.DBError);
								apiModel.setMessage("Update Fail!");
							}
							
						}else {
							apiModel.setResult(ErrorCodeType.InvalidParameter);
							apiModel.setMessage("You need to give this coupon first!");
						}
					//쿠폰 만료
					case 3 : 
						apiModel.setResult(ErrorCodeType.InvalidResult);
						apiModel.setMessage("This coupon is already expired!");
					//그 외
					default: 
						apiModel.setResult(ErrorCodeType.InvalidResult);
						apiModel.setMessage("Unknown error! Please contact the administrator!");
				}
			} else {
				//쿠폰은 하나만 있어야 하는데 2개 이상
				apiModel.setResult(ErrorCodeType.Duplicate);
				apiModel.setMessage("Coupon duplicated! Please contact the administrator!");
			}
		}else {
			//쿠폰번호로 검색했는데 쿠폰 없음	
			apiModel.setResult(ErrorCodeType.DBError);
			apiModel.setMessage("No coupon found!");
		}
		return apiModel;
	}

	@Override
	public JSONObject selectExpiredCoupon() throws Exception {
		JSONObject jsonResult = new JSONObject();
		String[] couponArr = null;
		String targetDate = getSimpleDate(0);
		
		//발급만 된 쿠폰 List
		List<CouponModel> couponList = getCouponList(CouponStatus.EXPIRED.value(), targetDate);
		
		//List check
		jsonResult = jsonParserForCouponArr(jsonResult, couponList, couponArr);
		
		return jsonResult;
	}

	@Override
	public void updateExpiredCoupon() throws Exception {
		String targetDate = getSimpleDate(0);
		List<CouponModel> couponList = getCouponList(targetDate);
		
		int[] couponIdArr = null;
		
		if(couponList.size() != 0) {
			couponIdArr = new int[couponList.size()];
			int index = 0;
			for(CouponModel arr : couponList) {
				couponIdArr[index] = arr.getId();
				index++;
			}
			
			updateCouponStatus(couponIdArr, CouponStatus.EXPIRED.value(), targetDate);
		}
	}

	@Override
	public void printSoonExpiredCoupon() throws Exception {
		String notifyDate = getSimpleDate(0);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("NOTIFY_DATE", notifyDate);
		
		//발급만 된 쿠폰 List
		List<CouponModel> couponList = couponDao.getCoupon(map);
		
		if(couponList.size() != 0) {
			for(CouponModel arr : couponList) {
				System.out.println("Given Coupon " + arr.getCode() + "has 3 days left until the expiration date.");
			}
		}
	}
	
	private List<CouponModel> getCouponList(int status, String targetDate) throws Exception{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("STATUS", status);
		map.put("TARGET_DATE", targetDate);
		
		//발급만 된 쿠폰 List
		List<CouponModel> couponList = couponDao.getCoupon(map);
		
		return couponList;
	}
	
	private List<CouponModel> getCouponList(String targetDate) throws Exception{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("TARGET_DATE", targetDate);
		map.put("STATUS_EXCLUDE", CouponStatus.EXPIRED.value());
		
		//발급만 된 쿠폰 List
		List<CouponModel> couponList = couponDao.getCoupon(map);
		
		return couponList;
	}
	
	private JSONObject jsonParserForCouponArr(JSONObject jsonResult, List<CouponModel> couponList , String[] couponArr) {
		//List check
		if(couponList.size() != 0) {
			couponArr = new String[couponList.size()];
			int index = 0;
			for(CouponModel arr : couponList) {
				couponArr[index] = arr.getCode();
				index++;
			}
			jsonResult.put("code", couponArr);
		}
		
		return jsonResult;
	}
	
	
	private int updateCouponStatus(HashMap<String, Object> map, int id , int status, String updateDate) {
		map.clear();
		map.put("ID", id);
		map.put("STATUS", status);
		map.put("UPDATE_DATE", updateDate);
		int count = couponDao.update(map);
		
		return count;
	}
	
	private int updateCouponStatus(int[] ids , int status, String updateDate) {

		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("IDs", ids);
		map.put("STATUS", status);
		map.put("UPDATE_DATE", updateDate);
		int count = couponDao.update(map);
		
		return count;
	}
	
	@Transactional(rollbackFor = { Exception.class })
	private int createMassiveCouponDevider(int count, HashMap<String, Object> map, int divInt, String createDate, String expiredDate) {
		int resultStatus = ErrorCodeType.Success;
		int page = 0;

		if ((count % divInt) != 0) {
			page = (count / divInt) + 1;
		} else {
			page = (count / divInt);
		}
		
		for (int i = 0; i < page; i++) {
			if(i == 0) {
				resultStatus = couponCreator(divInt, map , createDate, expiredDate);
			} else {
				if(count - (i * divInt) > divInt) {
					resultStatus = couponCreator(divInt, map , createDate, expiredDate);
				} else {
					int lastCount = count - (i * divInt);
					resultStatus = couponCreator(lastCount , map , createDate, expiredDate);
				}
			}
		}
		
		return resultStatus;
	}
	
	@Transactional(rollbackFor = { Exception.class })
	private int couponCreator(int count, HashMap<String, Object> map , String createDate, String expiredDate) {
		int resultStatus = ErrorCodeType.Success;
		
		map.clear();
		
		String[] randCodes = new String[count];
		
		randCodeGenerator(count , randCodes);

		map.put("CODES", randCodes);
		map.put("CREATE_DATE", createDate);
		map.put("EXPIRED_DATE", expiredDate);
		
		int resultCreateCount = couponDao.create(map);
		
		if(resultCreateCount > 0) {
			resultStatus = ErrorCodeType.Success;
		}else {
			resultStatus = ErrorCodeType.DBError;
		}
		
		return resultStatus;
	}

	private String getSimpleDate(int addValue) {
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Calendar cal = Calendar.getInstance();
		if(addValue > 0) {
		    cal.add(Calendar.MONTH, + addValue);
		}
		Date date=cal.getTime();
		
		return format.format(date);
	} 

	private String[] randCodeGenerator(int count, String[] randCodes) {
		
        try {
        	for(int i = 0; i < count ; i++) {
        		String uuid = UUID.randomUUID().toString();
                randCodes[i] = uuid;
        	}
        } catch (Exception e) {
			e.printStackTrace();
		}
        
		return randCodes;

	}
}
