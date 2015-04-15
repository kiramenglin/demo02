package com.xmdx.demo.doctor.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.e9rj.platform.common.BaseConstants;
import com.e9rj.platform.common.GenID;
import com.e9rj.platform.common.services.BusinessServices;
import com.e9rj.platform.util.SessionUtil;
import com.xmdx.demo.back.dao.HtmlRemoveUtil;
import com.xmzy.frameext.business.service.annotate.Service;
import com.xmzy.frameext.json.FastJsonUtil;
import com.xmzy.frameext.simpledb.DBConn;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.frameext.simpledb.page.JdbcPage;
import com.xmzy.framework.context.ActionContext;
@Service(name="doctor.blog")
public class BlogService extends BusinessServices {
	//功能号
	private static final String authFuncNo = "doctor.blog";
	//表名
	private static final String tableName = "BLOG";
	//主键名
	private static final String keyField = "ID";
	
	private static String uid = null;
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
		
		String userName=SessionUtil.getOpno(ac);
		
		StringBuilder sql = new StringBuilder("SELECT * FROM TS_OP U ");
		
		if(StringUtils.isNotBlank(userName)) {
			sql.append(" WHERE U.OPNO LIKE '%").append(userName).append("%' ");
		}
		DBDYPO[] po =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), sql.toString());
		String id =po[0].getString("PERSON_ID");
		uid = id;
		System.out.println("id="+id);
		
		StringBuilder ssql = new StringBuilder("SELECT * FROM BLOG B ");
		if(StringUtils.isNotBlank(id)) {
			ssql.append(" WHERE B.ID IN(SELECT BLOG_ID from BLOG_DOCTOR where DOCTOR_ID LIKE '%").append(id).append("%') ");
		}
		DBDYPO[] o =DBDYDao.selectBySQL(ac.getConnection(), ssql.toString());
		System.out.println(o[0].getString("ID"));
		
		
		int pageNumber = BaseConstants.getQueryPageNumber(ac);
		int pageSize = BaseConstants.getQueryPageSize(ac);
		System.out.println(pageNumber);
		System.out.println(pageSize);
		JdbcPage page =  DBDYDao.select2JdbcPage(ac.getConnection(), ssql.toString(), pageNumber, 2);	
		System.out.println("a");
		List<DBDYPO> polist = page.getThisPageList();
		List<DBDYPO> blogs = new ArrayList<DBDYPO>();
		for(int i = 0; i< polist.size();i++){
			DBDYPO pop = polist.get(i);	
			pop.set("TITLE", o[i].get("TITLE").toString());
			pop.set("CONTENT", o[i].get("CONTENT").toString());
			blogs.add(pop);
		}
		System.out.println("b");
		ac.setObjValue("BLOGS", blogs);
		
		String jsonStr  = FastJsonUtil.jdbcPage2JsonString(page);
		JSONObject jsonObject = JSONObject.parseObject(jsonStr);
		ac.setObjValue("PAGE_BEAN", jsonObject);
		System.out.println(jsonObject.get("TotalPage")+"!@#!@$@#%%^");
		ac.setStringValue("tabLogo", authFuncNo);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/doctor/doctor_bloglist.html");		
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
		String userName=SessionUtil.getOpno(ac);
		
		StringBuilder sql = new StringBuilder("SELECT * FROM TS_OP U ");
		
		if(StringUtils.isNotBlank(userName)) {
			sql.append(" WHERE U.OPNO LIKE '%").append(userName).append("%' ");
		}
		DBDYPO[] po =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), sql.toString());
		String pid =po[0].getString("PERSON_ID");
		
		
		DBDYPO blog = new DBDYPO(tableName, keyField);
		String id = request.getParameter(keyField);
		String title = ac.getHttpRequest().getParameter("TITLE");
		String content = ac.getHttpRequest().getParameter("CONTENT");
		//content = HtmlRemoveUtil.htmlRemoveTag(content);
		
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
			
//			DBDYPO con = new DBDYPO("BLOG_DOCTOR", "DOCTOR_ID,ID");
//			con.set("ID", id);
//			con.set("DOCTOR_ID", pid);
			result = DBDYDao.insert(ac.getConnection(), blog);
			//result = DBDYDao.insert(ac.getConnection(), con);
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

	public int initBlog(ActionContext ac) throws Exception {
		
		checkAuth(ac, authFuncNo, RIGHT_ONE);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/doctor/doctor_blog.html");		
		return CONST_RESULT_SUCCESS;
	}
}
