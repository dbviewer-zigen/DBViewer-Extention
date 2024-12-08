package dbviewer.extention.oracle.core;

import java.sql.Connection;
import java.sql.Statement;

import zigen.plugin.db.core.StatementUtil;

public class DropUserCmd {

	public static boolean execute(Connection con, String schema, boolean cascade) throws Exception {
		boolean b = false;
		Statement st = null;
		try {
			st = con.createStatement();
			if (cascade) {
				b = st.execute("DROP USER " + schema + " CASCADE");
			} else {
				b = st.execute("DROP USER " + schema + " ");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			StatementUtil.close(st);
		}
		return b;
	}

}
