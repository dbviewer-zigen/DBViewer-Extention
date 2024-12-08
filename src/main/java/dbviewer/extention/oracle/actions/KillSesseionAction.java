package dbviewer.extention.oracle.actions;

import java.sql.Connection;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import dbviewer.extention.DBViewerExtention;
import dbviewer.extention.oracle.core.AlterSystemKillSessionCmd;
import dbviewer.extention.oracle.core.OracleSession;
import dbviewer.extention.oracle.views.SessionView;

public class KillSesseionAction extends Action implements Runnable {
	
	SessionView view = null;
	
	public KillSesseionAction(SessionView view) {
		this.view = view;
		this.setToolTipText("Kill Sessoin"); //$NON-NLS-1$
		this.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		
	}
	
	public void run() {
		try {
			
			IStructuredSelection selection = (IStructuredSelection) view.getTableViewer().getSelection();
			
			int index = 0;
			for (Iterator iter = selection.iterator(); iter.hasNext();) {
				Object obj = iter.next();
				if (obj instanceof OracleSession) {
					OracleSession info = (OracleSession) obj;
					
					String msg = "選択したセッションを切断してもよろしいですか";
					String opt = "実行中のトランザクションの完了を待たずにセッションを切断する(IMMEDIATEオプションを有効)";
					MessageDialogWithToggle dialog = DBViewerExtention.getDefault().confirmDialogWithToggle(msg, opt, false);
					final int YES = 2;
					if (dialog.getReturnCode() == YES) {
						Connection con = view.getCurrentConnection();
						boolean b = AlterSystemKillSessionCmd.execute(con, info.getSid(), info.getSerial(), dialog.getToggleState());
						if (!b) {
							DBViewerExtention.getDefault().showErrorDialog(new Exception("切断に失敗しました"));
						} else {
							view.getTableViewer().refresh(info);
						}
					}
					
					index++;
				}
			}
			
		} catch (Exception e) {
			DBViewerExtention.getDefault().showErrorDialog(e);
		}
		
	}
	
	public void setSessionView(SessionView view) {
		this.view = view;
	}
	
}
