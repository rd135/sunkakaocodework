[REST API Guide] CouponManager 
======================

# Rest API 기반 쿠폰시스템 
##

## 구현 부분
### 
	1. 랜덤한 코드의 쿠폰을 N개 생성하여 데이터베이스에 보관하는 API를 구현하세요.[input : N]
	2. 생성된 쿠폰중 하나를 사용자에게 지급하는 API를 구현하세요. [output : 쿠폰번호]
	3. 사용자에게 지급된 쿠폰을 조회하는 API를 구현하세요.
	4. 지급된 쿠폰중 하나를 사용하는 API를 구현하세요. (쿠폰 재사용은 불가) [input : 쿠폰번호]
	5. 지급된 쿠폰중 하나를 사용 취소하는 API를 구현하세요. (취소된 쿠폰 재사용 가능) [input : 쿠폰번호]
	6. 발급된 쿠폰중 당일 만료된 전체 쿠폰 목록을 조회하는 API를 구현하세요.
	7. 발급된 쿠폰중 만료 3일전 사용자에게 메세지(“쿠폰이 3일 후 만료됩니다.”)를 발송하는 기능을 구현하 세요. (실제 메세지를 발송하는것이 아닌 stdout 등으로 출력하시면 됩니다.)
	8. API 인증을 위해 JWT(Json Web Token)를 이용해서 Token 기반 API 인증 기능을 개발하고 각 API 호출 시에 HTTP Header에 발급받은 토큰을 가지고 호출하세요. signup 계정생성 API: ID, PW를 입력 받아 내부 DB에 계정을 저장하고 토큰을 생성하여 출력한 다. 단, 패스워드는 안전한 방법으로 저장한다. signin 로그인 API: 입력으로 생성된 계정 (ID, PW)으로 로그인 요청하면 토큰을 발급한다.

****

# 개발 프레임워크
###

 Spring Framework
 Mybatis - mysql(Aws의 RDS 사용)
 Maven
 Tomcat 8.5 ver.
	
****

# 문제해결

### 1. 랜덤한 코드의 쿠폰을 N개 생성하여 데이터베이스에 보관하는 API를 구현하세요.[input : N]
	
	[API 위치]
	-com
		-couponmanager
			-controllers
				-CouponController
	[고려한 점]
	* 대량 insert 를 가능하게 하려면 어떻게 해야 하는가? -> insert 시 한번에 쿼리를 작동하게 하는 것이 아니라, 주어진 카운트를 10000개씩 잘라서 insert.
	* 생성 된 상태를 어떻게 표현 할 것인가? -> T_COUPON 의 status 를 0으로 표현.
	* 쿠폰의 형태를 어떻게 만들 것인가? -> java.util.UUID 사용. 형태가 쿠폰 형태와 비슷한 점 / 중복이 발생하기 힘든 구조 때문에 채택.

### 2. 생성된 쿠폰중 하나를 사용자에게 지급하는 API를 구현하세요. [output : 쿠폰번호]

	[API 위치]
	-com
		-couponmanager
			-controllers
				-CouponController
	[고려한 점]
	* 지급을 어떤 식으로 표현 할 것인가? -> T_COUPON 의 status 0에서 1로 업데이트.

### 3. 사용자에게 지급된 쿠폰을 조회하는 API를 구현하세요.

	[API 위치]
	-com
		-couponmanager
			-controllers
				-CouponController
	[고려한 점]
	* T_COUPON 의 Status 1인 것들 검색.
	
### 4. 지급된 쿠폰중 하나를 사용하는 API를 구현하세요. (쿠폰 재사용은 불가) [input : 쿠폰번호]

	[API 위치]
	-com
		-couponmanager
			-controllers
				-CouponController
	[고려한 점]
	* 사용을 어떤 식으로 표현 할 것인가? -> T_COUPON 의 status 1에서 2로 업데이트.
	
### 5. 지급된 쿠폰중 하나를 사용 취소하는 API를 구현하세요. (취소된 쿠폰 재사용 가능) [input : 쿠폰번호]

	[API 위치]
	-com
		-couponmanager
			-controllers
				-CouponController
	[고려한 점]
	* 사용 취소를 어떤 식으로 표현 할 것인가? -> T_COUPON 의 status 2에서 1로 업데이트.
	* 사용자에 대한 자세한 언급이 없었기에, T_COUPON을 사용함에 있어 지급만 생각하고 사용자에 대한 고려까지는 하지 않음.
	* 위의 이유로 4~5번은 같은 Api라고 판단, 그렇게 만듦.
	
### 6. 발급된 쿠폰중 당일 만료된 전체 쿠폰 목록을 조회하는 API를 구현하세요.

	[API 위치]
	-com
		-couponmanager
			-controllers
				-CouponController
	[고려한 점]
	* Mysql date 비교 / Status 3번인 List 가져 옴 
	
### 7. 발급된 쿠폰중 만료 3일전 사용자에게 메세지(“쿠폰이 3일 후 만료됩니다.”)를 발송하는 기능을 구현하 세요. (실제 메세지를 발송하는것이 아닌 stdout 등으로 출력하시면 됩니다.)

	[API 위치]
	-com
		-couponmanager
			-scheduler
				-NotifySchedulerService
	[고려한 점]
	* 다른 문제들은 API라고 했지만, 이 문제는 '기능' 이라 언급한 점을 참조, Java Scheduler 이용해서 일정 주기마다 1.오늘 날짜 기준으로 만료 된 것 상태 3으로 업데이트 2.오늘 날짜 기준 만료 3일 전 coupon을 찾아서 "Given Coupon 쿠폰코드 has 3 days left until the expiration date." System.out.println() 으로 출력.
	
### 8. API 인증을 위해 JWT(Json Web Token)를 이용해서 Token 기반 API 인증 기능을 개발하고 각 API 호출 시에 HTTP Header에 발급받은 토큰을 가지고 호출하세요.

	[API 위치]
	-com
		-couponmanager
			-controllers
				-UserController
	[고려한 점]
	* 1. create -> 계정 생성시, 우선적으로 DB에 같은 UserId가 있는지 비교 후, 비밀번호를 AES -> Base64 encode를 통해서 저장.
	* 2. login -> 로그인 시 jwt 토큰 생성해서 return. (POSTMAN 사용 기준)이후, 해당 토큰을 request header에 넣어 줘야 API 정상 호출 가능. 

### 9. 그 외

	 * Status 
	 * 0: 만들어지기만 한 상태 - 사용자에게 발급도 안 된 경우
	 * 1: 사용자에게 발급 된 경우 / 취소
	 * 2: 사용
	 * 3: 만료
	
	Table은 
	
	T_COUPON
	 `Id` int NOT NULL AUTO_INCREMENT,
	 `Code` varchar(45) NOT NULL,
	 `Status` int NOT NULL,
	 `CreateDate` datetime DEFAULT CURRENT_TIMESTAMP,
	 `UpdateDate` datetime DEFAULT NULL,
	 `ExpiredDate` datetime DEFAULT NULL,
	 PRIMARY KEY (`Id`)
	
	T_USER 
	`Id` int NOT NULL AUTO_INCREMENT,
	`UserId` varchar(45) NOT NULL,
	`Password` varchar(45) NOT NULL,
	PRIMARY KEY (`Id`)
	
	
****

# 빌드 및 배포
###
	1. 프로젝트를 eclipse 에 import.
	2. war file 형태로 export. 
	3. Tomcat webapp 에 안에 있는 내용 싹 지운 후, ROOT 폴더에 만든 war 파일을 압축 해제.
	4. Tomcat 기동 후, 아래 실행 방법 가이드 참조 해서 호출 	
****

# 실행 방법 가이드(API 명세서) 
###
*모든 테스트는 POSTMAN을 이용해서 진행하였습니다.
	
##API RESULT
###	
	(ApiModel)
		- int result;
		- String message;
###		
	(ErrorStatus)	
		- int Success = 0;
		- int Duplicate = 1;
		- int DBError = 2;
		- int InvalidParameter = 3;
		- int InvalidResult = 4;

###	
```
[POST]	[IP:PORT]/api/Users/create   
Header Content-Type	x-www-form-urlencoded   

Body    
x-www-form-urlencoded   
String userId   
String password   

Response   
ApiModel result   
```

###	
```
[POST]	[IP:PORT]/api/Users/login   
Header Content-Type	x-www-form-urlencoded   

Body    
x-www-form-urlencoded   
String userId   
String password   

Response   
ApiModel result : message 부분에 Authorization : Token 출력.    
```

*이후 모든 API 호출 시, header 부분에 Authorization : Token 출력 해 주지 않으면 401 에러.

###	
```
[POST]	[IP:PORT]/api/Coupons   
Header    
Content-Type : x-www-form-urlencoded   
Authorization : Login으로 발급받은 Token   

Body    
x-www-form-urlencoded   
int count : 생성할 Coupon 갯수   

Response   
ApiModel result    
```
###	
```
[PUT]	[IP:PORT]/api/Coupons
Header 
Content-Type : x-www-form-urlencoded
Authorization : Login으로 발급받은 Token

Body
x-www-form-urlencoded

Response
String code
```
###	
```
[GET]	[IP:PORT]/api/Coupons
Header   
Content-Type : x-www-form-urlencoded   
Authorization : Login으로 발급받은 Token   
   
Body 
x-www-form-urlencoded   

Response
JSONObject code
```
###	
```
[PUT]	[IP:PORT]/api/Coupons/{couponCode}/{status}
Header 
Content-Type : x-www-form-urlencoded
Authorization : Login으로 발급받은 Token

Body
x-www-form-urlencoded

Response
ApiModel result
```
###	
```
[GET]	[IP:PORT]/api/Coupons/expired
Header 
Content-Type : x-www-form-urlencoded
Authorization : Login으로 발급받은 Token

Body
x-www-form-urlencoded

Response
JSONObject code
```
****
