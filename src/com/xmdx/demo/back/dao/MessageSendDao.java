package com.e9rj.zkgk.backstage.message.dao;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.e9rj.platform.common.Constants;
import com.e9rj.platform.common.StringUtil;
import com.e9rj.platform.common.dao.BaseDao;
import com.e9rj.platform.util.SessionUtil;
import com.e9rj.zkgk.util.ZkgkUtil;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.frameext.simpledb.DBOperate;
import com.xmzy.framework.context.ActionContext;

public class MessageSendDao extends BaseDao {

	private static final String TABLE_NAME = "TB_MESSAGE_SEND";

	private static final String TABLE_KEY = "MESSAGE_SEND_ID";
	private static final String TABLE_MR = "TB_MESSAGE_RECEIVE";
	private static final String TABLE_MR_KEY = "MESSAGE_RECEIVE_ID";
	
	/**
	 * 添加发送信息
	 * @param ac
	 * @return 
	 * @throws Exception 
	 */
	public String addPo(ActionContext ac,DBDYPO dbdypo) throws Exception{

		String key = super.genIdString("MS", Constants.KEY_LENGTH);
		dbdypo.set(TABLE_KEY, key);
		dbdypo.set("SEND_OPNO", ZkgkUtil.getUserOpno(ac));
		dbdypo.set("SEND_OPNAME", ZkgkUtil.getOpName(ac));
		dbdypo.set("SEND_OPID", ZkgkUtil.getUserID(ac));
		dbdypo.set("IS_DEL", "0");
		setAddInfo(ac, dbdypo);
		dbdypo.set("SEND_TIME", dbdypo.get("CREATE_TIME"));
		dbdypo.set("ORG_ID",SessionUtil.getOrgCode(ac));
		int result = 0;
		result = DBDYDao.insert(ac.getConnection(), dbdypo);
		if(result==0){
			key=null;
		}
		return key;
	}

	/**
	 * 发送系统信息
	 * @param ac
	 * @return 
	 * @throws Exception 
	 */
	public int addSystemMsg(ActionContext ac,String[] attrReceiveOpno, String content) throws Exception{
		DBDYPO dbdypo = new DBDYPO(TABLE_NAME,TABLE_KEY);
		String key = super.genIdString("MS", Constants.KEY_LENGTH);
		dbdypo.set(TABLE_KEY, key);
		dbdypo.set("CONTENT", content);
		dbdypo.set("SEND_OPNO", "admin");
		dbdypo.set("SEND_OPNAME", "超级管理员");
		dbdypo.set("SEND_OPID", "P1309161506010800001");
		dbdypo.set("IS_DEL", "0");
		dbdypo.set("IS_SYSTEM", "2");
		setAddInfo(ac, dbdypo);
		dbdypo.set("SEND_TIME", dbdypo.get("CREATE_TIME"));
		dbdypo.set("ORGCODE", SessionUtil.getOrgCode(ac));
		dbdypo.set("ORG_ID", SessionUtil.getOrgCode(ac));
		int result = 0;
		result = DBDYDao.insert(ac.getConnection(), dbdypo);
		if(result==0){
			return 0;
		}
		int recOpnoLength = attrReceiveOpno.length;
		DBDYPO[] attrRecMsgPo = new DBDYPO[recOpnoLength];
		for (int i=0;i<recOpnoLength;i++) {
			attrRecMsgPo[i] = new DBDYPO(TABLE_MR,TABLE_MR_KEY);
			String key2 = super.genIdString("MR", Constants.KEY_LENGTH);
			attrRecMsgPo[i].set(TABLE_MR_KEY, key2);
			attrRecMsgPo[i].set("MESSAGE_SEND_ID", key);
			attrRecMsgPo[i].set("RECEIVE_OPNO", attrReceiveOpno[i]);
			attrRecMsgPo[i].set("IS_READ", "0");
			attrRecMsgPo[i].set("IS_DEL", "0");
			setAddInfo(ac, attrRecMsgPo[i]);
		}
		int result2 = DBDYDao.insert(ac.getConnection(), attrRecMsgPo);
		return result2;
	}

	/**
	 * 发送系统信息2;由于新注册的用户去不到session值;于是乎稍作了下改动
	 * @param ac
	 * @return 
	 * @throws Exception 
	 */
	public int addSystemMsg2(ActionContext ac, String[] attrReceiveOpno, String content, DBDYPO tsop) throws Exception {
		DBDYPO dbdypo = new DBDYPO(TABLE_NAME, TABLE_KEY);
		String key = super.genIdString("MS", Constants.KEY_LENGTH);
		dbdypo.set(TABLE_KEY, key);
		dbdypo.set("CONTENT", content);
		dbdypo.set("SEND_OPNO", "admin");
		dbdypo.set("SEND_OPNAME", "超级管理员");
		dbdypo.set("SEND_OPID", "P1309161506010800001");
		dbdypo.set("IS_DEL", "0");
		dbdypo.set("IS_SYSTEM", "2");
		setAddInfo(ac, dbdypo, tsop);
		dbdypo.set("SEND_TIME", dbdypo.get("CREATE_TIME"));
		dbdypo.set("ORGCODE", tsop.get("ORGCODE"));
		dbdypo.set("ORG_ID", tsop.get("ORGCODE"));
		int result = 0;
		result = DBDYDao.insert(ac.getConnection(), dbdypo);
		if (result == 0) {
			return 0;
		}
		int recOpnoLength = attrReceiveOpno.length;
		DBDYPO[] attrRecMsgPo = new DBDYPO[recOpnoLength];
		for (int i = 0; i < recOpnoLength; i++) {
			attrRecMsgPo[i] = new DBDYPO(TABLE_MR, TABLE_MR_KEY);
			String key2 = super.genIdString("MR", Constants.KEY_LENGTH);
			attrRecMsgPo[i].set(TABLE_MR_KEY, key2);
			attrRecMsgPo[i].set("MESSAGE_SEND_ID", key);
			attrRecMsgPo[i].set("RECEIVE_OPNO", attrReceiveOpno[i]);
			attrRecMsgPo[i].set("IS_READ", "0");
			attrRecMsgPo[i].set("IS_DEL", "0");
			setAddInfo(ac, attrRecMsgPo[i], tsop);
		}
		int result2 = DBDYDao.insert(ac.getConnection(), attrRecMsgPo);
		return result2;
	}
	
	/**
	 * 设置创建时间，创建人
	 * 
	 * @param ac
	 */
	public static DBDYPO setAddInfo(ActionContext ac, DBDYPO po, DBDYPO tsop) {
		// 因为手机端的登录操作时，SsoUtil.getSsoattribute取不到内容，所以改为从SESSION_USER_BEAN中取内容
		po.set("ENT_ID", tsop.get("ORGCODE"));
		po.set("CREATE_BY", tsop.get("OPNO"));
		po.set("CREATEBY", tsop.get("OPNO"));
		po.set("CREATE_TIME", new java.sql.Timestamp(System.currentTimeMillis()));
		po.set("CREATEDATE", new java.sql.Timestamp(System.currentTimeMillis()));
		return po;
	}

	public int updatePo(ActionContext ac, DBDYPO dbdypo) throws Exception {
		setUpdateInfo(ac, dbdypo);
		dbdypo.setTableName(TABLE_NAME);
		dbdypo.setKeyField(TABLE_KEY);
		return DBDYDao.update(ac.getConnection(), dbdypo);
	}

	/**
	 * 删除
	 * @param ac
	 * @return 
	 */
	public int delete(ActionContext ac, String keys) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE TB_MESSAGE_SEND SET IS_DEL = '1' ");
		if (StringUtils.isNotBlank(keys)) {
			sql.append("WHERE MESSAGE_SEND_ID IN (" + StringUtil.formatString2Sql(keys) + ") ");
		} else {
			sql.append("WHERE 1=2 ");
		}

		return DBOperate.execute(ac.getConnection(), sql.toString());
	}

	/**
	 * 生成查询的SQL语句
	 * 
	 * @param ac
	 * @return
	 */
	@Override
	public String genQuerySql(ActionContext ac) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM TB_MESSAGE_SEND SEND WHERE 1=1 ");
		return sql.toString();
	}

	/**
	 * 查询公共消息
	 * @param ac
	 * @return
	 */
	public DBDYPO[] queryPublicMsg(ActionContext ac) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT MESSAGE_SEND_ID,COURSE_ID,ORGCODE FROM "+TABLE_NAME);
		sql.append(" WHERE IS_SYSTEM='1'");
		return DBDYDao.selectBySQL(ac.getConnection(), sql.toString());
	}
	
	/**
	 * 查询列表
	 * @param ac
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DBDYPO> queryMsgSendList(ActionContext ac) {
		StringBuilder sql = new StringBuilder(genQuerySql(ac));

		buildSql(ac, sql);

		return DBDYDao.selectBySQL2List(ac.getConnection(), sql.toString());
	}

	private void buildSql(ActionContext ac, StringBuilder sql) {
		HttpServletRequest request = ac.getHttpRequest();

		String key = request.getParameter(TABLE_KEY);
		if (StringUtils.isNotBlank(key)) {
			sql.append("AND SEND.MESSAGE_SEND_ID = '" + key + "' ");
		}

		String sendOpno = request.getParameter("SEND_OPNO");
		if (StringUtils.isNotBlank(sendOpno)) {
			sql.append("AND SEND.SEND_OPNO = '" + sendOpno + "' ");
		}

		String orgcode = request.getParameter("ORGCODE");
		if (StringUtils.isNotBlank(orgcode)) {
			sql.append("AND SEND.ORGCODE = '" + orgcode + "' ");
		}

		String courseId = request.getParameter("COURSE_ID");
		if (StringUtils.isNotBlank(courseId)) {
			sql.append("AND SEND.COURSE_ID = '" + courseId + "' ");
		}

		String isSystem = request.getParameter("IS_SYSTEM");
		if (StringUtils.isNotBlank(isSystem)) {
			sql.append("AND SEND.IS_SYSTEM = '" + isSystem + "' ");
		}

		String isDel = request.getParameter("IS_DEL");
		if (StringUtils.isNotBlank(isDel)) {
			sql.append("AND SEND.IS_DEL = '" + isDel + "' ");
		}
	}
}
