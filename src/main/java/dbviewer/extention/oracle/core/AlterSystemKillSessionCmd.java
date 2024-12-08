package dbviewer.extention.oracle.core;

import java.sql.Connection;
import java.sql.Statement;

import zigen.plugin.db.core.StatementUtil;
public class AlterSystemKillSessionCmd {

	public static boolean execute(Connection con, int sid, int serial, boolean immediate) throws Exception {
		boolean b = false;
		Statement st = null;
		try {
			st = con.createStatement();
			if(immediate){
				b = st.execute("ALTER SYSTEM KILL SESSION '"+ sid +"," + serial + "' IMMEDIATE"); // for IMMEDIATE
			}else{
				b = st.execute("ALTER SYSTEM KILL SESSION '"+ sid +"," + serial + "'");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			StatementUtil.close(st);
		}
		return b;
	}

}
