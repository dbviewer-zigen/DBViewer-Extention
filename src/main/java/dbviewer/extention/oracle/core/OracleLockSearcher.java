package dbviewer.extention.oracle.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import zigen.plugin.db.DbPlugin;
import zigen.plugin.db.core.ResultSetUtil;
import zigen.plugin.db.core.StatementUtil;

public class OracleLockSearcher {
	
	public static Map execute(Connection con) throws Exception {
		ResultSet rs = null;
		Statement st = null;
		Map map = new HashMap();
		String sql = null;
		
		try {
			sql = getSQL();
			st = con.createStatement();
			rs = st.executeQuery(sql);
			
			while (rs.next()) {
				OracleLock info = new OracleLock();
				int i = 0;
				info.setSid(rs.getInt(++i));
				info.setSerial(rs.getInt(++i));
				info.setType(rs.getString(++i));
				info.setMin(rs.getString(++i));
				info.setSqlText(rs.getString(++i));
				
				map.put(new Integer(info.getSid()), info);
			}
			
		} catch (Exception e) {
			DbPlugin.log(e);
			throw e;
		} finally {
			ResultSetUtil.close(rs);
			StatementUtil.close(st);
		}
		return map;
		
	}
	
	private static String getSQL() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT").append("\r\n");
		sb.append("        a.sid").append("\r\n");
		sb.append("        ,a.serial#").append("\r\n");
		sb.append("        ,b. TYPE").append("\r\n");
		sb.append("        ,TO_CHAR(b.ctime / 60, '999990.9') MIN").append("\r\n");
		sb.append("        ,c.sql_text").append("\r\n");
		sb.append("    FROM").append("\r\n");
		sb.append("        V$Session a").append("\r\n");
		sb.append("        ,V$Lock b").append("\r\n");
		sb.append("        ,v$sqlarea c").append("\r\n");
		sb.append("    WHERE").append("\r\n");
		sb.append("        a.lockwait = b.kaddr").append("\r\n");
		sb.append("        AND a.sql_address = c.address").append("\r\n");
		sb.append("    ORDER BY").append("\r\n");
		sb.append("        b.ctime DESC").append("\r\n");
		
		return sb.toString();
		
	}
	
}
