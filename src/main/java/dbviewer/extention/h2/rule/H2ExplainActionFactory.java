/*
 * Copyright (c) 2007 - 2009 ZIGEN
 * Eclipse Public License - v 1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */
package dbviewer.extention.h2.rule;

import zigen.plugin.db.core.rule.DefaultExplainActionFactory;


public class H2ExplainActionFactory extends DefaultExplainActionFactory{
	protected boolean supportOnlySelect(){
		return false;
	}

	protected String getExplainSql(String sql){
		return "EXPLAIN PLAN FOR " + sql;
	}


}
