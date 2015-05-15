package com.xmdx.demo.doctor.service;

import java.sql.Timestamp;
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
	public int delete(ActionContext ac) throws Exception {
		checkAuth(ac, authFuncNo, RIGHT_EIGHT);
		
		String uidStr = ac.getHttpRequest().getParameter("ID");
		System.out.println("del_id = "+uidStr);
		if(StringUtils.isNotBlank(uidStr)) {
					int result = 0;
					DBDYPO po = new DBDYPO(tableName, keyField);
					po.set(keyField, uidStr);
					DBDYDao.selectByID(ac.getConnection(), po);
					result = DBDYDao.delete(ac.getConnection(), po);
					
					if(result == 0) {
						setMessage(ac,"删除失败");
					} else {
						setMessage(ac,"删除成功");
					}
				}
		return CONST_RESULT_AJAX;
	}
		
		
	

	@Override
	public int goTo(ActionContext ac) throws Exception {
		System.out.println("aaaaaaaa");
		DBDYPO po = new DBDYPO(tableName, keyField, ac.getHttpRequest());
		String uid = ac.getHttpRequest().getParameter("ID");
		System.out.println("blog_id="+uid);
		System.out.println(uid + "uid");
		if (StringUtils.isNotEmpty(uid)) {
			
			DBDYPO[] pos = DBDYDao.selectByID(ac.getConnection(), po);
			
			if(pos.length == 0) {
				ac.setErrorContext("您所选择文章已被删除！");
				return CONST_RESULT_ERROR;
			}
			DBDYPO old = pos[0];
			old.setCmd("U");
			
			System.out.println(old.get("TITLE")+" title here");
			ac.setObjValue("BLOG", old);
		} 
		ac.setStringValue("FORMNAME", "com/xmdx/demo/doctor/doctor_blog.html");
		return CONST_RESULT_SUCCESS;
	}

	@Override
	public int init(ActionContext ac) throws Exception {
//		checkAuth(ac, authFuncNo, RIGHT_ONE);
//		
//		String userName=SessionUtil.getOpno(ac);
//		
//		StringBuilder sql = new StringBuilder("SELECT * FROM TS_OP U ");
//		
//		if(StringUtils.isNotBlank(userName)) {
//			sql.append(" WHERE U.OPNO LIKE '%").append(userName).append("%' ");
//		}
//		DBDYPO[] po =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), sql.toString());
//		String id =po[0].getString("PERSON_ID");
//		uid = id;
//		System.out.println("id="+id);
//		
//		StringBuilder ssql = new StringBuilder("SELECT * FROM BLOG B ");
//		if(StringUtils.isNotBlank(id)) {
//			ssql.append(" WHERE B.ID IN(SELECT BLOG_ID from BLOG_DOCTOR where DOCTOR_ID LIKE '%").append(id).append("%') ");
//		}
//		DBDYPO[] o =DBDYDao.selectBySQL(ac.getConnection(), ssql.toString());
//		System.out.println(o[0].getString("ID"));
//		
//		
//		int pageNumber = BaseConstants.getQueryPageNumber(ac);
//		int pageSize = BaseConstants.getQueryPageSize(ac);
//		System.out.println(pageNumber);
//		System.out.println(pageSize);
//		JdbcPage page =  DBDYDao.select2JdbcPage(ac.getConnection(), ssql.toString(), pageNumber, 2);	
//		System.out.println("a");
//		List<DBDYPO> polist = page.getThisPageList();
//		List<DBDYPO> blogs = new ArrayList<DBDYPO>();
		String userName=SessionUtil.getOpno(ac);
//		
		StringBuilder sql = new StringBuilder("SELECT * FROM TS_OP U ");
//		
		if(StringUtils.isNotBlank(userName)) {
			sql.append(" WHERE U.OPNO LIKE '%").append(userName).append("%' ");
		}
		DBDYPO[] po =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), sql.toString());
		String id =po[0].getString("PERSON_ID");
		System.out.println("id="+id);
		StringBuilder ssql = new StringBuilder("SELECT * FROM BLOG B ");
		if(StringUtils.isNotBlank(id)) {
			ssql.append(" WHERE B.ID IN(SELECT BLOG_ID from BLOG_DOCTOR where DOCTOR_ID LIKE '%").append(id).append("%') ");
		}
		DBDYPO[] pop =DBDYDao.selectBySQL(ac.getConnection(), ssql.toString());

		
		System.out.println("size = "+String.valueOf(pop.length));
		if(pop.length==0)
		{
			ac.setStringValue("SIZE", String.valueOf(pop.length));
			ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/doctor/doctor_bloglist.html");		
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
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/doctor/doctor_bloglist.html");		
		return CONST_RESULT_SUCCESS;
		}
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
		System.out.println("modify_blog_id="+id);
		String title = ac.getHttpRequest().getParameter("TITLE");
		String content = ac.getHttpRequest().getParameter("CONTENT");
		//content = HtmlRemoveUtil.htmlRemoveTag(content);
		System.out.println("title="+title);
		System.out.println("content="+content);
		
		int result = 0;
		int result1 = 0;
		boolean isAdd = false;
		
		if (StringUtils.isNotBlank(id)) {
			//修改
			System.out.println("enter if");
			checkAuth(ac, authFuncNo, RIGHT_FOUR);
			blog.set("ID", id);
			DBDYDao.selectByID(ac.getConnection(), blog);
			blog.set("CONTENT", content);
			blog.set("TITLE", title);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 Timestamp nowdate1 = new Timestamp(System.currentTimeMillis());
			 String datestr = sdf.format(nowdate1);
			 blog.set("MODIFYTIME",datestr);
			result = DBDYDao.update(ac.getConnection(), blog);
			if(0 == result) {
				//log(ac, LOGLEVEL_W, "SYS01", blog.getTableName(), id, isAdd ? "insert" : "update", "保存用户失败!");
				//setMessage(ac, "修改失败!");
				ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/doctor/blog_success.html");
			} else {
				//log(ac, LOGLEVEL_I, "SYS01", blog.getTableName(), id, isAdd ? "insert" : "update", "保存用户成功!");
				//setMessage(ac, "修改成功!");
				ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/doctor/blog_success.html");
			}
			return CONST_RESULT_SUCCESS;
		} else {
			//新增
			checkAuth(ac, authFuncNo, RIGHT_TWO);
			
			id = GenID.genIdString("B", 21);
			blog.set(keyField, id);
			blog.set("TITLE", title);
			blog.set("MODIFYTIME", new java.sql.Date(System.currentTimeMillis()));
			blog.set("CONTENT", content);
			isAdd = true;
			
			DBDYPO con = new DBDYPO("BLOG_DOCTOR", "DOCTOR_ID,BLOG_ID");
			con.set("BLOG_ID", id);
			con.set("DOCTOR_ID", pid);
			result = DBDYDao.insert(ac.getConnection(), blog);
			result1 = DBDYDao.insert(ac.getConnection(), con);
			
			if((0 == result)||(0==result1)) {
				//log(ac, LOGLEVEL_W, "SYS01", blog.getTableName(), id, isAdd ? "insert" : "update", "保存用户失败!");
				setMessage(ac, "新增失败!");
			} else {
				//log(ac, LOGLEVEL_I, "SYS01", blog.getTableName(), id, isAdd ? "insert" : "update", "保存用户成功!");
				//setMessage(ac, "新增成功!");
				ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/doctor/blog_success.html");
			}
			return CONST_RESULT_SUCCESS;
		}
		
		
		
	}

	public int initBlog(ActionContext ac) throws Exception {
		
		checkAuth(ac, authFuncNo, RIGHT_ONE);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/doctor/doctor_blog.html");		
		return CONST_RESULT_SUCCESS;
	}
	
	public int blogView(ActionContext ac) throws Exception {
		System.out.println("aaaaaaaa");
		DBDYPO po = new DBDYPO(tableName, keyField, ac.getHttpRequest());
		String uid = ac.getHttpRequest().getParameter("ID");
		System.out.println("blog_id="+uid);
		System.out.println(uid + "uid");
		if (StringUtils.isNotEmpty(uid)) {
			
			DBDYPO[] pos = DBDYDao.selectByID(ac.getConnection(), po);
			
			if(pos.length == 0) {
				ac.setErrorContext("您所选择文章已被删除！");
				return CONST_RESULT_ERROR;
			}
			DBDYPO old = pos[0];
			old.setCmd("U");
			
			System.out.println(old.get("TITLE")+" title here");
			ac.setObjValue("BLOG", old);
		} 
		ac.setStringValue("FORMNAME", "com/xmdx/demo/doctor/doctor_blogview.html");
		return CONST_RESULT_SUCCESS;
	}
	
	public int myblog(ActionContext ac) throws Exception {
		String userName=SessionUtil.getOpno(ac);
//		
		StringBuilder sql = new StringBuilder("SELECT * FROM TS_OP U ");
//		
		if(StringUtils.isNotBlank(userName)) {
			sql.append(" WHERE U.OPNO LIKE '%").append(userName).append("%' ");
		}
		DBDYPO[] po =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), sql.toString());
		String id =po[0].getString("PERSON_ID");
		System.out.println("id="+id);
		StringBuilder ssql = new StringBuilder("SELECT * FROM BLOG B ");
		if(StringUtils.isNotBlank(id)) {
			ssql.append(" WHERE B.ID IN(SELECT BLOG_ID from BLOG_DOCTOR where DOCTOR_ID LIKE '%").append(id).append("%') ");
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
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/doctor/myblog_main.html");		
		return CONST_RESULT_SUCCESS;
	}
}
