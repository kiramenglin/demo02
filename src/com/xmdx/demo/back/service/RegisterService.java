package com.xmdx.demo.back.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.e9rj.platform.common.BaseConstants;
import com.e9rj.platform.common.Encrypt;
import com.e9rj.platform.common.services.BusinessServices;
import com.xmdx.demo.back.dao.ZkgkConstants;
import com.xmdx.demo.back.dao.ZkgkUtil;
import com.xmzy.frameext.cache.CachedFactory;
import com.xmzy.frameext.simpledb.DBConn;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.framework.context.ActionContext;
import com.xmzy.framework.core.FrameworkConstant;
import com.xmzy.framework.service.MessageService;

public class RegisterService extends BusinessServices {
	private static final String TABLE_REGUSER = "USER";
	private static final String TABLE_TSOP = "TS_OP";
	private static final String KEY_FIELD = "PERSON_ID";
	private static final String KEY_APP_CODE = "APP_CODE";
	private static final String KEY_OPNO = "OPNO";
	private static final String KEY_PWD = "PWD";
	private static final String KEY_USER_INTEGRAL = "USER_INTEGRAL_ID";
	private static final String CREATE_BY = "CREATE_BY";
	private static final String CREATE_TIME = "CREATE_TIME";
	private static final String MSG = "msg";
	@Override
	public int delete(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int goTo(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int init(ActionContext ac) throws Exception {
		String opNo = request.getParameter("opNo");		
		DBDYPO regUser = null;
		DBDYPO po = null;
		Connection conn = null;
		
		try {
			conn = DBConn.getConnection(ZkgkConstants.ID_SSODBSERVICE);
			if(StringUtils.isNotBlank(opNo)) {
				po = queryOpByOpno(conn, opNo);
			}
	
			// 操作员（ts_op）表为空，传向注册页面。注册为平台用户，及操作员。
			if(null == po) {
				ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/back/register.html");
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
			ac.setObjValue(ZkgkConstants.COURSE_CODE, request.getParameter(ZkgkConstants.COURSE_CODE));
			ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/back/register.html");
		} else {
			
			String appCode = "" + po.get(KEY_APP_CODE);
			if(!ZkgkUtil.authorityAppCode(appCode)) {
				CachedFactory.getCached().add("CANCEL_AUTH", true);
			}
			
			// 己经注册成为平台用户及操作员的，直接打开我的课程页面
			ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/back/registed.html");
		}
		
		return CONST_RESULT_SUCCESS;
	}

	@Override
	public int insertExportData(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int query(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int save(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
	
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
		
		
			// 新用户注册
		jsonObj = newRegister(ac);
		

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
				//int result = messageSendDao.addSystemMsg2(ac, new String[] { tsOp.get(ZkgkConstants.OPNO).toString() }, getContent(param), tsOp);
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

	
	private JSONObject newRegister(ActionContext ac) throws Exception {
		
		int rows = 0;
		JSONObject jsonObj = registerValidate(ac);
		
		if(!jsonObj.getBooleanValue(CONST_BIZRESULT)) {
			return jsonObj;
		}
		// 平台用户信息
		DBDYPO regUser = new DBDYPO("GO_REGUSER", KEY_FIELD, request);
		DBDYPO tsop = new DBDYPO(TABLE_TSOP, KEY_FIELD, request);		
		DBDYPO person = new DBDYPO("TB_PERSON", KEY_FIELD, request);
		
		
		Connection conn = null;
		Connection ssoconn = null;
		int idLen = 20;
		int dataSource = 4;
		int one = 1;
		int zero = 0;
		String personId = super.genIdString("", idLen);
		String integralId = super.genIdString("", idLen);		
		
		try {
			conn = DBConn.getConnection();
			ssoconn = DBConn.getConnection(ZkgkConstants.ID_SSODBSERVICE);
			conn.setAutoCommit(false);
			ssoconn.setAutoCommit(false);
			
			
			tsop.set(KEY_FIELD, personId);
			regUser.set(KEY_FIELD, personId);			
			person.set(KEY_FIELD, personId);
			
			
			
			String opno = "" + tsop.get(KEY_OPNO);
			String creatorKey = CREATE_BY;
			java.sql.Timestamp createTime = new java.sql.Timestamp(System.currentTimeMillis());
			
			tsop.set("OPNO_", opno);
			tsop.set("OPNAME", opno);
			tsop.set("OPLIMIT", zero);
			tsop.set("ENABLED", one);

			String appcodestr = "";
			// 用户访问其他系统而配置
			String appcode = BaseConstants.getGlobalValue("1338");
			if(StringUtils.isNotBlank(appcode)){
				appcodestr += "," + appcode;
			}
			tsop.set(KEY_APP_CODE, FrameworkConstant.getAppCode() + appcodestr);
			// 来源于子站；
			tsop.set("DATASOURCE", request.getParameter("ORGCODE"));
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
			
			String registerFail = "注册失败！";
			
			// 操作操作员信息
			rows = DBDYDao.insert(ssoconn, tsop);
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
			
			jsonObj.put(CONST_BIZRESULT, true);
			jsonObj.put(KEY_FIELD, personId);
			conn.commit();
			ssoconn.commit();
				
		} catch (SQLException e) {
			try {
				conn.rollback();
				ssoconn.rollback();
			} catch (SQLException e1) {
				MessageService.errString("用户注册失败，回滚异常！", e1);
			}
			
			MessageService.errString("注册用户异常", e);
			jsonObj.put(CONST_BIZRESULT, false);
			jsonObj.put(MSG, "数据库链接异常，注册失败！");
		} finally {
			DBConn.closeConnection(ssoconn);
			DBConn.closeConnection(conn);
		}
		
		return jsonObj;
	}
	
	private JSONObject registerValidate(ActionContext ac) {
		
		JSONObject  jsonObj = new JSONObject();
		
		String jsonStr = opnoCheck();
		JSONArray jsonArray = JSONArray.parseArray(jsonStr);
		if(!(Boolean)jsonArray.get(0)) {
			jsonObj.put(CONST_BIZRESULT, false);
			jsonObj.put(MSG, jsonArray.get(1));
			return jsonObj;
		} 
		
		jsonStr = codeCheck(ac);
		jsonArray = JSONArray.parseArray(jsonStr);
		if((Boolean)jsonArray.get(0)) {
			jsonObj.put(CONST_BIZRESULT, true);
		} else {
			jsonObj.put(CONST_BIZRESULT, false);
			jsonObj.put(MSG, jsonArray.get(1));
		}
		
		return jsonObj;
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
			MessageService.errString("查询ts_op中" + personId + "用户信息时出错！", e);
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
				MessageService.errString("", e);
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
}
