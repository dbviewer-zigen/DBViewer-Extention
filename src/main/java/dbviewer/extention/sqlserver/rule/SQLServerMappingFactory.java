package dbviewer.extention.sqlserver.rule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import zigen.plugin.db.core.JDBCUnicodeConvertor;
import zigen.plugin.db.core.StringUtil;
import zigen.plugin.db.core.TableColumn;
import zigen.plugin.db.core.rule.DefaultMappingFactory;
import zigen.plugin.db.core.rule.IMappingFactory;

public class SQLServerMappingFactory extends DefaultMappingFactory implements IMappingFactory {
	public static final int SQLServerNVARCHAR = -9;
	public static final int SQLServerNCHAR = -15; // nchar=-15

	public SQLServerMappingFactory() {
		super();
	}

	public Object getObject(ResultSet rs, int icol) throws SQLException {
		ResultSetMetaData rmd = rs.getMetaData();
		int type = rmd.getColumnType(icol);
		switch (type) {
		// case ORACLE_TIMESTAMP: // -100
		// return getTimestamp(rs, icol);
		case Types.CHAR:
		case SQLServerNCHAR:
		case SQLServerNVARCHAR:
			return getString(rs, icol);
		default:
			return super.getObject(rs, icol);
		}
	}

	public void setObject(PreparedStatement pst, int icol, TableColumn column, Object value) throws Exception {

		int type = column.getDataType();

		String str = String.valueOf(value);

		switch (type) {
		case SQLServerNCHAR:
			setChar(pst, icol, str);
			break;
		case SQLServerNVARCHAR:
			setVarchar(pst, icol, str);
			break;
		default:
			super.setObject(pst, icol, column, value);
		}

	}

	public boolean canModifyDataType(int dataType) {
		switch (dataType) {
		case SQLServerNCHAR:
		case SQLServerNVARCHAR:
			return true;
		default:
			return super.canModifyDataType(dataType);
		}
	}

	// protected String getChar(ResultSet rs, int icol) throws SQLException {
	// String value = rs.getString(icol);
	//
	// if (rs.wasNull())
	// return nullSymbol;
	//
	// if (convertUnicode) {
	// value = JDBCUnicodeConvertor.convert(value);
	// }
	//
	// if (value != null) {
	// value = StringUtil.padding(value.trim(),
	// rs.getMetaData().getColumnDisplaySize(icol));
	// }
	// return value;
	// }

}
