package dbviewer.extention;

public interface IOracleStatusChangeListener {

	// not use from 100 to 999 
	public static final int EVT_CHANGE_DATABASE = 1;
	public static final int EVT_SEARCH_SESSION = 2;
	
	public void statusChanged(Object obj, int status);

}
