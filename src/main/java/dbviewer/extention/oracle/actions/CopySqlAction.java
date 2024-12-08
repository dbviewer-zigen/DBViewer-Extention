package dbviewer.extention.oracle.actions;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

import zigen.plugin.db.core.ClipboardUtils;
import dbviewer.extention.DBViewerExtention;
import dbviewer.extention.oracle.core.OracleSqlText;

public class CopySqlAction extends Action implements Runnable {
	
	StructuredViewer viewer = null;
	
	public CopySqlAction(StructuredViewer viewer) {
		this.viewer = viewer;
		this.setText("&Copy SQL"); //$NON-NLS-1$
		this.setToolTipText("Copy SQL"); //$NON-NLS-1$
		
	}
	
	public void run() {
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		try {
			
			StringBuffer sb = new StringBuffer();
			Clipboard clipboard = ClipboardUtils.getInstance();
			
			int index = 0;
			for (Iterator iter = selection.iterator(); iter.hasNext();) {
				Object obj = iter.next();
				if (obj instanceof OracleSqlText) {
					OracleSqlText text = (OracleSqlText) obj;
					if (index == 0) {
						sb.append(text.getSql_text());
					} else {
						sb.append(DBViewerExtention.LINE_SEP);
						sb.append(text.getSql_text());
						
					}
					index++;
				}
			}
			clipboard.setContents(new Object[] {sb.toString()}, new Transfer[] {TextTransfer.getInstance()});
			
		} catch (Exception e) {
			DBViewerExtention.getDefault().showErrorDialog(e);
		}
		
	}
	
}
