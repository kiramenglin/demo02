package com.e9rj.zkgk.website.platform.dao;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.e9rj.platform.common.CONVERTSQL;
import com.e9rj.platform.common.Constants;
import com.e9rj.platform.common.GenID;
import com.e9rj.platform.common.dao.BaseDao;
import com.e9rj.platform.util.SsoUtil;
import com.e9rj.zkgk.system.ZkgkConstants;
import com.e9rj.zkgk.util.ZkgkUtil;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.framework.context.ActionContext;

public class MoneyRecordDao extends BaseDao {

	private final String TABLE_NAME = "GO_MONEY_RECORD";

	private final String TABLE_KEY = "RECORD_ID";

	@Override
	public String genQuerySql(ActionContext ac) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT T.RECORD_MONEY, T.RECORD_TYPE, T.RESOURCE, ");
		sql.append(CONVERTSQL.setdatetimeSQL("T.RECORD_DATE"));
		sql.append(" RECORD_DATE FROM GO_MONEY_RECORD T");
		sql.append(" WHERE AND T.PERSON_ID='").append(ZkgkUtil.getUserID(ac)).append("'");
		sql.append(" ORDER BY T.CREATE_TIME DESC");
		
		return sql.toString();
	}
	
	/**
	 * 生成personID所有成功收入(充值、赠送、积分兑换和退单)明细的查询sql, 默认按收入时间降序排序
	 * @param personId
	 * @return
	 */
	public String get4Income(String personId) {
		if(StringUtils.isBlank(personId)) {
			return "";
		}
		StringBuilder sql = gen4PersonNotOrder(personId);
		sql.append(" AND T.STATUS='1' ")
		.append("UNION ALL ")
		.append("SELECT R.CONVERSION_MONEY,"+ZkgkConstants.RECHARGE_TYPE_INTEGRAL+",CASE R.SOURCE WHEN "+ZkgkConstants.INTEGRAL_SOURCE_SCORE_COUPONS+" THEN "+ZkgkConstants.RESOURCE_SYSTEM_INTEGRAL+" ELSE '来源出错' END,"+ZkgkConstants.RECHARGE_STATUS_SUCCESS+",")
		.append(CONVERTSQL.setdatetimeSQL("R.GOAL_TIME"))
		.append(" FROM TB_INTEGRAL_RECORD R ")
		.append("WHERE R.PERSON_ID='"+personId+"' AND R.SOURCE='"+ZkgkConstants.INTEGRAL_SOURCE_SCORE_COUPONS+"'");
		sql.append("ORDER BY RECORD_DATE DESC");
		return sql.toString();
	}
	
	/**
	 * 生成查询personId的充值明细sql,默认按充值时间降序排序
	 * @param personId
	 * @return
	 */
	public String gen4Recharge(String personId) {
		if(StringUtils.isBlank(personId)) {
			return "";
		}
		StringBuilder sql = gen4PersonNotOrder(personId);
		sql.append(" AND T.RECORD_TYPE='1'  AND T.STATUS!='0' ORDER BY T.CREATE_TIME DESC");
		return sql.toString();
	}
	/**
	 * 生成查询personId所有收入明细的sql，不附带任何排序
	 * @param personId
	 * @return
	 */
	public StringBuilder gen4PersonNotOrder(String personId) {
		if(StringUtils.isBlank(personId)) {
			return null;
		}
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT T.RECORD_MONEY, T.RECORD_TYPE, T.RESOURCE,T.STATUS,");
		sql.append(CONVERTSQL.setdatetimeSQL("T.RECORD_DATE"));
		sql.append(" RECORD_DATE FROM GO_MONEY_RECORD T");
		sql.append(" WHERE T.PERSON_ID='").append(personId).append("'");
		return sql;
	}
	/**
	 * 生成查询组织机构下所有收入明细的sql，不附带排序、查询条件
	 * @param orgCode
	 * @return
	 */
	public StringBuilder genOrgcodeMoneyRecord(String orgCode){
		if(StringUtils.isBlank(orgCode)) {
			return null;
		}
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT P.OPNO,T.RECORD_MONEY, T.RECORD_TYPE, T.RESOURCE,T.STATUS,");
		sql.append(CONVERTSQL.setdatetimeSQL("T.RECORD_DATE"));
		sql.append(" RECORD_DATE FROM GO_MONEY_RECORD T LEFT JOIN " + Constants.DATABASE_SSO + "TS_OP P ON T.PERSON_ID =P.PERSON_ID  WHERE 1=1 ");
		return sql;
	}
	/**
	 * 生成查询组织机构下所有充值明细的sql，不附带排序、查询条件
	 * @param orgCode
	 * @return
	 */
	public StringBuilder genOrgcodeMoneyRecordDetail(String orgCode){
		if(StringUtils.isBlank(orgCode)) {
			return null;
		}
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT P.OPNO, T.RECORD_MONEY, T.RECORD_TYPE, T.RESOURCE,T.STATUS,");
		sql.append(CONVERTSQL.setdatetimeSQL("T.RECORD_DATE"));
		sql.append(" RECORD_DATE FROM GO_MONEY_RECORD T LEFT JOIN " + Constants.DATABASE_SSO + "TS_OP P ON T.PERSON_ID =P.PERSON_ID  WHERE 1=1 ");
		return sql;
	}
	/**
	 * 生成查询足够下所有支出明细的sql，不附带排序、查询条件
	 * @param orgCode
	 * @return
	 */
	public StringBuilder genOrgcodeAllOrder(String orgCode){
		if(StringUtils.isBlank(orgCode)) {
			return null;
		}
		StringBuilder sql = new StringBuilder("SELECT O.ORDER_ID,O.ORDER_TYPE,O.PAY_MENT,O.PAY_STATUS,P.OPNO, ");
		sql.append(CONVERTSQL.setdatetimeSQL("O.PAY_TIME") + " AS PAY_TIME_CONV ,C.COURSE_NAME FROM GO_ORDER O,OL_COURSE C," + Constants.DATABASE_SSO + "TS_OP P ");
		sql.append(" WHERE O.COURSE_ID=C.COURSE_ID AND P.PERSON_ID=O.PAY_PERSON_ID ");
		
		return sql;
	}
	/**
	 * 保存冲值金额
	 * @param conn
	 * @param personId
	 * @param money
	 * @param rec
	 * @return
	 */
	public boolean save4Recharge(Connection conn, String personId, double money, DBDYPO rec){
		if(null == conn || StringUtils.isBlank(personId)) {
			return false;
		}
		rec = genBasePo(rec);
		
		rec.set("RECORD_TYPE", "1");
		
		return save(conn,rec, personId, money);
		
	}
	/**
	 * 保存订单的退单充值款项(银联卡充值退款)
	 * @param conn 数据库链接，不传ActionContext,这样方便在action层进行事务控制
	 * @param personId
	 * @param money
	 * @param rec 传进来之前，请先设置创建人、创建时间信息，因为没有传入ActionContext参数,取不到当前操作人
	 * @return
	 */
	public boolean save4cancelRecharge(Connection conn, String personId, double money, DBDYPO rec) {
		return save4CancelOrder(conn, personId, money, "41", rec);
	}
	
	/**
	 * 保存订单的退单充值款项(优惠券退款)
	 * @param conn 数据库链接，不传ActionContext,这样方便在action层进行事务控制
	 * @param personId
	 * @param money
	 * @param rec 传进来之前，请先设置创建人、创建时间信息，因为没有传入ActionContext参数,取不到当前操作人
	 * @return
	 */
	public boolean save4cancelCoupon(Connection conn, String personId, double money, DBDYPO rec) {
		return save4CancelOrder(conn, personId, money, "42", rec);
	}
	/**
	 * 保存订单的退单充值款项
	 * @param conn, 数据库链接，不传ActionContext,这样方便在action层进行事务控制
	 * @param personId
	 * @param money
	 * @param moneyType  41:现金退款、42：优惠券退款
	 * @param rec, 传进来之前，请先设置创建人、创建时间信息，因为没有传入ActionContext参数,取不到当前操作人
	 * @return
	 */
	public boolean save4CancelOrder(Connection conn, String personId, double money, String moneyType, DBDYPO rec) {
		
		if(StringUtils.isBlank(personId) || StringUtils.isBlank(moneyType) || null == conn) {
			return false;
		}
		
		rec = genBasePo(rec);
		
		rec.set("RESOURCE", moneyType);
		rec.set("RECORD_TYPE", "4");
		
		return save(conn, rec, personId, money);
	}
	
	/**
	 * 保存赠送优惠券的充值记录
	 * @param conn
	 * @param personId
	 * @param money
	 * @param rec
	 * @return
	 */
	public boolean save4Coupon(Connection conn, String personId, double money, DBDYPO rec) {
		if(null == conn || StringUtils.isBlank(personId)) {
			return false;
		}
		rec = genBasePo(rec);
		
		rec.set("RECORD_TYPE", "2");
		rec.set("RESOURCE", "21");
		
		return save(conn, rec, personId, money);
	}
	
	private boolean save(Connection conn, DBDYPO rec, String personId, double money) {
		
		rec.set("RECORD_ID", "M" + GenID.gen(19));
		rec.set("RECORD_DATE", new java.sql.Timestamp(System.currentTimeMillis()));
		
		rec.set("RECORD_MONEY", money);
		rec.set("PERSON_ID", personId);
		rec.set("STATUS", "1");
		
		int result = DBDYDao.insert(conn, rec);
		
		return result > 0;
	}
	
	private DBDYPO genBasePo(DBDYPO rec) {
		String keyField = "RECORD_ID";
		String tableName = TABLE_NAME;
		
		if(rec == null ) {
			rec = new DBDYPO(tableName, keyField);
		} else {
			rec.setTableName(tableName);
			rec.setKeyField(keyField);
		}
		return rec;
	}

	/**
	 * 新增对象
	 * 
	 * @param ac
	 * @param po
	 * @return
	 */
	public int addPo(Connection conn, HttpServletRequest request, DBDYPO po) {
		po.set("ENT_ID", SsoUtil.getSsoAttribute(request, "ORGCODE"));
		po.set("CREATE_BY", SsoUtil.getSsoAttribute(request, "OPNO"));
		po.set("CREATE_TIME", new java.sql.Timestamp(System.currentTimeMillis()));
		po.setKeyField(TABLE_KEY);
		po.setTableName(TABLE_NAME);
		po.set(TABLE_KEY, super.genIdString("MR", Constants.KEY_LENGTH));
		return DBDYDao.insert(conn, po);
	}

	/**
	 * 更新对象
	 * @param ac
	 * @param po
	 * @return
	 */
	public int updatePo(Connection conn, DBDYPO po) {
		po.set("UPDATE_TIME", new java.sql.Timestamp(System.currentTimeMillis()));
		po.setKeyField(TABLE_KEY);
		po.setTableName(TABLE_NAME);
		return DBDYDao.update(conn, po);
	}

	/**
	 * 根据订单ID，获取充值记录
	 * 
	 * @param ac
	 * @param orderid
	 * @return
	 */
	public DBDYPO getPoByOrderid(Connection conn, String orderid) {
		String sql = "SELECT * FROM GO_MONEY_RECORD WHERE ORDERID = '" + orderid + "'";
		return DBDYDao.selectBySQL(conn, sql)[0];
	}
}
