package dbviewer.extention.oracle.views;

import java.math.BigDecimal;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import dbviewer.extention.oracle.core.OracleSession;

public class SessionSortListener extends SelectionAdapter {

	protected boolean desc = true;

	protected int columnIndex;

	protected TableColumn col;

	TableViewer viewer;

	public SessionSortListener(TableViewer viewer, int columnIndex) {
		this.viewer = viewer;
		this.columnIndex = columnIndex;
	}

	public void widgetSelected(SelectionEvent e) {
		TableColumn col = (TableColumn) e.widget;
		Table table = col.getParent();

		if (!desc) {
			viewer.setSorter(new TableColumnSorter(columnIndex, desc));
			desc = true;
			try {
				table.setSortColumn(col);
				table.setSortDirection(SWT.UP);

			} catch (Throwable ex) {
				ex.printStackTrace();
			}

		} else {
			viewer.setSorter(new TableColumnSorter(columnIndex, desc));
			desc = false;
			try {
				table.setSortColumn(col);
				table.setSortDirection(SWT.DOWN);
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

		}
		SessionViewUtil.changeBackgroundColor(viewer.getTable());
	}

	protected class TableColumnSorter extends ViewerSorter {

		boolean isDesc = false;

		int index;

		public TableColumnSorter(int index, boolean isDesc) {
			this.index = index;
			this.isDesc = isDesc;
		}

		public int compare(Viewer viewer, Object o1, Object o2) {

			OracleSession f = (OracleSession) o1;
			OracleSession s = (OracleSession) o2;

			switch (index) {
				// case 0:
				// Boolean b1 = new Boolean(f.isLocked());
				// Boolean b2 = new Boolean(s.isLocked());
				// return compareTo(b1, b2, isDesc);
				case 0:
					return compareTo(f.getSid(), s.getSid(), isDesc);
				case 1:
					return compareTo(f.getStatus(), s.getStatus(), isDesc);
				case 2:
					return compareTo(f.getUserName(), s.getUserName(), isDesc);
				case 3:
					return compareTo(f.getSchemaName(), s.getSchemaName(), isDesc);
				case 4:
					return compareTo(f.getOsUser(), s.getOsUser(), isDesc);
				case 5:
					return compareTo(f.getMachine(), s.getMachine(), isDesc);
				case 6:
					return compareTo(f.getProgram(), s.getProgram(), isDesc);
				case 7:
					return compareTo(f.getSerial(), s.getSerial(), isDesc);
				case 8:
					return compareTo(f.getFirstSqlText(), s.getFirstSqlText(), isDesc);

				default:
					break;
			}

			return 0;
		}
	}

	private int compareTo(BigDecimal d1, BigDecimal d2, boolean isDesc) {
		if (isDesc) {
			return (d2.compareTo(d1));
		} else {
			return (d1.compareTo(d2));
		}
	}

	private int compareTo(int v1, int v2, boolean isDesc) {
		BigDecimal d1 = new BigDecimal(v1);
		BigDecimal d2 = new BigDecimal(v2);
		return compareTo(d1, d2, isDesc);

	}

	private int compareTo(String v1, String v2, boolean isDesc) {
		try {
			if (v1 == null && v2 == null) {
				return 0;

			} else if (v1 != null && v2 == null) {
				return compareToStr(v1, "", isDesc);

			} else if (v1 == null && v2 != null) {
				return compareToStr("", v2, isDesc);

			} else {
				return compareTo(new BigDecimal(v1), new BigDecimal(v2), isDesc);
			}
		} catch (NumberFormatException ex) {
			return compareToStr(v1, v2, isDesc);
		}
	}

	private int compareToStr(String v1, String v2, boolean isDesc) {
		if (isDesc) {
			return (v2.compareTo(v1));
		} else {
			return (v1.compareTo(v2));
		}
	}
}