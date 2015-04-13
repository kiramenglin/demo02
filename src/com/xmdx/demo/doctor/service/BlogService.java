package com.xmdx.demo.doctor.service;

import java.text.SimpleDateFormat;
import java.util.Date;

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
	private static final String authFuncNo = "doctor.blog";
	//表名
	private static final String tableName = "BLOG";
	//主键名
	private static final String keyField = "ID";
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
		DBDYPO blog = new DBDYPO(tableName, keyField);
		String id = request.getParameter(keyField);
		String title = ac.getHttpRequest().getParameter("TITLE");
		String content = ac.getHttpRequest().getParameter("CONTENT");
		
		
		int result = 0;
		boolean isAdd = false;
		
		if (StringUtils.isNotBlank(id)) {
			//修改
			checkAuth(ac, authFuncNo, RIGHT_FOUR);
			result = DBDYDao.update(ac.getConnection(), blog);
			
		} else {
			//新增
			checkAuth(ac, authFuncNo, RIGHT_TWO);
			
			id = GenID.genIdString("B", 21);
			blog.set(keyField, id);
			blog.set("TITLE", title);
			blog.set("MODIFYDATE", new java.sql.Date(System.currentTimeMillis()));
			blog.set("CONTENT", content);
			isAdd = true;
			result = DBDYDao.insert(ac.getConnection(), blog);
		}
		if(0 == result) {
			//log(ac, LOGLEVEL_W, "SYS01", blog.getTableName(), id, isAdd ? "insert" : "update", "保存用户失败!");
			setMessage(ac, "保存失败!");
		} else {
			//log(ac, LOGLEVEL_I, "SYS01", blog.getTableName(), id, isAdd ? "insert" : "update", "保存用户成功!");
			setMessage(ac, "保存成功!");
		}
		
		return CONST_RESULT_AJAX;
	}

}
