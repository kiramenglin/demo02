package com.xmdx.demo.doctor.service;

import org.apache.commons.lang.StringUtils;

import com.e9rj.platform.common.GenID;
import com.e9rj.platform.common.services.BusinessServices;
import com.xmzy.frameext.business.service.annotate.Service;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.framework.context.ActionContext;
@Service(name="doctor.blog")
public class BlogService extends BusinessServices {
	//功能号
	private static final String authFuncNo = "doctor.list";
	//表名
	private static final String tableName = "DOCTOR";
	//主键名
	private static final String keyField = "DOCTOR_ID";
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
checkAuth(ac, authFuncNo, RIGHT_ONE);
		
		ac.setStringValue("tabLogo", authFuncNo);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/doctor/doctor_blog.html");		
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
	public int save(ActionContext ac) throws Exception {
		DBDYPO user = new DBDYPO(tableName, keyField, request);
		String uid = request.getParameter(keyField);
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
			log(ac, LOGLEVEL_W, "SYS01", user.getTableName(), uid, isAdd ? "insert" : "update", "保存用户失败!");
			setMessage(ac, "保存失败!");
		} else {
			log(ac, LOGLEVEL_I, "SYS01", user.getTableName(), uid, isAdd ? "insert" : "update", "保存用户成功!");
			setMessage(ac, "保存成功!");
		}
		
		return CONST_RESULT_AJAX;
	}

}
