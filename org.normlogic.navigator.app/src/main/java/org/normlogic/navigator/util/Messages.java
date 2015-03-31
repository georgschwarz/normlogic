package org.normlogic.navigator.util;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.normlogic.navigator.util.messages"; //$NON-NLS-1$
	public static String ContextMenu_AddAssertion;
	public static String ContextMenu_AddIndividual;
	public static String ContextMenu_ConclusionOf;
	public static String ContextMenu_ConditionFor;
	public static String ContextMenu_DeleteAssertion;
	public static String ContextMenu_DeleteIndividual;
	public static String ContextMenu_Pursue;
	public static String ContextMenu_Specialize;
	public static String LibraryPart_Titel;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
