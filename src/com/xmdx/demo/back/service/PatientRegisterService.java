package com.xmdx.demo.back.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.e9rj.platform.common.BaseConstants;
import com.e9rj.platform.common.Encrypt;
import com.e9rj.platform.common.OperateIP;
import com.e9rj.platform.common.services.BusinessServices;
import com.e9rj.platform.util.SessionUtil;
import com.e9rj.platform.util.SsoUtil;
import com.xmdx.demo.back.dao.CourseDao;
import com.xmdx.demo.back.dao.MessageSendDao;
import com.xmdx.demo.back.dao.MoneyRecordDao;
import com.xmdx.demo.back.dao.ZkgkConstants;
import com.xmdx.demo.back.dao.ZkgkUtil;
//import com.e9rj.zkgk.backstage.learncourse.dao.CourseDao;
//import com.e9rj.zkgk.backstage.message.dao.MessageSendDao;
//import com.e9rj.zkgk.system.ZkgkConstants;
//import com.e9rj.zkgk.util.ZkgkUtil;
//import com.e9rj.zkgk.website.platform.dao.MoneyRecordDao;
import com.xmzy.frameext.business.service.annotate.Service;
import com.xmzy.frameext.cache.CachedFactory;
import com.xmzy.frameext.simpledb.DBConn;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.framework.context.ActionContext;
import com.xmzy.framework.core.FrameworkConstant;
import com.xmzy.framework.log.businesslog.BusinessLogBean;
import com.xmzy.framework.log.businesslog.BusinessLogger;
import com.xmzy.framework.service.MessageService;
@Service(name="patient.register")
public class PatientRegisterService extends BusinessServices {
	
	private static final String tableName = "PATIENT";
	private static final String keyField = "PATIENT_ID";
	
	private static final String TABLE_REGUSER = "GO_REGUSER";
	private static final String TABLE_TSOP = "TS_OP";
	private static final String KEY_FIELD = "PERSON_ID";
	private static final String KEY_APP_CODE = "APP_CODE";
	private static final String KEY_OPNO = "OPNO";
	private static final String KEY_PWD = "PWD";
	private static final String KEY_USER_INTEGRAL = "USER_INTEGRAL_ID";
	private static final String CREATE_BY = "CREATE_BY";
	private static final String CREATE_TIME = "CREATE_TIME";
	private static final String MSG = "msg";
	
//	private MoneyRecordDao moneyRecordDao = new MoneyRecordDao();
//	private final MessageSendDao messageSendDao = new MessageSendDao();
//	private final CourseDao courseDao = new CourseDao();

	// 打开用户注册页面
	@Override
	public int init(ActionContext ac) throws Exception {
		
		String opNo = request.getParameter("opNo");		
		DBDYPO regUser = null;
		DBDYPO po = null;
		
//		// 取出子站参数据，没有测试子站参数值的，不能注册会员
//		String subSiteOrg = request.getParameter(ZkgkConstants.SUB_SITE_ORG_KEY);		
//		ZkgkUtil.getStationByOrgcode(ac);
//		if(StringUtils.isBlank(subSiteOrg)) {
//			ac.setErrorContext("", "没有设置子站ID(sub_site_org)参数值");
//			return CONST_RESULT_ERROR;
//		}
//		ac.setStringValue(ZkgkConstants.SUB_SITE_ORG_KEY, subSiteOrg);
		
		Connection conn = null;
		
		try {
			conn = DBConn.getConnection("SSOdbService");
			if(StringUtils.isNotBlank(opNo)) {
				po = queryOpByOpno(conn, opNo);
			}
	
			// 操作员（ts_op）表为空，传向注册页面。注册为平台用户，及操作员。
			if(null == po) {
				ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/back/pregister.html");
				return CONST_RESULT_SUCCESS;
			}
			
			String personId = ""+ po.get(KEY_FIELD);
	
			 regUser = queryGoUserById(conn, personId);
			
		} catch (Exception e) {
			MessageService.errString("", e);
		} finally {
			DBConn.closeConnection(conn);
		}
		
		if(null == regUser) {
			// 操作员注册为平台用户，直接打开注册页面
			ac.setObjValue("OP_BEAN", po);
//			ac.setObjValue(ZkgkConstants.COURSE_CODE, request.getParameter(ZkgkConstants.COURSE_CODE));
			ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/back/pregister.html");
		} else {
			
			String appCode = "" + po.get(KEY_APP_CODE);
			if(!ZkgkUtil.authorityAppCode(appCode)) {
				CachedFactory.getCached().add("CANCEL_AUTH", true);
			}
			
			// 己经注册成为平台用户及操作员的，直接打开我的课程页面
			ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/back/pregisted.html");
		}
		
		return CONST_RESULT_SUCCESS;
	}
//	public int gotoAgreement(ActionContext ac){
//		String subSiteOrg = request.getParameter(ZkgkConstants.SUB_SITE_ORG_KEY);
//		ZkgkUtil.getStationByOrgcode(ac);
//		ac.setStringValue(CONST_FORMNAME, "com/e9rj/zkgk/website/user/service_agreement.html");
//		return CONST_RESULT_SUCCESS;
//	}	
	
	/**
	 * 用户注册信息保存
	 * @param ac
	 * @return
	 * @throws Exception 
	 */
	public int register(ActionContext ac) throws Exception {
		
		String personId = request.getParameter(KEY_FIELD);
		
		// 注册结果
		JSONObject jsonObj = null;
		
//		if(StringUtils.isNotBlank(personId)) {
//			// 己有准考证用户注册
//			jsonObj = updateRegister(ac);
//		} else {
//			// 新用户注册
			jsonObj = newRegister(ac);
//		}

		/*
		 * create by lhj 141020 beg
		 * 注册成功后可以增加一条消息系统通知
		 * */
		if (null != jsonObj) {
			if ((Boolean) jsonObj.get(CONST_BIZRESULT)) {
				if (StringUtils.isBlank(personId)) {
					personId = jsonObj.get("PERSON_ID").toString();
				}
				DBDYPO tsOp = ZkgkUtil.getSsoPoById("TS_OP", "PERSON_ID", personId);
				String param = BaseConstants.getGlobalValue("1335");
				// 欢迎您开启金奖助学之旅，请点击查看
				// |如何学习课程？;http://www.goldoar.com/goldoar/index.php?m=content&c=index&a=show&catid=85&id=31;|
//				int result = messageSendDao.addSystemMsg2(ac, new String[] { tsOp.get(ZkgkConstants.OPNO).toString() }, getContent(param), tsOp);
//				if (0 == result) {
//					return CONST_RESULT_ERROR;
//				}
			}

		}
		/*
		 * create by lhj 141020 end
		 * */

		ac.getHttpResponse().getWriter().println(jsonObj.toJSONString());
		
		return CONST_RESULT_AJAX;
	}

	// 获取注册成功后系统消息
	public String getContent(String param) {
		// 欢迎您开启金奖助学之旅，请点击查看
		// |如何学习课程？;http://www.goldoar.com/goldoar/index.php?m=content&c=index&a=show&catid=85&id=31;|

		if (StringUtils.isBlank(param)) {
			return "";
		}
		
		if (!param.contains("|")) {
			return param;
		}

		StringBuffer content = new StringBuffer();

		String[] arr = param.split("\\|");

		content.append(arr[0]);

		int arrLen = arr.length;

		for (int i = 1; i < arrLen; i++) {
			String[] carr = arr[i].split(";");

			if (null == carr) {
				continue;
			}

			content.append("<a ");

			if (carr.length > 1) {

				content.append("href='").append(carr[1]).append("' target='_blank'");

			} else {

				content.append("href='javascript:void(0);'");

			}

			content.append(">").append(carr[0]).append("<a>");
		}
		
		return content.toString();
	}

	// 跳转成功提示页
	public int gosuccess(ActionContext ac) {
		System.out.println("enter gosuccess");
//		String courseCode = request.getParameter(ZkgkConstants.COURSE_CODE);
		String opno = request.getParameter("_username");
		String pwd = request.getParameter("_passwrod");
//		String subSiteOrg = request.getParameter(ZkgkConstants.SUB_SITE_ORG_KEY);
		
		String casUrl = BaseConstants.getGlobalValue("5");
		if(!StringUtils.endsWith(casUrl, "/")) {
			casUrl += "/";
		}
		casUrl += "login";
//		ZkgkUtil.getStationByOrgcode(ac);
		ac.setStringValue("casLoginUrl", casUrl);
		ac.setStringValue(KEY_OPNO, opno);
		ac.setStringValue(KEY_PWD, pwd);
//		ac.setStringValue(ZkgkConstants.SUB_SITE_ORG_KEY, subSiteOrg);
//		ac.setStringValue(ZkgkConstants.COURSE_CODE, courseCode);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/back/pregister_success.html");
		return CONST_RESULT_SUCCESS;
	}

	@Override
	public int query(ActionContext ac) throws Exception {
		return 0;
	}

	@Override
	public int save(ActionContext ac) throws Exception {
		return 0;
	}

	@Override
	public int delete(ActionContext ac) throws Exception {
		return 0;
	}

	@Override
	public int goTo(ActionContext ac) throws Exception {
		return 0;
	}

	@Override
	public int insertExportData(ActionContext ac) throws Exception {
		return 0;
	}
	
	/**
	 * 己有准考证号学生注册
	 * @param ac
	 * @return
	 */
//	private JSONObject updateRegister(ActionContext ac) {
//		JSONObject jsonObj = null;
//		DBDYPO tsop = new DBDYPO("TS_OP", KEY_FIELD, ac.getHttpRequest());
//		DBDYPO user = new DBDYPO("GO_REGUSER", KEY_FIELD, ac.getHttpRequest());
//		DBDYPO integral = new DBDYPO("TB_USER_INTEGRAL", KEY_USER_INTEGRAL, request);
//		
//		String personId = "" + user.get(KEY_FIELD);
//		
//		int addMoney = getAddMoney(personId);
//		
//		int idLen = 20;
//		
//		// 更新操作用户信息appCode权限，没有该应用权限则进行授权。
//		Connection ssoconn = null;
//		Connection conn = null;
//		int result = 0;
//		try {
//			ssoconn = DBConn.getConnection(ZkgkConstants.ID_SSODBSERVICE);
//			conn = DBConn.getConnection();
//			ssoconn.setAutoCommit(false);
//			conn.setAutoCommit(false);
//			
//			jsonObj = updateApp(ac, ssoconn, personId);
//
//			if(!jsonObj.getBooleanValue(CONST_BIZRESULT)) {
//				return jsonObj;
//			}
//
//			/**
//			 * create by lhj 1020
//			 */
//			tsop.set(KEY_FIELD, personId);
//			DBDYPO[] tsopArr = DBDYDao.selectByID(ssoconn, tsop);
//			if (null != tsopArr) {
//				tsop = tsopArr[0];
//				tsop.setTableName("TS_OP");
//				tsop.setKeyField(KEY_FIELD);
//				// 判断是否是zkxj用户
////				if (!courseDao.isExistSubSation(ac, SessionUtil.getOrgCode(ac))) {
////					tsop.set("DATASOURCE", "zkxj");
////				} else {
////					// 若不是zkxj用户且数据来源为空；则取组织机构；
////					if (null == tsop.get("DATASOURCE")) {
////						tsop.set("DATASOURCE", tsop.get("ORGCODE"));
////					}
////				}
//				result = DBDYDao.update(ssoconn, tsop);
//				if (0 == result) {
//					jsonObj.put(CONST_BIZRESULT, false);
//					jsonObj.put(MSG, "注册失败");
//
//					conn.rollback();
//					ssoconn.rollback();
//					return jsonObj;
//				}
//			}
//			/**
//			 * create by lhj 1020
//			 */
//
//			String opNo =  SsoUtil.getSsoAttribute(ac, KEY_OPNO);
//			user.set("IS_EXPERIENCE", "1");
//			user.set(CREATE_BY, opNo);
//			user.set(CREATE_TIME, new java.sql.Timestamp(System.currentTimeMillis()));
//			// 增加平台用户信息
//			result = DBDYDao.insert(ssoconn, user);
//			
//			if(0 == result) {
//				jsonObj.put(CONST_BIZRESULT, false);
//				jsonObj.put(MSG, "注册失败");
//				
//				conn.rollback();
//				ssoconn.rollback();
//				return jsonObj;
//			} 
			
			// 给帐户赠送优惠券
//			result = addMoney(conn,  personId);
//			if(0 == result) {
//				jsonObj.put(CONST_BIZRESULT, false);
//				jsonObj.put(MSG, "注册失败");
//				conn.rollback();
//				ssoconn.rollback();
//			} 
//			//添加赠送金额为0则不进行添加充值记录       ysp20141014
//			if(addMoney!=0){
//				saveMoneyRecord(ac,personId, addMoney);
//			}
//				
//			// personId没有创建积分信息，则为其创建积分帐户信息
//			DBDYPO integ = queryIntegral(personId);
//			
//			if(null == integ && StringUtils.isNotBlank(personId)) {
//								
//				integral.set(KEY_USER_INTEGRAL, super.genIdString("", idLen));
//				integral.set(KEY_FIELD, personId);
//				integral.set("INTEGRAL", Integer.valueOf("0"));
//				integral.set("ENT_ID", ZkgkConstants.SUB_SITECODE_E9RJ);
//				integral.set(CREATE_BY, personId);
//				integral.set(CREATE_TIME, new java.sql.Timestamp(System.currentTimeMillis()));
//				
//				result = DBDYDao.insert(conn, integral);
//				if(0 == result) {
//					jsonObj.put(CONST_BIZRESULT, false);
//					jsonObj.put(MSG, "注册失败");
//					conn.rollback();
//					ssoconn.rollback();
//					return jsonObj;
//				}
//			}
			
//			jsonObj.put(CONST_BIZRESULT, true);
//			ssoconn.commit();
//			conn.commit();
//			
//		} catch (Exception e) {
//			try {
//				conn.rollback();
//				ssoconn.rollback();
//			} catch (SQLException e1) {
//				MessageService.errString("", e1);
//			}
//			MessageService.errString("", e);
//		} finally {
//			DBConn.closeConnection(ssoconn);
//			DBConn.closeConnection(conn);
//		}
//		
//		return jsonObj;
//	} 
	
	/**
	 * 新用户名注册，
	 * @param ac
	 * @return
	 * @throws Exception 
	 */
	private JSONObject newRegister(ActionContext ac) throws Exception {
		System.out.println("enter newRegister");
		int rows = 0;
		JSONObject  jsonObj = new JSONObject();
		int result = 0;
		
//		if(!jsonObj.getBooleanValue(CONST_BIZRESULT)) {
//			System.out.println("enter input0");
//			return jsonObj;
//			
//		}
		// 平台用户信息
		DBDYPO regUser = new DBDYPO("GO_REGUSER", KEY_FIELD, request);
		DBDYPO tsop = new DBDYPO(TABLE_TSOP, KEY_FIELD, request);		
		DBDYPO person = new DBDYPO("TB_PERSON", KEY_FIELD, request);
		DBDYPO patient = new DBDYPO(tableName, keyField);
		// 积分信息
//		DBDYPO integral = new DBDYPO("TB_USER_INTEGRAL", KEY_USER_INTEGRAL, request);		
		
		Connection conn = null;
		Connection ssoconn = null;
		int idLen = 20;
		int dataSource = 4;
		int one = 1;
		int zero = 0;
		String personId = super.genIdString("", idLen);
		String integralId = super.genIdString("", idLen);	
		System.out.println("enter input1");
//		int addMoney = getAddMoney(personId);
		try {
			conn = DBConn.getConnection();
			ssoconn = DBConn.getConnection("SSOdbService");
			conn.setAutoCommit(false);
			ssoconn.setAutoCommit(false);
			
			patient.set(keyField, personId);
			tsop.set(KEY_FIELD, personId);
			regUser.set(KEY_FIELD, personId);			
			person.set(KEY_FIELD, personId);
//			integral.set(KEY_USER_INTEGRAL, integralId);
			
			System.out.println("enter input2");
			String opno = "" + tsop.get(KEY_OPNO);
			String creatorKey = CREATE_BY;
			java.sql.Timestamp createTime = new java.sql.Timestamp(System.currentTimeMillis());
			
			tsop.set("OPNO_", opno);
			tsop.set("OPNAME", opno);
			tsop.set("OPLIMIT", zero);
			tsop.set("ENABLED", one);
			tsop.set("ORGCODE", "e9rj");

			String appcodestr = "";
			// 用户访问其他系统而配置
			String appcode = BaseConstants.getGlobalValue("1338");
			if(StringUtils.isNotBlank(appcode)){
				appcodestr += "," + appcode;
			}
			tsop.set(KEY_APP_CODE, FrameworkConstant.getAppCode() + appcodestr);
			// 来源于子站；
			tsop.set("DATASOURCE", "e9rj");
			tsop.set("CREATETIME", createTime);
			
			tsop.set(KEY_PWD, Encrypt.getMixMD5(opno, "" + tsop.get(KEY_PWD)));
			
			person.set(creatorKey,  personId);
			person.set("PERSON_NAME", opno);
			person.set(CREATE_TIME, createTime);
			person.set("DATASOURCE", dataSource);
			
			regUser.set("IS_EXPERIENCE", "1");
			regUser.set("IS_ENABLED", one);
			regUser.set(creatorKey, personId);
			regUser.set(CREATE_TIME, createTime);
			
			
			
//			integral.set(KEY_FIELD, personId);
//			integral.set("INTEGRAL", Integer.valueOf("0"));
//			integral.set("ENT_ID", request.getParameter("ORGCODE"));
//			integral.set(creatorKey, personId);
//			integral.set(CREATE_TIME, createTime);
			String registerFail = "注册失败！";
			
			// 操作操作员信息
			rows = DBDYDao.insert(ssoconn, tsop);
			result = DBDYDao.insert(ac.getConnection(), patient);
			if(rows == zero) {
				jsonObj.put(CONST_BIZRESULT, false);
				jsonObj.put(MSG, registerFail);
				return jsonObj;
			}
			// 创建人员信息
			rows = DBDYDao.insert(ssoconn, person);
			if(rows == zero) {
				
				jsonObj.put(CONST_BIZRESULT, false);
				jsonObj.put(MSG, registerFail);
				ssoconn.rollback();
				conn.rollback();
				return jsonObj;
			}
			// 创建平台用户信息
			rows = DBDYDao.insert(ssoconn, regUser);
			if(rows == zero) {
				jsonObj.put(CONST_BIZRESULT, false);
				jsonObj.put(MSG, registerFail);
				ssoconn.rollback();
				conn.rollback();
				return jsonObj;
			} 
			// 为平台用户赠送优惠券
//			rows = addMoney(conn, personId);
//			if(rows == zero) {
//				jsonObj.put(CONST_BIZRESULT, false);
//				jsonObj.put(MSG, registerFail);
//				ssoconn.rollback();
//				conn.rollback();
//				return jsonObj;
//			} 
//			// 创建用户积分信息
//			rows = DBDYDao.insert(conn, integral);
//			if(rows == zero) {
//				jsonObj.put(CONST_BIZRESULT, false);
//				jsonObj.put(MSG, registerFail);
//				ssoconn.rollback();
//				conn.rollback();
//				return jsonObj;
//			} 
//			//添加赠送金额为0则不进行添加充值记录       ysp20141014
//			if(addMoney!=0){
//				saveMoneyRecord(ac, personId, addMoney);
//			}
			jsonObj.put(CONST_BIZRESULT, true);
			jsonObj.put(KEY_FIELD, personId);
			conn.commit();
			ssoconn.commit();
				
		} catch (SQLException e) {
			try {
				conn.rollback();
				ssoconn.rollback();
			} catch (SQLException e1) {
//				MessageService.errString("用户注册失败，回滚异常！", e1);
			}
			
//			MessageService.errString("注册用户异常", e);
			jsonObj.put(CONST_BIZRESULT, false);
			jsonObj.put(MSG, "数据库链接异常，注册失败！");
		} finally {
			DBConn.closeConnection(ssoconn);
			DBConn.closeConnection(conn);
		}
		
		return jsonObj;
	}
	
//	private JSONObject registerValidate(ActionContext ac) {
//		
//		JSONObject  jsonObj = new JSONObject();
//		
//		String orgcode = "e9rj";
//		if(StringUtils.isBlank(orgcode)) {
//			jsonObj.put(CONST_BIZRESULT, false);
////			jsonObj.put(MSG, "没有设置子站ID(sub_site_org)参数值，注册失败！");
//			return jsonObj;
//		}
//		
//		DBDYPO org = queryOrgPo(orgcode);
//		if(null == org) {
//			jsonObj.put(CONST_BIZRESULT, false);
//			jsonObj.put(MSG, "子站(" + orgcode + ")信息不存在，不能注册为该子站会员！");
//			return jsonObj;
//		}
//		
//		if(!"1".equals("" + org.get("ENABLED"))) {
//			jsonObj.put(CONST_BIZRESULT, false);
//			jsonObj.put(MSG, org.get("orgname") + "子站未激活，注册失败！");
//			return jsonObj;
//		}
//		
//		
//		String jsonStr = opnoCheck();
//		JSONArray jsonArray = JSONArray.parseArray(jsonStr);
//		if(!(Boolean)jsonArray.get(0)) {
//			jsonObj.put(CONST_BIZRESULT, false);
//			jsonObj.put(MSG, jsonArray.get(1));
//			return jsonObj;
//		} 
//		
//		jsonStr = codeCheck(ac);
//		jsonArray = JSONArray.parseArray(jsonStr);
//		if((Boolean)jsonArray.get(0)) {
//			jsonObj.put(CONST_BIZRESULT, true);
//		} else {
//			jsonObj.put(CONST_BIZRESULT, false);
//			jsonObj.put(MSG, jsonArray.get(1));
//		}
//		
//		return jsonObj;
//	}
	

	/**
	 * 用户是否己存在判断 
	 * @param ac
	 * @param opNo
	 * @return
	 * @throws Exception
	 */
	private boolean exists(Connection ac, String opNo) throws Exception {
		
		if(StringUtils.isBlank(opNo)) {
			logger.debug("用户名称为空，判断用户是否存在返回false");
			return false;
		}
		
		DBDYPO dbdypo = queryOpByOpno(ac, opNo);
	
		if (dbdypo == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * 查询人员ID是否己注册成显平台用户（go_reguser表中是否有记录）
	 * @param conn
	 * @param personId
	 * @return
	 */
	private DBDYPO queryGoUserById(Connection conn, String personId) {
		DBDYPO po = new DBDYPO(TABLE_REGUSER, KEY_FIELD);
		po.set(KEY_FIELD, personId);
		DBDYPO[] pos = DBDYDao.selectByID(conn, po);
		
		if(pos.length > 0) {
			return pos[0];
		}
		return null;
	}
	
	/**
	 * 取操作用户
	 * @param ac
	 * @param personId
	 * @return
	 */
	private DBDYPO queryOp(String personId) {
		
		DBDYPO po = new DBDYPO(TABLE_TSOP, KEY_FIELD);
		po.set(KEY_FIELD, personId);
		Connection  conn = DBConn.getConnection(ZkgkConstants.ID_SSODBSERVICE);
		try {
			
			DBDYPO[] pos = DBDYDao.selectByID(conn, po);
			po = pos.length > 0 ? pos[0] : null;
		} catch(Exception e) {
//			MessageService.errString("查询ts_op中" + personId + "用户信息时出错！", e);
		} finally  {
			DBConn.closeConnection(conn);
		}
		return po;
	}
	
	private DBDYPO queryOpByOpno(Connection conn, String opNo) {
		if(StringUtils.isBlank(opNo)) {
			return null;
		}
		
		StringBuilder sql = new StringBuilder("SELECT * FROM TS_OP T WHERE T.OPNO='");
		sql.append(opNo).append("'");
		
		DBDYPO[] pos = DBDYDao.selectBySQL(conn, sql.toString());
		
		if(pos.length > 0) {
			return pos[0];
		}
		return null;
 	}
	
	/**
	 * 更新ts_op操作员信息，授予zkgk应用权限
	 * @param conn
	 * @param personId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private JSONObject updateApp(ActionContext ac, Connection conn, String personId) {
		JSONObject jsonObj = new JSONObject();
		DBDYPO op = queryOp(personId);
		if(null == op) {
			jsonObj.put(CONST_BIZRESULT, false);
			jsonObj.put(MSG, "操作用户信息为空，注册失败！");
			logger.warn("ts_op操作用户(" + personId + ")信息为空，注册失败！");
			return jsonObj;
		}
		
		op.setTableName(TABLE_TSOP);
		op.setKeyField(KEY_FIELD);
		
		String appcodestr = "";
		// 用户访问其他系统而配置
		String appcode = BaseConstants.getGlobalValue("1338");
		if (StringUtils.isNotBlank(appcode)) {
			appcodestr += "," + appcode;
		}

		// 判断用户是否用权限，没有的话，要赋于自考过考应用的权限
		String haveAppCode = "" + op.get(KEY_APP_CODE);
		String appCode = FrameworkConstant.getAppCode();	
		
		if(!ZkgkUtil.authorityAppCode(haveAppCode)) {
			haveAppCode += "," + appCode + appcodestr;
			op.set(KEY_APP_CODE, haveAppCode);
			
			int result = DBDYDao.update(conn, op);

			// 重置缓存中该对象的值
			Map<String, Object> map = (Map<String, Object>) ac.getObjValue("ssoUserInfo");
			if (map != null) {
				map.put(KEY_APP_CODE, haveAppCode);
				ac.setObjValue("ssoUserInfo", map);
			}

			if(result == 0) {
//				logger.error(MessageService.getMessage("LOG_UPDATE_OP_FAILURE", personId, appCode));
				jsonObj.put(CONST_BIZRESULT, false);
//				jsonObj.put(MSG, MessageService.getMessage("MSG_UPDATE_OP_FAILURE"));
			} else {
				jsonObj.put(CONST_BIZRESULT, true);
			}
		} else {
			jsonObj.put(CONST_BIZRESULT, true);
		}
		return jsonObj;
	}
	
//	private int addMoney(Connection conn,String personId) {
//		DBDYPO po = new DBDYPO("GO_ACCOUNT_MANAGE", KEY_FIELD, request);
//		po.set(KEY_FIELD, personId);
//		
//		int rows = 0;
////		int addMoney = getAddMoney(personId);
////		BigDecimal money = new BigDecimal(addMoney); 
//		
//		String personMoney = "PRESENT_MONEY";
//		
//		
//		DBDYPO[] pos = DBDYDao.selectByID(conn, po);
//		
//		if(pos.length > 0) {
//			po = pos[0];
//			po.setKeyField(KEY_FIELD);
//			if(null !=po.get(personMoney)) {
//				money = money.add((BigDecimal)po.get(personMoney));
//			}
//			
//			po.set(personMoney, money);
//			po.set("RECHARGE_MONEY", 0);
//			rows = DBDYDao.update(conn, po);
//		} else {
//			po.set(personMoney, money);
//			po.set(CREATE_BY, personId);
//			po.set(CREATE_TIME, new java.sql.Timestamp(System.currentTimeMillis()));
//			po.set("UPDATE_BY", personId);
//			po.set("UPDATE_TIME", new java.sql.Timestamp(System.currentTimeMillis()));
//			
//			rows = DBDYDao.insert(conn, po);
//		}
//		
//		return rows ;
//	}
	
	/**
	 * 邮箱地址是否可用验证
	 * @param ac
	 * @return
	 * @throws IOException
	 */
	public int opnoValidate(ActionContext ac) throws IOException {
		
		String jsonString = opnoCheck();
		
		ac.getHttpResponse().getWriter().println(jsonString);
		
		return CONST_RESULT_AJAX;
	}
	
	/**
	 * 验证码一致性校验
	 * @param ac
	 * @return
	 * @throws IOException
	 */
	public int codeValidate(ActionContext ac) throws IOException {
		
		String jsonString = codeCheck(ac);
		
		ac.getHttpResponse().getWriter().println(jsonString);
		
		return CONST_RESULT_AJAX;
		
	}
	
	/**
	 * 用户名校验
	 * @param ac
	 * @return
	 * @throws IOException
	 */
	private String opnoCheck() {
		String opno = request.getParameter(KEY_OPNO);
		String jsonString = "";
		if(StringUtils.isBlank(opno)) {
			jsonString = "[false,\"* 请输入邮箱地址\"]";
		} else {
			Connection conn = null;
			
			boolean exists = false;
			try {
				conn = DBConn.getConnection(ZkgkConstants.ID_SSODBSERVICE);
				exists = exists(conn, opno);
			} catch (Exception e) {
//				MessageService.errString("", e);
			}finally {
				DBConn.closeConnection(conn);
			}
			jsonString = exists ? "[false,\"* 此用户名已被其他人使用\"]" : "[true]";
		}
		return jsonString;
	}
	
	private String codeCheck(ActionContext ac) {
		String code = request.getParameter("CHECKCODE");
		String jsonString = "";
		
		if(StringUtils.isBlank(code)) {
			jsonString = "[false, \"* 请输入验证码\"]";
		} else {
			String sessionCheckCode = "" + ac.getHttpSession().getAttribute("LoginValidateNumber");
			
			jsonString = code.equals(sessionCheckCode) ? "[true]" : "[false, \"* 验证码不正确\"]";
			
		}
		return jsonString;
	}
	
	/**
	 * 验证用户名是否己存在  
	 * @param ac
	 * @return true:存在/空值  false 不存在
	 * @throws Exception
	 */
	@Deprecated
	public int checkExists(ActionContext ac) throws Exception {
		String fieldId = ac.getHttpRequest().getParameter("fieldId");
		String fieldValue = ac.getHttpRequest().getParameter("fieldValue");
		if(StringUtils.isBlank(fieldValue)) {
//			setMessage(ac, "true");
		}
		
		Connection conn = null;
		
		
		StringBuilder  result = new StringBuilder("[\""); 
		String dataSplit = "\",";
		
		boolean exists = false;
		try {
			conn = DBConn.getConnection(ZkgkConstants.ID_SSODBSERVICE);
			exists = exists(conn, fieldValue);
		} catch (Exception e) {
//			MessageService.errString("", e);
		}finally {
			DBConn.closeConnection(conn);
		}
		

		if (exists) {
			result.append(fieldId).append(dataSplit).append(false).append("]");
		} else {
			result.append(fieldId).append(dataSplit).append(true).append("]");
		}
		
		ac.getHttpResponse().getWriter().write(result.toString());
		
		return CONST_RESULT_AJAX;
	}

	// 匹配验证码是否正确
	@Deprecated
	public int checkCode(ActionContext ac) throws IOException {
		
		String fieldId = ac.getHttpRequest().getParameter("fieldId");
		String fieldValue = ac.getHttpRequest().getParameter("fieldValue");
		String sessionCheckCode = "" + ac.getHttpSession().getAttribute("LoginValidateNumber");
		
		StringBuilder  result = new StringBuilder("[\""); 
		String dataSplit = "\",";
		
		if(sessionCheckCode.equals(fieldValue)) {
			result.append(fieldId).append(dataSplit).append(true).append("]");
		} else {
			result.append(fieldId).append(dataSplit).append(false).append("]");
		}
		
		ac.getHttpResponse().getWriter().write(result.toString());
		
		return CONST_RESULT_AJAX;
	}
	
//	private void saveMoneyRecord(ActionContext ac, String personId,  double money) throws Exception {
//		DBDYPO rec = new DBDYPO();
//		String moneyTableName = "GO_MONEY_RECORD";
//		String moneyKeyField = "RECORD_ID";
//		String msg = null;
//		boolean  flag = moneyRecordDao.save4Coupon(ac.getConnection(), personId, money, rec);
//		
//		String keyVal = null == rec.get(moneyKeyField) ? " " : rec.get(moneyKeyField).toString() ;
//		
//		if(flag) {
//			msg = MessageService.getMessage("LOG_MONEY_SUCCESS", new String[] { personId, "赠送", String.valueOf(money), "" });
//			BusinessLogger.log(new BusinessLogBean(LOGLEVEL_I, "O01", moneyTableName, keyVal, INSERT, null, personId, " ", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), OperateIP.getIpAddress(ac.getHttpRequest()), msg + "，主键：【" + (StringUtils.isBlank(keyVal) ? "" : keyVal) + "】", null, null, null, "default"));
//		} else {
//			msg = MessageService.getMessage("LOG_MONEY_FAILURE", new String[] { personId, "赠送", String.valueOf(money), "" });
//			BusinessLogger.log(new BusinessLogBean(LOGLEVEL_W, "O01", moneyTableName, keyVal, INSERT, null, personId, " ", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), OperateIP.getIpAddress(ac.getHttpRequest()), msg + "，主键：【" + (StringUtils.isBlank(keyVal) ? "" : keyVal) + "】", null, null, null, "default"));
//		}
//	}
	
	/**
	 * 根据组织机构ID,查询机构信息
	 * @param orgCode 机构ID
	 * @return
	 */
//	private DBDYPO queryOrgPo(String orgCode) {
//		if(StringUtils.isBlank(orgCode)) {
//			return null;
//		}
//		
//		Connection conn = null;
//		DBDYPO org = null;
//		try {
//			conn = DBConn.getConnection(ZkgkConstants.ID_SSODBSERVICE);
//			DBDYPO[] pos = DBDYDao.selectBySQL(conn, "SELECT * FROM TS_ORG T WHERE T.ORGCODE='"+ orgCode + "'");
//			if(pos.length > 0 )	{
//				org = pos[0];
//			}
// 		} catch (Exception e) {
////			MessageService.errString("根据机构ID" + orgCode + "查询机构信息时发生异常！", e);
//		} finally {
//			DBConn.closeConnection(conn);
//		}
//		return org;
//	}
	
	/**
	 * 查询指定人员的积分帐户信息
	 * @param personId
	 * @return
	 */
	private DBDYPO queryIntegral(String personId) {
		if(StringUtils.isBlank(personId)) {
			return null;
		}
		
		Connection conn = null;
		DBDYPO integral = null;
		try {
			conn = DBConn.getConnection();
			DBDYPO[] pos = DBDYDao.selectBySQL(conn, "SELECT * FROM TB_USER_INTEGRAL T WHERE T.PERSON_ID='" + personId + "'");
			if(pos.length > 0 ) {
				integral = pos[0];
			}
		} catch (Exception e) {
//			MessageService.errString("根据人员ID（" + personId + "）查询该用户的积分信息时，发生异常！", e);
		} finally {
			DBConn.closeConnection(conn);
		}
		return integral;
	}
	
	/**
	 * 密码正确性验证
	 * 
	 * @param ac
	 * @return
	 * @throws IOException
	 */
	public int pwdValidate(ActionContext ac) throws IOException {
		String result = "0";
		String pwd = ac.getHttpRequest().getParameter(KEY_PWD);
		String opno = SsoUtil.getSsoAttribute(ac, ZkgkConstants.OPNO);
		DBDYPO tsOp = ZkgkUtil.getSsoPoById( TABLE_TSOP, ZkgkConstants.OPNO, opno);
		if (Encrypt.getMixMD5(opno, pwd).equals(tsOp.get(KEY_PWD).toString())) {
			result = "1";
		}
		ac.getHttpResponse().getWriter().println(result);
		return CONST_RESULT_AJAX;

	}
	
//	private int getAddMoney(String personId) {
//		String moneyStr = BaseConstants.getGlobalValue("1319", "1");		
//		int addMoney  = 1;
//		if(StringUtils.isNumeric(moneyStr)) {			
//			 try {
//				addMoney = Integer.parseInt(moneyStr);
//			} catch (NumberFormatException e) {
//				
//				MessageService.errString("用户(" + personId + ")补充注册时，取赠送优惠券系统参数(1319),其值在转换为整数时发生异常!默认赠送金额1", e);
//			}
//		} else {
//			logger.debug("用户(" + personId + ")补充注册时，取赠送优惠券金额系统参数(1319)为空，默认赠送金额1");
//		}
//		return addMoney;
//	}

}
