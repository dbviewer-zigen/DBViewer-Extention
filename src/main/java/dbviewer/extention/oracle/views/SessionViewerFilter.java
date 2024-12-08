package dbviewer.extention.oracle.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import dbviewer.extention.oracle.core.OracleSqlText;

public class SessionViewerFilter extends ViewerFilter {

	public boolean select(Viewer viewer, Object parent, Object element) {
		/*
		OracleSqlText obj = (OracleSqlText)element;
		if(obj.getSql_text().toLowerCase().indexOf("sys.") >= 0){
			return false;
		}else if(obj.getSql_text().toLowerCase().indexOf("system.") >= 0){
			return false;
		}else if(obj.getSql_text().toLowerCase().indexOf("$") >= 0){
			return false;
		}*/
		return true;
	}

}
