/*
 * Copyright (c) 2007 - 2009 ZIGEN
 * Eclipse Public License - v 1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */
package dbviewer.extention.sqlserver.rule;

import zigen.plugin.db.DbPlugin;
import zigen.plugin.db.DbPluginConstant;
import zigen.plugin.db.core.SQLFormatter;
import zigen.plugin.db.core.SQLUtil;
import zigen.plugin.db.core.rule.DefaultSQLCreatorFactory;
import zigen.plugin.db.preference.SQLFormatPreferencePage;
import zigen.plugin.db.ui.internal.Column;

public class SQLServerSQLCreatorFactory extends DefaultSQLCreatorFactory {

	protected String getCreateView() {
		StringBuffer wk = new StringBuffer();
		try {
			boolean onPatch = DbPlugin.getDefault().getPreferenceStore().getBoolean(SQLFormatPreferencePage.P_FORMAT_PATCH);
			int type = DbPlugin.getDefault().getPreferenceStore().getInt(SQLFormatPreferencePage.P_USE_FORMATTER_TYPE);
//			wk.append("CREATE OR REPLACE VIEW "); //$NON-NLS-1$
//			wk.append(getTableNameWithSchemaForSQL(table, isVisibleSchemaName));
//
//			wk.append(DbPluginConstant.LINE_SEP);
//
//			wk.append("(");
//			for (int i = 0; i < cols.length; i++) {
//				Column col = cols[i];
//				if (i > 0) {
//					wk.append(",");
//				}
//				wk.append(col.getName());
//				wk.append(DbPluginConstant.LINE_SEP);
//			}
//
//			wk.append(")");
//			wk.append(DbPluginConstant.LINE_SEP);
//			wk.append("AS"); //$NON-NLS-1$
//			wk.append(DbPluginConstant.LINE_SEP);

			wk.append(getViewDDL(table.getDbConfig(), table.getSchemaName(), table.getName()));

			StringBuffer sb = new StringBuffer();
			sb.append(SQLFormatter.format(wk.toString(), type, onPatch));
			setDemiliter(sb);

			return sb.toString();
		} catch (Exception e) {
			DbPlugin.log(e);
		}
		return null;

	}

	protected String getViewDDL_SQL(String dbName, String owner, String view) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT");
		sb.append("        VIEW_DEFINITION");
		sb.append("    FROM");
		sb.append("        INFORMATION_SCHEMA.VIEWS");
		sb.append("    WHERE");
		sb.append("        TABLE_CATALOG = '" + SQLUtil.enclose(dbName, encloseChar) +"'");
		sb.append("        AND TABLE_SCHEMA = '"+SQLUtil.enclose(owner, encloseChar)+"'");
		sb.append("        AND TABLE_NAME = '"+SQLUtil.enclose(view, encloseChar)+"';");
		return sb.toString();
	}

	public SQLServerSQLCreatorFactory() {
		super();
	}

	public String createSelect(String _condition, int limit) {
		StringBuffer sb = new StringBuffer();

		if (limit > 0) {
			sb.append("SELECT TOP ");
			sb.append(++limit);
		} else {
			sb.append("SELECT ");
		}

		sb.append(" * FROM ");
		sb.append(getTableNameWithSchemaForSQL(table, isVisibleSchemaName));

		String[] conditions = SQLFormatter.splitOrderCause(_condition);
		String condition = conditions[0];
		String orderBy = conditions[1];

		if (condition != null && !"".equals(condition.trim())) {
			sb.append(" WHERE " + condition);
		}

		// ORDER BY
		if (orderBy != null && !"".equals(orderBy)) { //$NON-NLS-1$
			sb.append(" " + orderBy); //$NON-NLS-1$
		}

		return sb.toString();
	}

	public String[] createAddColumnDDL(Column column) {
		return null;
	}

	public String createCommentOnColumnDDL(Column column) {
		return null;
	}

	public String createCommentOnTableDDL(String commnets) {
		return null;
	}

	public String[] createDropColumnDDL(Column column, boolean cascadeConstraints) {
		return null;
	}

	public String[] createModifyColumnDDL(Column from, Column to) {
		return null;
	}

	public String createRenameColumnDDL(Column from, Column to) {
		return null;
	}

	public String createRenameTableDDL(String newTableName) {
		StringBuffer sb = new StringBuffer();
		sb.append("sp_rename");
		sb.append(" '" + SQLUtil.encodeQuotation(table.getSqlTableName()) + "'");
		sb.append(", '" + SQLUtil.encodeQuotation(newTableName) + "'");
		return sb.toString();
	}

	public boolean supportsModifyColumnSize(String columnType) {
		return false;
	}

	public boolean supportsModifyColumnType() {
		return false;
	}

	public boolean supportsRemarks() {
		return false;
	}

	public boolean supportsDropColumnCascadeConstraints() {
		return false;
	}

	// SQLServer is true
	public boolean supportsRollbackDDL() {
		return true;
	}
}
