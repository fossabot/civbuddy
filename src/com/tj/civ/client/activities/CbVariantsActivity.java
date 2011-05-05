/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 05.05.2011
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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.tj.civ.client.CbClientFactoryIF;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.common.CbStorage;
import com.tj.civ.client.common.CbUtil;
import com.tj.civ.client.places.CbAbstractPlace;
import com.tj.civ.client.places.CbVariantsPlace;
import com.tj.civ.client.views.CbVariantsViewIF;


/**
 * Presenter of the 'Variants' view.
 *
 * @author Thomas Jensen
 */
public class CbVariantsActivity
    extends CbAbstractActivity
    implements CbVariantsViewIF.CbPresenterIF
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbVariantsActivity.class);

    /** the name of the new game to be created as passed by the place */
    private String iGameName;



    /**
     * Constructor.
     * @param pPlace the place
     * @param pClientFactory our client factory
     */
    public CbVariantsActivity(final CbVariantsPlace pPlace,
        final CbClientFactoryIF pClientFactory)
    {
        super(pPlace, pClientFactory);
        LOG.enter(CbLogAdapter.CONSTRUCTOR);

        iGameName = pPlace != null ? pPlace.getGameName() : null;
        pClientFactory.getVariantsView().setVariants(CbStorage.loadVariantList());

        LOG.exit(CbLogAdapter.CONSTRUCTOR);
    }



    @Override
    public void start(final AcceptsOneWidget pContainerWidget, final EventBus pEventBus)
    {
        LOG.enter("start"); //$NON-NLS-1$

        CbVariantsViewIF view = getClientFactory().getVariantsView();
        view.setPresenter(this);
        view.setMarked(null);

        CbUtil.setBrowserTitle("Variants");
        pContainerWidget.setWidget(view.asWidget());

        LOG.exit("start"); //$NON-NLS-1$
    }



    @Override
    public void goTo(final CbAbstractPlace pPlace)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("goTo",  //$NON-NLS-1$
                new String[]{"pPlace"}, new Object[]{pPlace}); //$NON-NLS-1$
        }
        getClientFactory().getVariantsView().setMarked(null);
        super.goTo(pPlace);
        LOG.exit("goTo"); //$NON-NLS-1$
    }



    @Override
    public void onNewClicked()
    {
        Window.alert("This function is not implemented yet."); //$NON-NLS-1$
        // TODO allow adding new variants
    }



    @Override
    public void onChangeClicked(final String pVariantKey)
    {
        // do nothing
    }



    @Override
    public void onRemoveClicked(final String pVariantKey)
    {
        Window.alert("This function is not implemented yet."); //$NON-NLS-1$
        // TODO implement onRemoveClicked()
        getClientFactory().getVariantsView().setMarked(null);
    }



    @Override
    public String getGameName()
    {
        return iGameName;
    }
}
