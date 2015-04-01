package com.e9rj.zkgk.backstage.learncourse.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.e9rj.platform.common.BaseConstants;
import com.e9rj.platform.common.CONVERTSQL;
import com.e9rj.platform.common.Constants;
import com.e9rj.platform.common.StringUtil;
import com.e9rj.platform.common.dao.BaseDao;
import com.e9rj.platform.util.SessionUtil;
import com.e9rj.platform.util.SsoUtil;
import com.e9rj.zkgk.backstage.statistics.dao.StudentScoreDao;
import com.e9rj.zkgk.system.ZkgkConstants;
import com.e9rj.zkgk.util.ZkgkUtil;
import com.xmzy.frameext.simpledb.DBConn;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.frameext.simpledb.page.JdbcPage;
import com.xmzy.framework.context.ActionContext;
import com.xmzy.framework.service.MessageService;
import com.xmzy.framework.utils.DateUtil;

public class CourseDao extends BaseDao {
	// 表名
	private static final String TABLE_NAME = "OL_COURSE";
	
	// 主健
	private static final String TABLE_KEY = "COURSE_ID";
	
	/**
	 * create by lhj begin
	 */
	KnowledgeDao wKnowledgeDao = new KnowledgeDao();
	StudentScoreDao studentScoreDao = new StudentScoreDao();
	
	/**
	 * 用户已购的课程查询sql
	 * @param ac
	 * @param sql
	 */
	public void genQueryCourseSql(ActionContext ac, StringBuffer sql) {

		sql.append("SELECT AC.USER_ID, C.COURSE_ID, C.COURSE_CODE, C.COURSE_NAME, C.COVER_PIC_PATH,C.ENT_ID, ");
		sql.append("IFNULL(UCS.CURRENT_LEARN,0) AS CURRENT_LEARN,IFNULL(UCS.CURRENT_KNOWLEDGE,0) AS CURRENT_KNOWLEDGE, ");
		sql.append("IFNULL(UCS.CURRENT_PHASE,0) AS CURRENT_PHASE,IFNULL(UCS.CURRENT_COMPLEX,0) AS CURRENT_COMPLEX ");
		sql.append("from OL_APPLY_COURSE AC LEFT JOIN ");
		sql.append("(SELECT SS.STUDENT_ID,C.COURSE_ID, C.COURSE_CODE, C.COURSE_NAME, C.COVER_PIC_PATH,");
		sql.append("ifnull( round(((SUM((CASE WHEN SR.RATE_TYPE in(1,2,3) THEN (");
		sql.append("CASE WHEN (CASE WHEN SR.RATE_TYPE = '2' OR SR.RATE_TYPE = '3' THEN ROUND(SS.CURRENT_DATA/60,1) ELSE SS.CURRENT_DATA END) > SR.FULL_CONDITION THEN SR.FULL_CONDITION ELSE (CASE WHEN SR.RATE_TYPE = '2' OR SR.RATE_TYPE = '3' THEN ROUND(SS.CURRENT_DATA/60,1) ELSE SS.CURRENT_DATA END) END) ELSE 0 END))");
		sql.append("/SUM((CASE WHEN SR.RATE_TYPE in(1,2,3) THEN SR.FULL_CONDITION ELSE 0 END))) * 100 ), 0 ), 0) AS CURRENT_LEARN,");
		sql.append("ifnull( round(((SUM((CASE WHEN SR.RATE_TYPE = 11 THEN (CASE WHEN SS.CURRENT_DATA > SR.FULL_CONDITION THEN SR.FULL_CONDITION ELSE SS.CURRENT_DATA END) ELSE 0 END))");
		sql.append("/SUM((CASE WHEN SR.RATE_TYPE = 11 THEN SR.FULL_CONDITION ELSE 0 END))) * 100 ), 0 ), 0) AS CURRENT_KNOWLEDGE,");
		sql.append("ifnull( round(((SUM((CASE WHEN SR.RATE_TYPE = 12 THEN SS.CURRENT_DATA ELSE 0 END))/SUM((CASE WHEN SR.RATE_TYPE = 12 THEN SR.FULL_CONDITION ELSE 0 END))) * 100 ), 0 ), 0) AS CURRENT_PHASE,");
		sql.append("ifnull( round(((SUM((CASE WHEN SR.RATE_TYPE = 13 THEN SS.CURRENT_DATA ELSE 0 END))/SUM((CASE WHEN SR.RATE_TYPE = 13 THEN SR.FULL_CONDITION ELSE 0 END))) * 100 ), 0 ), 0) AS CURRENT_COMPLEX");
		sql.append(" FROM OL_COURSE C,OL_SCORE_RATE SR ");
		sql.append("LEFT JOIN OL_STUDENT_SCORE SS  ON SR.SCORE_RATE_ID = SS.SCORE_RATE_ID AND SS.STUDENT_ID='").append(ZkgkUtil.getUserID(ac)).append("' ");
		sql.append("WHERE SR.COURSE_ID = C.COURSE_ID ");
		sql.append("GROUP BY C.COURSE_ID) ");
		sql.append(" UCS ON (AC.USER_ID= UCS.STUDENT_ID AND AC.COURSE_ID = UCS.COURSE_ID),OL_COURSE C ");
		sql.append("WHERE  C.COURSE_ID = AC.COURSE_ID AND AC.USER_ID = '").append(ZkgkUtil.getUserID(ac)).append("' AND AC.APPLY_STATE IN ('2', '3')");
	}

	/**
	 * 首次-推荐课程查询sql----只推荐本站的课程
	 * @param ac
	 * @param sql
	 */
	public void genQueryRecomCourseSql(ActionContext ac, StringBuffer sql) {
		String orgCode = ac.getHttpRequest().getParameter(ZkgkConstants.SUB_SITE_ORG_KEY);
		// 推荐前6
		int getRecords = 6;
		int dbType = DBConn.getConnectionType(true);
		if (StringUtils.isNotBlank(orgCode)) {
			sql.append("SELECT");
			if (DBConn.DBTYPE_SYBASE == dbType || DBConn.DBTYPE_MSSQL == dbType) {
				sql.append(CONVERTSQL.getNumberRecord(getRecords));
			}
			sql.append(" C.COURSE_ID, C.COURSE_NAME, C.COURSE_CODE,C.COVER_PIC_PATH,C.LECTOR,(SELECT COUNT(DISTINCT A.USER_ID) ");
			sql.append("FROM OL_APPLY_COURSE A WHERE C.COURSE_ID=A.COURSE_ID AND A.APPLY_STATE IN ('2', '3')) JOINED_CNT ");
			sql.append("FROM OL_COURSE C ");
			if (ZkgkConstants.SUB_SITECODE_E9RJ.equals(orgCode) && !isExistSubSation(ac, SessionUtil.getOrgCode(ac))) {
				// 当前用户是zkxj的学生且登录的子站是e9rj，推荐专业下的课程。
				// 代码行1：关联自考衔接的课程。（原因是专业课程表不存在课程code，只存课程ID）
				sql.append("LEFT JOIN " + ZkgkConstants.PRE_ZKXJ + "EB_COURSE ZC ON C.COURSE_CODE = ZC.COURSECODE ");
				// 代码行2：关联与课程关联的专业。
				sql.append("LEFT JOIN " + ZkgkConstants.PRE_ZKXJ + "EB_SUBJECT_COURSE SC ON ZC.COURSE_ID = SC.COURSE_ID ");
				sql.append("LEFT JOIN " + ZkgkConstants.PRE_ZKXJ + "EB_SUBJECT S ON S.SUBJECT_ID = SC.SUBJECT_ID ");
				sql.append("," + ZkgkConstants.PRE_ZKXJ + "EB_SCHOOLROLL ES ");
				sql.append("WHERE 1=1 ");
				// （查询专业下的课程或者非某专业（不归属某个专业的课程，也就是后台创建的课程不是从【课程弹出框】选择的而是自主创建的课程，不存在与zkxj的课程关联）的课程；
				// 问题：当管理员创建的课程Code存在与zkxj相冲突的课程Code（这样的话就关联上了某个专业，而实际上该课程并不属于任何专业），
				// 这又该怎么处理该课程是否归属--某专业下的课程？？

				// SC.SUBJECT_ID IS NULL表示不与专业关联的课程(也就管理员自己在zkgk创建的课程)
				// 其实对学生这类用户推荐的课程来说不存在公开不公开的概念，只有是否本专业课程罢了。）
				sql.append("AND ((ES.SUBJECT_ID = SC.SUBJECT_ID  AND S.SUBJECT_ID = ES.SUBJECT_ID AND ES.STUDENT_ID = '" + ZkgkUtil.getUserID(ac) + "' )OR(SC.SUBJECT_ID IS NULL)) ");
				// 补充：这里是否需要介入"核心课程"与"衔接课程"的概念？？？（概念简单回顾：核心课程属主考院校开的课程；衔接课程属助学院校安排的课程）
			} else {
				if (orgCode.equals(ZkgkUtil.getEntId(ac))) {
					// 本组织机构的用户
					sql.append("LEFT JOIN OL_ROLE_COURSE RC ON C.COURSE_ID = RC.COURSE_ID ");
					sql.append("LEFT JOIN " + Constants.DATABASE_SSO + "TS_OPROLE OPRO ON RC.ROLE_ID = OPRO.ROLEID ");
					// 推荐公开或关联角色的课程
					sql.append("WHERE (OPRO.OPNO='" + SessionUtil.getOpno(ac) + "' OR C.OPEN_RANK='1') ");
				} else {
					// 非本组织机构的用户，推荐公开课程。
					sql.append(" WHERE C.OPEN_RANK='1' ");
				}
			}
			sql.append("AND C.ENABLED='1' AND C.ISSUE_STATE='1' AND C.IS_LOGIN_SEE <> '1' AND C.ENT_ID = '").append(orgCode)
					// 在学、结业的不推荐
					.append("' AND NOT EXISTS ( SELECT 1 FROM OL_APPLY_COURSE AC WHERE AC.COURSE_ID = C.COURSE_ID AND AC.USER_ID = '");
			sql.append(ZkgkUtil.getUserID(ac)).append("' AND AC.APPLY_STATE IN ('2', '3')) ");
			if (DBConn.DBTYPE_ORACLE == dbType) {
				sql.append(CONVERTSQL.getNumberRecord(getRecords));
			}
			sql.append("GROUP BY C.COURSE_ID  ORDER BY ");
			if (!isExistSubSation(ac, SessionUtil.getOrgCode(ac)) && ZkgkConstants.SUB_SITECODE_E9RJ.equals(orgCode)) {
				// 有专业按专业排序
				sql.append(CONVERTSQL.isnullSQL("S.SUBJECT_ID", "0") + " DESC,");
			}
			sql.append("JOINED_CNT DESC,C.ISSUE_TIME DESC");

			if (DBConn.DBTYPE_MYSQL == dbType) {
				sql.append(CONVERTSQL.getNumberRecord(getRecords));
			}
		}
	}

	public boolean isExistSubSation(ActionContext ac, String orgcode) {
		String sql = "SELECT COUNT(1) FROM TS_SUB_STATION WHERE ORGCODE='" + orgcode + "'";
		Object obj = DBDYDao.selectOneFieldValueBySQL(ac.getConnection(), sql);
		if (null != obj && Integer.valueOf(obj.toString()) > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 查询课程
	 * 
	 * @param ac
	 * @return DBDYPO[]
	 */
	public JdbcPage queryCourse(ActionContext ac) {
		StringBuffer sql = new StringBuffer();
		genQueryCourseSql(ac, sql);
		// 查询学生在学与结课的课程
		
		// sql.append(" AND AC.CREDENTIAL_ID IS NOT NULL ");
		sql.append(" ORDER BY ifnull(C.ORDER_BY,99999999) asc");
		// sql.append(" AND AC.USER_ID is null");
		return ZkgkUtil.select2JdbcPage2(ac, sql.toString());
	}

	/**
	 * 查询用户已有课程
	 * create by rgy 140722
	 * @param ac GROUP_CONCAT(ORGNAME SEPARATOR ' , ') AS ORGNAME
	 * @return DBDYPO[]
	 */
	public String queryUserCourse(ActionContext ac) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT GROUP_CONCAT(AC.COURSE_ID SEPARATOR ',') AS COURSE_ID ");
		sql.append("FROM OL_APPLY_COURSE AC LEFT JOIN v_user_course_schedule UCS ON (AC.USER_ID= UCS.STUDENT_ID AND AC.COURSE_ID = UCS.COURSE_ID),OL_COURSE C ");
		sql.append("WHERE  C.COURSE_ID = AC.COURSE_ID AND AC.APPLY_STATE IN ('2', '3')");
		sql.append(" AND AC.USER_ID = '").append(ZkgkUtil.getUserID(ac)).append("'");
		DBDYPO[] po = DBDYDao.selectBySQL(ac.getConnection(), sql.toString());
		if(po.length==0){
			return null;
		}
		if(!ZkgkUtil.isNotBlank(po[0].get("COURSE_ID"))){
			return null;
		}
		return po[0].get("COURSE_ID").toString();
	}
	
	/**
	 * 查询是否购买过此课程
	 * create by rgy 140708
	 * @param ac
	 * @return DBDYPO[]
	 */
	public DBDYPO queryHasCourse(ActionContext ac,String courseId) {
		if(StringUtils.isBlank(courseId)){
			return null;
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * FROM OL_APPLY_COURSE WHERE COURSE_ID ='").append(courseId).append("' AND USER_ID = '");
		sql.append(ZkgkUtil.getUserID(ac)).append("' AND APPLY_STATE IN ('2','3')");
		DBDYPO[] po = DBDYDao.selectBySQL(ac.getConnection(), sql.toString());
		if(po.length>0){
			return po[0];
		}
		return null;
	}
	
	/**
	 * coursetpe : buyed 在学课程；recom：推荐课程
	 * @param ac
	 * @param courseType
	 * @return
	 */

	public int countCourse(ActionContext ac, String courseType) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(1) FROM OL_COURSE C WHERE 1=1 ");
		if (StringUtils.isBlank(courseType)) {
			sql.append("AND FALSE ");
		}

		if ("buyed".equals(courseType)) {
			sql=sql.delete(0, sql.length());
			sql.append("SELECT COUNT(AC.COURSE_ID) FROM OL_APPLY_COURSE AC WHERE AC.USER_ID = '");
			sql.append(ZkgkUtil.getUserID(ac)).append("' AND AC.APPLY_STATE IN ('2','3')");
//			sql.append(" AND EXISTS  ");
//			sql.append("( SELECT 1 FROM OL_APPLY_COURSE AC WHERE AC.COURSE_ID = C.COURSE_ID AND AC.USER_ID = '")
//					.append(ZkgkUtil.getUserID(ac))
//					.append("' AND AC.APPLY_STATE IN ('2','3'))").append(" AND C.ISSUE_STATE = '1'");
		} else if ("recom".equals(courseType)) {
			sql.delete(0, sql.length());
			genQueryRecomCourseSql(ac, sql);
			if(StringUtils.isNotBlank(sql.toString())) {
				sql.insert(0, "SELECT COUNT(1) FROM (").append(") T");
			} else {
				return 0;
			}
		}
		Object obj = DBDYDao.selectOneFieldValueBySQL(ac.getConnection(), sql.toString());
		return null != obj ? Integer.valueOf(obj.toString()) : 0;
	}

	/**
	 * 查询推荐课程
	 * @param ac
	 * @return
	 */
	public JdbcPage queryNoCourse(ActionContext ac) {
		StringBuffer sql = new StringBuffer();
		genQueryRecomCourseSql(ac, sql);
		// 推荐课程
		return ZkgkUtil.select2JdbcPage2(ac, sql.toString());
	}

	@Override
	public String genQuerySql(ActionContext ac) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 新增用户对课程的点击数
	 * @param ac
	 * @return
	 * @throws Exception
	 */
	public String addStudentHit(ActionContext ac) throws Exception {
		DBDYPO dbdypo = new DBDYPO();
		dbdypo = wKnowledgeDao.learningProcesIsNullOrnot(ac, "'" + ZkgkConstants.RATE_TYPE_ONE + "'")[0];
		dbdypo.set("STUDENT_SCORE_ID", super.genIdString("SS", 20));
		dbdypo.set("SCORE_RATE_ID", dbdypo.get("SCORE_RATE_ID"));
		dbdypo.set("CURRENT_DATA", 1);
		dbdypo.set("CURRENT_SCORE", wKnowledgeDao.currentScore(dbdypo));
		dbdypo.set("STUDENT_ID", ZkgkUtil.getUserID(ac));
		// 取当前时间
		dbdypo.set("LAST_TIME", new java.sql.Timestamp(System.currentTimeMillis()));
		// 登录次数（默认一次）
		dbdypo.set("LONGIN_NUM", 1);

		return studentScoreDao.addPo(ac, dbdypo);
	}

	/**
	 * 更新用户对课程的点击数
	 * @param ac
	 * @return
	 * @throws Exception
	 */
	public String updStudentHit(ActionContext ac) throws Exception {
		DBDYPO dbdypo = new DBDYPO();

		dbdypo = wKnowledgeDao.learningProcesIsNullOrnot(ac, "'" + ZkgkConstants.RATE_TYPE_ONE + "'")[0];
		// 判断是否为当前
		String currentdate = DateUtil.toStrDateFromUtilDateByFormat(new Date(), "yyyy-MM-dd");
		String lastdate = StringUtil.converShortDate(dbdypo.get("LAST_TIME") == null ? "" : dbdypo.get("LAST_TIME").toString());
		if (currentdate.equals(lastdate)) {
			// 1、若是；判断登录次数是否超过4次
			String loginNum = BaseConstants.getGlobalValue("1340");
			Object loginNumobj = dbdypo.get("LONGIN_NUM") + "";
			if (loginNum.equals(loginNumobj)) {
				// 1.1已经有4次了跳过累计
				return "1";
			} else {
				// 1.2未超过累加登录次数
				if (null == loginNumobj) {
					dbdypo.set("LONGIN_NUM", 1);
				} else {
					dbdypo.set("LONGIN_NUM", Integer.valueOf(loginNumobj.toString()) + 1);
				}
			}
		} else {
			// 2、若不是当前天。则lastTime保存当前以及登录次数置1
			dbdypo.set("LAST_TIME", new java.sql.Timestamp(System.currentTimeMillis()));
			dbdypo.set("LONGIN_NUM", 1);
		}
		dbdypo.set("CURRENT_DATA", new BigDecimal(dbdypo.get("CURRENT_DATA").toString()).add(new BigDecimal(1)));
		dbdypo.set("CURRENT_SCORE", wKnowledgeDao.currentScore(dbdypo));

		return studentScoreDao.updPo(ac, dbdypo);
	}
	
	/**
	 * 查询课程下有文件的知识点sql
	 * @param ac
	 * @return
	 * @throws Exception
	 */
	public String genQueryCourseknowFileSql(ActionContext ac) throws Exception {
		String courseId = ac.getHttpRequest().getParameter("COURSE_ID");
		if (StringUtils.isBlank(courseId)) {
			return "";
		}
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT F.* FROM OL_COURSE OC, OL_KNOWLEDGE OK,OL_LINK_FILE LF,OL_FILE F ");
		sql.append("WHERE OC.COURSE_ID = OK.COURSE_ID AND OK.KNOWLEDGE_ID = LF.REC_ID AND LF.FILE_ID = F.FILE_ID ");
		sql.append("AND OC.COURSE_ID='").append(courseId).append("'");

		return sql.toString();
	}

	/**
	 * 查询课程下文件sql
	 * @param ac
	 * @return
	 * @throws Exception
	 */
	public String genQueryCourseFileSql(ActionContext ac) throws Exception {
		String courseId = ac.getHttpRequest().getParameter("COURSE_ID");
		if (StringUtils.isBlank(courseId)) {
			return "";
		}
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT F.* FROM OL_COURSE OC,OL_LINK_FILE LF,OL_FILE F ");
		sql.append("WHERE OC.COURSE_ID = LF.REC_ID AND LF.FILE_ID = F.FILE_ID ");
		sql.append("AND OC.COURSE_ID='").append(courseId).append("'");

		return sql.toString();
	}

	/**
	 * 查询课程下有文件的知识点
	 * @param ac
	 * @return
	 * @throws Exception
	 */
	public DBDYPO[] QueryCourseFile(ActionContext ac) throws Exception {
		String courseSql = this.genQueryCourseFileSql(ac);
		if (StringUtils.isBlank(courseSql)) {
			return new DBDYPO[0];
		}
		return DBDYDao.selectBySQL(ac.getConnection(), courseSql);
	}

	/**
	 * 带分页的课程文件
	 */
	public String getCourseFile(ActionContext ac) throws Exception {
		Map<String, List<JSONObject>> map = new HashMap<String, List<JSONObject>>();
		List<JSONObject> templist = null;
		StringBuffer sb = new StringBuffer();
		int j = 0;
		String fileId = "";
		String applytype = ac.getHttpRequest().getParameter("APPLYTYPE");
		String sql = "";
		if ("course".equals(applytype)) {
			sql = wKnowledgeDao.genQueryCourseFile(ac);
		} else if ("knowledge".equals(applytype)) {
			sql = wKnowledgeDao.genQueryCourseknowledgeFile(ac);
		}

		String jsonString = ZkgkUtil.select2JdbcPage(ac, sql);

		JSONObject obj = JSONObject.parseObject(jsonString);
		
		if (obj == null) {
			return jsonString;
		}

		JSONArray jsonArray = JSONObject.parseArray(obj.get("Rows").toString());
		JSONArray newArray = new JSONArray();

		String filetname = "", filetype = "", fileturl = "", uploadtype = "", cpath = "";
		for (int i = 0; i < jsonArray.size(); ++i) {
			JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(i).toString());
			filetname = String.valueOf(jsonObject.get("ATTACHNAME"));
			fileturl = String.valueOf(jsonObject.get("FILE_URL"));
			uploadtype = String.valueOf(jsonObject.get("UPLOAD_MODE"));
			filetype = wKnowledgeDao.belongTowhichType(uploadtype, fileturl, filetname);
			jsonObject.put("FILE_REG", filetype);
			/* 上传方式为“本地服务器”以及“文件库”的文件路径都存放TB_ATTACHFILE中*/
			if ("1".equals(uploadtype) || "4".equals(uploadtype)) {
				jsonObject.put("EXTENSION", filetname.substring(filetname.lastIndexOf(".")));
				/* 路径取得是attachpath */
				// 图片组文件
				if ("pic".equals(filetype)) {
					// 预览图地址或文件地址（如果是多个图片，中间用,号隔开)
					// 封装同一文件的一套图片---begin
					fileId = jsonObject.get("FILE_ID").toString();
					templist = map.get(fileId);
					if (templist != null && templist.size() > 0) {
						templist.add(jsonObject);
					} else {
						templist = new ArrayList<JSONObject>();
						templist.add(jsonObject);
						map.put(fileId, templist);
					}

					if (j == 0) {
						sb.append(fileId);
					}
					j++;
					if (sb.indexOf(fileId) == -1) {
						sb.append(",").append(fileId);
					}
				}else if ("wmv,flv,media".indexOf(filetype) > -1) {
					jsonObject.put("NEW_URL", BaseConstants.getGlobalValue("8") + jsonObject.get("ATTACHPATH"));
					newArray.add(jsonObject);
				} else if ("upload".indexOf(filetype) > -1) {
					jsonObject.put("UPLOAD_ID", jsonObject.get("ATTACH_ID"));
					newArray.add(jsonObject);
				}else {
					/* 路径取得是attachpath */
					cpath = String.valueOf(jsonObject.get("C_ATTACHPATH"));
					// 文档文件
					/*文档类型的文件转换后的路径保存在TB_ATTACHFILE：C_ATTACHPATH*/
					
					if ("pdf".equals(filetype) || "swf".equals(filetype)) {
						jsonObject.put("NEW_URL", BaseConstants.getGlobalValue("8") + jsonObject.get("ATTACHPATH"));
						jsonObject.put("FILE_REG", "swf");
					} else {
						if (StringUtils.isNotBlank(cpath) && !"null".equals(cpath)) {
							jsonObject.put("NEW_URL", BaseConstants.getGlobalValue("8") + cpath);
							jsonObject.put("FILE_REG", "swf");
						} else {
							jsonObject.put("UPLOAD_ID", jsonObject.get("ATTACH_ID"));
							jsonObject.put("FILE_REG", "upload");
						}
					}
					newArray.add(jsonObject);
			}
			}
			/*流媒体服务器以及网路地址超链接的文件地址存放OL_FILE：FILE_URL*/
			if ("3".equals(uploadtype) || "2".equals(uploadtype)) {
				/* 路径取得是fileturl */
				jsonObject.put("FILE_REG", filetype);
				jsonObject.put("NEW_URL", String.valueOf(jsonObject.get("F_SERVER_NAME")) + "/" + fileturl.replaceAll("[\\\\]", "/"));
				newArray.add(jsonObject);
			}			
		}

		if (sb.length() > 0) {
			String[] strarr = sb.toString().split(",");
			List<JSONObject> listArr = new ArrayList<JSONObject>();
			for (String cwId : strarr) {
				listArr = map.get(cwId);
				String picstr = "", desc = "";
				// 单张图片po
				JSONObject simplepo = new JSONObject();
				for (JSONObject listpo : listArr) {
					picstr += "," + (listpo.get("ATTACHPATH") != null ? (BaseConstants.getGlobalValue("8") + listpo.get("ATTACHPATH").toString()) : "");
					desc += "," + "";
					simplepo = listpo;
				}

				if (!"".equals(picstr)) {
					picstr = picstr.substring(1);
					desc = desc.substring(1);
				}

				simplepo.put("NEW_URL", picstr.replaceAll("[\\\\]", "/"));
				simplepo.put("PIC_DESC", desc);
				newArray.add(simplepo);
			}
		}
		obj.remove("Rows");
		obj.put("Rows", newArray);
		
		return obj.toString();
	}

	/**
	 * create by lhj begin
	 */
	/**------------------------------华丽的分割线--ysp--------------------------------------------*/
	/**
	 * 查询组织机构下的课程
	 * @param ac
	 * @return
	 * 
	 * 2014-07-08 ysp
	 */
	public DBDYPO[] orgCodeCourse(ActionContext ac){
		StringBuffer sql=new StringBuffer();
		String orgCode =  ac.getHttpRequest().getParameter(ZkgkConstants.SUB_SITE_ORG_KEY);
		sql.append("SELECT * FROM ").append(TABLE_NAME).append(" O WHERE O.ENT_ID='").append(orgCode).append("'");
		return DBDYDao.selectBySQL(ac.getConnection(), sql.toString());
	}
	/**
	 * 查询组织机构下已和角色关联的课程
	 * @param ac
	 * @return
	 * 
	 * 2014-07-08 ysp
	 */
	public DBDYPO[] roleCourse(ActionContext ac){
		String roleid=ac.getHttpRequest().getParameter("ROLEID");
		StringBuffer sql=new StringBuffer();
		String orgCode =  ac.getHttpRequest().getParameter(ZkgkConstants.SUB_SITE_ORG_KEY);
		sql.append("SELECT * FROM ").append(TABLE_NAME).append(" O WHERE O.ENT_ID='").append(orgCode).append("' ")
		.append(" AND O.COURSE_ID IN (SELECT COURSE_ID FROM OL_ROLE_COURSE R WHERE R.ROLE_ID='").append(roleid).append("')");
		return DBDYDao.selectBySQL(ac.getConnection(), sql.toString());
	}
	/**
	 * 根据条件分页查询所有课程信息（含讲师、课程参与人数统计信息）
	 * @param ac
	 * @return
	 * @throws Exception
	 */
	public JdbcPage queryCurriculum(ActionContext ac, int pageNum, int pageSize) throws Exception{
		StringBuffer sql=new StringBuffer();
		String courseSort = ac.getHttpRequest().getParameter("COURSESORT");
		String courseType = ac.getHttpRequest().getParameter("COURSETYPE");
		String courseName = ac.getHttpRequest().getParameter("COURSENAME");
		
		String sortField  = ac.getHttpRequest().getParameter("sidx");
		String sordMethod = ac.getHttpRequest().getParameter("sord");
		
		String orgCode =  ac.getHttpRequest().getParameter(ZkgkConstants.SUB_SITE_ORG_KEY);
		String currOrgCode = SessionUtil.getOrgCode(ac);
		String userId = ZkgkUtil.getUserID(ac);
		String opno = SessionUtil.getOpno(ac);
				
		sql.append("SELECT * FROM (SELECT C.COURSE_ID, C.COURSE_NAME, C.COURSE_CODE,C.COVER_PIC_PATH,C.LECTOR, C.ISSUE_TIME,");
		sql.append("(SELECT COUNT(DISTINCT A.USER_ID) FROM OL_APPLY_COURSE A WHERE C.COURSE_ID=A.COURSE_ID AND (A.APPLY_STATE='2' OR A.APPLY_STATE='3')) JOINED_CNT, ");
		sql.append(" (SELECT CASE WHEN COUNT(1) > 0 THEN 1 ELSE 0 END FROM OL_APPLY_COURSE AC WHERE C.COURSE_ID=AC.COURSE_ID AND AC.USER_ID='");
		sql.append(userId).append("' AND AC.APPLY_STATE IN ('2','3')) BUYED");
		sql.append(" FROM OL_COURSE C");
		sql.append(" WHERE C.ENABLED='1' AND C.ISSUE_STATE='1' AND C.IS_LOGIN_SEE <> '1'");
		
		if(StringUtils.isNotBlank(courseSort)){
			sql.append(" AND C.COURSE_SORT='").append(courseSort).append("'");
		}
		if(StringUtils.isNotBlank(courseType)){
			sql.append(" AND C.COURSE_TYPE='").append(courseType).append("'");
		}
		if(StringUtils.isNotBlank(courseName)) {
			sql.append(" AND C.COURSE_NAME LIKE '%").append(courseName).append("%'");
		}
		
		//移除非超级管理员，增加课程公开级别为本站或全站课程的过滤条件，
		
		//如果没有传入子站参与
		if(StringUtils.isBlank(orgCode)) {
			orgCode = ZkgkConstants.SUB_SITECODE_E9RJ;
		}
		
		if(currOrgCode.equals(orgCode) || (ZkgkConstants.SUB_SITECODE_E9RJ.equals(orgCode) && !isExistSubSation(ac, currOrgCode)) ) {
			//当前登录人访问所属机构的子站，则查询该子站所有课程.排除角色课程,即指定可查看课程的角色用户，当月用户又没有该角色
			sql.append(" AND C.ENT_ID='").append(orgCode);
			sql.append("' AND NOT EXISTS (SELECT 1 FROM OL_ROLE_COURSE RC WHERE NOT EXISTS ( SELECT 1 FROM ");
			sql.append(ZkgkConstants.SSOdb).append("TS_OPROLE R WHERE R.OPNO='").append(opno);
			sql.append("' AND R.ROLEID=RC.ROLE_ID) AND RC.COURSE_ID=C.COURSE_ID");
			sql.append(" AND NOT EXISTS(SELECT 1 FROM OL_ROLE_COURSE RC1 WHERE RC1.COURSE_ID=RC.COURSE_ID AND EXISTS(SELECT 1 FROM ");
			sql.append(ZkgkConstants.SSOdb).append("TS_OPROLE R1 WHERE R1.OPNO='");
			sql.append(opno).append("' AND R1.ROLEID=RC1.ROLE_ID)))");
			
		} else {
			
			//当前登录人访问的子站非所属机构子站，则查询该子站公开课程
			sql.append(" AND C.ENT_ID='").append(orgCode);
			sql.append("' AND C.OPEN_RANK='1'");			
		}
		
		sql.append(") c1");
		
		if(StringUtils.isNotBlank(sortField)) {
			if("COMPLEX".equalsIgnoreCase(sortField)) {
				sql.append(" ORDER BY BUYED DESC, JOINED_CNT DESC, ISSUE_TIME DESC");
			} else {
				sql.append(" ORDER BY C1.").append(sortField);
				sql.append("DESC".equalsIgnoreCase(sordMethod) ? " DESC" :" ASC");
			}
		}
				
		JdbcPage jp =DBDYDao.select2JdbcPage(ac.getConnection(), sql.toString(), pageNum, pageSize);
		return jp;
	}
	
	/**
	 * 课程详细查询
	 * @param ac
	 * @return
	 */
	public DBDYPO course(ActionContext ac){
		String host = BaseConstants.getGlobalValue("8");
		String courseId = ac.getHttpRequest().getParameter(TABLE_KEY);
		DBDYPO po=new DBDYPO(TABLE_NAME,TABLE_KEY);
		po.set(TABLE_KEY, courseId);
		po=DBDYDao.selectByID(ac.getConnection(), po)[0];
		if (null != po.get("COVER_PIC_PATH")) {
			po.set("COVER_PIC_PATH", ZkgkUtil.pathConvert(po.get("COVER_PIC_PATH").toString(),host));
		}
		return po;
	}
	
	/**
	 * 查询课程类别
	 * @return
	 */
	public DBDYPO[] courseSort(String orgId){
		if(StringUtils.isBlank(orgId)) {
			return new DBDYPO[0];
		}
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM TS_CODE C WHERE C.CATEGORYNO='W_COURSESORT'");
		sql.append(" AND C.ORG_ID='").append(orgId).append("' AND C.EXTITEM1 NOT IN ('W_C2','W_C3','W_C4','HDZZ','WZDT','PTJS')");
		return courseSortBySql(sql.toString());
	}
	
	public DBDYPO[] courseSort(ActionContext ac){
		String courseType = ac.getHttpRequest().getParameter("COURSETYPE");
		StringBuffer sql= new StringBuffer();
		String orgId = ac.getHttpRequest().getParameter(ZkgkConstants.SUB_SITE_ORG_KEY);
		
		if(StringUtils.isBlank(orgId)) {
			return new DBDYPO[0];
		}
		
		sql.append("SELECT * FROM TS_CODE C WHERE C.CATEGORYNO='W_COURSESORT'");
		sql.append(" AND C.ORG_ID='").append(orgId).append("'");
		if(StringUtils.isNotBlank(courseType)) {
			sql.append(" AND C.EXTITEM1='").append(courseType).append("'");
		}
		
		return courseSortBySql(sql.toString());
	}
	public DBDYPO[] courseSortBySql(String sql){
		
		if(StringUtils.isBlank(sql)) {
			return null;
		}
		DBDYPO[] sorts = null;
		Connection conn = null;
		try {
			conn = DBConn.getConnection("SSOdbService");
			sorts = DBDYDao.selectBySQL(conn, sql.toString());
		} catch (Exception e) {
			MessageService.errString("", e);
		} finally {
			DBConn.closeConnection(conn);
		}
		return sorts;
	}
	
	
	/**
	 * 查询课程类型
	 * @return
	 */
	public DBDYPO[] courseTYPE(String orgId){
		if(StringUtils.isBlank(orgId)) {
			return new DBDYPO[0];
		}
		StringBuffer sql= new StringBuffer();
		sql.append("SELECT * FROM TS_CODE C WHERE C.CATEGORYNO='W_COURSETYPE'");
		sql.append(" AND C.ORG_ID='").append(orgId).append("' AND C.CNO NOT IN ('W_C2','W_C3','W_C4','HDZZ','WZDT','PTJS')");
		
		DBDYPO[] poArr = null;
		Connection ssoConn = DBConn.getConnection("SSOdbService");
		try {
			poArr = DBDYDao.selectBySQL(ssoConn, sql.toString());
		} catch (Exception e) {
			MessageService.errString("", e);
		} finally {
			DBConn.closeConnection(ssoConn);
		}
		
		return poArr;
	}
	
	/**
	 * 查詢我的課程的sql語句
	 * @param sql
	 * @param userId
	 */
	private void myCourseSql(StringBuilder sql, String userId) {
		sql.append("SELECT COURSE.COURSE_ID, COURSE.COURSE_CODE, COURSE.COURSE_NAME, IFNULL(ROUND(SUM(CASE WHEN S.RATE_TYPE IN (1, 2, 3) THEN REAL_SCORE ELSE 0 END) / SUM(CASE WHEN S.RATE_TYPE IN (1, 2, 3) THEN S.SCORE_RATE ELSE 0 END) *100), 0) CURRENT_LEARN, ");
		sql.append("IFNULL(ROUND(SUM(CASE WHEN S.RATE_TYPE = 11 THEN REAL_SCORE ELSE 0 END)), 0) CURRENT_KNOWLEDGE, IFNULL(ROUND(SUM(CASE WHEN S.RATE_TYPE = 12 THEN REAL_SCORE ELSE 0 END)), 0) CURRENT_PHASE, ");
		sql.append("IFNULL(ROUND(SUM(CASE WHEN S.RATE_TYPE = 13 THEN REAL_SCORE ELSE 0 END)), 0) CURRENT_COMPLEX FROM ( SELECT SCORE.STUDENT_ID, RATE.COURSE_ID, RATE.RATE_TYPE, RATE.SCORE_RATE, RATE.FULL_CONDITION, SCORE.CURRENT_DATA, SCORE.CURRENT_SCORE , ");
		sql.append("CASE WHEN RATE.RATE_TYPE IN ('2', '3') THEN (CASE WHEN SCORE.CURRENT_DATA / 60 > RATE.FULL_CONDITION THEN RATE.SCORE_RATE ELSE SCORE.CURRENT_DATA / 60 / RATE.FULL_CONDITION * RATE.SCORE_RATE END) ");
		sql.append("ELSE (CASE WHEN SCORE.CURRENT_DATA > RATE.FULL_CONDITION THEN RATE.SCORE_RATE ELSE SCORE.CURRENT_DATA / RATE.FULL_CONDITION * RATE.SCORE_RATE END) END REAL_SCORE ");
		sql.append("FROM OL_SCORE_RATE RATE LEFT JOIN OL_STUDENT_SCORE SCORE ON SCORE.SCORE_RATE_ID = RATE.SCORE_RATE_ID AND SCORE.STUDENT_ID = '" + userId + "' ) S , OL_COURSE COURSE  ");
		sql.append("LEFT JOIN (SELECT COURSE_ID FROM OL_APPLY_COURSE WHERE USER_ID = '" + userId + "' AND APPLY_STATE IN ('2','3') GROUP BY COURSE_ID) AC ON  AC.COURSE_ID = COURSE.COURSE_ID ");
		sql.append("WHERE S.COURSE_ID = COURSE.COURSE_ID AND S.COURSE_ID = AC.COURSE_ID AND AC.COURSE_ID IS NOT NULL GROUP BY COURSE.COURSE_ID ORDER BY COURSE.ORDER_BY");
	}

	/**
	 * 查询指定用户的课程
	 * @param ac
	 * @param userId
	 * @return
	 */
	public DBDYPO[] queryByUserId(ActionContext ac, String userId) {
		StringBuilder sql = new StringBuilder();
		myCourseSql(sql, userId);
		return DBDYDao.selectBySQL(ac.getConnection(), sql.toString());
	}
	/**
	 * 查询课程下的所有产品
	 * @param ac
	 * @return
	 */
	public DBDYPO[] queryProduct(ActionContext ac){
		String courseId=ac.getHttpRequest().getParameter(TABLE_KEY);
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT PRODUCT_ID,COURSE_ID,PRODUCT_NAME,IFNULL(PRODUCT_MONEY,0) AS PRODUCT_MONEY,PRODUCT_MSG,ISSUE_STATE,PRODUCT_TYPE,ISSUE_TIME,ISSUE_USER_ID,CREATE_BY,CREATE_TIME,UPDATE_BY,UPDATE_TIME FROM GO_PRODUCT P  WHERE P.COURSE_ID='")
		.append(courseId).append("'");
		return DBDYDao.selectBySQL(ac.getConnection(), sql.toString());
	}
	/**
	 * 单产品购买
	 * @param ac
	 * @param productType
	 * @return
	 */
	public DBDYPO[] queryProductType(ActionContext ac,String productType){
		String courseId=ac.getHttpRequest().getParameter(TABLE_KEY);
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT PRODUCT_ID,COURSE_ID,PRODUCT_NAME,IFNULL(PRODUCT_MONEY,0) AS PRODUCT_MONEY,PRODUCT_MSG,ISSUE_STATE,PRODUCT_TYPE,ISSUE_TIME,ISSUE_USER_ID,CREATE_BY,CREATE_TIME,UPDATE_BY,UPDATE_TIME FROM GO_PRODUCT P   WHERE P.COURSE_ID='")
		.append(courseId).append("' and p.PRODUCT_TYPE='").append(productType).append("'");
		return DBDYDao.selectBySQL(ac.getConnection(), sql.toString());
	}
	/**
	 * 查询课程下的组合包括产品id使用，隔开
	 * @param ac
	 * @return
	 */
	public DBDYPO[] queryCombination(ActionContext ac){
		String courseId=ac.getHttpRequest().getParameter(TABLE_KEY);
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT C.*,GROUP_CONCAT(R.PRODUCT_ID SEPARATOR ',') AS PRODUCT_MAP FROM GO_COMBINATION C JOIN GO_PRODUCT_RELEVANCE R ON R.COMBINATION_ID=C.COMBINATION_ID WHERE COURSE_ID='")
		.append(courseId).append("' group by r.COMBINATION_ID");
		return DBDYDao.selectBySQL(ac.getConnection(), sql.toString());
	}
	/**
	 * 查询产品，使用课程id查询
	 * @param ac
	 * @return
	 */
	public DBDYPO[] queryCombinationProduct(ActionContext ac){
		String combinationId=ac.getHttpRequest().getParameter("COMBINATION_ID");
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT * FROM GO_PRODUCT P WHERE P.PRODUCT_ID IN ( SELECT R.PRODUCT_ID FROM GO_PRODUCT_RELEVANCE R JOIN GO_COMBINATION C ON C.COMBINATION_ID=R.COMBINATION_ID WHERE C.COMBINATION_ID='")
		.append(combinationId).append("')");
		return DBDYDao.selectBySQL(ac.getConnection(), sql.toString());
	}
	/**
	 * 查询组合价钱
	 * @param ac
	 * @param combinationId
	 * @return
	 */
	public double queryCombinationMoney(ActionContext ac,String combinationId ){
		String sql="SELECT COMBINATION_MONEY FROM GO_COMBINATION  WHERE COMBINATION_ID='"+combinationId+"'";
		DBDYPO po=DBDYDao.selectBySQL(ac.getConnection(), sql)[0];
		return Double.parseDouble(po.get("COMBINATION_MONEY").toString());
	}
	/**
	 * 查询多产品价格
	 * @param ac
	 * @param ids
	 * @return
	 */
	public double allProductMoney(ActionContext ac,String ids){
		String[] productIds=ids.split(",");
		int sum = 0;
		for(String productId:productIds){
			sum+=queryProductMoney(ac,productId);
		}
		return sum;
	}
	/**
	 * 查询单产品价格
	 * @param ac
	 * @param productId
	 * @return
	 */
	public static double queryProductMoney(ActionContext ac,String productId){
		String sql="SELECT IFNULL(PRODUCT_MONEY,0) AS  PRODUCT_MONEY FROM GO_PRODUCT WHERE PRODUCT_ID='"+productId+"'";
		DBDYPO po=DBDYDao.selectBySQL(ac.getConnection(), sql)[0];
		return  Double.parseDouble(po.get("PRODUCT_MONEY").toString());
	}
	
	
	//查询指定课程参与用户人数， excludeUserId ：排除统计的用户ID
	public int countJoinCourse(ActionContext ac, String courseId, String  excludeUserId) {
		
		if(StringUtils.isBlank(courseId)) {
			return 0;
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(DISTINCT A.USER_ID) CNT FROM OL_APPLY_COURSE A ");
		sql.append("LEFT JOIN OL_COURSE C ON A.COURSE_ID=C.COURSE_ID ");
		sql.append("WHERE (A.APPLY_STATE='2' OR A.APPLY_STATE='3') AND A.COURSE_ID='").append(courseId).append("'");
		
		if(StringUtils.isNotBlank(excludeUserId)) {
			sql.append(" AND A.USER_ID!='").append(excludeUserId).append("'");
		}
		
		Object cnt = DBDYDao.selectOneFieldValueBySQL(ac.getConnection(), sql.toString());
		
		if(null == cnt) {
			return 0;
		}
		return ((Long)cnt).intValue();
	}
	
	//查询用户是否参与了指定的课程
	public boolean joinedCourse(ActionContext ac, String courseId, String  userId) {
		if(StringUtils.isAlpha(courseId) || StringUtils.isBlank(userId)) {
			return false;
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT A.USER_ID CNT FROM OL_APPLY_COURSE A ");
		sql.append("LEFT JOIN OL_COURSE C ON A.COURSE_ID=C.COURSE_ID ");
		sql.append("WHERE A.COURSE_ID='").append(courseId).append("'");
		sql.append(" AND A.USER_ID='").append(userId).append("'");
		sql.append(" AND A.APPLY_STATE IN ('2','3')");
		
		DBDYPO[] pos = DBDYDao.selectBySQL(ac.getConnection(), sql.toString());
		if(pos.length > 0) {
			return true;
		}
		
		return false;
	}
	
	// 当前用户有权限使用的产品
	public String rightProduct(ActionContext ac) throws Exception {
		String courseId = ac.getHttpRequest().getParameter("COURSE_ID");
		if (StringUtils.isBlank(courseId)) {
			return "";
		}

		StringBuilder sql = new StringBuilder("SELECT P.PRODUCT_TYPE FROM GO_PRODUCT P WHERE P.PRODUCT_ID IN(");
		sql.append("select od.PRODUCT_ID from go_order o join go_orderdetail od ON o.ORDER_ID=od.ORDER_ID");
		sql.append(" where o.BUY_PERSON_ID='" + SsoUtil.getSsoAttribute(ac, "PERSON_ID") + "' and o.PAY_STATUS='1' and od.APPLY_ID='" + courseId + "' )");

		DBDYPO[] productPos = DBDYDao.selectBySQL(ac.getConnection(), sql.toString());
		if (null == productPos || productPos.length == 0) {
			return "";
		}
		
		String producttype = "";
		for (DBDYPO productpo : productPos) {
			producttype += "," + productpo.get("PRODUCT_TYPE");
 		}
		return producttype;
	}

	// 最后一道防线
	public String rightProductAjax(ActionContext ac) throws Exception {
		String courseId = ac.getHttpRequest().getParameter("COURSE_ID");
		String protype = ac.getHttpRequest().getParameter("PRODUCT_TYPE");
		if (StringUtils.isBlank(courseId) || StringUtils.isBlank(protype)) {
			return "null";
		}

		StringBuilder sql = new StringBuilder("SELECT COUNT(1)  FROM GO_PRODUCT P WHERE P.PRODUCT_ID IN(");
		sql.append("SELECT OD.PRODUCT_ID FROM GO_ORDER O JOIN GO_ORDERDETAIL OD ON O.ORDER_ID=OD.ORDER_ID");
		sql.append(" WHERE O.BUY_PERSON_ID='" + SsoUtil.getSsoAttribute(ac, "PERSON_ID") + "' AND O.PAY_STATUS='1' AND OD.APPLY_ID='" + courseId + "'")
				.append(" AND P.PRODUCT_TYPE = '").append(protype).append("')");
		Object obj = DBDYDao.selectOneFieldValueBySQL(ac.getConnection(), sql.toString());
		return null != obj && Integer.valueOf(obj.toString()) > 0 ? "true" : "false";
	}
	
	/**
	 * 统计子站课程总数,如果访问的子站为当前操作人所属组织机构，则查询该子站课程数量，排除角色课程。否则查询该子站公开课程数量
	 * @param ac
	 * @return
	 */
	public int totalCourses(ActionContext ac) {
		StringBuilder sql = new StringBuilder();
		String subSiteOrg = ac.getHttpRequest().getParameter(ZkgkConstants.SUB_SITE_ORG_KEY);
		String currOrgCode = SessionUtil.getOrgCode(ac);
		String opno = SessionUtil.getOpno(ac);
		
		
		if(StringUtils.isBlank(subSiteOrg)) {			
			subSiteOrg = ZkgkConstants.SUB_SITECODE_E9RJ;
		}
		
		sql.append("SELECT COUNT(1) TOTAL FROM OL_COURSE C WHERE C.ENABLED='1' AND C.ISSUE_STATE = '1' AND C.IS_LOGIN_SEE <> '1' ");
		
		if(currOrgCode.equals(subSiteOrg) || (ZkgkConstants.SUB_SITECODE_E9RJ.equals(subSiteOrg) && !isExistSubSation(ac, currOrgCode))) {
			//当前登录人访问所属机构的子站，则查询该子站所有课程.排除角色课程,即指定可查看课程的角色用户，当月用户又没有该角色
			sql.append(" AND C.ENT_ID='").append(subSiteOrg);
			sql.append("' AND NOT EXISTS (SELECT 1 FROM OL_ROLE_COURSE RC WHERE NOT EXISTS ( SELECT 1 FROM ");
			sql.append(ZkgkConstants.SSOdb).append("TS_OPROLE R WHERE R.OPNO='").append(opno);
			sql.append("' AND R.ROLEID=RC.ROLE_ID) AND RC.COURSE_ID=C.COURSE_ID");
			sql.append(" AND NOT EXISTS(SELECT 1 FROM OL_ROLE_COURSE RC1 WHERE RC1.COURSE_ID=RC.COURSE_ID AND EXISTS(SELECT 1 FROM ");
			sql.append(ZkgkConstants.SSOdb).append("TS_OPROLE R1 WHERE R1.OPNO='");
			sql.append(opno).append("' AND R1.ROLEID=RC1.ROLE_ID)))");
			
		} else {
			//当前登录人访问的子站非所属机构子站，则查询该子站公开课程
			sql.append(" AND C.ENT_ID='").append(subSiteOrg);
			sql.append("' AND C.OPEN_RANK='1'");			
		}
		
		
		Object obj = DBDYDao.selectOneFieldValueBySQL(ac.getConnection(), sql.toString());
		
		if(null == obj ) {
			return 0;
		} 
		
		return Integer.valueOf(obj.toString());
	}
	
	
	/**------------------------------华丽的分割线--ysp--------------------------------------------*/

	/**
	 * 查询课程列表
	 * @param ac
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DBDYPO> queryCourseList(ActionContext ac) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM OL_COURSE WHERE 1=1 ");

		HttpServletRequest request = ac.getHttpRequest();
		Object courseCode = request.getAttribute("COURSE_CODE");
		if (ZkgkUtil.isNotBlank(courseCode)) {
			sql.append(" AND COURSE_CODE = '" + courseCode + "' ");
		}

		if (!Constants.SUPER_USER_NAME.equals(ZkgkUtil.getUserOpno(ac))) {
			sql.append(" AND ENT_ID = '" + ZkgkUtil.getUserStationOrgCode(ac) + "'");
		}
		return DBDYDao.selectBySQL2List(ac.getConnection(), sql.toString());
	}

	/**
	 * 根据课程ID获取课程对象
	 * @param ac
	 * @param courseId
	 * @return
	 */
	public DBDYPO getCourseById(ActionContext ac, String courseId) {
		String sql = "SELECT * FROM OL_COURSE WHERE COURSE_ID = '" + courseId + "' ";

		DBDYPO[] arr = DBDYDao.selectBySQL(ac.getConnection(), sql);
		if (arr != null && arr.length > 0) {
			return arr[0];
		}
		return null;
	}

}
