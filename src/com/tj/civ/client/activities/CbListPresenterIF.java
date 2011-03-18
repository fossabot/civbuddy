/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 13.03.2011
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
package com.tj.civ.client.activities;

import com.tj.civ.client.views.CcCanGoPlacesIF;


/**
 * Describes the presenter expected by
 * {@link com.tj.civ.client.views.CcAbstractListView}.
 *
 * @author Thomas Jensen
 */
public interface CcListPresenterIF
    extends CcCanGoPlacesIF
{
    /**
     * The 'New' button was clicked.
     */
    void onNewClicked();



    /**
     * The 'Change' button was clicked.
     * @param pItemId ID of the marked item
     */
    void onChangeClicked(final String pItemId);



    /**
     * The 'Remove' button was clicked.
     * @param pItemId ID of the marked item
     */
    void onRemoveClicked(final String pItemId);
}
