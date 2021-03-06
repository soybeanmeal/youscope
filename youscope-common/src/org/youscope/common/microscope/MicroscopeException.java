/*******************************************************************************
 * Copyright (c) 2017 Moritz Lang.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Moritz Lang - initial API and implementation
 ******************************************************************************/
package org.youscope.common.microscope;

/**
 * Class representing an exception which occurred in the execution of some microscope tasks.
 * 
 * @author Moritz Lang
 */
public class MicroscopeException extends Exception
{
    /**
     * Version UID.
     */
    private static final long serialVersionUID = 4055279582579151582L;

    /**
     * Constructor.
     * 
     * @param description Human readable description of the exception.
     */
    public MicroscopeException(String description)
    {
        super(description);
    }

    /**
     * Constructor.
     * 
     * @param description Human readable description of the exception.
     * @param cause The cause of the exception.
     */
    public MicroscopeException(String description, Exception cause)
    {
        super(description, cause);
    }
}
