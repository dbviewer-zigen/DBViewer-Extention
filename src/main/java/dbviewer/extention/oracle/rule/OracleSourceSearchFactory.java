package dbviewer.extention.oracle.rule;

import zigen.plugin.db.DbPluginConstant;
import zigen.plugin.db.core.SQLUtil;
import zigen.plugin.db.core.StringUtil;
import zigen.plugin.db.core.rule.AbstractSourceSearcherFactory;
import zigen.plugin.db.core.rule.SequenceInfo;

public class OracleSourceSearchFactory extends AbstractSourceSearcherFactory {
	public OracleSourceSearchFactory() {
		super();
	}

	protected String getSequenceInfoSQL(String owner) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT");
		sb.append("        SEQUENCE_OWNER OWNER");
		sb.append("        ,SEQUENCE_NAME NAME");
		sb.append("        ,MIN_VALUE");
		sb.append("        ,MAX_VALUE");
		sb.append("        ,INCREMENT_BY");
		sb.append("        ,CYCLE_FLAG");
		sb.append("        ,ORDER_FLAG");
		sb.append("        ,CACHE_SIZE");
		sb.append("        ,LAST_NUMBER");
		sb.append("    FROM");
		sb.append("        ALL_SEQUENCES");
		sb.append("    WHERE"); //$NON-NLS-1$
		sb.append("        SEQUENCE_OWNER = '" + SQLUtil.encodeQuotation(owner) + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("    ORDER BY"); //$NON-NLS-1$
		sb.append("        SEQUENCE_NAME"); //$NON-NLS-1$

		return sb.toString();

	}
	protected String getSequenceInfoSQL(String owner, String sequence) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT");
		sb.append("        SEQUENCE_OWNER OWNER");
		sb.append("        ,SEQUENCE_NAME NAME");
		sb.append("        ,MIN_VALUE");
		sb.append("        ,MAX_VALUE");
		sb.append("        ,INCREMENT_BY");
		sb.append("        ,CYCLE_FLAG");
		sb.append("        ,ORDER_FLAG");
		sb.append("        ,CACHE_SIZE");
		sb.append("        ,LAST_NUMBER");
		sb.append("    FROM");
		sb.append("        ALL_SEQUENCES");
		sb.append("    WHERE"); //$NON-NLS-1$
		sb.append("        SEQUENCE_OWNER = '" + SQLUtil.encodeQuotation(owner) + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("      AND SEQUENCE_NAME = '" + SQLUtil.encodeQuotation(sequence) + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		return sb.toString();

	}
	protected String getSourceInfoSQL(String owner, String type) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT"); //$NON-NLS-1$
		sb.append("         DISTINCT OWNER"); //$NON-NLS-1$
		sb.append("         ,NAME"); //$NON-NLS-1$
		sb.append("         ,TYPE"); //$NON-NLS-1$
		sb.append("     FROM"); //$NON-NLS-1$
		sb.append("         ALL_SOURCE"); //$NON-NLS-1$
		sb.append("     WHERE"); //$NON-NLS-1$
		sb.append("         OWNER = '" + SQLUtil.encodeQuotation(owner) + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("         AND TYPE = '" + SQLUtil.encodeQuotation(type) + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("     ORDER BY NAME"); //$NON-NLS-1$

		return sb.toString();
	}
	protected String getErrorInfoSQL(String owner, String name, String type) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT"); //$NON-NLS-1$
		sb.append("         OWNER"); //$NON-NLS-1$
		sb.append("         ,NAME"); //$NON-NLS-1$
		sb.append("         ,TYPE"); //$NON-NLS-1$
		sb.append("         ,LINE"); //$NON-NLS-1$
		sb.append("         ,POSITION"); //$NON-NLS-1$
		sb.append("         ,TEXT"); //$NON-NLS-1$
		sb.append("     FROM"); //$NON-NLS-1$
		sb.append("         ALL_ERRORS"); //$NON-NLS-1$
		sb.append("     WHERE"); //$NON-NLS-1$
		sb.append("         OWNER = '" + SQLUtil.encodeQuotation(owner) + "'"); //$NON-NLS-1$ //$NON-NLS-2$
//		sb.append("         AND NAME = '" + SQLUtil.encodeQuotation(name) + "'"); //$NON-NLS-1$ //$NON-NLS-2$
//		sb.append("         AND TYPE = '" + SQLUtil.encodeQuotation(type) + "'"); //$NON-NLS-1$ //$NON-NLS-2$

		sb.append("         AND NAME = '" + SQLUtil.encodeQuotation(name.toUpperCase()) + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("         AND TYPE = '" + SQLUtil.encodeQuotation(type.toUpperCase()) + "'"); //$NON-NLS-1$ //$NON-NLS-2$

		sb.append("     ORDER BY SEQUENCE"); //$NON-NLS-1$

		return sb.toString();

	}

	protected String getErrorInfoSQL(String owner, String type) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT"); //$NON-NLS-1$
		sb.append("         OWNER"); //$NON-NLS-1$
		sb.append("         ,NAME"); //$NON-NLS-1$
		sb.append("         ,TYPE"); //$NON-NLS-1$
		sb.append("         ,LINE"); //$NON-NLS-1$
		sb.append("         ,POSITION"); //$NON-NLS-1$
		sb.append("         ,TEXT"); //$NON-NLS-1$
		sb.append("     FROM"); //$NON-NLS-1$
		sb.append("         ALL_ERRORS"); //$NON-NLS-1$
		sb.append("     WHERE"); //$NON-NLS-1$
		sb.append("         OWNER = '" + SQLUtil.encodeQuotation(owner) + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("         AND TYPE = '" + SQLUtil.encodeQuotation(type.toUpperCase()) + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("     ORDER BY OWNER, NAME, TYPE, SEQUENCE"); //$NON-NLS-1$
		return sb.toString();

	}

	protected String getDetailInfoSQL(String owner, String name, String type) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT"); //$NON-NLS-1$
		sb.append("         OWNER"); //$NON-NLS-1$
		sb.append("         ,NAME"); //$NON-NLS-1$
		sb.append("         ,TYPE"); //$NON-NLS-1$
		sb.append("         ,LINE"); //$NON-NLS-1$
		sb.append("         ,TEXT"); //$NON-NLS-1$
		sb.append("     FROM"); //$NON-NLS-1$
		sb.append("         ALL_SOURCE"); //$NON-NLS-1$
		sb.append("     WHERE"); //$NON-NLS-1$
		sb.append("         OWNER = '" + SQLUtil.encodeQuotation(owner) + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("         AND NAME = '" + SQLUtil.encodeQuotation(name) + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("         AND TYPE = '" + SQLUtil.encodeQuotation(type) + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("     ORDER BY LINE"); //$NON-NLS-1$
		return sb.toString();
	}



	public String getSequenceDDL(SequenceInfo sequenceInfo){
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE"); //$NON-NLS-1$
		sb.append(" SEQUENCE "); //$NON-NLS-1$
		if (StringUtil.isNumeric(sequenceInfo.getSequece_owner())) {
			sb.append("\"");
			sb.append(sequenceInfo.getSequece_owner());
			sb.append("\"");

		} else {
			sb.append(sequenceInfo.getSequece_owner());
		}
		sb.append("."); //$NON-NLS-1$
		sb.append(sequenceInfo.getSequence_name());
		sb.append(DbPluginConstant.LINE_SEP);

		sb.append("     INCREMENT BY " + sequenceInfo.getIncrement_by()); //$NON-NLS-1$
		sb.append(DbPluginConstant.LINE_SEP);

		sb.append("     START WITH " + sequenceInfo.getLast_number()); //$NON-NLS-1$
		sb.append(DbPluginConstant.LINE_SEP);
		sb.append("     MAXVALUE " + sequenceInfo.getMax_value()); //$NON-NLS-1$
		sb.append(DbPluginConstant.LINE_SEP);
		sb.append("     MINVALUE " + sequenceInfo.getMin_value()); //$NON-NLS-1$
		sb.append(DbPluginConstant.LINE_SEP);

		if ("Y".equals(sequenceInfo.getCycle_flg())) { //$NON-NLS-1$
			sb.append("     CYCLE "); //$NON-NLS-1$
		} else {
			sb.append("     NOCYCLE "); //$NON-NLS-1$
		}

		sb.append(DbPluginConstant.LINE_SEP);
		sb.append("     CACHE "); //$NON-NLS-1$
		sb.append(sequenceInfo.getCache_size());

		sb.append(DbPluginConstant.LINE_SEP);
		if ("Y".equals(sequenceInfo.getOrder_flg())) { //$NON-NLS-1$
			sb.append("     ORDER "); //$NON-NLS-1$
		} else {
			sb.append("     NOORDER "); //$NON-NLS-1$
		}
		sb.append(DbPluginConstant.LINE_SEP);
		return sb.toString();
	}

	protected String getTriggerInfoSQL(String owner, String table) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT");
		sb.append("        OWNER");
		sb.append("        ,TRIGGER_NAME");
		sb.append("        ,TRIGGER_TYPE");
		sb.append("        ,TRIGGERING_EVENT");
		sb.append("    FROM");
		sb.append("        ALL_TRIGGERS");
		sb.append("    WHERE");
		sb.append("        TABLE_OWNER = '" + SQLUtil.encodeQuotation(owner) + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("        AND TABLE_NAME = '" + SQLUtil.encodeQuotation(table) + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("     ORDER BY TRIGGER_NAME"); //$NON-NLS-1$

		return sb.toString();

	}
}
