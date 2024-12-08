package dbviewer.extention.oracle.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OracleSession {
	
	OracleLock oracleLock;
	
	int sid;
	
	String status;
	
	String userName;
	
	String schemaName;
	
	String osUser;
	
	String machine;
	
	String program;
	
	int serial;
	
	String command;
	
	List sqlTexts;
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (o.getClass() != getClass()) {
			return false;
		}
		OracleSession castedObj = (OracleSession) o;
		if (castedObj.sid == sid) {
			return true;
		} else {
			return false;
		}
		
	}
	
	
	public OracleSession() {
		sqlTexts = new ArrayList();
	}
	
	public void addSqlText(OracleSqlText text) {
		sqlTexts.add(text);
	}
	
	public OracleSqlText[] getSqlText() {
		if (sqlTexts != null) {
			
			Map temp = new LinkedHashMap();
			
			BigDecimal hash = null;
			
			for (Iterator iter = sqlTexts.iterator(); iter.hasNext();) {
				OracleSqlText wk = (OracleSqlText) iter.next();
				
				if (hash == null) {
					temp.put(wk.hash_value, wk);
				} else if (wk.hash_value.compareTo(hash) != 0) {
					temp.put(wk.hash_value, wk);
				} else {
					OracleSqlText pre = (OracleSqlText) temp.get(wk.hash_value);
					String str = pre.getSql_text();
					pre.setSql_text(str + wk.getSql_text());
					
				}
				hash = wk.hash_value;
			}
			
			return (OracleSqlText[]) temp.values().toArray(new OracleSqlText[0]);
		}
		return null;
	}
	
	public String getFirstSqlText() {
		if (sqlTexts.size() > 0) {
			return ((OracleSqlText) sqlTexts.get(0)).getSql_text();
		} else {
			return null;
		}
	}
	
	public String getCommand() {
		return command == null ? "" : command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public String getMachine() {
		return machine;
	}
	
	public void setMachine(String machine) {
		this.machine = machine;
	}
	
	public String getOsUser() {
		return osUser == null ? "" : osUser;
	}
	
	public void setOsUser(String osUser) {
		this.osUser = osUser;
	}
	
	public String getProgram() {
		return program == null ? "" : program;
	}
	
	public void setProgram(String program) {
		this.program = program;
	}
	
	public String getSchemaName() {
		return schemaName;
	}
	
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	
	public int getSerial() {
		return serial;
	}
	
	public void setSerial(int serial) {
		this.serial = serial;
	}
	
	public int getSid() {
		return sid;
	}
	
	public void setSid(int sid) {
		this.sid = sid;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public boolean isLocked() {
		return (oracleLock != null);
	}
	
	
	/**
	 * @return the oracleLock
	 */
	public OracleLock getOracleLock() {
		return oracleLock;
	}
	
	
	/**
	 * @param oracleLock
	 *            the oracleLock to set
	 */
	public void setOracleLock(OracleLock oracleLock) {
		this.oracleLock = oracleLock;
	}
}
