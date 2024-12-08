package dbviewer.extention;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import zigen.plugin.db.DbPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class DBViewerExtention extends AbstractUIPlugin {

	public static String LINE_SEP = System.getProperty("line.separator"); //$NON-NLS-1$

	public static final String TITLE = "DBViewer Plugin Extention"; //$NON-NLS-1$

	public static final String PLUGIN_ID = "dbviewer.extention";


	private static DBViewerExtention plugin;

	public DBViewerExtention() {
		plugin = this;
	}


	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static DBViewerExtention getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public IWorkbenchPage getPage() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		return window.getActivePage();
	}

	public static IViewPart findView(String viewId, String secondaryId) throws PartInitException {
		IWorkbenchPage page = getDefault().getPage();
		if (page != null) {
			return page.showView(viewId, secondaryId, IWorkbenchPage.VIEW_VISIBLE);
		} else {
			return null;
		}
	}

	public static IViewPart showView(String viewId, String secondaryId) throws PartInitException {
		IWorkbenchPage page = getDefault().getPage();
		if (page != null) {
			return page.showView(viewId, secondaryId, IWorkbenchPage.VIEW_ACTIVATE);
		} else {
			return null;
		}
	}

	public Shell getShell() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		return window.getShell();
	}

	public static IStatus createWarningStatus(Throwable throwable) {
		return createWarningStatus(-1, throwable);
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static IStatus createErrorStatus(Throwable throwable) {
		return createErrorStatus(-1, throwable);
	}

	private static String getErrorMessage(Throwable throwable) {
		if (throwable != null) {
			String msg = throwable.getMessage();
			if (msg == null) {
				return getErrorMessage(throwable.getCause());
			} else {
				return msg;
			}
		} else {
			return Messages.getString("Activator.0"); //$NON-NLS-1$
		}
	}

	static IStatus createWarningStatus(int errorCode, Throwable throwable) {
		String msg = getErrorMessage(throwable);
		int endIndex = msg.indexOf(DBViewerExtention.LINE_SEP);
		if (endIndex > 0) {
			msg = msg.substring(0, endIndex);
		}
		return new Status(IStatus.WARNING, PLUGIN_ID, errorCode, msg, null);
	}

	static IStatus createErrorStatus(int errorCode, Throwable throwable) {
		String msg = getErrorMessage(throwable);
		int endIndex = msg.indexOf(DBViewerExtention.LINE_SEP);
		if (endIndex > 0) {
			msg = msg.substring(0, endIndex);
		}
		return new Status(IStatus.ERROR, PLUGIN_ID, errorCode, msg, throwable);
	}

	public void showErrorDialog(Throwable throwable) {
		String message = Messages.getString("Activator.1"); //$NON-NLS-1$
		IStatus status = null;
		if (throwable instanceof SQLException) {
			status = createWarningStatus(throwable);
		} else {
			status = createErrorStatus(throwable);
		}

		ErrorDialog.openError(getShell(), DBViewerExtention.TITLE, message, status);

	}

	public MessageDialogWithToggle confirmDialogWithToggle(String message, String toggleMessage, boolean toggleStatus) {
		return MessageDialogWithToggle.openYesNoQuestion(getShell(), DBViewerExtention.TITLE, message, toggleMessage, toggleStatus, null, null);
	}
	public static void log(String message, Exception e) {
		IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, message, e);
		getDefault().getLog().log(status);
	}

	public static void log(Exception e) {
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		String message = stringWriter.getBuffer().toString();
		log(message, e);

		// for debug
		if (e != null)
			e.printStackTrace();
	}

	private static List listeners = new ArrayList();

	public static void addStatusChangeListener(IOracleStatusChangeListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	public static void removeStatusChangeListener(IOracleStatusChangeListener listener) {
		listeners.remove(listener);
	}

	public static void fireStatusChangeListener(Object obj, int status) {
		for (Iterator iter = listeners.iterator(); iter.hasNext();) {
			IOracleStatusChangeListener element = (IOracleStatusChangeListener) iter.next();
			if (element != null) {
				element.statusChanged(obj, status);
			}
		}

	}


	public static final String IMG_CODE_LOCKED = "locked.gif"; //$NON-NLS-1$

	protected void initializeImageRegistry(ImageRegistry registry) {
		registerImage(registry, IMG_CODE_LOCKED);
	}

	private void registerImage(ImageRegistry registry, String fileName) {
		try {
			IPath path = new Path("icons/" + fileName); //$NON-NLS-1$
			URL url = find(path);
			if (url != null) {
				ImageDescriptor desc = ImageDescriptor.createFromURL(url);
				registry.put(fileName, desc);
			}
		} catch (Exception e) {
			DbPlugin.log(e);
		}
	}
}
