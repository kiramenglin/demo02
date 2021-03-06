package com.xmdx.demo.doctor.service;

import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.e9rj.platform.common.BaseConstants;
import com.e9rj.platform.common.CodeNameConvert;
import com.e9rj.platform.common.GenID;
import com.e9rj.platform.common.services.BusinessServices;
import com.e9rj.platform.util.SessionUtil;
import com.xmzy.frameext.business.service.annotate.Service;
import com.xmzy.frameext.json.FastJsonUtil;
import com.xmzy.frameext.simpledb.DBConn;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.frameext.simpledb.page.JdbcPage;
import com.xmzy.framework.context.ActionContext;

@Service(name="doctor.connected")
/**
 * 
 * @author Jinghui lu
 * 我的私人医生
 *
 */
public class ConnectedDoctorService extends BusinessServices {

	//功能号
	private static final String authFuncNo = "doctor.connected";
	//表名
	private static final String tableName = "TS_OP";
	//主键名
	private static final String keyField = "PERSON_ID";
	
	private static final String TABLE_PATIENT = "PATIENT";
	
	private static String uid = null;
	
	/**
	 * 删除用户
	 */
	@Override
	public int delete(ActionContext ac) throws Exception {
		
		checkAuth(ac, authFuncNo, RIGHT_EIGHT);
		
		String uidStr = ac.getHttpRequest().getParameter("PATIENT_ID");
		
		if(StringUtils.isNotBlank(uidStr)) {
			String[] uids = uidStr.split(",");
			int result = 0;
			for (String uid : uids) {
				if(StringUtils.isNotBlank(uid)) {
					DBDYPO po = new DBDYPO(TABLE_PATIENT, "PATIENT_ID");
					po.set("PATIENT_ID", uid);
					
					result = DBDYDao.delete(ac.getConnection(), po);
					
					if(result == 0) {
						super.log(ac, LOGLEVEL_W, "SYS01", po.getTableName(), uid, "delete", "删除病患失败!");
					} else {
						super.log(ac, LOGLEVEL_I, "SYS01", po.getTableName(), uid, "delete", "删除病患成功!");
					}
				}
			}
		}
		setMessage(ac, "删除成功！");
		return CONST_RESULT_AJAX;
	}

	/**
	 * 打开用户信息编辑页面
	 */
	@Override
	public int goTo(ActionContext ac) throws Exception {
		DBDYPO po = new DBDYPO(TABLE_PATIENT, "PATIENT_ID", ac.getHttpRequest());
		String uid = ac.getHttpRequest().getParameter("PATIENT_ID");
		if (StringUtils.isNotEmpty(uid)) {
			
			if("read".equalsIgnoreCase(ac.getStringValue(CONST_RESOURCEAUTH))) {
				//查看
				checkAuth(ac, authFuncNo, RIGHT_ONE);
			} else {
				// 修改
				checkAuth(ac, authFuncNo, RIGHT_FOUR);
			}
			
			DBDYPO[] pos = DBDYDao.selectByID(ac.getConnection(), po);
			
			if(pos.length == 0) {
				ac.setErrorContext("您所选择的病患已被删除！");
				return CONST_RESULT_ERROR;
			}
			DBDYPO old = pos[0];
			old.setCmd("U");
			ac.setObjValue("USER_BEAN", old);
		} else {
			// 新增
			checkAuth(ac, authFuncNo, RIGHT_TWO);
			po.setCmd("A");
			ac.setObjValue("USER_BEAN", po);
		}
		ac.setStringValue("FORMNAME", "com/xmdx/demo/patient/patientConnected_edit.html");
		return CONST_RESULT_SUCCESS;
	}

	/**
	 * 打开用户列表页
	 */
	@Override
	public int init(ActionContext ac) throws Exception {
		
		checkAuth(ac, authFuncNo, RIGHT_ONE);
		
		ac.setStringValue("tabLogo", authFuncNo);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/doctor/doctorConnected_main.html");		
		return CONST_RESULT_SUCCESS;
	}

	@Override
	public int insertExportData(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 查询用户信息
	 */
	@Override
	public int query(ActionContext ac) throws Exception {
		String userName=SessionUtil.getOpno(ac);
		
		StringBuilder sql = new StringBuilder("SELECT * FROM TS_OP U ");
		
		if(StringUtils.isNotBlank(userName)) {
			sql.append(" WHERE U.OPNO LIKE '%").append(userName).append("%' ");
		}
		DBDYPO[] po =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), sql.toString());
		String id =po[0].getString("PERSON_ID");
		System.out.println("userid="+id);
		StringBuilder ssql = new StringBuilder("SELECT * FROM DOCTOR P,FRIEND_APPLY F");
		if(StringUtils.isNotBlank(id)) {
			ssql.append(" WHERE F.PATIENT_ID LIKE '%").append(id).append("%' and P.DOCTOR_ID = F.DOCTOR_ID and F.STATE = 1 ");
		}
//		String userName = request.getParameter("NAME");
//		
//		if(StringUtils.isNotBlank(userName)) {
//			sql.append(" WHERE U.NAME LIKE '%").append(userName).append("%' ");
//		}
		
		//设置排序条件，默认的按BIRTHDAY降序
//		ssql.append(super.order(ac, "U.NAME", "DESC"));
		
		querySql = ssql.toString();
		
		//代码转换
//		addNameCodeConvert("GENDER", CodeNameConvert.getCachedData("SYSCODE", "SEX"), REPLACE);
		return CONST_RESULT_AJAX;
	}

	/**
	 * 保存用户信息
	 */
	@Override
	public int save(ActionContext ac) throws Exception {
		
		DBDYPO user = new DBDYPO(tableName, keyField, request);
		String uid = request.getParameter(keyField);
		System.out.println("patient_id="+uid);
		int result = 0;
		boolean isAdd = false;
		
		if (StringUtils.isNotBlank(uid)) {
			//修改
			checkAuth(ac, authFuncNo, RIGHT_FOUR);
			result = DBDYDao.update(ac.getConnection(), user);
			
		} else {
			//新增
			checkAuth(ac, authFuncNo, RIGHT_TWO);
			
			uid = GenID.genIdString("U", 21);
			user.set(keyField, uid);
			isAdd = true;
			result = DBDYDao.insert(ac.getConnection(), user);
		}
		if(0 == result) {
			log(ac, LOGLEVEL_W, "SYS01", user.getTableName(), uid, isAdd ? "insert" : "update", "保存病患失败!");
			setMessage(ac, "保存病患失败!");
		} else {
			log(ac, LOGLEVEL_I, "SYS01", user.getTableName(), uid, isAdd ? "insert" : "update", "保存病患成功!");
			setMessage(ac, "保存病患成功!");
		}
		
		return CONST_RESULT_AJAX;
	}
	
	public int doctorInfo(ActionContext ac) throws Exception {
		System.out.println("enter goto");
		String uid = ac.getHttpRequest().getParameter("DOCTOR_ID");
		System.out.println("doctor id = "+uid);
		// TODO Auto-generated method stub

		
		StringBuilder sqlp = new StringBuilder("SELECT * FROM DOCTOR U ");
		if(StringUtils.isNotBlank(uid)) {
			sqlp.append(" WHERE U.DOCTOR_ID LIKE '%").append(uid).append("%' ");
		}
		DBDYPO[] pop =DBDYDao.selectBySQL(ac.getConnection(), sqlp.toString());
		ac.setObjValue("USER", pop[0]);
		
		checkAuth(ac, authFuncNo, RIGHT_ONE);
		
		ac.setStringValue("tabLogo", authFuncNo);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/doctor/doctor_patient_connected.html");		
		return CONST_RESULT_SUCCESS;
	}
	
	public int blogInfo(ActionContext ac) throws Exception {
		System.out.println("enter goto");
		uid = ac.getHttpRequest().getParameter("DOCTOR_ID");
		System.out.println("doctor id = "+uid);
		// TODO Auto-generated method stub

		StringBuilder ssql = new StringBuilder("SELECT * FROM BLOG B ");
		if(StringUtils.isNotBlank(uid)) {
			ssql.append(" WHERE B.ID IN(SELECT BLOG_ID from BLOG_DOCTOR where DOCTOR_ID LIKE '%").append(uid).append("%') ");
		}
		
		DBDYPO[] pop =DBDYDao.selectBySQL(ac.getConnection(), ssql.toString());
		ac.setObjValue("USER", pop[0]);
		
		if(pop.length==0)
		{
			ac.setStringValue("SIZE", String.valueOf(pop.length));
			ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/doctor/blog_connected.html");		
			return CONST_RESULT_SUCCESS;
		}
		else{
		int pageNumber = BaseConstants.getQueryPageNumber(ac);
		int pageSize = BaseConstants.getQueryPageSize(ac);
					
		JdbcPage page =  DBDYDao.select2JdbcPage(ac.getConnection(), ssql.toString(), pageNumber, 10);
		
		
		List<DBDYPO> polist = page.getThisPageList();
		List<DBDYPO> blogs = new ArrayList<DBDYPO>();
		for(int i = 0; i< polist.size();i++){
			DBDYPO po1 = polist.get(i);	
			po1.set("TITLE", pop[i].get("TITLE").toString());
			po1.set("CONTENT", pop[i].get("CONTENT").toString());
			blogs.add(po1);
		}
		System.out.println("b");
		ac.setObjValue("BLOGS", blogs);
		
		String jsonStr  = FastJsonUtil.jdbcPage2JsonString(page);
		JSONObject jsonObject = JSONObject.parseObject(jsonStr);
		ac.setObjValue("PAGE_BEAN", jsonObject);
		System.out.println(jsonObject.get("TotalPage")+"!@#!@$@#%%^");
		ac.setStringValue("tabLogo", authFuncNo);
		ac.setStringValue("SIZE", String.valueOf(pop.length));
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/doctor/blog_connected.html");		
		return CONST_RESULT_SUCCESS;
		}
	}
	
	public int myblog(ActionContext ac) throws Exception {
		String id = ac.getHttpRequest().getParameter("DOCTOR_ID");
		ac.setStringValue("DOCTOR_ID", uid);
		System.out.println("id="+uid);
		StringBuilder ssql = new StringBuilder("SELECT * FROM BLOG B ");
		if(StringUtils.isNotBlank(uid)) {
			ssql.append(" WHERE B.ID IN(SELECT BLOG_ID from BLOG_DOCTOR where DOCTOR_ID LIKE '%").append(uid).append("%') ");
		}
		DBDYPO[] pop =DBDYDao.selectBySQL(ac.getConnection(), ssql.toString());

		
		int pageNumber = BaseConstants.getQueryPageNumber(ac);
		int pageSize = BaseConstants.getQueryPageSize(ac);
					
		JdbcPage page =  DBDYDao.select2JdbcPage(ac.getConnection(), ssql.toString(), pageNumber, 10);
		
		
		List<DBDYPO> polist = page.getThisPageList();
		List<DBDYPO> blogs = new ArrayList<DBDYPO>();
		String jsonStr  = FastJsonUtil.jdbcPage2JsonString(page);
		JSONObject jsonObject = JSONObject.parseObject(jsonStr);
		String currentpage = jsonObject.getString("CurrentPage");
		int c = Integer.parseInt(currentpage);
		System.out.println("currentpage="+c);
		System.out.println("polistsize="+polist.size());
		int n = (c-1)*10;
		for(int i = 0; i< polist.size();i++,n++){
			System.out.println("enter if");
			DBDYPO po1 = polist.get(i);	
			po1.set("TITLE", pop[n].get("TITLE").toString());
			po1.set("CONTENT", pop[n].get("CONTENT").toString());
			blogs.add(po1);
		}
		System.out.println("b");
		ac.setObjValue("BLOGS", blogs);
		
		
		ac.setObjValue("PAGE_BEAN", jsonObject);
		System.out.println(jsonObject.get("TotalPage")+"!@#!@$@#%%^");
		ac.setStringValue("tabLogo", authFuncNo);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/doctor/connectedblog_main.html");		
		return CONST_RESULT_SUCCESS;
	}

}
