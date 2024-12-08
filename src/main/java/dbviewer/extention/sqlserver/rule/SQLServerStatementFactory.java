/*
 * Copyright (c) 2007 - 2009 ZIGEN
 * Eclipse Public License - v 1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */
package dbviewer.extention.sqlserver.rule;

import java.sql.SQLException;
import java.sql.Types;

import zigen.plugin.db.core.rule.DefaultStatementFactory;

public class SQLServerStatementFactory extends DefaultStatementFactory{
	public SQLServerStatementFactory() {
		super();
	}

	public String getString(int DataType, Object value) throws SQLException {
		int type = DataType;
		switch (type) {
		case SQLServerMappingFactory.SQLServerNCHAR:
			return super.getString(Types.CHAR,value);
		case SQLServerMappingFactory.SQLServerNVARCHAR:
			return super.getString(Types.VARCHAR, value);
		default:
			return super.getString(DataType, value);
		}

	}
}
