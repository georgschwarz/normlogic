/*******************************************************************************
 * Copyright (c) 2015 Georg Schwarz.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     Georg Schwarz - initial API and implementation
 ******************************************************************************/
package org.normlogic.navigator.app;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.normlogic.navigator.core.ModelContainer;
import org.normlogic.navigator.core.util.LoadException;

public class OpenHandler {

	final ModelContainer container = ModelContainer.getContainer();
	
	@Execute
	public void execute(final Shell shell, final IEventBroker broker){
		FileDialog dialog = new FileDialog(shell);
		final String fileName = dialog.open();
		if (fileName == null)
			return;
		Runnable task = new Runnable() {
            @Override
            public void run() {
        		try {
	    			container.load(fileName, broker);
        		} catch (LoadException e) { 
        			MessageDialog.openError(shell, "Fehler", e.getMessage());
        		}
        		catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}		
            }
        };
        BusyIndicator.showWhile(shell.getDisplay(), task);
	}
}
