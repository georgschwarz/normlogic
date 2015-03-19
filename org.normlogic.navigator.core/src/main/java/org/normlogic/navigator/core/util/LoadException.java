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
package org.normlogic.navigator.core.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadException extends RuntimeException {

	private static final long serialVersionUID = 5328673377906417664L;
	
    public LoadException(final String message) {
        super(message);
        log(this);
    }
    
    public LoadException(final String message, final Throwable cause) {
        super(message, cause);
        
        log(cause);
    }

    private static void log(final Throwable exception) {
        LOGGER.log(Level.WARNING, "Initialization failed.", exception); //$NON-NLS-1$
    }
    private static final Logger LOGGER = Logger.getLogger(InitFailedException.class.getName());
}

