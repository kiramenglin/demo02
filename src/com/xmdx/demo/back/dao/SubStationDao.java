package com.xmdx.demo.back.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.e9rj.platform.common.Constants;
import com.e9rj.platform.common.StringUtil;
import com.e9rj.platform.common.dao.BaseDao;
import com.e9rj.platform.util.SessionUtil;
import com.xmdx.demo.back.dao.ZkgkConstants;
import com.xmdx.demo.back.dao.ZkgkUtil;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.frameext.simpledb.DBOperate;
import com.xmzy.framework.context.ActionContext;

public class SubStationDao extends BaseDao {

	private final String TABLE_NAME = "TS_SUB_STATION";

	private final String TABLE_KEY = "SUB_STATION_ID";

	@Override
	public String genQuerySql(ActionContext ac) {
		return "SELECT * FROM TS_SUB_STATION STA WHERE 1=1 ";
	}

	public String addPo(ActionContext ac, DBDYPO po) {
		setAddInfo(ac, po);
		po.setKeyField(TABLE_KEY);
		po.setTableName(TABLE_NAME);
		po.set(TABLE_KEY, super.genIdString("US", Constants.KEY_LENGTH));
		int result = DBDYDao.insert(ac.getConnection(), po);
		if (result != 0) {
			return po.get(TABLE_KEY).toString();
		}
		return "";
	}

	public int updatePo(ActionContext ac, DBDYPO po) {
		setUpdateInfo(ac, po);
		po.setKeyField(TABLE_KEY);
		po.setTableName(TABLE_NAME);
		return DBDYDao.update(ac.getConnection(), po);
	}

	@SuppressWarnings("unchecked")
	public List<DBDYPO> queryList(ActionContext ac) throws Exception {
		StringBuilder sql = new StringBuilder(genQuerySql(ac));
		return DBDYDao.selectBySQL2List(ac.getConnection(), sql.toString());
	}

	public String queryWithPage(ActionContext ac) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT STA.* FROM TS_SUB_STATION STA WHERE 1=1 ");

		HttpServletRequest request = ac.getHttpRequest();
		String stationName = request.getParameter("STATION_NAME");
		if (StringUtils.isNotBlank(stationName)) {
			sql.append(" AND STATION_NAME LIKE '%" + stationName + "%' ");
		}
		String orgcode = request.getParameter(ZkgkConstants.ORGCODE);
		if (StringUtils.isNotBlank(orgcode)) {
			sql.append(" AND ORGCODE = '" + orgcode + "' ");
		}

		if (!Constants.SUPER_USER_NAME.equals(SessionUtil.getOpno(ac))) {
			sql.append(" AND STA.ORGCODE = '").append(SessionUtil.getOrgCode(ac)).append("' ");
		}

		sql.append(super.order(ac, "STA.CREATE_TIME", "DESC"));
		return ZkgkUtil.select2JdbcPage(ac, sql.toString());
	}

	public DBDYPO getPo(ActionContext ac) {
		HttpServletRequest request = ac.getHttpRequest();
		StringBuilder sql = new StringBuilder(genQuerySql(ac));

		Object key = request.getAttribute(TABLE_KEY);
		if (ZkgkUtil.isNotBlank(key)) {
			sql.append(" AND SUB_STATION_ID = '" + key + "' ");
		}

		Object orgcode = request.getAttribute(ZkgkConstants.ORGCODE);
		if (ZkgkUtil.isNotBlank(orgcode)) {
			sql.append(" AND ORGCODE = '" + orgcode + "' ");
		}

		DBDYPO[] poArr = DBDYDao.selectBySQL(ac.getConnection(), sql.toString());
		if (poArr != null && poArr.length > 0) {
			return poArr[0];
		}
		return null;
	}

	public int delete(ActionContext ac, String keys) {
		String sql = "DELETE FROM TS_SUB_STATION WHERE SUB_STATION_ID IN (" + StringUtil.formatString2Sql(keys) + ")";
		return DBOperate.execute(ac.getConnection(), sql);
	}

	//查询用户所在的组织机构的person_id
	public DBDYPO orgCodePersonId(ActionContext ac){
		StringBuilder sql = new StringBuilder(genQuerySql(ac));
		SubStationDao subStationDao=new SubStationDao();
		//子站组织机构
		String courseId=ac.getHttpRequest().getParameter("COURSE_ID");
		String orgCode=subStationDao.getOrgcodeByCourseId(ac, courseId);
		if(ZkgkUtil.isNotBlank(orgCode)){
			sql.append(" AND ORGCODE ='").append(orgCode).append("'");
			return DBDYDao.selectBySQL(ac.getConnection(), sql.toString())[0];
		}
		return null;
	}

	/**
	 * 根据课程ID获取子站ID
	 * @param ac
	 * @param courseId
	 * @return
	 */
	public String getOrgcodeByCourseId(ActionContext ac, String courseId) {
		DBDYPO po = getStationByCourseId(ac, courseId);
		if (po != null) {
			return po.get(ZkgkConstants.ORGCODE).toString();
		}
		return null;
	}
	/**
	 * 根据课程ID获取子站对象
	 * @param ac
	 * @param courseId
	 * @return
	 */
	public DBDYPO getStationByCourseId(ActionContext ac, String courseId) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT STATION.* FROM OL_COURSE COURSE, TS_SUB_STATION STATION WHERE COURSE.ENT_ID = STATION.ORGCODE ");
		sb.append("AND COURSE.COURSE_ID = '" + courseId + "'");
		DBDYPO[] poArr = DBDYDao.selectBySQL(ac.getConnection(), sb.toString());
		if (poArr != null && poArr.length > 0) {
			return poArr[0];
		}
		return null;
	}

	/**
	 * 根据知识点ID获取子站ID
	 * @param ac
	 * @param courseId
	 * @return
	 */
	public String getOrgcodeByKnowId(ActionContext ac, String knowledgeId) {
		DBDYPO po = getStationByKnowId(ac, knowledgeId);
		if (po != null) {
			return po.get(ZkgkConstants.ORGCODE).toString();
		}
		return null;
	}

	/**
	 * 根据知识点ID获取子站对象
	 * @param ac
	 * @param courseId
	 * @return
	 */
	public DBDYPO getStationByKnowId(ActionContext ac, String knowledgeId) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT STATION.* FROM OL_COURSE COURSE, OL_KNOWLEDGE KNOW, TS_SUB_STATION STATION WHERE COURSE.ENT_ID = STATION.ORGCODE ");
		sb.append("AND COURSE.COURSE_ID = KNOW.COURSE_ID AND KNOW.KNOWLEDGE_ID = '" + knowledgeId + "'");
		DBDYPO[] poArr = DBDYDao.selectBySQL(ac.getConnection(), sb.toString());
		if (poArr != null && poArr.length > 0) {
			return poArr[0];
		}
		return null;
	}

	/**
	 * 根据机构取子站信息
	 * @param ac
	 * @param orgId
	 * @return
	 */
	public DBDYPO getStationByOrgId(ActionContext ac, String orgId) {
		String sql = "SELECT * FROM TS_SUB_STATION WHERE ORGCODE='" + orgId + "'";
		DBDYPO[] poArr = DBDYDao.selectBySQL(ac.getConnection(), sql);
		if (poArr != null && poArr.length > 0) {
			return poArr[0];
		}
		return null;
	}

	/**
	 * 用于控制测评类型是否可修改。
	 * @param ac
	 * @return
	 */
	public boolean isExistPublished(ActionContext ac) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(1) FROM OL_COURSE WHERE ENT_ID='" + SessionUtil.getOrgCode(ac) + "' AND ISSUE_STATE='1'");
		Object obj = DBDYDao.selectOneFieldValueBySQL(ac.getConnection(), sql.toString());
		if (null != obj && Integer.valueOf(obj.toString()) > 0) {
			return true;
		}
		return false;
	}

	public List<DBDYPO> initTscode(ActionContext ac) {
		int i = 0;
		List<DBDYPO> listpo = new ArrayList<DBDYPO>();
		String[] cnoArr = { "GCXKH", "KG" };
		String[] cvalueArr = { "在线测评", "课程要点" };
		String[] extitem1 = { "在线测评", "考纲" };
		for (String cno : cnoArr) {
			DBDYPO tscodecategorypo = new DBDYPO();
			tscodecategorypo.set("CNO", cno);
			tscodecategorypo.set("CATEGORYNO", "W_SUBSITE_PARAM");
			tscodecategorypo.set("PARENTNO", "W_SUBSITE_PARAM");
			tscodecategorypo.set("CVALUE", cvalueArr[i]);
			tscodecategorypo.set("ENABLED", "1");
			tscodecategorypo.set("ORDIDX", i);
			tscodecategorypo.set("EXTITEM1", extitem1[i++]);
			tscodecategorypo.set("CMD", "A");
			listpo.add(tscodecategorypo);
		}
		return listpo;
	}

	public List<DBDYPO> getTscode(ActionContext ac, String orgId) {
		String sql = "SELECT * FROM " + Constants.DATABASE_SSO + "TS_CODE WHERE PARENTNO = 'W_SUBSITE_PARAM' AND ORG_ID = '" + orgId + "'";
		List<DBDYPO> poList = DBDYDao.selectBySQL2List(ac.getConnection(), sql);
		int i = 0;
		for (DBDYPO po : poList) {
			po.set("CMD", "U");
			poList.set(i, po);
			i++;
		}
		return poList;
	}

	/**
	 * 用户字典分类
	 * @param ac
	 * @param ssoconn
	 * @return
	 */
	public int addtscodeCategory(ActionContext ac,Connection ssoconn) {
		int result = 0;
		String sql = "SELECT COUNT(1) FROM " + Constants.DATABASE_SSO + "TS_CODECATEGORY WHERE CNO='W_SUBSITE_PARAM'";
		Object obj = DBOperate.selectOneFieldValue(ac.getConnection(), sql);
		if (null != obj && Integer.valueOf(obj.toString()) > 0) {
			return 1;
		}
		DBDYPO po = new DBDYPO("TS_CODECATEGORY", "CNO");
		po.setTableName("TS_CODECATEGORY");
		po.setTableName("CNO");
		po.set("CNO", "W_SUBSITE_PARAM");
		po.set("CNAME", "子站参数");
		po.set("opno", SessionUtil.getOpno(ac));
		po.set("optime", new java.sql.Timestamp(System.currentTimeMillis()));
		po.set("codetype", "3");
		po.set("APP_CODE", "zkgk");
		result = DBDYDao.insert(ssoconn, po);
		if (1 == result) {
			return result;
		}
		return result;
	}

	/**
	 * 获取测评类型选中的值：'1','2'...
	 * @param testType
	 * @return
	 */
	public String selectedTestType(int testType) {
		int[] allTypeArr = { 1, 2, 4, 8 };
		StringBuilder existType = new StringBuilder();
		for (int childType : allTypeArr) {
			int withresutl = childType & testType;
			switch (withresutl) {
			case 1:
				existType.append(",'").append("1").append("','").append("2").append("','").append("3").append("'");
				break;
			case 2:
				existType.append(",'").append("11").append("'");
				break;
			case 4:
				existType.append(",'").append("12").append("'");
				break;
			case 8:
				existType.append(",'").append("13").append("'");
				break;
			}
		}
		if (existType.length() > 0) {
			existType.deleteCharAt(0);
			return existType.toString();
		}
		return "";
	}

	/**
	 *  删除取消选中成绩权重
	 */

	public int deleteScoreRate(ActionContext ac, DBDYPO po) {
		int result = 0;
		int testType = Integer.valueOf(po.get("TEST_TYPE").toString());
		int oldtestType = Integer.valueOf(po.get("OLD_TEST_TYPE").toString());

		String oldTesttypestr = selectedTestType(oldtestType);
		String Testtypestr = selectedTestType(testType);

		StringBuilder delTesttype = new StringBuilder();
		if (StringUtils.isNotBlank(oldTesttypestr)) {
			String[] oldTesttypeArr = oldTesttypestr.split(",");
			for (String oldtest : oldTesttypeArr) {
				if (Testtypestr.indexOf(oldtest) == -1) {
					delTesttype.append(",").append(oldtest);
				}
			}
		}

		if (delTesttype.length() > 0) {
			delTesttype.deleteCharAt(0);
			DBDYPO[] scoreRateIdArr = DBDYDao.selectBySQL(ac.getConnection(), "SELECT o2.SCORE_RATE_ID,o2.COURSE_ID FROM ol_score_rate o2 " +
					"WHERE  EXISTS (SELECT 1 FROM ol_course O1 WHERE O1.COURSE_ID = o2.COURSE_ID AND O1.ENT_ID='" + po.get("ORGCODE").toString() + "') " +
					"AND o2.RATE_TYPE IN (" + delTesttype.toString() + ")");

			StringBuilder scoreRateIds = new StringBuilder();
			StringBuilder courseIds = new StringBuilder();
			if(null != scoreRateIdArr && scoreRateIdArr.length > 0){
				for (DBDYPO scorepo : scoreRateIdArr) {
					scoreRateIds.append(",'").append(scorepo.get("SCORE_RATE_ID").toString()).append("'");
					if (courseIds.indexOf(scorepo.get("COURSE_ID").toString()) == -1) {
						courseIds.append(",'").append(scorepo.get("COURSE_ID").toString()).append("'");
					}
				}
			}

			if (scoreRateIds.length() > 0) {
				scoreRateIds.deleteCharAt(0);
				result = DBOperate.execute(ac.getConnection(),
						"DELETE FROM OL_STUDENT_SCORE  WHERE SCORE_RATE_ID IN (" + scoreRateIds.toString() + ")"
						);
				if (0 != result) {
					result = DBOperate.execute(ac.getConnection(),
							"DELETE FROM OL_SCORE_RATE  WHERE SCORE_RATE_ID IN (" + scoreRateIds.toString() + ") "
							);
				}
				if (courseIds.length() > 0) {
					courseIds.deleteCharAt(0);
					if (0 != result) {
						result = DBOperate.execute(ac.getConnection(),
								"DELETE FROM OL_KNOWLEDGE_STUDY  WHERE COURSE_ID IN (" + courseIds.toString() + ") "
								);
					}
				}
			} else {
				result = 1;
			}
			
		} else {
			return 1;
		}

		return result;
	}
}
