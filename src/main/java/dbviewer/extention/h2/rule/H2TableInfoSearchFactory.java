package dbviewer.extention.h2.rule;

import zigen.plugin.db.core.SQLUtil;
import zigen.plugin.db.core.rule.AbstractTableInfoSearchFactory;


public class H2TableInfoSearchFactory extends AbstractTableInfoSearchFactory {
	public H2TableInfoSearchFactory(){
		super();
	}
	public String getTableInfoAllSql(String schema, String[] types) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT");
		sb.append("        table_name TABLE_NAME");
		sb.append("        ,table_type TABLE_TYPE");
		sb.append("        ,REMARKS REMARKS");
		sb.append("    FROM");
		sb.append("        INFORMATION_SCHEMA.TABLES");
		sb.append("    WHERE");
		sb.append("        table_schema = '" + SQLUtil.encodeQuotation(schema) + "'");
		if (types.length > 0) {
			sb.append("    AND (");
			for (int i = 0; i < types.length; i++) {
				if (i > 0) {
					sb.append(" OR ");
				}
				//sb.append("    table_type = '" + SQLUtil.encodeQuotation(types[i]) + "'");
				sb.append("    table_type Like '%" + SQLUtil.encodeQuotation(types[i]) + "'");
			}
			sb.append("    )");
		}



		return sb.toString();
	}

	public String getTableInfoSql(String schema, String tableName, String type) {
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT");
		sb.append("        table_name TABLE_NAME");
		sb.append("        ,table_type TABLE_TYPE");
		sb.append("        ,REMARKS REMARKS");
		sb.append("    FROM");
		sb.append("        INFORMATION_SCHEMA.TABLES");
		sb.append("    WHERE");
		sb.append("        table_schema = '" + SQLUtil.encodeQuotation(schema) + "'");
		sb.append("        AND table_type Like '%" + SQLUtil.encodeQuotation(type) + "'");
		sb.append("        AND table_name = '" + SQLUtil.encodeQuotation(tableName) + "';");
		return sb.toString();

	}

	public String getDbName() {
		return null;
	}


}
