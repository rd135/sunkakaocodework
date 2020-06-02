package com.couponmanager.scheduler;

import org.springframework.stereotype.Service;

import com.couponmanager.service.ICouponService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class NotifySchedulerService {

	@Autowired
	private ICouponService couponService;

	/**
	 * 7.발급된 쿠폰중 만료 3일전 사용자에게 메세지(“쿠폰이 3일 후 만료됩니다.”)를 발송하는 기능을 구현하세요. (실제 메세지를 발송하는것이 아닌 stdout 등으로 출력하시면 됩니다.) 
	 * 
	 * @throws Throwable
	 */
	@Scheduled(fixedDelay=100000)
	//@Scheduled(cron="0 0 0 * * ?")
	@Async
	public void runSchedulerProcessor() throws Exception {
		//맨 처음 시작할때 만료 날짜 체크 후 만료상태로 업데이트
		couponService.updateExpiredCoupon();
		//쿠폰이 만료 3일전이면 (쿠폰이 3일 후 만료됩니다.) 메시지 출력 
		couponService.printSoonExpiredCoupon();
	}
}
