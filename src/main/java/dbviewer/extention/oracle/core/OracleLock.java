package dbviewer.extention.oracle.core;

public class OracleLock {
	
	/**
	 * SID
	 */
	private int sid;
	
	/**
	 * SERIAL#
	 */
	private int serial;
	
	/**
	 * TYPE
	 */
	private String type;
	
	/**
	 * MIN
	 */
	private String min;
	
	/**
	 * SQL_TEXT
	 */
	private String sqlText;
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[OracleLock:");
		buffer.append(" sid: ");
		buffer.append(sid);
		buffer.append(" serial#: ");
		buffer.append(serial);
		buffer.append(" type: ");
		buffer.append(type);
		buffer.append(" min: ");
		buffer.append(min);
		buffer.append(" sqlText: ");
		buffer.append(sqlText);
		buffer.append("]");
		return buffer.toString();
	}
	
	/**
	 * @return the sid
	 */
	public int getSid() {
		return sid;
	}
	
	
	/**
	 * @param sid
	 *            the sid to set
	 */
	public void setSid(int sid) {
		this.sid = sid;
	}
	
	
	/**
	 * @return the serial
	 */
	public int getSerial() {
		return serial;
	}
	
	
	/**
	 * @param serial
	 *            the serial to set
	 */
	public void setSerial(int serial) {
		this.serial = serial;
	}
	
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	
	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	
	/**
	 * @return the min
	 */
	public String getMin() {
		return min;
	}
	
	
	/**
	 * @param min
	 *            the min to set
	 */
	public void setMin(String min) {
		this.min = min;
	}
	
	
	/**
	 * @return the sqlText
	 */
	public String getSqlText() {
		return sqlText;
	}
	
	
	/**
	 * @param sqlText
	 *            the sqlText to set
	 */
	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}
}
