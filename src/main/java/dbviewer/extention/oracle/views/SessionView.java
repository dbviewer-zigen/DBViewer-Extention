package dbviewer.extention.oracle.views;

import java.sql.Connection;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;

import zigen.plugin.db.DbPlugin;
import zigen.plugin.db.core.ConnectionManager;
import zigen.plugin.db.core.IDBConfig;
import dbviewer.extention.DBViewerExtention;
import dbviewer.extention.IOracleStatusChangeListener;
import dbviewer.extention.oracle.actions.CopyFormatSqlAction;
import dbviewer.extention.oracle.actions.CopySqlAction;
import dbviewer.extention.oracle.actions.RefreshAction;
import dbviewer.extention.oracle.actions.SelectAllRecordAction;
import dbviewer.extention.oracle.core.OracleLock;
import dbviewer.extention.oracle.core.OracleLockSearcher;
import dbviewer.extention.oracle.core.OracleSession;
import dbviewer.extention.oracle.core.OracleSessionSearcher;
import dbviewer.extention.oracle.core.OracleSqlText;

//import zigen.plugin.db.ui.views.internal.SQLToolBar;

public class SessionView extends ViewPart implements IOracleStatusChangeListener {

	class ViewContentProvider implements IStructuredContentProvider {

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {}

		public void dispose() {}

		public Object[] getElements(Object parent) {
			if (parent instanceof OracleSession[]) {
				return (OracleSession[]) parent;
			} else if (parent instanceof OracleSqlText[]) {
				return (OracleSqlText[]) parent;
			}
			return null;
		}

	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		public String getColumnText(Object obj, int index) {
			if (obj == null)
				return null;

			OracleSession session = (OracleSession) obj;
			switch (index) {
				// case 0:
				// return (session.isLocked()) ? "locked" : "--";
				case 0:
					return String.valueOf(session.getSid());
				case 1:
					return session.getStatus();
				case 2:
					return session.getUserName();
				case 3:
					return session.getSchemaName();
				case 4:
					return session.getOsUser();
				case 5:
					return session.getMachine();
				case 6:
					return session.getProgram();
				case 7:
					return String.valueOf(session.getSerial());
				case 8:
					return session.getFirstSqlText();
				default:
					break;
			}
			return null;
		}

		public Image getColumnImage(Object obj, int index) {
			if (index == 0) {
				OracleSession session = (OracleSession) obj;
				if (session.isLocked()) {
					return DbPlugin.getDefault().getImage(DbPlugin.IMG_CODE_UNKNOWN);
				} else {
					return DbPlugin.getDefault().getImage(DbPlugin.IMG_CODE_SQL);
				}
			} else {
				return null;
			}

		}

		public Image getImage(Object obj) {
			return null;
		}
	}

	class SQLViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		public String getColumnText(Object obj, int index) {
			if (obj == null)
				return null;
			OracleSqlText text = (OracleSqlText) obj;
			return text.getSql_text().trim();

		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			return null;
		}
	}

	public static final String ID = "dbviewer.extention.oracle.views.SessionView"; //$NON-NLS-1$

	TableViewer viewer;

	private Action action1;

	private Action action2;

	private CopySqlAction copySqlAction;

	private CopyFormatSqlAction copyFormatSqlAction;

	private Action doubleClickAction;

	IDBConfig config;

	Connection con;

	SessionViewToolBar bar;

	SashForm sash;

	TableViewer sqlViewer;


	public SessionView() {}

	public void dispose() {
		DBViewerExtention.removeStatusChangeListener(this);
		if (bar != null) {
			bar.dipose();
		}

		if (con != null) {
			ConnectionManager.closeConnection(con);
		}
	}


	private static final String[] headers = {"Session", "Status", "User", "Schema", "OS User", "Machine", "Program", "Serial", "Command"};


	public void createPartControl(Composite parent) {
		sash = new SashForm(parent, SWT.VERTICAL | SWT.NONE);
		Composite header = new Composite(sash, SWT.NONE);

		FormLayout layout = new FormLayout();
		header.setLayout(layout);
		bar = new SessionViewToolBar(header);
		bar.setSessionViewer(this);

		Composite tableComposite = new Composite(header, SWT.BORDER);
		tableComposite.setLayout(new FillLayout());
		FormData data = new FormData();
		data.top = new FormAttachment(bar.getCoolBar(), 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(100, 0);
		tableComposite.setLayoutData(data);

		viewer = new TableViewer(tableComposite, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

		final Table table = viewer.getTable();
		table.setFont(DbPlugin.getDefaultFont());
		setHeaderColumn(table, headers);
		table.setHeaderVisible(true);// �w�b�_����ɂ���
		table.setLinesVisible(true); // ���C����\��

		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				OracleSession oracleSession = (OracleSession) selection.getFirstElement();
				// System.out.println(oracleSession.getSid());
				setSqlText(oracleSession);
				viewer.getTable().setFocus();
			}

		});

		table.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				if (table.getSelectionIndex() == -1) {
					table.select(0); // ���I��̏ꍇ�́A�����I��1���R�[�h�ڂ�I��
					// table.notifyListeners(SWT.Selection, null); // �I���Ԃ�ʒm
				}

				// �e�[�u������p��ActionBar�ɂ���
				IActionBars bars = getViewSite().getActionBars();
				bars.clearGlobalActionHandlers();
				setGlobalActionList(bars);
				bars.updateActionBars();

			}

		});

		createSqlViewer(sash);

		makeActions();
		hookContextMenu();
		// hookDoubleClickAction();
		// contributeToActionBars();

		sash.setWeights(new int[] {70, 30});

		DBViewerExtention.addStatusChangeListener(this);
	}

	private void createSqlViewer(Composite parent) {
		sqlViewer = new TableViewer(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		final Table table = sqlViewer.getTable();
		table.setFont(DbPlugin.getDefaultFont());
		sqlViewer.setContentProvider(new ViewContentProvider());
		sqlViewer.setLabelProvider(new SQLViewLabelProvider());

		sqlViewer.addFilter(new SessionViewerFilter());

		table.setLinesVisible(true); // ���C����\��

		table.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				if (table.getSelectionIndex() == -1) {
					table.select(0); // ���I��̏ꍇ�́A�����I��1���R�[�h�ڂ�I��
					table.notifyListeners(SWT.Selection, null); // �I���Ԃ�ʒm

				}
				// �e�[�u������p��ActionBar�ɂ���
				IActionBars bars = getViewSite().getActionBars();
				bars.clearGlobalActionHandlers();
				setGlobalActionSql(bars);
				bars.updateActionBars();

			}
		});

	}

	void setGlobalActionList(IActionBars bars) {
		bars.setGlobalActionHandler(ActionFactory.REFRESH.getId(), new RefreshAction(this));
	}

	void setGlobalActionSql(IActionBars bars) {
		bars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), new SelectAllRecordAction(sqlViewer));
		bars.setGlobalActionHandler(ActionFactory.REFRESH.getId(), new RefreshAction(this));
		bars.setGlobalActionHandler(ActionFactory.COPY.getId(), new CopySqlAction(sqlViewer));
	}


	public void setSessionInfo() {
		viewer.setInput(getOracleSessions());
		columnsPack(viewer.getTable());
		SessionViewUtil.changeBackgroundColor(viewer.getTable());
	}

	public void setSqlText(OracleSession session) {
		if (session != null) {
			sqlViewer.setInput(session.getSqlText());
			columnsPack(viewer.getTable());
		} else {
			sqlViewer.setInput(null);
			columnsPack(viewer.getTable());

		}
	}

	public Connection getCurrentConnection() {
		return con;
	}

	private OracleSession[] getOracleSessions() {
		try {
			if (bar != null && bar.getConfig() != null) {
				IDBConfig selectConfig = bar.getConfig();
				if (config == null) {
					config = selectConfig;
					con = ConnectionManager.getConnection(config);

				} else if (!config.equals(selectConfig)) {
					ConnectionManager.closeConnection(con);
					config = selectConfig;
					con = ConnectionManager.getConnection(config);

				} else if (con == null) {
					con = ConnectionManager.getConnection(config);
				}

				// ���b�N�}�b�v��擾����
				Map lockMap = OracleLockSearcher.execute(con);

				OracleSession[] session = OracleSessionSearcher.execute(con);
				for (int i = 0; i < session.length; i++) {
					OracleSession oracleSession = session[i];
					Integer sID = new Integer(oracleSession.getSid());
					if (lockMap.containsKey(sID)) {
						oracleSession.setOracleLock((OracleLock) lockMap.get(sID));
					}
				}
				return session;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Connection getConnection(IDBConfig config) {
		Connection con = null;
		try {
			if (bar != null && bar.getConfig() != null) {
				IDBConfig selectConfig = bar.getConfig();
				if (config == null) {
					config = selectConfig;
					con = ConnectionManager.getConnection(config);

				} else if (!config.equals(selectConfig)) {
					ConnectionManager.closeConnection(con);
					config = selectConfig;
					con = ConnectionManager.getConnection(config);

				} else if (con == null) {
					con = ConnectionManager.getConnection(config);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}

	private void setHeaderColumn(Table table, String[] headers) {
		for (int i = 0; i < headers.length; i++) {
			TableColumn col = new TableColumn(table, SWT.LEFT, i);
			col.setText(headers[i]);
			col.setResizable(true);
			col.addSelectionListener(new SessionSortListener(viewer, i));
			col.pack();
		}
	}

	private void columnsPack(Table table) {
		table.setVisible(false);
		TableColumn[] cols = table.getColumns();
		for (int i = 0; i < cols.length; i++) {
			cols[i].pack();
		}
		table.setVisible(true);
	}


	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				// SessionView.this.fillContextMenu(manager);
				SessionView.this.fillContextMenu2(manager);
			}
		});

		/*
		 * Menu menu = menuMgr.createContextMenu(viewer.getControl()); viewer.getControl().setMenu(menu); getSite().registerContextMenu(menuMgr, viewer);
		 */
		Menu menu2 = menuMgr.createContextMenu(sqlViewer.getControl());
		sqlViewer.getControl().setMenu(menu2);
		getSite().registerContextMenu(menuMgr, sqlViewer);

	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillContextMenu2(IMenuManager manager) {
		manager.add(copySqlAction);
		manager.add(copyFormatSqlAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {

			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		action2 = new Action() {

			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {

			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};


		copySqlAction = new CopySqlAction(sqlViewer);
		copyFormatSqlAction = new CopyFormatSqlAction(sqlViewer);
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Oracle Session", message);
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void statusChanged(Object obj, int status) {
		switch (status) {
			case IOracleStatusChangeListener.EVT_SEARCH_SESSION:
				if (obj instanceof IDBConfig) {
					setSessionInfo();
				}
				break;

			default:
				break;
		}
	}

	public TableViewer getTableViewer() {
		return viewer;
	}

}