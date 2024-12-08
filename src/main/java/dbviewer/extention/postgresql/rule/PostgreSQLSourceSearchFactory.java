package dbviewer.extention.postgresql.rule;

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

public class PostgreSQLSourceSearchFactory extends AbstractSourceSearcherFactory {

	public PostgreSQLSourceSearchFactory(){
		super();
	}
	protected String getSequenceInfoSQL(String owner) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT");
		sb.append("        sequence_name AS NAME");
		sb.append("    FROM");
		sb.append("        information_schema.sequences");
		sb.append("    WHERE");
		sb.append("        sequence_schema = '"+SQLUtil.encodeQuotation(owner)+"'");
		sb.append("    ORDER BY sequence_name");
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
	protected String getSequenceInfoSQL(String owner, String sequence) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT");
		sb.append("		'"+owner+"'AS OWNER");
		sb.append("        ,sequence_name AS NAME");
		sb.append("        ,min_value AS MIN_VALUE");
		sb.append("        ,max_value AS MAX_VALUE");
		sb.append("        ,INCREMENT_by");
		sb.append("        ,case");
		sb.append("		   when is_cycled = true then 'Y' ");
		sb.append("		   when is_cycled = false then 'N'");
		sb.append("		   else 'N'");
		sb.append("		end AS CYCLE_FLAG");
		sb.append("		, 'N' AS ORDER_FLAG");
		sb.append("		, cache_value as CACHE_SIZE");
		sb.append("		,last_value as LAST_NUMBER");
		sb.append("    FROM");
		sb.append("        " + sequence);
		return sb.toString();

	}

	public SourceDetailInfo getSourceDetailInfo(Connection con, String owner, String name, String type, boolean visibleSchema) throws Exception {
		if("FUNCTION".equalsIgnoreCase(type)){
			return getFunctionSourceDetailInfo(con, owner, name, type, visibleSchema);
		}else if("TRIGGER".equalsIgnoreCase(type)){
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
		
		
	public SourceDetailInfo getFunctionSourceDetailInfo(Connection con, String owner, String name, String type, boolean visibleSchema) throws Exception {
		ResultSet rs = null;
		Statement st = null;

		SourceDetailInfo info = null;
		try {
			st = con.createStatement();
			
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT");
			sb.append("        p.ordinal_position");
			sb.append("        ,p.data_type");
			sb.append("        ,p.parameter_mode");
			sb.append("        ,p.parameter_name");
			sb.append("    FROM");
			sb.append("        information_schema.parameters p");
			sb.append("        ,information_schema.routines r");
			sb.append("        WHERE p.specific_catalog = r.specific_catalog");
			sb.append("         AND p.specific_schema = r.specific_schema");
			sb.append("         AND p.specific_name = r.specific_name");
			sb.append("         AND r.routine_schema = '" + SQLUtil.encodeQuotation(owner) + "'");
			sb.append("         AND r.routine_name = '" + SQLUtil.encodeQuotation(name) + "'");
			sb.append("         AND r.routine_type = '" + SQLUtil.encodeQuotation(type) + "'");
			sb.append("         ORDER BY p.ordinal_position");
			
			String argSql = sb.toString();
			StringBuffer argString = new StringBuffer();
			
			rs = st.executeQuery(argSql);
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					argString.append(rs.getString(2));
				} else {
					argString.append(", ").append(rs.getString(2));
				}
				i++;
			}

			
			StringBuffer ddl = new StringBuffer();
			rs = st.executeQuery(getDetailInfoSQL(owner, name, type));
			
			if (rs.next()) {
					info = new SourceDetailInfo();
					info.setOwner(rs.getString("OWNER")); //$NON-NLS-1$
					info.setName(rs.getString("NAME")); //$NON-NLS-1$
					info.setType(rs.getString("TYPE")); //$NON-NLS-1$

					String language = rs.getString("external_language");//$NON-NLS-1$
					String definition = rs.getString("routine_definition");//$NON-NLS-1$
					String returnType = rs.getString("data_type");
					
					definition = SQLUtil.encodeQuotation(definition);
					
					if (visibleSchema) {
						ddl.append("CREATE OR REPLACE ").append(info.getType());
						ddl.append(" ").append(info.getOwner()).append(".").append(info.getName());					
					} else {
						ddl.append("CREATE OR REPLACE ").append(info.getType()).append(" ").append(info.getName());
					}
					
					ddl.append("(").append(argString.toString()).append(")").append("\n");
					ddl.append("  RETURNS ").append(returnType).append(" AS").append("\n");
					ddl.append("'").append(definition).append("'").append("\n");
					ddl.append("  LANGUAGE '").append(language).append("'");
					
			}

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
	protected String getDetailInfoSQL(String owner, String name, String type) {
		StringBuffer sbDetail = new StringBuffer();
		sbDetail.append("SELECT");
		sbDetail.append("		routine_schema AS OWNER");
		sbDetail.append("		,routine_name AS NAME");
		sbDetail.append("		,routine_type AS TYPE");
		sbDetail.append("		,data_type");
		sbDetail.append("		,external_language");
		sbDetail.append("		,routine_definition");
		sbDetail.append("    FROM");
		sbDetail.append("        information_schema.routines");
		sbDetail.append("        WHERE routine_schema = '" + SQLUtil.encodeQuotation(owner) + "'");
		sbDetail.append("         AND routine_name = '" + SQLUtil.encodeQuotation(name) + "'");
		sbDetail.append("         AND routine_type = '" + SQLUtil.encodeQuotation(type) + "'");
		return sbDetail.toString();
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
			sb.append("        DISTINCT trigger_schema AS OWNER");
			sb.append("        ,trigger_name AS NAME");
			sb.append("        ,'TRIGGER' AS TYPE");
			sb.append("    FROM");
			sb.append("        information_schema.triggers");
			sb.append("    WHERE");
			sb.append("        trigger_schema = '"+SQLUtil.encodeQuotation(owner)+"'");
			sb.append("    ORDER BY");
			sb.append("        NAME");

		}else{
			// for Function etc..
			sb.append("SELECT");
			sb.append("        routine_schema AS OWNER");
			sb.append("        ,routine_name AS NAME");
			sb.append("        ,routine_type AS TYPE");
			sb.append("    FROM");
			sb.append("        information_schema.routines");
			sb.append("    WHERE");
			sb.append("        routine_schema = '"+SQLUtil.encodeQuotation(owner)+"'");
			sb.append("    AND routine_type = '"+SQLUtil.encodeQuotation(type)+"'");
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
		sb.append(DbPluginConstant.LINE_SEP);

		sb.append("     INCREMENT " + sequenceInfo.getIncrement_by()); //$NON-NLS-1$
		sb.append(DbPluginConstant.LINE_SEP);
		sb.append("     MINVALUE " + sequenceInfo.getMin_value()); //$NON-NLS-1$
		sb.append(DbPluginConstant.LINE_SEP);
		sb.append("     MAXVALUE " + sequenceInfo.getMax_value()); //$NON-NLS-1$
		sb.append(DbPluginConstant.LINE_SEP);

		
		sb.append("     START " + sequenceInfo.getLast_number()); //$NON-NLS-1$
		sb.append(DbPluginConstant.LINE_SEP);
		
		sb.append("     CACHE "); //$NON-NLS-1$
		sb.append(sequenceInfo.getCache_size());

		if ("Y".equals(sequenceInfo.getCycle_flg())) { //$NON-NLS-1$
			sb.append("     CYCLE "); //$NON-NLS-1$
			sb.append(DbPluginConstant.LINE_SEP);
		} else {
			;
		}
		return sb.toString();
	}

	protected String getTriggerInfoSQL(String owner, String table) {
		return null;
	}
}
