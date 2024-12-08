package dbviewer.extention.oracle.actions;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

import zigen.plugin.db.DbPlugin;
import zigen.plugin.db.core.ClipboardUtils;
import zigen.plugin.db.core.SQLFormatter;
import zigen.plugin.db.preference.SQLEditorPreferencePage;
import zigen.plugin.db.preference.SQLFormatPreferencePage;
import dbviewer.extention.DBViewerExtention;
import dbviewer.extention.oracle.core.OracleSqlText;

public class CopyFormatSqlAction extends Action implements Runnable {

	StructuredViewer viewer = null;

	IPreferenceStore ps;

	public CopyFormatSqlAction(StructuredViewer viewer) {
		this.viewer = viewer;
		this.setText("Copy &Formatted SQL"); //$NON-NLS-1$
		this.setToolTipText("Copy Formatted SQL"); //$NON-NLS-1$
		ps = DbPlugin.getDefault().getPreferenceStore();

	}
	public void run() {
		String demiliter = ps.getString(SQLEditorPreferencePage.P_SQL_DEMILITER);
		boolean onPatch = ps.getBoolean(SQLFormatPreferencePage.P_FORMAT_PATCH);
		int type = ps.getInt(SQLFormatPreferencePage.P_USE_FORMATTER_TYPE);

		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		try {

			StringBuffer sb = new StringBuffer();
			Clipboard clipboard = ClipboardUtils.getInstance();

			int index = 0;
			for (Iterator iter = selection.iterator(); iter.hasNext();) {
				Object obj = iter.next();
				if (obj instanceof OracleSqlText) {
					OracleSqlText text = (OracleSqlText) obj;
					System.out.println(text.getSql_text());

					if (index == 0) {
						sb.append(format(text.getSql_text(), type, onPatch));
					} else {
						addLine(sb, demiliter);
						sb.append(format(text.getSql_text(), type, onPatch));

					}
					index++;
				}
			}
			clipboard.setContents(new Object[] {sb.toString()}, new Transfer[] {TextTransfer.getInstance()});

		} catch (Exception e) {
			DBViewerExtention.getDefault().showErrorDialog(e);
		}

	}

	private String format(String sql, int formatterType, boolean onPatch) {
		return SQLFormatter.format(sql, formatterType, onPatch);
	}

	private void addLine(StringBuffer sb, String demiliter) {
		if ("/".equals(demiliter)) { //$NON-NLS-1$
			sb.append(DBViewerExtention.LINE_SEP);
		}
		sb.append(demiliter);
		sb.append(DBViewerExtention.LINE_SEP);
	}
}
