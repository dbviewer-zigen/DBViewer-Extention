package dbviewer.extention.h2.rule;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zigen.plugin.db.DbPlugin;
import zigen.plugin.db.DbPluginConstant;
import zigen.plugin.db.core.ResultSetUtil;
import zigen.plugin.db.core.SQLUtil;
import zigen.plugin.db.core.StatementUtil;
import zigen.plugin.db.core.StringUtil;
import zigen.plugin.db.core.rule.AbstractSourceSearcherFactory;
import zigen.plugin.db.core.rule.SequenceInfo;
import zigen.plugin.db.core.rule.SourceDetailInfo;
import zigen.plugin.db.preference.SQLEditorPreferencePage;

public class H2SourceSearchFactory extends AbstractSourceSearcherFactory{

	protected String getErrorInfoSQL(String owner, String type) {
		// TODO Auto-generated method stub
		return null;
	}
	protected String getErrorInfoSQL(String owner, String name, String type) {
		// TODO Auto-generated method stub
		return null;
	}
	protected String getDetailInfoSQL(String owner, String name, String type) {
		// TODO Auto-generated method stub
		return null;
	}
	public H2SourceSearchFactory(){
		super();
	}
	protected String getSequenceInfoSQL(String owner) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT");
		sb.append("        SEQUENCE_NAME NAME");
		sb.append("    FROM");
		sb.append("        INFORMATION_SCHEMA.SEQUENCES");
		sb.append("    WHERE");
		sb.append("        SEQUENCE_SCHEMA = '"+SQLUtil.encodeQuotation(owner)+"'");
		sb.append("    ORDER BY SEQUENCE_NAME");
		return sb.toString();
	}
	public SequenceInfo[] getSequenceInfos(Connection con, String owner) throws Exception {
		ResultSet rs = null;
		Statement st = null;
		List list = new ArrayList();

		try {
			st = con.createStatement();
			String sql = getSequenceInfoSQL(owner);
			if(sql == null){
				return null;
			}
			rs = st.executeQuery(sql);

			List wkList = new ArrayList();
			while (rs.next()) {
				wkList.add(rs.getString("NAME"));
			}

			for (Iterator iterator = wkList.iterator(); iterator.hasNext();) {
				String name = (String) iterator.next();
				list.add(getSequenceInfo(con, owner, name));
			}

		} catch (Exception e) {
			DbPlugin.log(e);
			throw e;
		} finally {
			ResultSetUtil.close(rs);
			StatementUtil.close(st);
		}
		return (SequenceInfo[]) list.toArray(new SequenceInfo[0]);

	}

	public SequenceInfo getSequenceInfo(Connection con, String owner, String sequence) throws Exception {
		ResultSet rs = null;
		Statement st = null;
		SequenceInfo info = null;

		try {
			st = con.createStatement();
			String sql = getSequenceInfoSQL(owner, sequence);
			if(sql == null){
				return null;
			}
			rs = st.executeQuery(sql);

			if (rs.next()) {
				info = new SequenceInfo();
				info.setSequece_owner(rs.getString(1));
				info.setSequence_name(rs.getString(2));
				info.setMin_value(rs.getBigDecimal(3));
				info.setMax_value(rs.getBigDecimal(4));
				info.setIncrement_by(rs.getBigDecimal(5));
				info.setCycle_flg(rs.getString(6));
				info.setOrder_flg(rs.getString(7));
				info.setCache_size(rs.getBigDecimal(8));
				info.setLast_number(rs.getBigDecimal(9));

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
	protected String getSequenceInfoSQL(String owner, String sequence) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT");
		sb.append("        SEQUENCE_SCHEMA OWNER");
		sb.append("        ,SEQUENCE_NAME NAME");
		sb.append("        ,null AS MIN_VALUE");
		sb.append("        ,null AS MAX_VALUE");
		sb.append("        ,INCREMENT INCREMENT_BY");
		sb.append("        ,null CYCLE_FLAG");
		sb.append("        ,null ORDER_FLAG");
		sb.append("        ,CACHE AS CACHE_SIZE");
		sb.append("        ,CURRENT_VALUE AS LAST_NUMBER");
		sb.append("    FROM");
		sb.append("        INFORMATION_SCHEMA.SEQUENCES");
		sb.append("    WHERE");
		sb.append("        SEQUENCE_SCHEMA = '"+SQLUtil.encodeQuotation(owner)+"'");
		sb.append("        AND SEQUENCE_NAME = '"+SQLUtil.encodeQuotation(sequence)+"'");
		return sb.toString();
	}

	public SourceDetailInfo getSourceDetailInfo(Connection con, String owner, String name, String type, boolean visibleSchema) throws Exception {
		if("TRIGGER".equalsIgnoreCase(type)){
			//return getTriggerSourceDetailInfo(con, owner, name, type, visibleSchema);
			return getTriggerSourceDetailInfo(con, owner, name, type, false);
		}else{
			return null;
		}
	}

	public SourceDetailInfo getTriggerSourceDetailInfo(Connection con, String owner, String name, String type, boolean visibleSchema) throws Exception {
		ResultSet rs = null;
		Statement st = null;

		SourceDetailInfo info = null;
		try {
			st = con.createStatement();

			StringBuffer sb = new StringBuffer();
			sb.append("SELECT");
			sb.append("        trigger_schema AS OWNER");
			sb.append("        ,trigger_name AS NAME");
			sb.append("        ,'TRIGGER' AS TYPE");
			sb.append("        ,condition_timing");
			sb.append("        ,event_manipulation");
			sb.append("        ,event_object_schema");
			sb.append("        ,event_object_table");
			sb.append("        ,action_statement");
			sb.append("    FROM");
			sb.append("        information_schema.triggers");
			sb.append("    WHERE");
			sb.append("        trigger_schema = '" + SQLUtil.encodeQuotation(owner) + "'");
			sb.append("        AND trigger_name = '" + SQLUtil.encodeQuotation(name) + "'");

			StringBuffer event = new StringBuffer();
			rs = st.executeQuery(sb.toString());
			int i = 0;
			String timing = null;
			String targetSchema = null;
			String targetTable = null;
			String stat = null;
			while (rs.next()) {
				if (i == 0) {
					info = new SourceDetailInfo();
					info.setOwner(rs.getString("OWNER")); //$NON-NLS-1$
					info.setName(rs.getString("NAME")); //$NON-NLS-1$
					info.setType(rs.getString("TYPE")); //$NON-NLS-1$

					timing = rs.getString("condition_timing");
					targetSchema = rs.getString("event_object_schema");
					targetTable = rs.getString("event_object_table");
					stat = rs.getString("action_statement");

					event.append(rs.getString("event_manipulation"));

				} else {
					event.append(" OR ").append(
							rs.getString("event_manipulation"));
				}
				i++;
			}
			StringBuffer ddl = new StringBuffer();
			if (visibleSchema) {
				ddl.append("CREATE ").append(info.getType());
				ddl.append(" ").append(info.getOwner()).append(".").append(info.getName()).append("\n");
			} else {
				ddl.append("CREATE ").append(info.getType()).append(" ").append(info.getName()).append("\n");
			}

			ddl.append("  ").append(timing).append(" ").append(event.toString()).append("\n");
			ddl.append("  ON");
			if (visibleSchema) {
				ddl.append(" ").append(targetSchema).append(".").append(targetTable).append("\n");
			} else {
				ddl.append(" ").append(targetTable).append("\n");
			}
			ddl.append("  FOR EACH ROW").append("\n");
			ddl.append("  ").append(stat);

			if (info != null) {
				info.setText(ddl.toString());
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



	protected String getSourceInfoSQL(String owner, String type) {
		StringBuffer sb = new StringBuffer();
		if("TRIGGER".equalsIgnoreCase(type)){

			sb.append("SELECT");
			sb.append("        DISTINCT trigger_schema AS OWNER");
			sb.append("        ,trigger_name AS NAME");
			sb.append("        ,'TRIGGER' AS TYPE");
			sb.append("    FROM");
			sb.append("        information_schema.triggers");
			sb.append("    WHERE");
			sb.append("        trigger_schema = '"+SQLUtil.encodeQuotation(owner)+"'");
			sb.append("    ORDER BY");
			sb.append("        NAME");

		}
		return sb.toString();
	}

	public String getSequenceDDL(SequenceInfo sequenceInfo){
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE"); //$NON-NLS-1$
		sb.append(" SEQUENCE "); //$NON-NLS-1$
		if (StringUtil.isNumeric(sequenceInfo.getSequece_owner())) {
			sb.append("\"");
			sb.append(sequenceInfo.getSequece_owner());
			sb.append("\"");

		} else {
			sb.append(sequenceInfo.getSequece_owner());
		}
		sb.append("."); //$NON-NLS-1$
		sb.append(sequenceInfo.getSequence_name());

		String demiliter = DbPlugin.getDefault().getPreferenceStore().getString(SQLEditorPreferencePage.P_SQL_DEMILITER);
		if ("/".equals(demiliter)) { //$NON-NLS-1$
			sb.append(DbPluginConstant.LINE_SEP);
		}
		sb.append(demiliter);
		sb.append(DbPluginConstant.LINE_SEP);


		sb.append("--INCREMENT " + sequenceInfo.getIncrement_by()); //$NON-NLS-1$
		sb.append(DbPluginConstant.LINE_SEP);
//		sb.append("     MINVALUE " + sequenceInfo.getMin_value()); //$NON-NLS-1$
//		sb.append(DbPluginConstant.LINE_SEP);
//		sb.append("     MAXVALUE " + sequenceInfo.getMax_value()); //$NON-NLS-1$
//		sb.append(DbPluginConstant.LINE_SEP);
		sb.append("--CURRENT " + sequenceInfo.getLast_number()); //$NON-NLS-1$
		sb.append(DbPluginConstant.LINE_SEP);

		sb.append("--CACHE "); //$NON-NLS-1$
		sb.append(sequenceInfo.getCache_size());
		sb.append(DbPluginConstant.LINE_SEP);

		return sb.toString();
	}

	protected String getTriggerInfoSQL(String owner, String table) {
		return null;
	}
}
