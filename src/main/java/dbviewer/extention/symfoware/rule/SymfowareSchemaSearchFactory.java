package dbviewer.extention.symfoware.rule;

import zigen.plugin.db.core.SQLUtil;
import zigen.plugin.db.core.rule.DefaultSchemaSearchFactory;

public class SymfowareSchemaSearchFactory extends DefaultSchemaSearchFactory{

	public SymfowareSchemaSearchFactory(){
		super();
	}
	protected String getSchemaSearchSql(String dbName){
		//return "SELECT TRIM(SCHEMA_NAME) AS TABLE_SCHEM FROM RDBII_SYSTEM.RDBII_SCHEMA WHERE DB_NAME = '"+dbName+"'";
		//return "SELECT TRIM(SCHEMA_NAME) AS TABLE_SCHEM FROM RDBII_SYSTEM.RDBII_SCHEMA ORDER BY SCHEMA_NAME";

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT");
		sb.append("        TRIM(SCHEMA_NAME) AS TABLE_SCHEM");
		sb.append("    FROM");
		sb.append("        RDBII_SYSTEM.RDBII_SCHEMA");
		sb.append("    WHERE");
		sb.append("        DB_NAME = '" + SQLUtil.encodeQuotation(dbName)+ "'");
		sb.append("        OR DB_NAME = 'RDBII_DICTIONARY'");
		sb.append("    ORDER BY");
		sb.append("        SCHEMA_NAME");
		return sb.toString();
	}

}
