/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-02-15
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.tj.civ.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;


/**
 * The 'Games' place.
 *
 * @author Thomas Jensen
 */
public class CbGamesPlace
    extends CbAbstractPlace
{
    /** dummy token */
    private static final String TOKEN = "ok"; //$NON-NLS-1$



    /**
     * Constructor.
     */
    public CbGamesPlace()
    {
        super();
    }



    /**
     * Performs text serialization and deserialization of {@link CbGamesPlace}s.
     * @author Thomas Jensen
     */
    public static class CbTokenizer implements PlaceTokenizer<CbGamesPlace>
    {
        @Override
        public String getToken(final CbGamesPlace pPlace)
        {
            // GWT urlencodes the token so it will be valid within one browser.
            // However, links containing a token cannot necessarily be shared among
            // users of different browsers. We don't need that, so we're ok.
            return pPlace.getToken();
        }

        @Override
        public CbGamesPlace getPlace(final String pToken)
        {
            return new CbGamesPlace();
        }
    }



    @Override
    public String getToken()
    {
        return TOKEN;
    }
}