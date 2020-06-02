package com.couponmanager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.couponmanager.model.ApiModel;
import com.couponmanager.model.CouponModel;
import com.couponmanager.service.ICouponService;
import com.couponmanager.util.ErrorCodeType;

import net.sf.json.JSONObject;


@Controller
@RequestMapping(value = "/api/Coupons")
public class CouponController {

	@Autowired
	private ICouponService couponService;
	
	/**
	 * 1.랜덤한 코드의 쿠폰을 N개 생성하여 데이터베이스에 보관하는 API를 구현하세요.
	 * 
	 * count 갯수만큼 쿠폰 생성 : status 0 
	 * @param count
	 * @return responseCode
	 * @throws Throwable
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ApiModel createCoupon (@RequestParam(required=false , defaultValue="1") int count) throws Throwable {
		ApiModel apiModel = new ApiModel();
		if(count == 0) {
			apiModel.setResult(ErrorCodeType.Success);
		}else {
			try {
				int resultStatus = couponService.createCoupon(count);
				apiModel.setResult(resultStatus);
				apiModel.setMessage("Create Success!");
			}catch(Exception e) {
				apiModel.setResult(ErrorCodeType.DBError);
				apiModel.setMessage(e.getMessage());
			}
		}
		return apiModel;
	}
			
	/**
	 * 2.생성된 쿠폰중 하나를 사용자에게 지급하는 API를 구현하세요.
	 * 
	 * 생성된 쿠폰 중 하나를 지급 : status 0 -> 1
	 * @return responseCode
	 * @throws Throwable
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody String giveCoupon() throws Throwable {
		String Code;
		CouponModel resultCoupon = couponService.giveCoupon();
		if(resultCoupon != null) {
			Code = resultCoupon.getCode();
		}else {
			Code = "No Coupon Available!";
		}
		return Code;
	}
	
	/**
	 * 3.사용자에게 지급된 쿠폰을 조회하는 API를 구현하세요.
	 * 
	 * 지급된 쿠폰 조회
	 * @return responseCode
	 * @throws Throwable
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody JSONObject selectGivenCoupon() throws Throwable {
		JSONObject givenCoupon = couponService.selectGivenCoupon();
		return givenCoupon;
	}
	
	/**
	 * 4.지급된 쿠폰중 하나를 사용하는 API를 구현하세요. (쿠폰 재사용은 불가)
	 * 5.지급된 쿠폰중 하나를 사용 취소하는 API를 구현하세요. (취소된 쿠폰 재사용 가능)
	 * 
	 * 지급된 쿠폰 사용(input -> 쿠폰번호) : status 1 -> 2
	 * 사용된 쿠폰 취소(input -> 쿠폰번호) : status 2 -> 1 
	 * @param code,status
	 * @return ApiModel
	 * @throws Throwable
	 */
	@RequestMapping(value="/{code}/{status}", method = RequestMethod.PUT)
	public @ResponseBody ApiModel doCoupon(
			@PathVariable String code,
			@PathVariable int status
			) throws Throwable {
		ApiModel apiModel = new ApiModel();
		apiModel = couponService.doCoupon(code , status , apiModel);
		return apiModel;
	}
	
	/**
	 * 6.발급된 쿠폰중 당일 만료된 전체 쿠폰 목록을 조회하는 API를 구현하세요.
	 * 
	 * 당일 만료된 쿠폰 조회
	 * @return responseCode
	 * @throws Throwable
	 */
	@RequestMapping(value="/expired", method = RequestMethod.GET)
	public @ResponseBody JSONObject selectExpiredCoupon() throws Throwable {
		JSONObject givenCoupon = couponService.selectExpiredCoupon();
		return givenCoupon;
	}
	
}
