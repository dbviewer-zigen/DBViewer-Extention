package dbviewer.extention.oracle.actions;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

import zigen.plugin.db.DbPlugin;
import zigen.plugin.db.core.DBType;
import zigen.plugin.db.core.Transaction;
import zigen.plugin.db.ui.internal.Schema;
import zigen.plugin.db.ui.views.TreeView;
import dbviewer.extention.DBViewerExtention;
import dbviewer.extention.oracle.core.DropUserCmd;

public class DropUserAction implements IViewActionDelegate {

	protected Schema schema;

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
			if(obj instanceof Schema){
				this.schema = (Schema)obj;
				if(DBType.getType(this.schema.getDbConfig()) == DBType.DB_TYPE_ORACLE){
					action.setEnabled(true);
					return;
				}
			}
		}
	}

	public void run(IAction action) {
		try {
			String msg = "選択したスキーマを削除しますか？";
			String opt = "cascade オプションを有効にする";
			MessageDialogWithToggle dialog = DBViewerExtention.getDefault().confirmDialogWithToggle(msg, opt, false);
			final int YES = 2;
			if (dialog.getReturnCode() == YES) {
				Transaction.getInstance(schema.getDbConfig()).getConnection();
				if(DropUserCmd.execute(Transaction.getInstance(schema.getDbConfig()).getConnection(), schema.getName(), dialog.getToggleState())){
					DbPlugin.getDefault().showInformationMessage("スキーマを削除しました。");
				}else{
					DbPlugin.getDefault().showWarningMessage("スキーマの削除に失敗しました。");
				}
			}

		} catch (Exception e) {
			DBViewerExtention.getDefault().showErrorDialog(e);
		}

	}

}
