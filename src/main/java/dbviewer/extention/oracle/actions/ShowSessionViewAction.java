package dbviewer.extention.oracle.actions;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

import zigen.plugin.db.core.DBType;
import zigen.plugin.db.ui.internal.DataBase;
import zigen.plugin.db.ui.views.TreeView;
import dbviewer.extention.DBViewerExtention;
import dbviewer.extention.IOracleStatusChangeListener;
import dbviewer.extention.oracle.views.SessionView;

public class ShowSessionViewAction implements IViewActionDelegate {

	protected DataBase db;

	protected IStructuredSelection selection;

	protected TreeView treeView;


	public void init(IViewPart view) {
		this.treeView = (TreeView)view;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		if (targetPart instanceof TreeView) {
			this.treeView = (TreeView)targetPart;
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		action.setEnabled(false);

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection s = (IStructuredSelection)selection;
			Object obj = s.getFirstElement();
			if(obj instanceof DataBase){
				this.db = (DataBase)obj;
				if(DBType.getType(this.db.getDbConfig()) == DBType.DB_TYPE_ORACLE){
					action.setEnabled(true);
					return;
				}
			}
		}
	}

	public void run(IAction action) {
		try {
			String secondaryId = db.getName();
			SessionView view = (SessionView)DBViewerExtention.getDefault().showView(SessionView.ID, null);
			DBViewerExtention.fireStatusChangeListener(db.getDbConfig(), IOracleStatusChangeListener.EVT_CHANGE_DATABASE);
		} catch (PartInitException e) {
			DBViewerExtention.getDefault().showErrorDialog(e);
		}

	}

}
