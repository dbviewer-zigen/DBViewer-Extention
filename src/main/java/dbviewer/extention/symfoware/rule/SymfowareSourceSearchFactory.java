package dbviewer.extention.symfoware.rule;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import zigen.plugin.db.core.rule.AbstractTableInfoSearchFactory;
import zigen.plugin.db.core.rule.ITableInfoSearchFactory;
import zigen.plugin.db.core.rule.SequenceInfo;
import zigen.plugin.db.core.rule.SourceDetailInfo;
import zigen.plugin.db.core.rule.SourceInfo;
import zigen.plugin.db.core.rule.SymfowareSequenceInfo;
import zigen.plugin.db.core.rule.TriggerInfo;
import zigen.plugin.db.preference.SQLEditorPreferencePage;
import zigen.plugin.db.ui.internal.DataBase;

public class SymfowareSourceSearchFactory extends AbstractSourceSearcherFactory{

	protected String getErrorInfoSQL(String owner, String type) {
		return null;
	}
	protected String getErrorInfoSQL(String owner, String name, String type) {
		return null;
	}
	protected String getDetailInfoSQL(String owner, String name, String type) {
		return null;
	}
	public SymfowareSourceSearchFactory(){
		super();
	}
	protected String getSequenceInfoSQL(String owner) {
		return null;
	}

	protected String getSequenceInfoSQL(String owner, String sequence) {
		return null;
	}

	protected String getSourceInfoSQL(String owner, String type) {
		return null;
	}

	protected String getTriggerInfoSQL(String owner, String table) {
		return null;
	}

	private String getDBName(DatabaseMetaData meta) throws SQLException{
		ITableInfoSearchFactory factory = AbstractTableInfoSearchFactory.getFactory(meta);
		return factory.getDbName();

	}
	public SequenceInfo[] getSequenceInfos(Connection con, String owner) throws Exception {
		ResultSet rs = null;
		Statement st = null;
		List list = new ArrayList();

		try {
			String dbName = getDBName(con.getMetaData());
			st = con.createStatement();

			StringBuffer sb = new StringBuffer();
			sb.append("SELECT");
			sb.append("        TRIM(SEQUENCE_NAME)");
			sb.append("    FROM");
			sb.append("        RDBII_SYSTEM.RDBII_SEQUENCE");
			sb.append("    WHERE");
			sb.append("        DB_NAME = '"+SQLUtil.encodeQuotation(dbName)+"'");
			sb.append("        AND SCHEMA_NAME = '"+SQLUtil.encodeQuotation(owner)+"'");
			sb.append("    ORDER BY");
			sb.append("        SEQUENCE_NAME");
			String sql = sb.toString();

			rs = st.executeQuery(sql);

			List wkList = new ArrayList();
			while (rs.next()) {
				wkList.add(rs.getString(1));
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
		SymfowareSequenceInfo info = null;

		try {
			String dbName = getDBName(con.getMetaData());
			st = con.createStatement();

			StringBuffer sb = new StringBuffer();
			sb.append("SELECT");
			sb.append("        TRIM(SCHEMA_NAME)"); // not SEQUENCE_OWNER but SCHEMA_NAME
			sb.append("        ,TRIM(SEQUENCE_NAME)");
			sb.append("        ,MIN_VAL AS MIN_VALUE");
			sb.append("        ,MAX_VAL AS MAX_VALUE");
			sb.append("        ,INCREMENT_VAL AS INCREMENT_BY");
			sb.append("        ,CYCLE_SPEC AS CYCLE_FLAG");
			sb.append("        ,ORDER_SPEC AS ORDER_FLAG");
			sb.append("        ,CACHE_VAL AS CACHE_SIZE");
			sb.append("        ,CACHE_MIN_VAL AS CACHE_MIN_SIZE");
			sb.append("        ,START_WITH_VAL AS LAST_NUMBER");
			sb.append("    FROM");
			sb.append("        RDBII_SYSTEM.RDBII_SEQUENCE");
			sb.append("    WHERE");
			sb.append("        DB_NAME = '"+SQLUtil.encodeQuotation(dbName)+"'");
			sb.append("        AND SCHEMA_NAME = '"+SQLUtil.encodeQuotation(owner)+"'");
			sb.append("        AND SEQUENCE_NAME = '"+SQLUtil.encodeQuotation(sequence)+"'");
			String sql = sb.toString();

			rs = st.executeQuery(sql);

			if (rs.next()) {
				info = new SymfowareSequenceInfo();
				info.setSequece_owner(rs.getString(1));
				info.setSequence_name(rs.getString(2));
				info.setMin_value(rs.getBigDecimal(3));
				info.setMax_value(rs.getBigDecimal(4));
				info.setIncrement_by(rs.getBigDecimal(5));
				info.setCycle_flg(rs.getString(6).trim());	// Y or N
				info.setOrder_flg(rs.getString(7).trim());	// Y or N
				info.setCache_size(rs.getBigDecimal(8));
				info.setCache_min_size(rs.getBigDecimal(9));	// add element
				info.setLast_number(rs.getBigDecimal(10));
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


	public SourceDetailInfo getSourceDetailInfo(Connection con, String owner, String name, String type, boolean visibleSchema) throws Exception {
		if("TRIGGER".equalsIgnoreCase(type)){
			return getTriggerSourceDetailInfo(con, owner, name, type, visibleSchema);
		}else{
			return null;
		}
	}
	public TriggerInfo[] getTriggerInfos(Connection con, String owner, String table) throws Exception {
		ResultSet rs = null;
		Statement st = null;
		List list = new ArrayList();

		try {
			String dbName = getDBName(con.getMetaData());
			st = con.createStatement();

			StringBuffer sb = new StringBuffer();
			sb.append("SELECT");
			sb.append("        TRIM(TRIG_SCHEMA_NAME) AS OWNER");
			sb.append("        ,TRIM(TRIG_NAME) AS TRIGGER_NAME");
			sb.append("        ,TRIM(COND_TIMING) AS TRIGGER_TYPE");
			sb.append("        ,TRIM(EVENT_MANIPURATION) AS TRIGGERING_EVENT");
			sb.append("        ,TRIM(ACTION_STMT_LIST) ACTION_STMT_LIST");
			sb.append("    FROM");
			sb.append("        RDBII_SYSTEM.RDBII_TRIGGER");
			sb.append("    WHERE");
			sb.append("        TRIG_DB_NAME = '"+SQLUtil.encodeQuotation(dbName)+"'");
			sb.append("        AND EVENT_OBJ_SCH_NAME = '"+SQLUtil.encodeQuotation(owner)+"'");
			sb.append("        AND EVENT_OBJ_TBL_NAME = '"+SQLUtil.encodeQuotation(table)+"'");
			sb.append("    ORDER BY");
			sb.append("        TRIG_NAME");
			String sql = sb.toString();

			rs = st.executeQuery(sql);

			while (rs.next()) {

				TriggerInfo info = new TriggerInfo();
				info.setOwner(rs.getString("OWNER")); //$NON-NLS-1$
				info.setName(rs.getString("TRIGGER_NAME")); //$NON-NLS-1$
				info.setType(rs.getString("TRIGGER_TYPE")); //$NON-NLS-1$
				info.setEvent(rs.getString("TRIGGERING_EVENT")); //$NON-NLS-1$
				info.setContent(rs.getString("ACTION_STMT_LIST"));
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
	public SourceDetailInfo getTriggerSourceDetailInfo(Connection con, String owner, String name, String type, boolean visibleSchema) throws Exception {
		ResultSet rs = null;
		Statement st = null;

		SourceDetailInfo info = null;
		try {
			String dbName = getDBName(con.getMetaData());
			st = con.createStatement();

			StringBuffer sb = new StringBuffer();
			sb.append("SELECT");
			sb.append("        TRIM(T.TRIG_DB_NAME) TRIG_DB_NAME");
			sb.append("        ,TRIM(T.TRIG_SCHEMA_NAME) TRIG_SCHEMA_NAME");
			sb.append("        ,TRIM(T.TRIG_NAME) TRIG_NAME");
			sb.append("        ,TRIM(T.TRIG_OWNER) TRIG_OWNER");
			sb.append("        ,TRIM(T.EVENT_MANIPURATION) EVENT_MANIPURATION");
			sb.append("        ,TRIM(T.EVENT_OBJ_SCH_NAME) EVENT_OBJ_SCH_NAME");
			sb.append("        ,TRIM(T.EVENT_OBJ_TBL_NAME) EVENT_OBJ_TBL_NAME");
			sb.append("        ,T.EVENT_OBJ_SCH_CODE");
			sb.append("        ,T.EVENT_OBJ_TBL_CODE");
			sb.append("        ,T.ACTION_ORDER");
			sb.append("        ,TRIM(T.ACTION_CONDITION) ACTION_CONDITION");
			sb.append("        ,TRIM(T.ACTION_STMT_LIST) ACTION_STMT_LIST");
			sb.append("        ,TRIM(T.ACTION_ORIENTATION) ACTION_ORIENTATION");
			sb.append("        ,TRIM(T.COND_TIMING) COND_TIMING");
			sb.append("        ,TRIM(T.COND_REF_OLD_TABLE) COND_REF_OLD_TABLE");
			sb.append("        ,TRIM(T.COND_REF_NEW_TABLE) COND_REF_NEW_TABLE");
			sb.append("        ,TRIM(T.COL_LIST_IS_IMPLCT) COL_LIST_IS_IMPLCT");
			sb.append("        ,TRIM(COL.COLUMN_NAME) COLUMN_NAME");
			sb.append("        ,'TRIGGER' TYPE");
			sb.append("    FROM");
			sb.append("        RDBII_SYSTEM.RDBII_TRIGGER T");
			sb.append("            LEFT OUTER JOIN RDBII_SYSTEM.RDBII_TRIG_COLUMNS C");
			sb.append("                ON (");
			sb.append("                    T.TRIG_DB_CODE = C.TRIG_DB_CODE");
			sb.append("                    AND T.TRIG_SCHEMA_CODE = C.TRIG_SCHEMA_CODE");
			sb.append("                    AND T.TRIG_CODE = C.TRIG_CODE");
			sb.append("                )");
			sb.append("            LEFT OUTER JOIN RDBII_SYSTEM.RDBII_COLUMN COL");
			sb.append("                ON (");
			sb.append("                    COL.DB_CODE = T.TRIG_DB_CODE");
			sb.append("                    AND COL.SCHEMA_CODE = T.EVENT_OBJ_SCH_CODE");
			sb.append("                    AND COL.TABLE_CODE = T.EVENT_OBJ_TBL_CODE");
			sb.append("                    AND COL.COLUMN_CODE = C.EVENT_OBJ_COL_CODE");
			sb.append("                )");
			sb.append("    WHERE");
			sb.append("        T.TRIG_DB_NAME = '"+SQLUtil.encodeQuotation(dbName)+"'");
			sb.append("        AND T.TRIG_SCHEMA_NAME = '"+SQLUtil.encodeQuotation(owner)+"'");
			sb.append("        AND T.TRIG_NAME = '"+SQLUtil.encodeQuotation(name)+"'");

			StringBuffer event = new StringBuffer();
			rs = st.executeQuery(sb.toString());

			String timing = null;
			String targetSchema = null;
			String targetTable = null;
			String stat = null;
			String column = null;

			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					info = new SourceDetailInfo();
					info.setOwner(rs.getString("TRIG_SCHEMA_NAME")); //$NON-NLS-1$
					info.setName(rs.getString("TRIG_NAME")); //$NON-NLS-1$
					info.setType(rs.getString("TYPE")); //$NON-NLS-1$
					timing = rs.getString("COND_TIMING");
					targetSchema = rs.getString("EVENT_OBJ_SCH_NAME");
					targetTable = rs.getString("EVENT_OBJ_TBL_NAME");
					stat = rs.getString("ACTION_STMT_LIST");
					event.append(rs.getString("EVENT_MANIPURATION"));
					column = rs.getString("COLUMN_NAME");
				} else {
					event.append(" OR ").append(rs.getString("EVENT_MANIPURATION"));
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

			ddl.append("  ").append(timing).append(" ").append(event.toString());

			if(column != null){
				ddl.append(" OF ").append(column);
			}

			ddl.append(" ON");
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


	public SourceInfo[] getSourceInfos(Connection con, String owner, String type) throws Exception {
		ResultSet rs = null;
		Statement st = null;
		List list = new ArrayList();

		try {
			String dbName = getDBName(con.getMetaData());

			st = con.createStatement();
			String sql = null;
			boolean hasDDL = true;

			if("TRIGGER".equalsIgnoreCase(type)){
				hasDDL = true;
				StringBuffer sb = new StringBuffer();
				sb.append("SELECT");
				sb.append("        TRIM(TRIG_SCHEMA_NAME) AS OWNER");
				sb.append("        ,TRIM(TRIG_NAME) AS TRIG_NAME");
				sb.append("        ,'TRIGGER' AS TYPE");
				sb.append("    FROM");
				sb.append("        RDBII_SYSTEM.RDBII_TRIGGER");
				sb.append("    WHERE");
				sb.append("        TRIG_DB_NAME = '"+SQLUtil.encodeQuotation(dbName)+"'");
				sb.append("        AND TRIG_SCHEMA_NAME = '"+SQLUtil.encodeQuotation(owner)+"'");
				sb.append("    ORDER BY");
				sb.append("        TRIG_NAME");
				sql = sb.toString();
			}else if("PROCEDURE".equalsIgnoreCase(type)){
				hasDDL = false;	// can not get ddl source.
				StringBuffer sb = new StringBuffer();
				sb.append("SELECT");
				sb.append("        TRIM(SCHEMA_NAME) AS SCHEMA_NAME");
				sb.append("        ,TRIM(PROCEDURE_NAME) AS PROCEDURE_NAME");
				sb.append("        ,'PROCEDURE' AS TYPE");
				sb.append("    FROM");
				sb.append("        RDBII_SYSTEM.RDBII_PROC");
				sb.append("    WHERE");
				sb.append("        PROCEDURE_TYPE = 1");
				sb.append("        AND DB_NAME = '"+SQLUtil.encodeQuotation(dbName)+"'");
				sb.append("        AND SCHEMA_NAME = '"+SQLUtil.encodeQuotation(owner)+"'");
				sb.append("    ORDER BY");
				sb.append("        PROCEDURE_NAME");
				sql = sb.toString();
			}else if("FUNCTION".equalsIgnoreCase(type)){
				hasDDL = false;	// can not get ddl source.
				StringBuffer sb = new StringBuffer();
				sb.append("SELECT");
				sb.append("        TRIM(SCHEMA_NAME) AS SCHEMA_NAME");
				sb.append("        ,TRIM(PROCEDURE_NAME) AS PROCEDURE_NAME");
				sb.append("        ,'FUNCTION' AS TYPE");
				sb.append("    FROM");
				sb.append("        RDBII_SYSTEM.RDBII_PROC");
				sb.append("    WHERE");
				sb.append("        PROCEDURE_TYPE = 2");
				sb.append("        AND DB_NAME = '"+SQLUtil.encodeQuotation(dbName)+"'");
				sb.append("        AND SCHEMA_NAME = '"+SQLUtil.encodeQuotation(owner)+"'");
				sb.append("    ORDER BY");
				sb.append("        PROCEDURE_NAME");
				sql = sb.toString();
			}

			if(sql == null) return null;

			rs = st.executeQuery(sql);

			while (rs.next()) {
				SourceInfo info = new SourceInfo();
				info.setOwner(rs.getString(1)); //$NON-NLS-1$
				info.setName(rs.getString(2)); //$NON-NLS-1$
				info.setType(rs.getString(3)); //$NON-NLS-1$
				info.setHasDDL(hasDDL);
				list.add(info);
			}

		} catch (Exception e) {
			DbPlugin.log(e);
			throw e;
		} finally {
			ResultSetUtil.close(rs);
			StatementUtil.close(st);
		}

		return (SourceInfo[]) list.toArray(new SourceInfo[0]);

	}

	public String getSequenceDDL(SequenceInfo sequenceInfo){

		if(sequenceInfo instanceof SymfowareSequenceInfo){
			StringBuffer sb = new StringBuffer();
			SymfowareSequenceInfo info = (SymfowareSequenceInfo)sequenceInfo;

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
			sb.append("     INCREMENT BY " + sequenceInfo.getIncrement_by()); //$NON-NLS-1$
			sb.append(DbPluginConstant.LINE_SEP);
			sb.append("     START WITH " + sequenceInfo.getLast_number()); //$NON-NLS-1$
			sb.append(DbPluginConstant.LINE_SEP);
			sb.append("     MAXVALUE " + sequenceInfo.getMax_value()); //$NON-NLS-1$
			sb.append(DbPluginConstant.LINE_SEP);
			sb.append("     MINVALUE " + sequenceInfo.getMin_value()); //$NON-NLS-1$
			sb.append(DbPluginConstant.LINE_SEP);

			if("Y".equals(sequenceInfo.getCycle_flg())){
				sb.append("     CYCLE"); //$NON-NLS-1$
				sb.append(DbPluginConstant.LINE_SEP);
			}
			sb.append("     CACHE "); //$NON-NLS-1$
			sb.append(sequenceInfo.getCache_size());
			sb.append(", ");
			sb.append(info.getCache_min_size());
			sb.append(DbPluginConstant.LINE_SEP);

			if("Y".equals(sequenceInfo.getOrder_flg())){
				sb.append("     ORDER"); //$NON-NLS-1$
				sb.append(DbPluginConstant.LINE_SEP);
			}

			String demiliter = DbPlugin.getDefault().getPreferenceStore().getString(SQLEditorPreferencePage.P_SQL_DEMILITER);
			if ("/".equals(demiliter)) { //$NON-NLS-1$
				sb.append(DbPluginConstant.LINE_SEP);
			}
			sb.append(demiliter);
			sb.append(DbPluginConstant.LINE_SEP);
			System.out.println(sb.toString());
			return sb.toString();
		}
		return null;

	}

}
