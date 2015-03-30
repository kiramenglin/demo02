package com.xmdx.demo.back.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.e9rj.platform.common.GenID;
import com.e9rj.platform.common.services.BusinessServices;
import com.xmzy.frameext.business.service.annotate.Service;
import com.xmzy.frameext.json.FastJsonUtil;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.framework.context.ActionContext;

@Service(name="back.org")
public class OrgService extends BusinessServices {

	//功能号
	private static final String authFuncNo = "back.org";
	//表名
	private static final String tableName = "TB_ORG";	
	//主键名
	private static final String keyField = "O_ID";
	
	/**
	 * 删除组织机构
	 */
	@Override
	public int delete(ActionContext ac) throws Exception {
		//权限验证
		checkAuth(ac, authFuncNo, RIGHT_EIGHT);
		
		String uidStr = ac.getHttpRequest().getParameter("O_ID");
		
		if(StringUtils.isNotBlank(uidStr)) {
			String[] uids = uidStr.split(",");
			int result = 0;
			for (String uid : uids) {
				if(StringUtils.isNotBlank(uid)) {
					DBDYPO po = new DBDYPO(tableName, keyField);
					po.set(keyField, uid);
					
					result = DBDYDao.delete(ac.getConnection(), po);
					//递归删除所有下级组织机构
					deleteChild(ac, po.get("O_ID").toString());
					//保存操作日志
					if(result == 0) {
						super.log(ac, LOGLEVEL_W, "SYS01", po.getTableName(), uid, "delete", "删除组织机构失败!");
					} else {
						super.log(ac, LOGLEVEL_I, "SYS01", po.getTableName(), uid, "delete", "删除组织机构成功!");
					}
				}
			}
		}
		setMessage(ac, "true");
		return CONST_RESULT_AJAX;
	}

	@Override
	public int goTo(ActionContext arg0) throws Exception {
		
		return 0;
	}

	/**
	 * 打开组织机构主页面
	 */
	@Override
	public int init(ActionContext ac) throws Exception {
		checkAuth(ac, authFuncNo, RIGHT_ONE);
		
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/back/org_main.html");
		
		return CONST_RESULT_SUCCESS;
	}

	@Override
	public int insertExportData(ActionContext arg0) throws Exception {
	
		return 0;
	}

	/**
	 * 查询组织机构信息
	 */
	@Override
	public int query(ActionContext ac) throws Exception {
		
		checkAuth(ac, authFuncNo, RIGHT_ONE);
		
		String sql = "SELECT * FROM TB_ORG T";
		List<DBDYPO> pos = DBDYDao.selectBySQL2List(ac.getConnection(), sql.toString());
		
		//将组织机构转为ztree树结点数据结构形式
		String json = FastJsonUtil.dbdypoList2JsonString(pos);		
		json = json.replaceAll("O_ID", "id");
		json = json.replaceAll("ORG_NAME", "name");
		json = json.replaceAll("P_ID", "pId");		
		JSONObject jsonObj = JSONObject.parseObject(json);
		
		ac.getHttpResponse().getWriter().write(jsonObj.get("Rows").toString());
		return CONST_RESULT_AJAX;
	}

	/**
	 * 保存组织机构信息
	 */
	@Override
	public int save(ActionContext ac) throws Exception {
		
		DBDYPO org = new DBDYPO(tableName, keyField, request);
		String oid = request.getParameter(keyField);
		int result = 0;
		boolean isAdd = false;
		
		if (StringUtils.isNotBlank(oid)) {
			//修改
			checkAuth(ac, authFuncNo, RIGHT_FOUR);
			
			result = DBDYDao.update(ac.getConnection(), org);
			
		} else {
			
			//新增
			checkAuth(ac, authFuncNo, RIGHT_TWO);
			
			oid = GenID.genIdString("O", 21);
			org.set(keyField, oid);
			isAdd = true;
			result = DBDYDao.insert(ac.getConnection(), org);
		}
		//操作日志
		if(0 == result) {
			log(ac, LOGLEVEL_W, "SYS01", org.getTableName(), oid, isAdd ? "insert" : "update", "保存组织机构失败!");
			setMessage(ac, "保存组织机构失败!");
		} else {
			log(ac, LOGLEVEL_I, "SYS01", org.getTableName(), oid, isAdd ? "insert" : "update", "保存组织机构成功!");
			setMessage(ac, "保存组织机构成功!");
		}
		
		return CONST_RESULT_AJAX;
	}
	/**
	 * 删除pid的子机构信息
	 * @param ac
	 * @param pid
	 */
	private void deleteChild(ActionContext ac, String pid) {
		if(StringUtils.isNotBlank(pid)) {
			StringBuilder sql = new StringBuilder("SELECT * FROM TB_ORG O WHERE O.P_ID='");
			sql.append(pid).append("'");
					
			DBDYPO[] pos = DBDYDao.selectBySQL(ac.getConnection(), sql.toString());
			
			if(pos.length > 0) {
				for(DBDYPO delPo : pos) {
					DBDYDao.delete(ac.getConnection(), delPo);
					//递归删除下级子机构
					deleteChild(ac, delPo.get("O_ID").toString());
				}
			}
		}
	}

}
