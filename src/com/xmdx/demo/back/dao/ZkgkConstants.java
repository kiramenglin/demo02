package com.xmdx.demo.back.dao;

import org.apache.commons.lang.StringUtils;

import com.e9rj.platform.common.BaseConstants;

public class ZkgkConstants {

	// 用户类型： 教师
	public static final String USERTYPE_5 = "5";
	// 数据库前缀：ZKXJ
	public static final String PRE_ZKXJ = "zkxj.";
	// 数据库前缀：SSO
	public static final String PRE_SSO = "sso.";
	
	public static final String ID_SSODBSERVICE = "SSOdbService";
	
	// ZKXJ：信息栏目->系统帮助HELP
	public static final String ZKXJ_HELP = "HELP";
	// ZKXJ：信息栏目->网站动态WZDT
	public static final String ZKXJ_WZDT = "WZDT";
	// 用户类型：管理机构
	public static final String USERTYPE_0 = "0";
	// 用户类型：省自考办
	public static final String USERTYPE_1 = "1";
	// 用户类型：主考院校
	public static final String USERTYPE_2 = "2";
	// 用户类型：助学院校
	public static final String USERTYPE_3 = "3";
	// 用户类型：学生
	public static final String USERTYPE_4 = "4";

	public static final String USER_APPCODE_ZKXJ = "zkxj";

	public static final String SUB_SITECODE_E9RJ = "e9rj";

	// 组卷方式(试卷生成方式)
	/**
	 * "1"："录入组卷"，"2"："随机组卷"
	 */
	public static final String EXAM_TYPE = "EXAM_TYPE";
	/**
	 * "1"："录入组卷"，"2"："随机组卷"
	 */
	public static final String EXAM_TYPE_INPUT = "1";
	/**
	 * "2"："随机组卷"， "1"："录入组卷"
	 */
	public static final String EXAM_TYPE_RANDOM = "2";

	// 试题类型
	/**
	 * "O1"："单选题"，"O2"："多选题"，"O3"："判断题"，"O4"："完形填空(单选题)"，"S5"："完形填空(填空题)"，"O6"："组合题"，"S1"：
	 * "论述题"，"S2"："材料分析题" ，"S3"："简答题"，"S4"： "操作题"
	 */
	public static final String OE_QS_TYPE = "OE_QS_TYPE";
	/**
	 * "O1"："单选题"，"O2"："多选题"，"O3"："判断题"，"O4"："完形填空(单选题)"，"S5"："完形填空(填空题)"，"O6"："组合题"，"S1"：
	 * "论述题"，"S2"："材料分析题" ，"S3"："简答题"，"S4"： "操作题"，"S5"："完形填空(填空题)"
	 */
	public static final String OE_QS_TYPE_SINGLE = "O1";
	/**
	 * "O2"："多选题"，"O1"："单选题"，"O3"："判断题"，"O4"："完形填空(单选题)"，"S5"："完形填空(填空题)"，"O6"："组合题"，"S1"：
	 * "论述题"，"S2"："材料分析题" ，"S3"："简答题"，"S4"： "操作题"，"S5"："完形填空(填空题)"
	 */
	public static final String OE_QS_TYPE_MULTI = "O2";
	/**
	 * "O3"："判断题"，"O1"："单选题"，"O2"："多选题"，"O4"："完形填空(单选题)"，"S5"："完形填空(填空题)"，"O6"："组合题"，"S1"：
	 * "论述题"，"S2"："材料分析题" ，"S3"："简答题"，"S4"： "操作题"，"S5"："完形填空(填空题)"
	 */
	public static final String OE_QS_TYPE_JUDGE = "O3";
	/**
	 * "O4"："完形填空(单选题)"，"O3"："判断题"，"O1"："单选题"，"O2"："多选题"，"S5"："完形填空(填空题)"，"O6"："组合题"，"S1"：
	 * "论述题"，"S2"："材料分析题" ，"S3"："简答题"，"S4"： "操作题"，"S5"："完形填空(填空题)"
	 */
	public static final String OE_QS_TYPE_CLOZE_SINGLE = "O4";
	/**
	 * "S5"："完形填空(填空题)"，"O1"："单选题"，"O2"："多选题"，"O3"："判断题"，"O4"："完形填空(单选题)"，"O6"："组合题"，"S1"：
	 * "论述题"， "S2"："材料分析题"，"S3"： "简答题"，"S4"："操作题"
	 */
	public static final String OE_QS_TYPE_CLOZE_FILL = "S5";
	/**
	 * "O6"："组合题"，"O1"："单选题"，"O2"："多选题"，"O3"："判断题"，"O4"："完形填空(单选题)"，"S5"："完形填空(填空题)"，"S1"：
	 * "论述题"， "S2"："材料分析题"，"S3"： "简答题"，"S4"："操作题"
	 */
	public static final String OE_QS_TYPE_COMBINE = "O6";
	/**
	 * "S1"："论述题"，"O1"："单选题"，"O2"："多选题"，"O3"："判断题"，"O4"："完形填空(单选题)"，"S5"：
	 * "完形填空(填空题)"，"O6"："组合题"，"S2"："材料分析题" ，"S3"："简答题"，"S4"： "操作题"
	 */
	public static final String OE_QS_TYPE_THESIS = "S1";
	/**
	 * "S2"："材料分析题"，"O1"："单选题"，"O2"："多选题"，"O3"："判断题"，"O4"："完形填空(单选题)"，"S5"：
	 * "完形填空(填空题)"，"O6"："组合题"，"S1"："论述题" ，"S3"："简答题"，"S4"： "操作题"
	 */
	public static final String OE_QS_TYPE_ANALYSE = "S2";
	/**
	 * "S3"："简答题"，"O1"："单选题"，"O2"："多选题"，"O3"："判断题"，"O4"："完形填空(单选题)"，"S5"：
	 * "完形填空(填空题)"，"O6"："组合题"，"S1"："论述题"， "S2"："材料分析题"，"S4"： "操作题"
	 */
	public static final String OE_QS_TYPE_SHORT = "S3";
	/**
	 * "S4"："操作题"，"O1"："单选题"，"O2"："多选题"，"O3"："判断题"，"O4"："完形填空(单选题)"，"S5"：
	 * "完形填空(填空题)"，"O6"："组合题"，"S1"："论述题"， "S2"："材料分析题"，"S3"： "简答题"
	 */
	public static final String OE_QS_TYPE_OPERATE = "S4";

	// 试卷状态
	/**
	 * "0"："未提交"，"1"："已提交"，"2"："已审核"，"9"："驳回"
	 */
	public static final String OE_AUDIT_STATUS = "OE_AUDIT_STATUS";
	/**
	 * "0"："未提交"，"1"："已提交"，"2"："已审核"，"9"："驳回"
	 */
	public static final String OE_AUDIT_STATUS_NOT_COMMIT = "0";
	/**
	 * "1"："已提交"，"0"："未提交"，"2"："已审核"，"9"："驳回"
	 */
	public static final String OE_AUDIT_STATUS_COMMIT = "1";
	/**
	 * "2"："已审核"，"0"："未提交"，"1"："已提交"，"9"："驳回"
	 */
	public static final String OE_AUDIT_STATUS_AUDIT = "2";
	/**
	 * "9"："驳回"，"0"："未提交"，"1"："已提交"，"2"："已审核"
	 */
	public static final String OE_AUDIT_STATUS_BACK = "9";

	// 难度系数
	/**
	 * "1"："容易"，"2"："中等"，"3"："困难"
	 */
	public static final String OE_DIFF_GREE = "OE_DIFF_GREE";
	/**
	 * "1"："容易"，"2"："中等"，"3"："困难"
	 */
	public static final String OE_DIFF_GREE_ONE = "1";
	/**
	 * "2"："中等"，"1"："容易"，"3"："困难"
	 */
	public static final String OE_DIFF_GREE_TWO = "2";
	/**
	 * "3"："困难"，"1"："容易"，"2"："中等"
	 */
	public static final String OE_DIFF_GREE_THREE = "3";

	// 考试时长
	/**
	 * 10：10， 30：30， 60：60， 90：90， 120：120， 150：150， 180：：180
	 */
	public static final String OE_EXAM_TIME = "OE_EXAM_TIME";
	public static final String OE_EXAM_TIME_TEM = "10";
	public static final String OE_EXAM_TIME_THIRTY = "30";
	public static final String OE_EXAM_TIME_SIXTY = "60";
	public static final String OE_EXAM_TIME_NINTY = "90";
	public static final String OE_EXAM_TIME_TWELVE_HUNDRED = "120";
	public static final String OE_EXAM_TIME_FIFTEEN_HUNDRED = "150";
	public static final String OE_EXAM_TIME_EIGHTEEN_HUNDRED = "180";

	// 得分方式
	/**
	 * 1：全答案匹配; 2：增量得分
	 */
	public static final String OE_SCORE_METHOD = "ZKGK_SCORE_METHOD";
	/**
	 * 1：全答案匹配; 2：增量得分
	 */
	public static final String OE_SCORE_METHOD_ALL = "1";
	/**
	 * 2：增量得分; 1：全答案匹配
	 */
	public static final String OE_SCORE_METHOD_INCREMENT = "2";

	// 考试模式
	/**
	 * 1.固定时间、2.时间范围、3.任意时间
	 */
	public static final String OE_EXAM_MODEL_ABS = "1";
	/**
	 * 2.时间范围、1.固定时间、3.任意时间
	 */
	public static final String OE_EXAM_MODEL_RANGE = "2";
	/**
	 * 3.任意时间、1.固定时间、2.时间范围
	 */
	public static final String OE_EXAM_MODEL_ANYTIME = "3";

	// 考试次数限制
	/**
	 * 1.1次、2.2次、3.3次、5.5次、99.无限次
	 */
	public static final String OE_EXAM_COUNT_LIMIT_ONE = "1";
	/**
	 * 2.2次、1.1次、3.3次、5.5次、99.无限次
	 */
	public static final String OE_EXAM_COUNT_LIMIT_TWO = "2";
	/**
	 * 3.3次、1.1次、2.2次、5.5次、99.无限次
	 */
	public static final String OE_EXAM_COUNT_LIMIT_THREE = "3";
	/**
	 * 5.5次、1.1次、2.2次、3.3次、99.无限次
	 */
	public static final String OE_EXAM_COUNT_LIMIT_FIVE = "5";
	/**
	 * 99.无限次、1.1次、2.2次、3.3次、5.5次
	 */
	public static final String OE_EXAM_COUNT_LIMIT_NONE = "99";

	// 行为类别
	/**
	 * 1：点击;2：评论;3：收藏;4：分享;5：推荐
	 * 
	 */
	public static final String BEHAVIOR_TYPE = "BEHAVIOR_TYPE";
	/**
	 * 1：点击;
	 */
	public static final String BEHAVIOR_TYPE_ONE = "1";
	/**
	 * 2：评论;
	 */
	public static final String BEHAVIOR_TYPE_TWO = "2";
	/**
	 * 3：收藏;
	 */
	public static final String BEHAVIOR_TYPE_THREE = "3";
	/**
	 * 4：分享
	 */
	public static final String BEHAVIOR_TYPE_FOUR = "4";
	/**
	 * 5：推荐
	 */
	public static final String BEHAVIOR_TYPE_FIVE = "5";

	// 功能号
	/**
	 * 1.查看、2.新增、4.修改、8.删除
	 */
	public static final int AUTH_FUNC_NO_ONE = 1;

	/**
	 * 2.新增、4.修改、8.删除、1.查看
	 */
	public static final int AUTH_FUNC_NO_TWO = 2;

	/**
	 * 4.修改、8.删除、1.查看、2.新增
	 */
	public static final int AUTH_FUNC_NO_FOUR = 4;

	/**
	 * 8.删除、1.查看、2.新增、4.修改
	 */
	public static final int AUTH_FUNC_NO_EIGHT = 8;

	// SSO库名
	public static final String SSOdb = "sso.";

	/**
	 * 院校类型：1：主考院校，2：助学院校
	 */
	/**
	 * 1：主考院校，2：助学院校
	 */
	public static final String SCHOOL_TYPE_EXAM = "1";
	/**
	 * 2：助学院校，1：主考院校，
	 */
	public static final String SCHOOL_TYPE_STUDY = "2";
	
	/**
	 * 权重类别; 1：学习进度（登录次数）
	 */
	public static final String RATE_TYPE_ONE = "1";
	/**
	 * 权重类别; 2：学习进度（学习时长：考核知识点）
	 */
	public static final String RATE_TYPE_TWO = "2";
	/**
	 * 权重类别; 3：学习进度（学习时长：非考核知识点）
	 */
	public static final String RATE_TYPE_THREE = "3";

	/**
	 * 权重类别： 11：知识点测评，12：阶段测评，13：综合测评
	 */
	public static final String RATE_TYPE_KNOWLEDGE = "11";
	/**
	 *  权重类别： 12：阶段测评，11：知识点测评，13：综合测评
	 */
	public static final String RATE_TYPE_STAGE_EXAM = "12";
	/**
	 *  权重类别： 13：综合测评，11：知识点测评，12：阶段测评
	 */
	public static final String RATE_TYPE_COMPLEX_EXAM = "13";

	/**
	 * 在学状态; 1：报名
	 */
	public static final String APPLY_STATE_ONE = "1";
	/**
	 * 在学状态; 2：在学
	 */
	public static final String APPLY_STATE_TWO = "2";
	/**
	 * 在学状态; 3：结课
	 */
	public static final String APPLY_STATE_THREE = "3";

	/**
	 * 一些常用的变量.
	 */
	public static final String KNOWLEDGE_ID = "KNOWLEDGE_ID";

	public static final String EXAMPAPER_ID = "EXAMPAPER_ID";

	public static final String FORMNAME = "FORMNAME";

	public static final String COURSE_ID = "COURSE_ID";

	public static final String COURSE_NAME = "COURSE_NAME";
	
	public static final String COURSE_CODE = "COURSE_CODE";
	
	public static final String COURSE_TYPE = "COURSE_TYPE";
	
	public static final String COURSE_SORT = "COURSE_SORT";

	public static final String CMD = "CMD";

	public static final String USE_TYPE = "USE_TYPE";

	public static final String QS_TYPE = "QS_TYPE";

	public static final String QUESTION_ID = "QUESTION_ID";

	public static final String SYSCODE = "SYSCODE";

	public static final String ATTACH = "attach";

	public static final String ORGCODE = "ORGCODE";

	public static final String MSG_SAVE_SUCCESS = "MSG_SAVE_SUCCESS";

	public static final String MSG_SAVE_FAILURE = "MSG_SAVE_FAILURE";

	public static final String OPNO = "OPNO";

	public static final String PERSON_ID = "PERSON_ID";

	public static final String SCORE = "SCORE";

	public static final String QS_SN = "QS_SN";

	public static final String QS_INFO = "QS_INFO";

	public static final String ITEMNO = "ITEMNO";

	public static final String ITEM_INFO = "ITEM_INFO";

	public static final String RIGHT_ANSWER = "RIGHT_ANSWER";

	public static final String SCORE_METHOD = "SCORE_METHOD";

	/**
	 * 产品类型：1、学习，2、习题，3、过考
	 */
	public static final String PRODUCT_TYPE_ONE = "1";
	public static final String PRODUCT_TYPE_TWO = "2";
	public static final String PRODUCT_TYPE_THREE = "3";
	/**
	 * 空白的userId
	 */
	public static final String USER_ID_BLANK = "-1";

	// USE_TYPE 习题用途
	/**
	 *：0：阶段测评
	 */
	public static final String EXAM_USE_TYPE_STAGE = "0";
	/**
	 * 1：综合测评
	 */
	public static final String EXAM_USE_TYPE_COMPLEX = "1";
	/**
	 * 2：知识点测评
	 */
	public static final String EXAM_USE_TYPE_KNOWLEDGE = "2";
	/**
	 * 3：历年真题
	 */
	public static final String EXAM_USE_TYPE_REAL = "3";

	// QS_RANGE 习题性质
	/**
	 * 1：练习， 2：知识点测评， 4：阶段测评， 8：综合测评
	 */
	public static final String QS_RANGE_PRACTICE = "1";
	/**
	 * 2：知识点测评， 1：练习， 4：阶段测评， 8：综合测评
	 */
	public static final String QS_RANGE_KNOW = "2";
	/**
	 * 4：阶段测评， 1：练习， 2：知识点测评， 8：综合测评
	 */
	public static final String QS_RANGE_STAGE = "4";
	/**
	 * 8：综合测评， 1：练习， 2：知识点测评， 4：阶段测评
	 */
	public static final String QS_RANGE_COMPLEX = "8";

	/**
	 * 金桨网地址
	 */
	private static String goldoarAppName = "";

	public static String getGoldoarAppName() {
		if (StringUtils.isBlank(goldoarAppName)) {
			goldoarAppName = BaseConstants.getGlobalValue("1300");
		}
		return goldoarAppName;
	}

	/**
	 * 知识点关联表(关系类型)RELATION_TYPE 0 ：父子，		
	 */
	public static final String RELATION_TYPE_ZERO = "0";
	/**
	 * 知识点关联表(关系类型)RELATION_TYPE 3 ：无向，		
	 */
	public static final String RELATION_TYPE_THREE = "3";
	/**
	 * 知识点关联表(关联系数)COEFFICIENT 0.2，		
	 */
	public static final String COEFFICIENT_DEFUALT_VAL = "0.2";
	/**
	 * 订单状态PAY_STATUS0：代付款		
	 */
	public static final String PAY_STATUS_ZERO = "0";
	/**
	 * 订单状态PAY_STATUS1：已付款
	 */
	public static final String PAY_STATUS_ONE = "1";
	/**
	 * 订单状态PAY_STATUS2：已退款	
	 */
	public static final String PAY_STATUS_TWO = "2";

	/**
	 * 审核状态  0：待审核，1：已通过，2：已驳回
	 */
	public static final String APPLY_AUDIT_STATUS = "APPLYAUDITSTATUS";
	/**
	 * 0：待审核，1：已通过，2：已驳回
	 */
	public static final String AUDIT_STATUS_WAIT = "0";

	/**
	 * 1：已通过，0：待审核，2：已驳回
	 */
	public static final String AUDIT_STATUS_PASS = "1";

	/**
	 * 2：已驳回，0：待审核，1：已通过
	 */
	public static final String AUDIT_STATUS_REJECT = "2";
	
	/**
	 * 子站组织机构session key
	 */
	public static final String SUB_SITE_ORG_KEY = "sub_site_org";

	/**
	 * 子站组织机构 默认值
	 */
	public static final String SUB_SITE_ORG_DEFAULT_VALUE = "e9rj";

	// 充值状态
	/**
	 * 0：待确认，等待支付宝、银联返回信息，1：充值成功，2：充值失败
	 */
	public static final String RECHARGE_STATUS = "RECHARGE_STATUS";
	/**
	 * 0：待确认，等待支付宝、银联返回信息，1：充值成功，2：充值失败
	 */
	public static final String RECHARGE_STATUS_WAIT = "0";

	/**
	 * 1：充值成功，0：待确认，等待支付宝、银联返回信息，2：充值失败
	 */
	public static final String RECHARGE_STATUS_SUCCESS = "1";

	/**
	 * 2：充值失败，0：待确认，等待支付宝、银联返回信息，1：充值成功
	 */
	public static final String RECHARGE_STATUS_FAIL = "2";

	// 充值方式
	/**
	 * 1：充值，2：赠送，3：积分兑换，4：退单
	 */
	public static final String RECORD_TYPE = "GO_RECHARGE_TYPE";
	/**
	 * 1：充值，2：赠送，3：积分兑换，4：退单
	 */
	public static final String RECORD_TYPE_RECHARGE = "1";
	/**
	 * 2：赠送，1：充值，3：积分兑换，4：退单
	 */
	public static final String RECORD_TYPE_GIVE = "2";
	/**
	 * 3：积分兑换，1：充值，2：赠送，4：退单
	 */
	public static final String RECORD_TYPE_EXCHANGE = "3";
	/**
	 * 4：退单，1：充值，2：赠送，3：积分兑换
	 */
	public static final String RECORD_TYPE_CHARGE_BACK = "4";
	//充值类型
	/**
	 *5：积分兑换；1:支付宝；2：银行卡；3：手机话费；4：桨卡；
	 */
	public static final String RECHARGE_TYPE_INTEGRAL="5";
	// 充值来源
	/**
	 * 11：支付宝，12：银联，21：系统，22:积分兑换，41：现金退款，42：优惠券退款
	 */
	public static final String RESOURCE = "GO_RECHARGE_SOURCE";
	/**
	 * 11：支付宝，12：银联，21：系统，22:积分兑换，41：现金退款，42：优惠券退款
	 */
	public static final String RESOURCE_ALIPAY = "11";
	/**
	 * 12：银联，11：支付宝，21：系统，22:积分兑换，41：现金退款，42：优惠券退款
	 */
	public static final String RESOURCE_BANK = "12";
	/**
	 * 21：系统，11：支付宝，12：银联，41：现金退款，42：优惠券退款
	 */
	public static final String RESOURCE_SYSTEM = "21";
	/**
	 * 22:积分兑换，42：优惠券退款，11：支付宝，12：银联，21：系统，41：现金退款
	 */
	public static final String RESOURCE_SYSTEM_INTEGRAL = "22";
	/**
	 * 41：现金退款，11：支付宝，12：银联，21：系统，22:积分兑换，42：优惠券退款
	 */
	public static final String RESOURCE_CASH_BACK = "41";
	/**
	 * 42：优惠券退款，11：支付宝，12：银联，21：系统，22:积分兑换，41：现金退款
	 */
	public static final String RESOURCE_COUPONS = "42";
	
	
	//积分得分来源
	/**
	 * 1：上传资源，2：下载资源，3：资源被下载，4：优惠券兑换积分，5：完善题目解析，6：内容纠错，7：系统任务，8：积分兑换优惠券，9：现金兑换积分，10：精选评论
	 */
	public static final String INTEGRAL_SOURCE = "INTEGRAL_SOURCE";
	/**
	 * 1：上传资源，2：下载资源，3：资源被下载，4：优惠券兑换积分，5：完善题目解析，6：内容纠错，7：系统任务，8：积分兑换优惠券，9：现金兑换积分，10：精选评论
	 */
	public static final String INTEGRAL_SOURCE_UP = "1";
	/**
	 * 2：下载资源，1：上传资源，3：资源被下载，4：优惠券兑换积分，5：完善题目解析，6：内容纠错，7：系统任务，8：积分兑换优惠券，9：现金兑换积分，10：精选评论
	 */
	public static final String INTEGRAL_SOURCE_DOWN = "2";
	/**
	 * 3：资源被下载，1：上传资源，2：下载资源，4：优惠券兑换积分，5：完善题目解析，6：内容纠错，7：系统任务，8：积分兑换优惠券，9：现金兑换积分，10：精选评论
	 */
	public static final String INTEGRAL_SOURCE_DOWN_OTHER = "3";
	/**
	 * 4：优惠券兑换积分，1：上传资源，2：下载资源，3：资源被下载，5：完善题目解析，6：内容纠错，7：系统任务，8：积分兑换优惠券，9：现金兑换积分，10：精选评论
	 */
	public static final String INTEGRAL_SOURCE_COUPONS_SCORE = "4";
	/**
	 * 5：完善题目解析，1：上传资源，2：下载资源，3：资源被下载，4：优惠券兑换积分，6：内容纠错，7：系统任务，8：积分兑换优惠券，9：现金兑换积分，10：精选评论
	 */
	public static final String INTEGRAL_SOURCE_QUESTION = "5";
	/**
	 * 6：内容纠错，1：上传资源，2：下载资源，3：资源被下载，4：优惠券兑换积分，5：完善题目解析，7：系统任务，8：积分兑换优惠券，9：现金兑换积分，10：精选评论
	 */
	public static final String INTEGRAL_SOURCE_ERROR = "6";
	/**
	 * 7：系统任务，1：上传资源，2：下载资源，3：资源被下载，4：优惠券兑换积分，5：完善题目解析，6：内容纠错，8：积分兑换优惠券，9：现金兑换积分，10：精选评论
	 */
	public static final String INTEGRAL_SOURCE_SYSTEM = "7";
	/**
	 * 8：积分兑换优惠券，1：上传资源，2：下载资源，3：资源被下载，4：优惠券兑换积分，5：完善题目解析，6：内容纠错，7：系统任务，9：现金兑换积分，10：精选评论
	 */
	public static final String INTEGRAL_SOURCE_SCORE_COUPONS = "8";
	/**
	 * 9：现金兑换积分，1：上传资源，2：下载资源，3：资源被下载，4：优惠券兑换积分，5：完善题目解析，6：内容纠错，7：系统任务，8：积分兑换优惠券，10：精选评论
	 */
	public static final String INTEGRAL_SOURCE_MONEY_SCORE = "9";
	/**
	 * 10：精选评论，1：上传资源，2：下载资源，3：资源被下载，4：优惠券兑换积分，5：完善题目解析，6：内容纠错，7：系统任务，8：积分兑换优惠券，9：现金兑换积分
	 */
	public static final String INTEGRAL_SOURCE_COMMENT = "10";
	
	// 资源操作类型
	/**
	 * 1：上传，2：下载，3：收藏
	 */
	public static final String RESOURCE_OPTYPE = "RESOURCE_OPTYPE";
	/**
	 * 1：上传，2：下载，3：收藏
	 */
	public static final String RESOURCE_OPTYPE_UP = "1";
	/**
	 * 2：下载，1：上传，3：收藏
	 */
	public static final String RESOURCE_OPTYPE_DOWN = "2";
	/**
	 * 3：收藏，1：上传，2：下载
	 */
	public static final String RESOURCE_OPTYPE_COLLECT = "3";
	
	
	// 积分兑换类型
	/**
	 * 1：积分兑换优惠券，2：优惠券兑换积分，3：现金兑换积分
	 */
	public static final String EXCHANGE_TYPE = "EXCHANGE_TYPE";
	/**
	 * 1：积分兑换优惠券，2：优惠券兑换积分，3：现金兑换积分
	 */
	public static final String EXCHANGE_TYPE_SCORE_COUPONS = "1";
	/**
	 * 2：优惠券兑换积分，1：积分兑换优惠券，3：现金兑换积分
	 */
	public static final String EXCHANGE_TYPE_COUPONS_SCORE = "2";
	/**
	 * 3：现金兑换积分，1：积分兑换优惠券，2：优惠券兑换积分
	 */
	public static final String EXCHANGE_TYPE_MONEY_SCORE = "3";

	// 评论对象类型
	/**
	 * 0：课程，1：知识点，2：习题，3：资源
	 */
	public static final String COMMENT_TYPE = "COMMENT_TYPE";
	/**
	 * 0：课程，1：知识点，2：习题，3：资源
	 */
	public static final String COMMENT_TYPE_COURSE = "0";
	/**
	 * 1：知识点，0：课程，2：习题，3：资源
	 */
	public static final String COMMENT_TYPE_KNOW = "1";
	/**
	 * 2：习题，0：课程，1：知识点，3：资源
	 */
	public static final String COMMENT_TYPE_QS = "2";
	/**
	 * 3：资源，0：课程，1：知识点，2：习题
	 */
	public static final String COMMENT_TYPE_RES = "3";

	// 试卷批阅方式
	/**
	 * 1：智能批阅，2：人工批阅
	 */
	public static final String MARKING_TYPE = "MARKING_TYPE";
	/**
	 * 1：智能批阅，2：人工批阅
	 */
	public static final String MARKING_TYPE_AUTO = "1";
	/**
	 * 2：人工批阅，1：智能批阅
	 */
	public static final String MARKING_TYPE_PERSON = "2";

	public static final int zero = 0;
	public static final int one = 1;
	public static final int two = 2;
	public static final int three = 3;
	public static final int four = 4;
	public static final int five = 5;
	public static final int six = 6;
	public static final int seven = 7;
	public static final int eight = 8;
	public static final int nine = 9;
	public static final int ten = 10;
	public static final int eleven = 11;
	public static final int twelve = 12;
	public static final int thirteen = 13;
	public static final int fourteen = 14;
	public static final int fivteen = 15;
	public static final int sixteen = 16;
	public static final int seventeen = 17;
	public static final int eighteen = 18;
	public static final int ninteen = 19;
	public static final int twenty = 20;

	public static final int twenty_five = 25;

	public static final int int_256 = 256;
	
	public static final int int_512 = 512;

	public static final int int_1024 = 1024;

}
