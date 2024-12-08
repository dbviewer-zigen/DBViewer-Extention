package dbviewer.extention.oracle.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.ui.IEditorPart;

import zigen.plugin.db.DbPlugin;
import zigen.plugin.db.IStatusChangeListener;
import zigen.plugin.db.PluginSettingsManager;
import zigen.plugin.db.core.DBConfigManager;
import zigen.plugin.db.core.DBType;
import zigen.plugin.db.core.IDBConfig;
import zigen.plugin.db.core.SQLHistoryManager;
import dbviewer.extention.DBViewerExtention;
import dbviewer.extention.IOracleStatusChangeListener;
import dbviewer.extention.oracle.actions.KillSesseionAction;
import dbviewer.extention.oracle.actions.RefreshAction;

public class SessionViewToolBar implements IStatusChangeListener, IOracleStatusChangeListener {

	public void statusChanged(Object obj, int status) {
		if (status == IStatusChangeListener.EVT_UpdateDataBaseList) {
			initializeSelectCombo();

		}
		if (status == IOracleStatusChangeListener.EVT_CHANGE_DATABASE) {
			if (obj instanceof IDBConfig) {
				IDBConfig config = (IDBConfig) obj;
				updateCombo(config);
			}
		}

	}

	protected PluginSettingsManager pluginMgr = DbPlugin.getDefault().getPluginSettingsManager();

	protected SQLHistoryManager historyManager = DbPlugin.getDefault().getHistoryManager();

	private IDBConfig[] configs;

	private CoolBar coolBar;

	private Combo selectCombo;

	private ComboContributionItem comboContributionItem = new ComboContributionItem("SelectDataBase"); //$NON-NLS-1$

	protected RefreshAction refreshAction = new RefreshAction(null);

	protected KillSesseionAction killSessionAction = new KillSesseionAction(null);

	String lastSelectedDB;

	boolean lastAutoFormatMode;

	SessionView view;

	public SessionViewToolBar(final Composite parent, IEditorPart editor) {
		coolBar = new CoolBar(parent, SWT.FLAT);

		FormData data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		coolBar.setLayoutData(data);

		CoolBarManager coolBarMgr = new CoolBarManager(coolBar);

		// ToolBarManager toolBarMgr4 = new ToolBarManager(SWT.FLAT);
		// toolBarMgr4.add(formatModeAction);
		ToolBarManager toolBarMgr6 = new ToolBarManager(SWT.FLAT);
		// toolBarMgr6.add(openAction);
		toolBarMgr6.add(refreshAction);
		toolBarMgr6.add(killSessionAction);
		ToolBarManager toolBarMgr5 = new ToolBarManager(SWT.FLAT);
		toolBarMgr5.add(comboContributionItem); //$NON-NLS-1$
		// toolBarMgr5.add(lockDataBaseAction);

		coolBarMgr.add(new ToolBarContributionItem(toolBarMgr5));
		coolBarMgr.add(new ToolBarContributionItem(toolBarMgr6));
		// coolBarMgr.add(new ToolBarContributionItem(toolBarMgr4));
		coolBarMgr.update(true);

		coolBar.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
			}

			public void controlResized(ControlEvent e) {
				parent.getParent().layout(true);
				parent.layout(true);
			}
		});

		DbPlugin.addStatusChangeListener(this);
		DBViewerExtention.addStatusChangeListener(this);

	}

	public void dipose() {
		DBViewerExtention.removeStatusChangeListener(this);
		DbPlugin.removeStatusChangeListener(this);
	}

	public SessionViewToolBar(final Composite parent) {
		this(parent, null);
	}

	public void setSessionViewer(SessionView view) {
		this.view = view;
		refreshAction.setSessionView(view);
		killSessionAction.setSessionView(view);

	}

	public void updateCombo(IDBConfig config) {
		comboContributionItem.updateCombo(config);
	}

	public void initializeSelectCombo() {
		comboContributionItem.initializeSelectCombo();
	}

	class ComboContributionItem extends ControlContribution {
		public ComboContributionItem(String id) {
			super(id);

		}

		protected Control createControl(Composite parent) {
			selectCombo = new Combo(parent, SWT.READ_ONLY);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.widthHint = 200;
			selectCombo.setLayoutData(data);
			initializeSelectCombo();

			selectCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					IDBConfig config = getConfig();
					updateCombo(config);
				}
			});

			return selectCombo;
		}

		String getLastSelectedDBName() {
			Object obj = pluginMgr.getValue(PluginSettingsManager.KEY_DEFAULT_DB);
			if (obj != null) {
				return (String) obj;
			} else {
				return null;
			}
		}

		void setLastSelectedDBName(String dbName) {
			pluginMgr.setValue(PluginSettingsManager.KEY_DEFAULT_DB, dbName);
		}

		void initializeSelectCombo() {
			IDBConfig config = getConfig();
			selectCombo.removeAll();
			configs = DBConfigManager.getDBConfigs();

			List oracleList = new ArrayList();
			
			for (int i = 0; i < configs.length; i++) {
				IDBConfig w_config = configs[i];
				if (DBType.getType(w_config) == DBType.DB_TYPE_ORACLE) {
					oracleList.add(w_config);
					selectCombo.add(w_config.getSchema() + " : " + w_config.getDbName() + "  "); //$NON-NLS-1$ //$NON-NLS-2$
					if (lastSelectedDB != null && lastSelectedDB.equals(w_config.getDbName())) {
						selectCombo.select(i);
					}

					if (config != null && config.getDbName().equals(w_config.getDbName())) {
						selectCombo.select(i);
					}
				}
			}
			configs = (IDBConfig[])oracleList.toArray(new IDBConfig[0]);
		}

		void updateCombo(IDBConfig newConfig) {
			if (newConfig != null) {
				for (int i = 0; i < configs.length; i++) {
					IDBConfig w_config = configs[i];
					if (newConfig != null) {
						if (newConfig.getDbName().equals(w_config.getDbName())) {
							selectCombo.select(i);
							DBViewerExtention.fireStatusChangeListener(w_config, IOracleStatusChangeListener.EVT_SEARCH_SESSION);
							break;
						}
					}
				}
			} else {
				selectCombo.select(-1);
			}
		}

		public IDBConfig selectedConfig() {
			int index = selectCombo.getSelectionIndex();
			if (index >= 0) {
				return configs[index];
			} else {
				return null;
			}
		}
	}

	public ComboContributionItem getComboContributionItem() {
		return comboContributionItem;
	}

	public IDBConfig getConfig() {
		return comboContributionItem.selectedConfig();
	}

	public IDBConfig[] getConfigs() {
		return configs;
	}

	public CoolBar getCoolBar() {
		return coolBar;
	}

	public Combo getSelectCombo() {
		return selectCombo;
	}


}
