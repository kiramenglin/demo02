package com.xmdx.demo.patient.service;

import java.io.File;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;

import com.e9rj.platform.common.GenID;
import com.e9rj.platform.common.StringUtil;
import com.e9rj.platform.common.services.BusinessServices;
import com.e9rj.platform.util.SessionUtil;
import com.xmzy.frameext.business.service.annotate.Service;
import com.xmzy.frameext.simpledb.DBConn;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.framework.context.ActionContext;
@Service(name="patient.eye")
public class EyeService extends BusinessServices {
	//功能号
	private static final String authFuncNo = "patient.eye";
	//表名
	private static final String tableName = "EYE_TEST";
	//主键名
	private static final String keyField = "EYE_ID";
	
	private static String uid = null;
	
	private static String code = null;
	@Override
	public int delete(ActionContext ac) throws Exception {
		checkAuth(ac, authFuncNo, RIGHT_EIGHT);
		
		String uidStr = ac.getHttpRequest().getParameter("EYE_ID");
		
		if(StringUtils.isNotBlank(uidStr)) {
			String[] uids = uidStr.split(",");
			int result = 0;
			for (String uid : uids) {
				if(StringUtils.isNotBlank(uid)) {
					DBDYPO po = new DBDYPO(tableName, keyField);
					po.set(keyField, uid);
					
					result = DBDYDao.delete(ac.getConnection(), po);
					
					if(result == 0) {
						super.log(ac, LOGLEVEL_W, "SYS01", po.getTableName(), uid, "delete", "删除失败!");
					} else {
						super.log(ac, LOGLEVEL_I, "SYS01", po.getTableName(), uid, "delete", "删除成功!");
					}
				}
			}
		}
		setMessage(ac, "删除成功！");
		return CONST_RESULT_AJAX;
	}

	@Override
	public int goTo(ActionContext ac) throws Exception {
		DBDYPO po = new DBDYPO(tableName, keyField, ac.getHttpRequest());
		String uid = ac.getHttpRequest().getParameter("EYE_ID");
		System.out.println("eye_id="+uid);
		System.out.println(uid + "uid");
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
				ac.setErrorContext("您所选择的项目已被删除！");
				return CONST_RESULT_ERROR;
			}
			DBDYPO old = pos[0];
			old.setCmd("U");
			ac.setObjValue("USER", old);
		} else {
			// 新增
			checkAuth(ac, authFuncNo, RIGHT_TWO);
			po.setCmd("A");
			ac.setObjValue("USER", po);
		}
		ac.setStringValue("FORMNAME", "com/xmdx/demo/patient/eye_modify.html");
		return CONST_RESULT_SUCCESS;
	}

	@Override
	public int init(ActionContext ac) throws Exception {
		
		checkAuth(ac, authFuncNo, RIGHT_ONE);
		
		ac.setStringValue("tabLogo", authFuncNo);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/patient/eye_test.html");		
		return CONST_RESULT_SUCCESS;
	}

	@Override
	public int insertExportData(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int query(ActionContext ac) throws Exception {
		String userName=SessionUtil.getOpno(ac);
//		
		StringBuilder sql = new StringBuilder("SELECT * FROM TS_OP U ");
//		
		if(StringUtils.isNotBlank(userName)) {
			sql.append(" WHERE U.OPNO LIKE '%").append(userName).append("%' ");
		}
		DBDYPO[] po =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), sql.toString());
		String id =po[0].getString("PERSON_ID");
		StringBuilder ssql = new StringBuilder("SELECT * FROM EYE_TEST U ");
		if(StringUtils.isNotBlank(id)) {
			ssql.append(" WHERE U.EYE_ID IN(SELECT EYE_ID from PATIENT_EYE where PATIENT_ID LIKE '%").append(id).append("%') ");
		}
		

		ssql.append(super.order(ac, "U.EYE_ID", "DESC"));
		
		querySql = ssql.toString();
		
		
		return CONST_RESULT_AJAX;
	}

	@Override
	public int save(ActionContext ac) throws Exception {
		String userName=SessionUtil.getOpno(ac);
		
		StringBuilder sql = new StringBuilder("SELECT * FROM TS_OP U ");
		
		if(StringUtils.isNotBlank(userName)) {
			sql.append(" WHERE U.OPNO LIKE '%").append(userName).append("%' ");
		}
		DBDYPO[] po =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), sql.toString());
		String pid =po[0].getString("PERSON_ID");
		
		DBDYPO eye = new DBDYPO(tableName, keyField, request);
		
		String id = request.getParameter(keyField);
		System.out.println("modify_id="+id);
		int result = 0;
		int result1 = 0;
		boolean isAdd = false;
		String nv = ac.getHttpRequest().getParameter("NV");
		String tension = ac.getHttpRequest().getParameter("TENSION");
		String canal = ac.getHttpRequest().getParameter("CANAL");
		String tbut = ac.getHttpRequest().getParameter("TBUT");
		
		
		if (StringUtils.isNotBlank(id)) {
			//修改
			
			checkAuth(ac, authFuncNo, RIGHT_FOUR);
			eye.set("EYE_ID", id);
			DBDYDao.selectByID(ac.getConnection(), eye);
			eye.set("NV", nv);
			eye.set("TENSION", tension);
			eye.set("CANAL", canal);
			eye.set("TBUT", tbut);
			eye.set("STAINING", code);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Timestamp nowdate1 = new Timestamp(System.currentTimeMillis());
			String datestr = sdf.format(nowdate1);
			eye.set("DATE",datestr);
			result = DBDYDao.update(ac.getConnection(), eye);
			result1 = 1;
			
			
		} else {
			//新增
			checkAuth(ac, authFuncNo, RIGHT_TWO);
			
			id = GenID.genIdString("E", 21);
			eye.set(keyField, id);
			eye.set("NV", nv);
			eye.set("TENSION", tension);
			eye.set("CANAL", canal);
			eye.set("TBUT", tbut);
			eye.set("STAINING", code);
			eye.set("DATE", new java.sql.Date(System.currentTimeMillis()));
			isAdd = true;
			
			
			DBDYPO con = new DBDYPO("PATIENT_EYE", "PATIENT_ID,EYE_ID");
			con.set("EYE_ID", id);
			con.set("PATIENT_ID", pid);
			result = DBDYDao.insert(ac.getConnection(), eye);
			result1 = DBDYDao.insert(ac.getConnection(), con);
		}
		if(0 == result||(0==result1)) {
			log(ac, LOGLEVEL_W, "SYS01", eye.getTableName(), uid, isAdd ? "insert" : "update", "保存失败!");
			setMessage(ac, "保存失败!");
		} else {
			log(ac, LOGLEVEL_I, "SYS01", eye.getTableName(), uid, isAdd ? "insert" : "update", "保存成功!");
			setMessage(ac, "保存成功!");
		}
		
		return CONST_RESULT_AJAX;
	}

	public int newInfo(ActionContext ac) throws Exception {
		System.out.println("enter goto");
		uid = ac.getHttpRequest().getParameter("EYE_ID");
		System.out.println("test id = "+uid);
		// TODO Auto-generated method stub

		ac.setStringValue("tabLogo", authFuncNo);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/patient/eye_modify.html");
		return CONST_RESULT_SUCCESS;
	}
	
	public int info(ActionContext ac) throws Exception {
		DBDYPO po = new DBDYPO(tableName, keyField, ac.getHttpRequest());
		String uid = ac.getHttpRequest().getParameter("EYE_ID");
		DBDYPO[] pos = DBDYDao.selectByID(ac.getConnection(), po);
		DBDYPO old = pos[0];
		old.setCmd("U");
		ac.setObjValue("USER", old);
		
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/patient/eye_info.html");
		return CONST_RESULT_SUCCESS;
	}
	
	public int convertToBase64(ActionContext ac) throws Exception {
		System.out.println("enter converttobase64");
		int result = 0;
		String filename = ac.getHttpRequest().getParameter("FILENAME");
		String imgFilePath = ac.getHttpSession().getServletContext().getRealPath("tmpfiles");
		imgFilePath = imgFilePath + File.separator + filename;
		code = StringUtil.convertToBase64(imgFilePath);
		code = URLEncoder.encode(code, "UTF-8");
		
		setMessage(ac, code);
		return CONST_RESULT_AJAX;
	}
}
