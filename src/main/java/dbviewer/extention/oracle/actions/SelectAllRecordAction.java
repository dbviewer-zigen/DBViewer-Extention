package dbviewer.extention.oracle.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import dbviewer.extention.DBViewerExtention;

public class SelectAllRecordAction extends Action {

	TableViewer viewer;
	
	public SelectAllRecordAction(TableViewer viewer) {
		this.viewer = viewer;
		setText("Select &All"); //$NON-NLS-1$
	}
	public void run() {
		try {
			if(viewer != null){
				viewer.getTable().selectAll();
				viewer.getTable().notifyListeners(SWT.Selection, null);
			}
		} catch (Exception e) {
			DBViewerExtention.getDefault().showErrorDialog(e);
		}

	}
}
