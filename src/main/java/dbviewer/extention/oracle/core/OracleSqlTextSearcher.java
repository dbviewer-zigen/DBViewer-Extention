package dbviewer.extention.oracle.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import zigen.plugin.db.DbPlugin;
import zigen.plugin.db.core.ResultSetUtil;
import zigen.plugin.db.core.StatementUtil;

public class OracleSqlTextSearcher {

	public static OracleSqlText[] execute(Connection con, int sid) throws Exception {
		ResultSet rs = null;
		Statement st = null;
		List list = new ArrayList();

		try {
			st = con.createStatement();
			rs = st.executeQuery(getSQL(sid));

			while (rs.next()) {

				OracleSqlText info = new OracleSqlText();
				int i = 0;
				info.setSid(rs.getInt(++i));
				info.setHash_value(rs.getBigDecimal(++i));
				info.setSql_text(rs.getString(++i).trim());
				info.setPiece(rs.getLong(++i));
				list.add(info);
			}

		} catch (Exception e) {
			DbPlugin.log(e);
			throw e;
		} finally {
			ResultSetUtil.close(rs);
			StatementUtil.close(st);
		}

		return (OracleSqlText[]) list.toArray(new OracleSqlText[0]);

	}

	private static String trim(String str){

		char[] chars = str.toCharArray();
		if(Integer.toHexString(chars[chars.length-1]).equals("0")){
			char[] temp = new char[chars.length-1];
			System.arraycopy(chars, 0, temp, 0, chars.length-1);
			return new String(temp);
		}
		return str;
	}


	private static String getSQL(int sid) {
		StringBuffer sb = new StringBuffer();
//		sb.append("SELECT			A.SID");
//		sb.append("                ,B.HASH_VALUE");
//		sb.append("                ,B.SQL_TEXT");
//		sb.append("                ,B.PIECE");
//		sb.append("            FROM");
//		sb.append("                V$OPEN_CURSOR A");
//		sb.append("                ,V$SQLTEXT B");
//		sb.append("            WHERE");
//		sb.append("                A.SID = " + sid);
//		sb.append("                AND A.ADDRESS = B.ADDRESS");
//		sb.append("                AND A.HASH_VALUE = B.HASH_VALUE");
//		sb.append("            ORDER BY");
//		sb.append("                B.ADDRESS");
//		sb.append("                ,B.HASH_VALUE");
//		sb.append("                ,B.COMMAND_TYPE");
//		sb.append("                ,B.PIECE");

		sb.append("SELECT");
		sb.append("        T.SID");
		sb.append("        ,T.HASH_VALUE");
		sb.append("        ,T.SQL_TEXT");
		sb.append("        ,T.PIECE");
		sb.append("    FROM");
		sb.append("        (");
		sb.append("            SELECT DISTINCT");
		sb.append("                    A.SID");
		sb.append("                    ,B.HASH_VALUE");
		sb.append("                    ,B.SQL_TEXT");
		sb.append("                    ,B.PIECE");
		sb.append("                    ,B.ADDRESS");
		sb.append("                    ,B.COMMAND_TYPE");
		sb.append("                FROM");
		sb.append("                    V$OPEN_CURSOR A");
		sb.append("                    ,V$SQLTEXT B");
		sb.append("                WHERE");
		sb.append("                    A.SID = " + sid);
		sb.append("                    AND A.ADDRESS = B.ADDRESS");
		sb.append("                    AND A.HASH_VALUE = B.HASH_VALUE");
		sb.append("        ) T");
		sb.append("    ORDER BY");
		sb.append("        ADDRESS");
		sb.append("        ,HASH_VALUE");
		sb.append("        ,COMMAND_TYPE");
		sb.append("        ,PIECE");

		return sb.toString();

	}

}
