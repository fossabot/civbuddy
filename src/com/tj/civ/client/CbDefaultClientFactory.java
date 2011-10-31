/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 15.02.2011
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
package com.tj.civ.client;

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import com.tj.civ.client.views.CbCardsView;
import com.tj.civ.client.views.CbCardsViewIF;
import com.tj.civ.client.views.CbDetailView;
import com.tj.civ.client.views.CbDetailViewIF;
import com.tj.civ.client.views.CbFundsView;
import com.tj.civ.client.views.CbFundsViewIF;
import com.tj.civ.client.views.CbGamesView;
import com.tj.civ.client.views.CbGamesViewIF;
import com.tj.civ.client.views.CbPlayersView;
import com.tj.civ.client.views.CbPlayersViewIF;
import com.tj.civ.client.views.CbVariantsView;
import com.tj.civ.client.views.CbVariantsViewIF;


/**
 * Our application's client factory default implementation.
 *
 * @author Thomas Jensen
 */
public class CbDefaultClientFactory
    implements CbClientFactoryIF
{
    /** the event bus instance */
    private static final EventBus EVENT_BUS = new SimpleEventBus();

    /** the place controller instance */
    private static final PlaceController PLACE_CTRL = new PlaceController(EVENT_BUS);

    /** the 'Games' view instance */
    private static final CbGamesViewIF GAMES_VIEW = new CbGamesView();

    /** the 'Players' view instance */
    private static final CbPlayersViewIF PLAYERS_VIEW = new CbPlayersView();

    /** the 'Cards' view instance */
    private static final CbCardsViewIF CARDS_VIEW = new CbCardsView();

    /** the 'Funds' view instance */
    private static final CbFundsViewIF FUNDS_VIEW = new CbFundsView();

    /** the 'Variants' view instance */
    private static final CbVariantsViewIF VARIANTS_VIEW = new CbVariantsView();

    /** the 'Detail' view instance */
    private static final CbDetailViewIF DETAIL_VIEW = new CbDetailView();



    @Override
    public EventBus getEventBus()
    {
        return EVENT_BUS;
    }



    @Override
    public PlaceController getPlaceController()
    {
        return PLACE_CTRL;
    }



    @Override
    public CbGamesViewIF getGamesView()
    {
        return GAMES_VIEW;
    }



    @Override
    public CbPlayersViewIF getPlayersView()
    {
        return PLAYERS_VIEW;
    }



    @Override
    public CbCardsViewIF getCardsView()
    {
        return CARDS_VIEW;
    }



    @Override
    public CbFundsViewIF getFundsView()
    {
        return FUNDS_VIEW;
    }



    @Override
    public CbVariantsViewIF getVariantsView()
    {
        return VARIANTS_VIEW;
    }



    @Override
    public CbDetailViewIF getDetailView()
    {
        return DETAIL_VIEW;
    }
}
