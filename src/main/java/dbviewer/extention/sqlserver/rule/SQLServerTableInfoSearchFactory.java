package dbviewer.extention.sqlserver.rule;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import zigen.plugin.db.DbPlugin;
import zigen.plugin.db.core.ResultSetUtil;
import zigen.plugin.db.core.SQLUtil;
import zigen.plugin.db.core.StatementUtil;
import zigen.plugin.db.core.TableInfo;
import zigen.plugin.db.core.rule.DefaultTableInfoSearchFactory;

public class SQLServerTableInfoSearchFactory extends DefaultTableInfoSearchFactory {
	protected DatabaseMetaData meta;

	public void setDatabaseMetaData(DatabaseMetaData meta) {
		this.meta = meta;

	}

	public SQLServerTableInfoSearchFactory() {
		super();

	}

	private String getSQLServerTableType(String type) {
		String wk_type = null;
		if ("TABLE".equalsIgnoreCase(type)) {
			wk_type = "BASE TABLE";
		} else {
			wk_type = type;
		}
		return wk_type;
	}

	public String getTableInfoAllSql(String schema, String[] types) {
		if("INFORMATION_SCHEMA".equalsIgnoreCase(schema)){
			return null;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT");
		sb.append("        TABLE_NAME");
		sb.append("        ,CASE TABLE_TYPE");
		sb.append("            WHEN 'BASE TABLE' THEN 'TABLE'");
		sb.append("            ELSE TABLE_TYPE");
		sb.append("        END AS TABLE_TYPE");
		sb.append("        ,null AS REMARKS");
		sb.append("    FROM");
		sb.append("        INFORMATION_SCHEMA.TABLES");
		sb.append("    WHERE");
		sb.append("        TABLE_CATALOG = '" + SQLUtil.encodeQuotation(getDbName()) + "'");
		sb.append("        AND TABLE_SCHEMA = '" + SQLUtil.encodeQuotation(schema) + "'");

		if (types.length > 0) {
			sb.append("        AND (");
			for (int i = 0; i < types.length; i++) {
				if (i > 0) {
					sb.append(" OR ");
				}
				sb.append("    TABLE_TYPE = '" + getSQLServerTableType(types[i]) + "'");
			}
			sb.append("    )");
		}
		return sb.toString();
	}

	public String getTableInfoSql(String schema, String tableName, String type) {
		if("INFORMATION_SCHEMA".equalsIgnoreCase(schema)){
			return null;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT");
		sb.append("        TABLE_NAME");
		sb.append("        ,CASE TABLE_TYPE");
		sb.append("            WHEN 'BASE TABLE' THEN 'TABLE'");
		sb.append("            ELSE TABLE_TYPE");
		sb.append("        END AS TABLE_TYPE");
		sb.append("        ,null AS REMARKS");
		sb.append("    FROM");
		sb.append("        INFORMATION_SCHEMA.TABLES");
		sb.append("    WHERE");
		sb.append("        TABLE_CATALOG = '" + SQLUtil.encodeQuotation(getDbName()) + "'");
		sb.append("        AND TABLE_SCHEMA = '" + SQLUtil.encodeQuotation(schema) + "'");
		sb.append("        AND TABLE_TYPE = '" + getSQLServerTableType(type) + "'");
		return sb.toString();
	}

	public String getDbName() {
		String name = null;
		try {
			final String paramName = ";databasename=";
			String url = meta.getURL();
			int startPos = url.toLowerCase().indexOf(paramName);
			int endPos = url.indexOf(";", startPos + 1);

			if (endPos == -1) {
				name = url.substring(startPos + paramName.length());
			} else {
				name = url.substring(startPos + paramName.length(), endPos);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return name;
	}

	public List getTableInfoAll(Connection con, String owner, String[] types, Character encloseChar) throws Exception {
		List result = null;
		ResultSet rs = null;
		Statement st = null;
		try {
			st = con.createStatement();
			String query = getTableInfoAllSql(owner, types);
			if (query != null) {
				result = new ArrayList();
				rs = st.executeQuery(query);
				while (rs.next()) {
					TableInfo info = new TableInfo();
					info.setName(rs.getString("TABLE_NAME"));
					String type = rs.getString("TABLE_TYPE");
					info.setTableType(type);
					info.setComment(rs.getString("REMARKS"));
					result.add(info);
				}
			}else{
				return super.getTableInfoAll(con, owner, types, encloseChar);
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
			String query = getTableInfoSql(owner, tableName, type);
			System.out.println(query);
			if (query != null) {
				rs = st.executeQuery(query);
				while (rs.next()) {
					info = new TableInfo();
					info.setName(rs.getString("TABLE_NAME"));
//					String t = rs.getString("TABLE_TYPE");
					info.setTableType(type);
					info.setComment(rs.getString("REMARKS"));
				}
			}else{
				return super.getTableInfo(con, owner, tableName, type, encloseChar);
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
