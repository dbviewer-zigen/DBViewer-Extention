package dbviewer.extention.oracle.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;

import zigen.plugin.db.DbPlugin;
import dbviewer.extention.DBViewerExtention;
import dbviewer.extention.oracle.views.SessionView;

public class RefreshAction extends Action implements Runnable {

	SessionView view = null;

	public RefreshAction(SessionView view) {
		this.view = view;
		this.setToolTipText("&Reload"); //$NON-NLS-1$
		this.setImageDescriptor(DbPlugin.getDefault().getImageDescriptor(DbPlugin.IMG_CODE_REFRESH));

	}
	public void run() {
		try {
			
			if(view != null){
				ISelection selection = view.getTableViewer().getSelection();
				view.setSessionInfo();
				
				if(selection != null){
					view.getTableViewer().setSelection(selection, true);
				}
			}

	} catch (Exception e) {
			DBViewerExtention.getDefault().showErrorDialog(e);
		}

	}
	
	public void setSessionView(SessionView view){
		this.view = view;
	}
//
}
