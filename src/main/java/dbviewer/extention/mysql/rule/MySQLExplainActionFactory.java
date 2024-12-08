/*
 * Copyright (c) 2007 - 2009 ZIGEN
 * Eclipse Public License - v 1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */
package dbviewer.extention.mysql.rule;

import zigen.plugin.db.core.rule.DefaultExplainActionFactory;


public class MySQLExplainActionFactory extends DefaultExplainActionFactory{

	protected boolean supportOnlySelect(){
		return true;
	}

	protected String getExplainSql(String sql){
		return "EXPLAIN " + sql;
	}
}
