package dbviewer.extention.mysql.rule;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import zigen.plugin.db.DbPlugin;
import zigen.plugin.db.core.DBType;
import zigen.plugin.db.core.ResultSetUtil;
import zigen.plugin.db.core.SQLUtil;
import zigen.plugin.db.core.SchemaSearcher;
import zigen.plugin.db.core.StatementUtil;
import zigen.plugin.db.core.TableInfo;
import zigen.plugin.db.core.rule.AbstractTableInfoSearchFactory;


public class MySQLTableInfoSearchFactory extends AbstractTableInfoSearchFactory{
	
	public MySQLTableInfoSearchFactory(){
		super();
	}
	
	public String getDbName() {
		return null;
	}

	protected DatabaseMetaData meta;
	
	public void setDatabaseMetaData(DatabaseMetaData meta) {
		this.meta = meta;
	}
	
	// for MySQL V5
	protected String getTableInfoAllSql(String owner, String[] types){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT");
		sb.append("        TABLE_NAME");
		sb.append("        ,TABLE_TYPE");
		sb.append(" ,TABLE_COMMENT REMARKS");
		// sb.append(" ,'' REMARKS");
		sb.append("    FROM");
		sb.append("        information_schema.TABLES");
		sb.append("    WHERE");
		sb.append("        TABLE_SCHEMA = '" + SQLUtil.encodeQuotation(owner) + "'");
		if (types.length > 0) {
			sb.append("    AND (");
			for (int i = 0; i < types.length; i++) {
				if (i > 0) {
					sb.append(" OR ");
				}
				sb.append("    TABLE_TYPE Like '%" + SQLUtil.encodeQuotation(types[i]) + "'");
			}
			sb.append("    )");
		}
		return sb.toString();
	}
	
	// for MySQL V5
	protected String getTableInfoSql(String owner, String table, String type){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT");
		sb.append("        TABLE_NAME");
		sb.append("        ,TABLE_TYPE");
		sb.append(" ,TABLE_COMMENT REMARKS");
		// sb.append(" ,'' REMARKS");
		sb.append("    FROM");
		sb.append("        information_schema.TABLES");
		sb.append("    WHERE");
		sb.append("        TABLE_SCHEMA = '" + SQLUtil.encodeQuotation(owner) + "'");
		sb.append("        AND TABLE_NAME = '" + SQLUtil.encodeQuotation(table) + "'");
		sb.append("        AND TABLE_TYPE Like '%" + SQLUtil.encodeQuotation(type) + "'");
		return sb.toString();
	}
	public List getTableInfoAll(Connection con, String owner, String[] types, Character encloseChar) throws Exception {
		List result = null;
		ResultSet rs = null;
		Statement st = null;
		try {
			st = con.createStatement();
			result = new ArrayList();
			
			if (DBType.getType(meta) == DBType.DB_TYPE_MYSQL && meta.getDatabaseMajorVersion() >= 5) {
				st = con.createStatement();
				rs = st.executeQuery(getTableInfoAllSql(owner, types));
			} else {
				if (SchemaSearcher.isSupport(con)) {
					rs = meta.getTables(null, owner, "%", types); //$NON-NLS-1$
				} else {
					rs = meta.getTables(null, "%", "%", types); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}

			while (rs.next()) {
				TableInfo info = new TableInfo();
				String wtableName = rs.getString("TABLE_NAME"); //$NON-NLS-1$
				if (encloseChar != null) {
					info.setName(SQLUtil.enclose(wtableName, encloseChar.charValue()));
				} else {
					info.setName(wtableName);
				}
				info.setTableType(rs.getString("TABLE_TYPE")); //$NON-NLS-1$
				info.setComment(rs.getString("REMARKS")); //$NON-NLS-1$				
				result.add(info);
			}
		} catch (Exception e) {
			DbPlugin.log(e);
			throw e;
		} finally {
			ResultSetUtil.close(rs);
			StatementUtil.close(st);
		}
		return result;
	}

	public TableInfo getTableInfo(Connection con, String owner, String tableName, String type, Character encloseChar) throws Exception {
		
		TableInfo info = null;
		ResultSet rs = null;
		Statement st = null;
		try {
			st = con.createStatement();
			
			if (DBType.getType(meta) == DBType.DB_TYPE_MYSQL && meta.getDatabaseMajorVersion() >= 5) {
				st = con.createStatement();
				rs = st.executeQuery(getTableInfoSql(owner, tableName, type));
			} else {
				if (SchemaSearcher.isSupport(con)) {
					rs = meta.getTables(null, owner, tableName, new String[]{type}); //$NON-NLS-1$
				} else {
					rs = meta.getTables(null, "%", tableName, new String[]{type}); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			
			if (rs.next()) {
				info = new TableInfo();
				String wtableName = rs.getString("TABLE_NAME"); //$NON-NLS-1$
				if (encloseChar != null) {
					info.setName(SQLUtil.enclose(wtableName, encloseChar.charValue()));
				} else {
					info.setName(wtableName);
				}
				info.setTableType(rs.getString("TABLE_TYPE")); //$NON-NLS-1$
				info.setComment(rs.getString("REMARKS")); //$NON-NLS-1$
	
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

}
