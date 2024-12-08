package dbviewer.extention.mysql.rule;

import java.sql.SQLException;

import zigen.plugin.db.core.rule.DefaultSchemaSearchFactory;

public class MySQLSchemaSearchFactory extends DefaultSchemaSearchFactory{
	
	public MySQLSchemaSearchFactory(){
		super();
	}
	protected String getSchemaSearchSql(String dbName){
		try {
			if (meta.getDatabaseMajorVersion() >= 5) {
				return "SELECT SCHEMA_NAME AS TABLE_SCHEM FROM information_schema.SCHEMATA";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	public boolean isSupport() {
		try {
			if (meta.getDatabaseMajorVersion() >= 5) {
				return true;
			} else {
				return meta.supportsSchemasInTableDefinitions();
			}
		} catch (SQLException e) {
			return false;
		}
	}
}
