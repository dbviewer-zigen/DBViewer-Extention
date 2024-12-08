package dbviewer.extention.oracle.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import dbviewer.extention.oracle.core.OracleSession;

public class SessionViewUtil {
	
	private static Color red = new Color(Display.getDefault(), 255, 147, 147);
	
	private static Color white = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
	
	public static void changeBackgroundColor(Table table) {
		int cnt = table.getItemCount();
		for (int i = 0; i < cnt; i++) {
			TableItem item = table.getItem(i);
			Object obj = item.getData();
			if (obj instanceof OracleSession) {
				OracleSession session = (OracleSession) obj;
				if (session.isLocked()) {
					item.setBackground(red);
				} else {
					item.setBackground(white);
				}
			}
		}
	}
}
