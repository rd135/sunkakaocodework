package com.couponmanager.service;

import com.couponmanager.model.ApiModel;
import com.couponmanager.model.CouponModel;

import net.sf.json.JSONObject;

public interface ICouponService {
	
	/**
	 * CouponService
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
	public enum CouponStatus{
		CREATE(0), GAVE(1), USED(2), EXPIRED(3);
		
		private int value;
		private CouponStatus(int value){
			this.value = value;
		}
		
		public int value(){
			return value;
		}
	}
	
	public int createCoupon(int count) throws Exception;
	public CouponModel giveCoupon() throws Exception;
	public JSONObject selectGivenCoupon() throws Exception;
	public ApiModel doCoupon(String code , int status, ApiModel apiModel) throws Exception;
	public JSONObject selectExpiredCoupon() throws Exception;
	public void updateExpiredCoupon() throws Exception;
	public void printSoonExpiredCoupon() throws Exception;
}
