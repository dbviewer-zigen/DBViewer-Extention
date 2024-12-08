package dbviewer.extention.oracle.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import zigen.plugin.db.DbPlugin;
import zigen.plugin.db.core.ResultSetUtil;
import zigen.plugin.db.core.StatementUtil;

public class OracleSessionSearcher {
	
	public static OracleSession[] execute(Connection con) throws Exception {
		ResultSet rs = null;
		Statement st = null;
		List list = new ArrayList();
		String sql = null;
		
		try {
			sql = getSQL();
			st = con.createStatement();
			rs = st.executeQuery(sql);
			
			while (rs.next()) {
				
				OracleSession info = new OracleSession();
				int i = 0;
				info.setSid(rs.getInt(++i));
				info.setStatus(rs.getString(++i));
				info.setUserName(rs.getString(++i));
				info.setSchemaName(rs.getString(++i));
				info.setOsUser(rs.getString(++i));
				info.setMachine(rs.getString(++i));
				info.setProgram(rs.getString(++i));
				info.setSerial(rs.getInt(++i));
				info.setCommand(rs.getString(++i));
				
				setCommand(con, info);
				
				list.add(info);
			}
			
		} catch (Exception e) {
			DbPlugin.log(e);
			throw e;
		} finally {
			ResultSetUtil.close(rs);
			StatementUtil.close(st);
		}
		
		return (OracleSession[]) list.toArray(new OracleSession[0]);
		
	}
	
	private static void setCommand(Connection con, OracleSession info) throws Exception {
		
		OracleSqlText[] txts = OracleSqlTextSearcher.execute(con, info.getSid());
		for (int i = 0; i < txts.length; i++) {
			OracleSqlText text = txts[i];
			info.addSqlText(text);
		}
		
	}
	
	private static String getSQL() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT");
		sb.append("        SID");
		sb.append("        ,STATUS");
		sb.append("        ,USERNAME");
		sb.append("        ,SCHEMANAME");
		sb.append("        ,OSUSER");
		sb.append("        ,MACHINE");
		sb.append("        ,PROGRAM");
		sb.append("        ,SERIAL#");
		sb.append("        ,COMMAND");
		sb.append("    FROM");
		sb.append("        V$SESSION");
		sb.append("    WHERE");
		sb.append("        TYPE = 'USER'");
		return sb.toString();
	}
	
}
