/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 15.01.2011
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License Version 2 as published by the Free
 * Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.tj.civ.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tj.civ.client.model.CcSituation;


/**
 * Abstract superclass of all application internal events.
 *
 * @author Thomas Jensen
 * @param <H> handler type
 */
public abstract class CcEvent<H extends EventHandler>
    extends GwtEvent<H>
{
    /** the current player's situation */
    private CcSituation iSituation;


    /**
     * Constructor.
     */
    protected CcEvent()
    {
        super();
    }



    /**
     * Getter.
     * @return {@link #iSituation}
     */
    public CcSituation getSituation()
    {
        return iSituation;
    }


    /**
     * Setter.
     * @param pSituation the new value of {@link #iSituation}
     */
    public void setSituation(final CcSituation pSituation)
    {
        iSituation = pSituation;
    }
}