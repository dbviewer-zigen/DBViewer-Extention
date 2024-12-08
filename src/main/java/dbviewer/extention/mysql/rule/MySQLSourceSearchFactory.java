package dbviewer.extention.mysql.rule;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import zigen.plugin.db.DbPlugin;
import zigen.plugin.db.core.ResultSetUtil;
import zigen.plugin.db.core.SQLUtil;
import zigen.plugin.db.core.StatementUtil;
import zigen.plugin.db.core.rule.AbstractSourceSearcherFactory;
import zigen.plugin.db.core.rule.SequenceInfo;
import zigen.plugin.db.core.rule.SourceDetailInfo;
import zigen.plugin.db.core.rule.TriggerInfo;

public class MySQLSourceSearchFactory extends AbstractSourceSearcherFactory {
	
	public MySQLSourceSearchFactory(){
		super();
	}
	public String getSequenceDDL(SequenceInfo sequenceInfo) {
		return null;
	}
	protected String getSequenceInfoSQL(String owner) {
		return null;
	}
	protected String getSequenceInfoSQL(String owner, String sequence) {
		return null;
	}
	public SourceDetailInfo getSourceDetailInfo(Connection con, String owner, String name, String type, boolean visibleSchema) throws Exception {
		ResultSet rs = null;
		Statement st = null;

		SourceDetailInfo info = null;
		try {
			st = con.createStatement();
			rs = st.executeQuery(getDetailInfoSQL(owner, name, type));
			while (rs.next()) {
				info = new SourceDetailInfo();
				info.setOwner(owner); //$NON-NLS-1$
				info.setName(name); //$NON-NLS-1$
				info.setType(type); //$NON-NLS-1$
				info.setText(rs.getString(3));
			}

		} catch (Exception e) {
			DbPlugin.log(e);
			throw e;
		} finally {
			ResultSetUtil.close(rs);
			StatementUtil.close(st);
		}

		return info;

	}
	protected String getDetailInfoSQL(String owner, String name, String type) {
		StringBuffer sb = new StringBuffer();
		sb.append("show create " + type + " ").append(owner).append(".").append(name);
		return sb.toString();
	}

	protected String getErrorInfoSQL(String owner, String type) {
		return null;
	}

	protected String getErrorInfoSQL(String owner, String name, String type) {
		return null;
	}

	protected String getSourceInfoSQL(String owner, String type) {
		StringBuffer sb = new StringBuffer();
		if("TRIGGER".equalsIgnoreCase(type)){
			sb.append("SELECT");
			sb.append("        TRIGGER_SCHEMA OWNER");
			sb.append("        ,TRIGGER_NAME NAME");
			sb.append("        ,'TRIGGER' TYPE");
			sb.append("    FROM");
			sb.append("        information_schema.TRIGGERS");
			sb.append("    WHERE");
			sb.append("        TRIGGER_SCHEMA = '"+SQLUtil.encodeQuotation(owner)+"'");
			sb.append("    ORDER BY");
			sb.append("        NAME");
		}else{
			sb.append("SELECT");
			sb.append("        ROUTINE_SCHEMA OWNER");
			sb.append("        ,ROUTINE_NAME NAME");
			sb.append("        ,ROUTINE_TYPE TYPE");
			sb.append("    FROM");
			sb.append("        information_schema.ROUTINES");
			sb.append("    WHERE");
			sb.append("        ROUTINE_SCHEMA = '"+SQLUtil.encodeQuotation(owner)+"'");
			sb.append("        AND ROUTINE_TYPE = '"+SQLUtil.encodeQuotation(type)+"'");
			sb.append("    ORDER BY");
			sb.append("        NAME");
		}

		
		return sb.toString();
	}
	public TriggerInfo[] getTriggerInfos(Connection con, String owner, String table) throws Exception {
		ResultSet rs = null;
		Statement st = null;
		List list = new ArrayList();

		try {
			st = con.createStatement();
			String sql = getTriggerInfoSQL(owner, table);
			if(sql == null){
				return null;
			}
			rs = st.executeQuery(sql);

			while (rs.next()) {

				TriggerInfo info = new TriggerInfo();
				info.setOwner(rs.getString("OWNER")); //$NON-NLS-1$
				info.setName(rs.getString("TRIGGER_NAME")); //$NON-NLS-1$
				info.setType(rs.getString("TRIGGER_TYPE")); //$NON-NLS-1$
				info.setEvent(rs.getString("TRIGGERING_EVENT")); //$NON-NLS-1$
				info.setContent(rs.getString("ACTION_STATEMENT")); //$NON-NLS-1$

				list.add(info);
			}

		} catch (Exception e) {
			DbPlugin.log(e);
			throw e;
		} finally {
			ResultSetUtil.close(rs);
			StatementUtil.close(st);
		}

		return (TriggerInfo[]) list.toArray(new TriggerInfo[0]);

	}
	// for tableEditor
	protected String getTriggerInfoSQL(String owner, String table) {
		StringBuffer sbDetail = new StringBuffer();
		sbDetail.append("SELECT");
		sbDetail.append("		TRIGGER_SCHEMA AS OWNER");
		sbDetail.append("		,TRIGGER_NAME AS TRIGGER_NAME");
		sbDetail.append("		,ACTION_TIMING AS TRIGGER_TYPE");
		sbDetail.append("		,EVENT_MANIPULATION AS TRIGGERING_EVENT");
		sbDetail.append("		,ACTION_STATEMENT AS ACTION_STATEMENT");
		sbDetail.append("    FROM");
		sbDetail.append("        information_schema.TRIGGERS");
		sbDetail.append("        WHERE EVENT_OBJECT_SCHEMA = '" + SQLUtil.encodeQuotation(owner) + "'");
		sbDetail.append("         AND EVENT_OBJECT_TABLE = '" + SQLUtil.encodeQuotation(table) + "'");
		return sbDetail.toString();
	}
}
