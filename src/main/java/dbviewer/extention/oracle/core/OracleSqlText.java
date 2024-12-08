package dbviewer.extention.oracle.core;

import java.math.BigDecimal;

public class OracleSqlText {

	int sid;

	BigDecimal hash_value;

	String sql_text;
	
	long piece;

	
	public OracleSqlText(){
		
	}


	public BigDecimal getHash_value() {
		return hash_value;
	}


	public void setHash_value(BigDecimal hash_value) {
		this.hash_value = hash_value;
	}


	public long getPiece() {
		return piece;
	}


	public void setPiece(long piece) {
		this.piece = piece;
	}


	public int getSid() {
		return sid;
	}


	public void setSid(int sid) {
		this.sid = sid;
	}


	public String getSql_text() {
		return sql_text;
	}


	public void setSql_text(String sql_text) {
		this.sql_text = sql_text;
	}

}
