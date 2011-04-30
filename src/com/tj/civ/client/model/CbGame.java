/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-01-05
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
package com.tj.civ.client.model;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.common.CbStorage;
import com.tj.civ.client.model.jso.CcGameJSO;
import com.tj.civ.client.model.vo.CcGameVO;
import com.tj.civ.client.model.vo.CcHasViewObjectIF;


/**
 * Describes a game of Civilization. This is the top data object in the model.
 * 
 * @author Thomas Jensen
 */
public class CcGame
    extends CcIndependentlyPersistableObject<CcGameJSO>
    implements CcHasViewObjectIF<CcGameVO>
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CcGame.class);

    /** the game variant we're playing */
    private CcVariantConfig iVariant;

    /** the currently active situation */
    private CcSituation iCurrentSituation = null;

    /** Map of players in this game to their individual situations.
     *  The keys are player names. The player objects are linked from the situation. */
    private Map<String, CcSituation> iSituations;



    /**
     * Constructor.
     * @param pJso the game JSO
     */
    public CcGame(final CcGameJSO pJso)
    {
        super(pJso);
        LOG.touch(CbLogAdapter.CONSTRUCTOR);
    }



    /**
     * Adds a player to the game.
     * @param pSituation the player's newly initialized situation, including a link
     *          to the player object
     */
    public void addPlayer(final CcSituation pSituation)
    {
        String playerName = pSituation.getPlayer().getName();
        iSituations.put(playerName, pSituation);
        getJso().addPlayer(playerName, pSituation.getPersistenceKey());
    }



    /**
     * Removes a player from the game. His situation is deleted.
     * @param pSituation the situation to remove
     */
    public void removePlayer(final CcSituation pSituation)
    {
        String playerName = pSituation.getPlayer().getName();
        iSituations.remove(playerName);
        getJso().removePlayer(playerName);
    }



    public String getName()
    {
        return getJso().getName();
    }

    /**
     * Setter.
     * @param pName the new value
     */
    public void setName(final String pName)
    {
        getJso().setName(pName);
    }


    public CcVariantConfig getVariant()
    {
        return iVariant;
    }



    public CcSituation getCurrentSituation()
    {
        return iCurrentSituation;
    }

    /**
     * Setter.
     * @param pCurrentSit the new value
     */
    public void setCurrentSituation(final CcSituation pCurrentSit)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("setCurrentSituation",  //$NON-NLS-1$
                new String[]{"pCurrentSit"}, new Object[]{pCurrentSit}); //$NON-NLS-1$
        }

        iCurrentSituation = pCurrentSit;
        if (pCurrentSit != null) {
            addPlayer(pCurrentSit);
        } else if (LOG.isDebugEnabled()) {
            LOG.debug("setCurrentSituation", "clearing"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        LOG.exit("setCurrentSituation"); //$NON-NLS-1$
    }



    public Map<String, CcSituation> getSituations()
    {
        return iSituations;
    }



    /**
     * Looks through the values of {@link #iSituations} to find the situation with
     * the given persistence key.
     * @param pSituationKey the situation's persistence key
     * @return the situation itself
     */
    public CcSituation getSituationByKey(final String pSituationKey)
    {
        CcSituation result = null;
        if (iSituations != null) {
            for (CcSituation sit : iSituations.values()) {
                if (sit != null && sit.getPersistenceKey().equals(pSituationKey)) {
                    result = sit;
                    break;
                }
            }
        }
        return result;
    }



    @Override
    public void evaluateJsoState(final CcGameJSO pJso)
    {
        iVariant = CbStorage.loadVariant(pJso.getVariantId());
        iSituations = new TreeMap<String, CcSituation>();
        if (pJso.getPlayers() != null) {
            for (Entry<String, String> entry : pJso.getPlayers().entrySet()) {
                String playerName = entry.getKey();
                String sitKey = entry.getValue();
                CcSituation sit = CbStorage.loadSituation(sitKey, iVariant);
                if (sit != null) {
                    iSituations.put(playerName, sit);
                }
            }
        }
    }



    /**
     * Set the game reference of all situations contained in this object to this game.
     */
    public void setBackrefs()
    {
        LOG.enter("setBackrefs"); //$NON-NLS-1$
        if (iSituations != null) {
            for (CcSituation sit : iSituations.values()) {
                if (sit != null) {
                    if (LOG.isDetailEnabled()) {
                        LOG.detail("setBackrefs", //$NON-NLS-1$
                            "Setting backreference from " + sit //$NON-NLS-1$
                            + " to " + this); //$NON-NLS-1$
                    }
                    sit.setGame(this);
                }
            }
        }
        LOG.exit("setBackrefs"); //$NON-NLS-1$
    }



    @Override
    public CcGameVO getViewObject()
    {
        return new CcGameVO(getPersistenceKey(), getName(), iVariant.getLocalizedDisplayName());
    }
}
