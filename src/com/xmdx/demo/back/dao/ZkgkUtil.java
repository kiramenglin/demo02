package com.xmdx.demo.back.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import jxl.Cell;
import jxl.Sheet;

import org.apache.axis.client.Call;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.wmf.tosvg.WMFTranscoder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.e9rj.platform.common.BaseConstants;
import com.e9rj.platform.common.Constants;
import com.e9rj.platform.common.GenID;
import com.e9rj.platform.common.StringUtil;
import com.e9rj.platform.common.bean.UserBean;
import com.e9rj.platform.util.SessionUtil;
import com.e9rj.platform.util.SsoUtil;
import com.e9rj.platform.util.WebServiceClient;
import com.e9rj.zkgk.backstage.station.dao.SubStationDao;
import com.e9rj.zkgk.system.ZkgkConstants;
import com.xmzy.frameext.json.FastJsonUtil;
import com.xmzy.frameext.simpledb.DBConn;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.frameext.simpledb.page.JdbcPage;
import com.xmzy.framework.context.ActionContext;
import com.xmzy.framework.core.FrameworkConstant;
import com.xmzy.framework.service.MessageService;

public class ZkgkUtil {

	private static final String ORGCODE = "ORGCODE";

	private static final String ATTACH_ID = "ATTACH_ID";

	private static final String ATTACHNAME = "ATTACHNAME";

	private static final String ATTACHPATH = "ATTACHPATH";

	private static final String PAPER_QS_LIST = "paperQsList";

	private static final String UNCHECKED = "unchecked";

	/**
	 * 获取当前用户所在单位的ID
	 * 
	 * @param ac
	 * @return
	 */
	public static String getEntId(ActionContext ac) {
		return SessionUtil.getOrgCode(ac);
	}

	public static String getOpNo(ActionContext ac) {
		UserBean userpo = ((UserBean) ac.getObjValue("SESSION_USER_BEAN"));
		return userpo.getSysUserPO().getOpNo();
	}

	public static String getOpName(ActionContext ac) {
		UserBean userpo = ((UserBean) ac.getObjValue("SESSION_USER_BEAN"));
		return userpo.getSysUserPO().getOpName();
	}

	/**
	 * 获取用户所属的子站的组织结构代码
	 * @param ac
	 * @return
	 */
	public static String getUserStationOrgCode(ActionContext ac) {
		// 在已有的子站中找不到对应的组织结构，那么当前用户就属于e9rj
		UserBean ub = ((UserBean) ac.getObjValue("SESSION_USER_BEAN"));
		DBDYPO userExtendPo = (DBDYPO) ub.getSysUserExtendPO();

		if (userExtendPo == null) {
			userExtendPo = new DBDYPO();
		}
		Object code = userExtendPo.get("STATION_ORGCODE");
		if (isNotBlank(code)) {
			return code.toString();
		}

		boolean flag = true;
		String userOrgCode = SsoUtil.getSsoAttribute(ac, ZkgkConstants.ORGCODE);
		String sql = "SELECT ORGCODE FROM TS_SUB_STATION";
		DBDYPO[] arr = DBDYDao.selectBySQL(ac.getConnection(), sql);
		if (arr != null && arr.length > 0) {
			for (DBDYPO dbdypo : arr) {
				if (dbdypo.get(ZkgkConstants.ORGCODE).toString().equals(userOrgCode)) {
					flag = false;
					userExtendPo.set("STATION_ORGCODE", userOrgCode);
					ac.setObjValue("SESSION_USER_BEAN", ub);
					break;
				}
			}
		}

		if (flag) {
			userExtendPo.set("STATION_ORGCODE", ZkgkConstants.SUB_SITECODE_E9RJ);
			ac.setObjValue("SESSION_USER_BEAN", ub);
		}

		return ZkgkConstants.SUB_SITECODE_E9RJ;
	}

	/**
	 * 判断当前用户类型
	 * 
	 * @param ac
	 * @return
	 */
	public static String getUsertype(ActionContext ac) {
		return SsoUtil.getSsoAttribute(ac, "USERTYPE");
	}

	/**
	 * 取当前用户学校ID
	 * 
	 * @param ac
	 * @return
	 */
	public static String getSchoolId(ActionContext ac) {
		return SsoUtil.getSsoAttribute(ac, ORGCODE);
	}

	/**
	 * 取当前用户opno
	 * 
	 * @param ac
	 * @return
	 */
	public static String getUserOpno(ActionContext ac) {
		return SsoUtil.getSsoAttribute(ac, "OPNO");
	}
	/**
	 * 判断当前用户是否主考院校
	 * 
	 * @param ac
	 * @return
	 */
	public static boolean isUsertype2(ActionContext ac) {
		if (ZkgkConstants.USERTYPE_2.equals(ZkgkUtil.getUsertype(ac))) {
			return true;
		}
		return false;
	}

	/**
	 * 判断当前用户是否助学院校
	 * 
	 * @param ac
	 * @return
	 */
	public static boolean isUsertype3(ActionContext ac) {
		if (ZkgkConstants.USERTYPE_3.equals(ZkgkUtil.getUsertype(ac))) {
			return true;
		}
		return false;
	}

	/**
	 * 判断传入的object对象是否为空
	 * 
	 * @param ac
	 * @return
	 */
	public static boolean isNotBlank(Object obj) {
		if (obj != null && StringUtils.isNotBlank(obj.toString())) {
			return true;
		}
		return false;
	}

	public static String getUserID(ActionContext ac) {
		HashMap hm = (HashMap) ac.getObjValue("ssoUserInfo");
		return String.valueOf(hm.get("PERSON_ID"));
	}
	
	public static String getUserIdFromSession(ActionContext ac) {
		Object sessionBean = ac.getObjValue("SESSION_USER_BEAN");
		return null == sessionBean ? "" : ((UserBean) sessionBean).getSysUserPO().getPersonId();
	}

	/**
	 * 统一平台 webSerbice调用服务
	 * 
	 * @param serviceName
	 * @param method
	 * @param params
	 * @return
	 */
	public static String callWebService(ActionContext ac, String serviceName, String method, Object[] params) {

		String serviceUrl = "";
		String result = "";

		if (StringUtils.isBlank(serviceName) || StringUtils.isBlank(method)) {
			String errMsg = StringUtils.isBlank(serviceName) ? "服务名为空，调用服务失败！" : "服务方法为空，调用服务失败！";
			MessageService.errString(errMsg);
			return "";
		}

		if (null == ac) {
			Connection conn = null;
			try {
				conn = DBConn.getConnection();
				serviceUrl = BaseConstants.getGlobalValue( "9");
			} catch (Exception e) {
				MessageService.errString("调用web服务失败！", e);
			} finally {
				DBConn.closeConnection(conn);
			}
		} else {
			serviceUrl = BaseConstants.getGlobalValue("9");
		}

		if (StringUtils.isBlank(serviceUrl)) {
			MessageService.errString("未设置参数号为9的系统参数（平台管理webService路径）");
			return "";
		}
		serviceUrl += "/" + serviceName;

		// 创建一个服务(service)调用(call)
		org.apache.axis.client.Service service = new org.apache.axis.client.Service();
		try {

			// 通过service创建call对象
			Call call = (Call) service.createCall();
			// 设置service所在URL
			call.setTargetEndpointAddress(new java.net.URL(serviceUrl));
			call.setOperationName(method);

			result = (String) call.invoke(null == params ? new Object[] {} : params);
		} catch (Exception e) {
			MessageService.errString(MessageService.getMessage("MSG_WEBSERVICE_FAILURE", new String[] { serviceUrl, method }));
		}

		return result;

	}

	/**
	 * 复制附件（含本地文件）
	 * 
	 * @param ac
	 * @param attachfilePoArr
	 * @return
	 */
	public static int copyAttachFile(ActionContext ac, String qsIdSql, Map<Object, Object> map, String tablename) {
		int k = 0;
		String sql = "SELECT * FROM TB_ATTACHFILE WHERE REC_ID IN (" + qsIdSql + ")";
		DBDYPO[] filePoArr = DBDYDao.selectBySQL(ac.getConnection(), sql);
		DBDYPO[] attachfilePoArr = new DBDYPO[filePoArr.length];
		String opNo = ZkgkUtil.getOpNo(ac);
		Date date = new Date();
		int sixTeenLen = 16;
		for (DBDYPO dbdypo : filePoArr) {
			DBDYPO file = new DBDYPO();
			file.setTableName("TB_ATTACHFILE");
			file.setKeyField(ATTACH_ID);
			file.set(ATTACH_ID, "FILE" + GenID.gen(sixTeenLen));
			file.set("ATTACHTYPE", dbdypo.get("ATTACHTYPE"));
			file.set("REC_ID", map.get(dbdypo.get("REC_ID")));
			file.set("TABLENAME", tablename);
			file.set(ATTACHNAME, dbdypo.get(ATTACHNAME));
			file.set(ATTACHPATH, dbdypo.get(ATTACHPATH));
			file.set("ATTACHSIZE", dbdypo.get("ATTACHSIZE"));
			file.set("UPLOADDATE", dbdypo.get("UPLOADDATE"));
			file.set("ATTACHORDER", dbdypo.get("ATTACHORDER"));
			file.set("CREATEDATE", date);
			file.set("CREATEBY", opNo);
			file.set("CAN_DEL", dbdypo.get("CAN_DEL"));
			attachfilePoArr[k++] = file;
		}

		if (attachfilePoArr == null || attachfilePoArr.length == 0) {
			return 1;
		}

		String pathSeparator = "/";
		String basePath = BaseConstants.getGlobalValue("4");
		String path = basePath;

		String month = new SimpleDateFormat("yyyyMM").format(new Date());

		if (!path.startsWith(pathSeparator)) {
			// linux下或unix下的文件目录
			path = pathSeparator + path;
		}

		if (!path.endsWith(pathSeparator)) {
			path = path + pathSeparator;
		}

		path = path + month + pathSeparator;
		if (!(new File(path)).exists()) {
			(new File(path)).mkdirs();
		}

		// 把文件复制一份
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int year = cal.get(Calendar.YEAR);
		int mon = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		int nine = 9;
		String now = "" + year + (mon > nine ? mon : "0" + mon) + (day > nine ? day : "0" + day) + (hour > nine ? hour : "0" + hour) + (minute > nine ? minute : "0" + minute) + (second > nine ? second : "0" + second);
		for (DBDYPO dbdypo : attachfilePoArr) {
			if (dbdypo == null) {
				continue;
			}

			// 把文件复制一份
			File file = new File(basePath + dbdypo.get(ATTACHPATH).toString());
			String tmpName = path + now + file.getName();
			File newFile = new File(tmpName);
			try {
				FileUtils.copyFile(file, newFile);
			} catch (IOException e) {
			}

			// 更新路径
			dbdypo.set(ATTACHPATH, tmpName.substring(tmpName.indexOf(basePath) + basePath.length()));
		}
		return DBDYDao.insert(ac.getConnection(), attachfilePoArr);
	}

	public static String queryOrgTree(String orgCode) throws Exception {
		Connection conn = null;
		String jsonString = "";
		try {
			conn = DBConn.getConnection();
			jsonString = (String) WebServiceClient.invoke(BaseConstants.getGlobalValue("9", "") + "Org.jws", "listOrgByOrgCode", new Object[] { orgCode, "" });

			JSONObject jsonObj = JSONObject.parseObject(jsonString);

			List<DBDYPO> orgs = FastJsonUtil.jsonArray2DbdypoList(jsonObj.getJSONArray("Rows"));

			jsonString = list2ZTreeString(orgs);
		} catch (Exception e) {
			MessageService.errString("", e);
		} finally {
			DBConn.closeConnection(conn);
		}
		return jsonString;
	}

	public static List<DBDYPO> queryPost() {

		Connection conn = null;
		String jsonString = "";
		try {
			conn = DBConn.getConnection();
			jsonString = (String) WebServiceClient.invoke(BaseConstants.getGlobalValue("9", "") + "Post.jws", "queryAll", new Object[] {});
			if (StringUtils.isNotBlank(jsonString)) {
				JSONObject jsonObj = JSONObject.parseObject(jsonString);
				return FastJsonUtil.jsonArray2DbdypoList(jsonObj.getJSONArray("Rows"));
			}
		} catch (Exception e) {
			MessageService.errString("岗位服务queryAll查询失败", e);
		} finally {
			DBConn.closeConnection(conn);
		}

		return Collections.EMPTY_LIST;
	}

	// 把poList转为ztree的数据格式
	private static String list2ZTreeString(List<DBDYPO> list) {
		JSONArray jsonArray = new JSONArray();
		JSONObject pObj = new JSONObject();
		pObj.put("id", "0");
		pObj.put("pId", "-1");
		pObj.put("name", "组织机构");
		pObj.put("isParent", "true");
		pObj.put("click", "setOrgInfo('','','所有')");
		jsonArray.add(pObj);
		if (list == null) {
			return jsonArray.toString();
		}
		for (DBDYPO po : list) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", po.get(ORGCODE));
			jsonObject.put("pId", po.get("PARENTORGCODE"));
			jsonObject.put("name", po.get("ORGNAME") + "(" + ("1".equals(po.get("ORGTYPE")) ? "机构" : "部门") + ")");
			jsonObject.put("click", "setOrgInfo('" + po.get(ORGCODE) + "','" + po.get("CONTEXTCODE") + "','" + po.get("ORGNAME") + "')");
			jsonArray.add(jsonObject);
		}
		return jsonArray.toString();
	}
	/**
	 * 实现List转换成Map 
	 * @param list	需转换的list
	 * @param cno	Map key字段
	 * @param val	Map value字段
	 * @return
	 */
    public static Map<String, String> listZTreeMap(List<DBDYPO> list,String cno,String val) {  
  
        Map<String, String> map = new HashMap<String, String>();  
  
        if ((list != null) && (list.size() != 0)) {  
            for (DBDYPO po : list) {  
                map.put(po.get(cno).toString(),po.get(val).toString()); 
            }  
        }  
        return map;  
    }  
	/**
	 * 删除附件记录和附件文件
	 * 
	 * @param ac
	 * @param recIds
	 * @return
	 */
	public static int delAttachFile(ActionContext ac, String recIds) {
		String pathSeparator = "/";
		String path = BaseConstants.getGlobalValue("4");

		if (recIds == null || "".equals(recIds.trim())) {
			return 1;
		}

		if (!path.startsWith(pathSeparator)) {
			// linux下或unix下的文件目录
			path = pathSeparator + path;
		}

		if (!path.endsWith(pathSeparator)) {
			path = path + pathSeparator;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM TB_ATTACHFILE WHERE REC_ID IN (" + StringUtil.formatString2Sql(recIds) + ")");
		DBDYPO[] poArr = DBDYDao.selectBySQL(ac.getConnection(), sb.toString());
		if (poArr != null && poArr.length > 0) {
			for (DBDYPO dbdypo : poArr) {
				dbdypo.setTableName("TB_ATTACHFILE");
				dbdypo.setKeyField(ATTACH_ID);

				Object attachpath = dbdypo.get(ATTACHPATH);
				File file = new File(path + attachpath);
				if (file.exists()) {
					boolean falg = file.delete();
					if (!falg) {
						return 0;
					}
				}
			}
		}
		return DBDYDao.delete(ac.getConnection(), poArr);
	}

	/**
	 * 验证导入模板是否正确（导入文件的首列和尾列是否和模板一致）
	 * 
	 * @return
	 */
	public static boolean checkImportExcelModel(Sheet rs, int rowIndex, int firstColumnIndex, String firstColumnName, int lastColumnIndex, String lastColumnName) {
		boolean flag = false;
		Cell[] cellRow = rs.getRow(rowIndex);
		if (lastColumnIndex < cellRow.length) {
			String firsttitle = cellRow[firstColumnIndex].getContents().trim();
			String lasttitle = cellRow[lastColumnIndex].getContents().trim();
			if (firstColumnName.equals(firsttitle) && lastColumnName.equals(lasttitle)) {
				flag = true;
			}
		}

		return flag;
	}

	/**
	 * 分页查询
	 * @param ac
	 * @param sb
	 * @return
	 */
	public static String select2JdbcPage(ActionContext ac, String sql) {
		HttpServletRequest request = ac.getHttpRequest();
		String num = request.getParameter("pageNumber");
		String size = request.getParameter("pageSize");
		
		String pageNumberBack = ac.getHttpRequest().getParameter("page");
		String pageSizeBack = ac.getHttpRequest().getParameter("rows");

		// 每页查询数量、当前页
		int pageSize = 9999;
		int pageNumber = 1;
		if (!StringUtil.StringIsNullOrEmpty(size) && StringUtil.isValidateInteger(size)) {
			pageSize = Integer.parseInt(size);
		} else if (!StringUtil.StringIsNullOrEmpty(pageSizeBack) && StringUtil.isValidateInteger(pageSizeBack)) {
			pageSize = Integer.parseInt(pageSizeBack);
		}

		if (!StringUtil.StringIsNullOrEmpty(num) && StringUtil.isValidateInteger(num)) {
			pageNumber = Integer.parseInt(num);
		} else if (!StringUtil.StringIsNullOrEmpty(pageNumberBack) && StringUtil.isValidateInteger(pageNumberBack)) {
			pageNumber = Integer.parseInt(pageNumberBack);
		}

		JdbcPage jp;
		String dbtype = DBConn.getConnectionType();
		if ("oracle".equalsIgnoreCase(dbtype)) {
			jp = DBDYDao.oracleSelect2JdbcPage(ac.getConnection(), sql, pageNumber, pageSize);
		} else if ("sybase".equalsIgnoreCase(dbtype)) {
			jp = DBDYDao.sybaseSelect2JdbcPage(ac.getConnection(), sql, pageNumber, pageSize);
		} else if ("mssql".equalsIgnoreCase(dbtype)) {
			jp = DBDYDao.select2JdbcPage(ac.getConnection(), sql, pageNumber, pageSize);
		} else if ("mysql".equalsIgnoreCase(dbtype)) {
			jp = DBDYDao.mysqlSelect2JdbcPage(ac.getConnection(), sql, pageNumber, pageSize);
		} else {
			jp = DBDYDao.select2JdbcPage(ac.getConnection(), sql, pageNumber, pageSize);
		}

		String jsonString = FastJsonUtil.jdbcPage2JsonString(jp);
		return jsonString == null ? "" : jsonString;
	}

	/**
	 * 分页查询
	 * @param ac
	 * @param sb
	 * @return
	 */
	public static JdbcPage select2JdbcPage2(ActionContext ac, String sql) {
		HttpServletRequest request = ac.getHttpRequest();
		String num = request.getParameter("pageNumber");
		String size = request.getParameter("pageSize");

		// 每页查询数量、当前页
		int pageSize = 10;
		int pageNumber = 1;
		if (!StringUtil.StringIsNullOrEmpty(size) && StringUtil.isValidateInteger(size)) {
			pageSize = Integer.parseInt(size);
		}
		if (!StringUtil.StringIsNullOrEmpty(num) && StringUtil.isValidateInteger(num)) {
			pageNumber = Integer.parseInt(num);
		}

		return DBDYDao.select2JdbcPage(ac.getConnection(), sql, pageNumber, pageSize);
	}
	/**
	 *读取图片地址转换 
	 * @param relatePath 数据库保存的连接无加工程名
	 * @return
	 */
	public static String pathConvert(String relatePath, String host) {

		if(StringUtils.isBlank(relatePath)) {
			return "";
		}
		StringBuilder path = new StringBuilder();
		String allPath = "";
		
		path.append("/");			
		path.append(FrameworkConstant.getAppCode());
		path.append(relatePath);
		
		allPath = path.toString().replaceAll("//", "/");
		path.delete(0, path.length());
		if (allPath.startsWith("/")) {
			path.append(host).append(allPath);
		} else {
			path.append(host).append("/").append(allPath);
		}
		return path.toString();
	}


	/**
	 * 权限验证，除了admin，其余用户只能看见自己本机构的数据
	 * 
	 * @param ac
	 * @param tableAliasName 表别名
	 * @param sql
	 */
	public static void authoritySql(ActionContext ac, String tableAliasName, StringBuilder sql) {
		if (!Constants.SUPER_USER_NAME.equals(SessionUtil.getOpno(ac))) {
			sql.append(" AND ").append(tableAliasName).append(".ENT_ID = '").append(SessionUtil.getOrgCode(ac)).append("' ");
		}
	}
	
	/**
	 * 验证checkAppCode是否具有zkgk应用的权限
	 * @param checkAppCode
	 * @return
	 */
	public static boolean authorityAppCode(String checkAppCode) {
		if(StringUtils.isBlank(checkAppCode)) {
			return false;
		}
		StringBuilder regex = new StringBuilder();
		
		String appCode = FrameworkConstant.getAppCode();	
		regex.append("(^|(\\w*,)*)").append(appCode).append("((,\\w*)*|$)");
		
		return Pattern.matches(regex.toString(), checkAppCode.trim());
	}

	/** 
	 * 删除单个文件 
	 * @param   sPath    被删除文件的文件名 
	 * @return 单个文件删除成功返回true，否则返回false 
	 */
	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/** 
	 * 删除目录（文件夹）以及目录下的文件 
	 * @param   sPath 被删除目录的文件路径 
	 * @return  目录删除成功返回true，否则返回false 
	 */
	public static boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
		}
		if (!flag) {
			return false;
		}
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取sso库下的某个对象
	 *  
	 * @param tableName 表名，大写
	 * @param keyName 主键或者具有唯一性的列， 大写
	 * @param keyValue keyName对应的值
	 * @return
	 */
	public static DBDYPO getSsoPoById(String tableName, String keyName, String keyValue) {
		DBDYPO po = null;
		Connection ssoConn = null;
		try {
			ssoConn = DBConn.getConnection("SSOdbService");
			String sql = "SELECT * FROM " + tableName + " WHERE " + keyName + " = '" + keyValue + "'";
			DBDYPO[] arr = DBDYDao.selectBySQL(ssoConn, sql);
			if (arr != null && arr.length == 1) {
				po = arr[0];
			}
		} catch (Exception e) {
			MessageService.errString(e.getMessage());
		} finally {
			DBConn.closeConnection(ssoConn);
		}
		return po;
	}

	/**
	 *  获取当前时间的字符串
	 * @param isMilliSecond 是否需要毫秒
	 * @return 如20140101130101 或 20140101130101001
	 */
	public static String getCurrTime(boolean isMilliSecond) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);
		int mi = cal.get(Calendar.MILLISECOND);
		String result = "" + year + (month < 10 ? "0" + month : month) + (day < 10 ? "0" + day : day) + (hour < 10 ? "0" + hour : hour) + (min < 10 ? "0" + min : min) + (sec < 10 ? "0" + sec : sec);
		if(isMilliSecond){
			result += +mi;
		}
		return result;
	}

	/**
	 * wmf格式的图片转成jpg
	 * @param wmfPath
	 * @throws Exception
	 */
	public static void wmfToJpg(String wmfPath) throws Exception {
		File wmf = new File(wmfPath);
		FileInputStream wmfStream = new FileInputStream(wmf);
		ByteArrayOutputStream imageOut = new ByteArrayOutputStream();
		int noOfByteRead = 0;
		while ((noOfByteRead = wmfStream.read()) != -1)
		{
			imageOut.write(noOfByteRead);
		}
		imageOut.flush();
		imageOut.close();

		WMFTranscoder transcoder = new WMFTranscoder();
		TranscodingHints hints = new TranscodingHints();
		hints.put(ImageTranscoder.KEY_WIDTH, 20f);
		hints.put(ImageTranscoder.KEY_HEIGHT, 20f);

		transcoder.setTranscodingHints(hints);
		TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(imageOut.toByteArray()));
		ByteArrayOutputStream svg = new ByteArrayOutputStream();
		TranscoderOutput output = new TranscoderOutput(svg);
		transcoder.transcode(input, output);

		ImageTranscoder it = new JPEGTranscoder();
		ByteArrayOutputStream jpg = new ByteArrayOutputStream();
		it.transcode(new TranscoderInput(new ByteArrayInputStream(svg.toByteArray())), new TranscoderOutput(jpg));
		String jpgFile = StringUtils.replace(wmfPath, "wmf", "jpg");
		FileOutputStream jpgOut = new FileOutputStream(jpgFile);
		jpgOut.write(jpg.toByteArray());
		jpgOut.flush();
		jpgOut.close();

	}
	/**
	 * 根据子站对应的组织机构代码 获取子站对象
	 * @param ac
	 * @return
	 * @throws IOException
	 */
	public static void getStationByOrgcode(ActionContext ac)  {
		SubStationDao subStationDao = new SubStationDao();
		String subSiteOrg =  ac.getHttpRequest().getParameter(ZkgkConstants.SUB_SITE_ORG_KEY);
		if(!StringUtils.isNotBlank(subSiteOrg)){
			subSiteOrg=(String) ac.getHttpRequest().getAttribute(ZkgkConstants.SUB_SITE_ORG_KEY);
		}
		ac.getHttpRequest().getAttribute(ZkgkConstants.SUB_SITE_ORG_KEY);
		ac.setStringValue(ZkgkConstants.SUB_SITE_ORG_KEY, subSiteOrg);
		ac.getHttpRequest().setAttribute(ZkgkConstants.ORGCODE,  subSiteOrg);
		DBDYPO po = subStationDao.getPo(ac);
		ac.setObjValue("STATIONURL", po.get("STATION_URL"));
		/*ac.setObjValue("HEADLOGOIMG", po.get("LOGO_PATH"));*/
		
		/**
		 * create by lhj 0924 beg
		 */
		if(null != po){
			String sql = "SELECT * FROM TB_ATTACHFILE WHERE REC_ID = '" + po.get("SUB_STATION_ID") + "'";
			DBDYPO[] attachArr = DBDYDao.selectBySQL(ac.getConnection(), sql);
			Map<String, String> imgMap = new HashMap<String, String>();
			if (null != attachArr) {
				for(DBDYPO attachpo : attachArr){
					if (!imgMap.containsKey(attachpo.get("attachtype".toUpperCase()))) {
						imgMap.put(attachpo.get("attachtype".toUpperCase()).toString(), attachpo.get("attachpath".toUpperCase()).toString());
					}
				}
			}
			ac.setObjValue("IMG_PATH", BaseConstants.getGlobalValue("8") + imgMap.get("IMGPATH"));
			ac.setObjValue("HEADLOGOIMG", BaseConstants.getGlobalValue("8") + imgMap.get("LOGOPATH"));
		}
		/**
		 * create by lhj 0924 end
		 */
		
		ac.setObjValue("STATION_COPYRIGHT", po.get("STATION_COPYRIGHT"));
	}
	/**
	 * 判断用户是否有系统参数系统控制权限，判断有符合一个就跳出
	 * @param appCode session 登陆用户的APP_CODE
	 * @param system	系统参数1334值
	 * @return flg表示无控制权限，true有控制权限
	 */
	public static boolean appCodeAuthority(String appCode,String system){
		boolean flg=false;
		String[] arr=appCode.split(",");
		String[] sysarr=system.split(",");
			of:for(int i=0;i<arr.length;i++){
				for(int j=0;j<sysarr.length;j++){
					if(arr[i].equals(sysarr[j])){
						flg=true;
						break of;
					}
				}
				
			}
		return flg;
	}
}
