package dbviewer.extention.postgresql.rule;

import zigen.plugin.db.core.SQLUtil;
import zigen.plugin.db.core.rule.AbstractTableInfoSearchFactory;
import zigen.plugin.db.core.rule.DefaultTableInfoSearchFactory;


public class PostgreSQLTableInfoSearchFactory extends AbstractTableInfoSearchFactory{
	public PostgreSQLTableInfoSearchFactory(){
		super();
	}
	public String getTableInfoAllSql(String schema, String[] types) {
//		StringBuffer sb = new StringBuffer();
//		sb.append("SELECT");
//		sb.append("        CAT.TABLE_NAME");
//		sb.append("        ,CAT.TABLE_TYPE");
//		sb.append("        ,C.COMMENTS REMARKS");
//		sb.append("    FROM");
//		sb.append("        ALL_CATALOG CAT");
//		sb.append("        ,ALL_TAB_COMMENTS C");
//		sb.append("    WHERE");
//		sb.append("        CAT.OWNER = C.OWNER(+)");
//		sb.append("        AND CAT.TABLE_NAME = C.TABLE_NAME(+)");
//		sb.append("        AND CAT.TABLE_TYPE = C.TABLE_TYPE(+)");
//		sb.append("        AND CAT.OWNER = '" + SQLUtil.encodeQuotation(schema) + "'");
//		if (types.length > 0) {
//			sb.append("    AND (");
//			for (int i = 0; i < types.length; i++) {
//				if (i > 0) {
//					sb.append(" OR ");
//				}
//				sb.append("    CAT.TABLE_TYPE = '" + SQLUtil.encodeQuotation(types[i]) + "'");
//			}
//			sb.append("    )");
//		}
//		StringBuffer sb = new StringBuffer();
//		sb.append("SELECT");
//		sb.append("        table_name TABLE_NAME");
//		sb.append("        ,table_type TABLE_TYPE");
//		sb.append("        ,'' REMARKS");
//		sb.append("    FROM");
//		sb.append("        INFORMATION_SCHEMA.TABLES");
//		sb.append("    WHERE");
//		sb.append("        table_schema = '" + SQLUtil.encodeQuotation(schema) + "'");
//		if (types.length > 0) {
//			sb.append("    AND (");
//			for (int i = 0; i < types.length; i++) {
//				if (i > 0) {
//					sb.append(" OR ");
//				}
//				//sb.append("    table_type = '" + SQLUtil.encodeQuotation(types[i]) + "'");
//				sb.append("    table_type Like '%" + SQLUtil.encodeQuotation(types[i]) + "'");
//			}
//			sb.append("    )");
//		}
//		
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT");
		sb.append("        a.relname as TABLE_NAME");
		sb.append("        ,  case");
		sb.append("           when a.relkind = 'r' then 'TABLE'");
		sb.append("           when a.relkind = 'v' then 'VIEW'");
		sb.append("           else ''");
		sb.append("        end AS TABLE_TYPE");
		sb.append("        , des.description REMARKS");
		sb.append("    FROM");
		sb.append("        pg_catalog.pg_namespace ns");
		sb.append("        ,pg_catalog.pg_class a left join pg_catalog.pg_description des");
		sb.append("                               on a.oid = des.objoid and des.objsubid = 0");
		sb.append("    WHERE");
		sb.append("        ns.oid = a.relnamespace");
		sb.append("        AND ns.nspname = '" + SQLUtil.encodeQuotation(schema) + "'");
		if (types.length > 0) {
			sb.append("    AND (");
			for (int i = 0; i < types.length; i++) {
				if (i > 0) {
					sb.append(" OR ");
				}
				if("TABLE".equalsIgnoreCase(types[i])){
					sb.append("    a.relkind = 'r'");	
				}else if("VIEW".equalsIgnoreCase(types[i])){
					sb.append("    a.relkind = 'v'");
				}
				
			}
			sb.append("    )");
		}
		
		return sb.toString();
	}

	public String getTableInfoSql(String schema, String tableName, String type) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT");
		sb.append("        a.relname as TABLE_NAME");
		sb.append("        ,case");
		sb.append("           when a.relkind = 'r' then 'TABLE'");
		sb.append("           when a.relkind = 'v' then 'VIEW'");
		sb.append("           else ''");
		sb.append("        end AS TABLE_TYPE");
		sb.append("        , des.description REMARKS");
		sb.append("    FROM");
		sb.append("        pg_catalog.pg_namespace ns");
		sb.append("        ,pg_catalog.pg_class a left join pg_catalog.pg_description des");
		sb.append("                               on a.oid = des.objoid and des.objsubid = 0");
		sb.append("    WHERE");
		sb.append("        ns.oid = a.relnamespace");
		sb.append("        AND ns.nspname = '" + SQLUtil.encodeQuotation(schema) + "'");
		sb.append("        AND a.relname = '" + SQLUtil.encodeQuotation(tableName) + "'");
		
		if("TABLE".equalsIgnoreCase(type)){
			sb.append("        AND a.relkind IN ('r')");
		}else{
			// for View
			sb.append("        AND a.relkind IN ('v')");
		}
				
//		sb.append("SELECT");
//		sb.append("        table_name TABLE_NAME");
//		sb.append("        ,table_type TABLE_TYPE");
//		sb.append("        ,'' REMARKS");
//		sb.append("    FROM");
//		sb.append("        INFORMATION_SCHEMA.TABLES");
//		sb.append("    WHERE");
//		sb.append("        table_schema = '" + SQLUtil.encodeQuotation(schema) + "'");
//		sb.append("        AND table_type Like '%" + SQLUtil.encodeQuotation(type) + "'");
//		sb.append("        AND table_name = '" + SQLUtil.encodeQuotation(tableName) + "';");
		return sb.toString();
		
//		StringBuffer sb = new StringBuffer();
//		sb.append("SELECT");
//		sb.append("        CAT.TABLE_NAME");
//		sb.append("        ,CAT.TABLE_TYPE");
//		sb.append("        ,C.COMMENTS REMARKS");
//		sb.append("    FROM");
//		sb.append("        ALL_CATALOG CAT");
//		sb.append("        ,ALL_TAB_COMMENTS C");
//		sb.append("    WHERE");
//		sb.append("        CAT.OWNER = C.OWNER (+)");
//		sb.append("        AND CAT.TABLE_NAME = C.TABLE_NAME (+)");
//		sb.append("        AND CAT.TABLE_TYPE = C.TABLE_TYPE (+)");
//		sb.append("        AND CAT.OWNER = '" + SQLUtil.encodeQuotation(schema) + "'");
//		sb.append("        AND CAT.TABLE_TYPE = '" + SQLUtil.encodeQuotation(type) + "'");
//		sb.append("        AND CAT.TABLE_NAME = '" + SQLUtil.encodeQuotation(tableName) + "'");
//		return sb.toString();
	}

	public String getDbName() {
		return null;
	}


}
