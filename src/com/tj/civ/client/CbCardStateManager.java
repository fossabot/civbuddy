/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 07.01.2011
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tj.civ.client.event.CcFundsEvent;
import com.tj.civ.client.event.CcFundsHandlerIF;
import com.tj.civ.client.model.CcCardConfig;
import com.tj.civ.client.model.CcCardCurrent;
import com.tj.civ.client.model.CcState;
import com.tj.civ.client.model.CcVariantConfig;
import com.tj.civ.client.resources.CcConstants;
import com.tj.civ.client.views.CbCardsViewIF;


/**
 * Manges state transitions of cards according to their definition in
 * {@link CcState}.
 *
 * @author Thomas Jensen
 */
public class CcCardStateManager
{
    /** logger for this class */
    private static final Logger LOG = Logger.getLogger(CcCardStateManager.class.getName());

    /** the 'Cards' activity we're going to be associated to */
    private CbCardsViewIF.CcPresenterIF iPresenter;

    /** mapping cache */
    private static Integer[] Real2SpecialIdxCache = null; 
    
    /** the current card states in the order of the specially sorted array<br>
     *  This is a short-term cache for the duration of a single {@link #recalcAll}. */
    private static CcState[] StatesForSpecial = null;

    /** the game variant which is being played */
    private CcVariantConfig iVariant;
    
    /** number of points that the player must reach to win */
    private int iTargetPoints;

    /** the current enablement state of the funds tracking feature, kept current
     *  by means of {@link CcFundsEvent}s via the event bus */
    private boolean iFundsEnabled = false;

    /** the currently available funds, kept current  by means of
     *  {@link CcFundsEvent}s via the event bus */
    private int iFundsTotal = 0;



    /**
     * Constructor.
     * @param pActivity the 'Cards' activity we're going to be associated to
     * @param pVariant the game variant which is being played
     * @param pTargetPoints target points of this player's civilization. This value
     *          should be 0 (zero) if the game variant does not feature a card
     *          limit
     */
    public CcCardStateManager(final CbCardsViewIF.CcPresenterIF pActivity,
        final CcVariantConfig pVariant, final int pTargetPoints)
    {
        super();
        iPresenter = pActivity;
        iVariant = pVariant;
        iTargetPoints = pTargetPoints;

        pActivity.getEventBus().addHandler(CcFundsEvent.TYPE, new CcFundsHandlerIF() {
            @Override
            public void onFundsChanged(final CcFundsEvent pEvent)
            {
                CcCardStateManager.this.iFundsEnabled = pEvent.isFundsEnabled();
                CcCardStateManager.this.iFundsTotal = pEvent.getFunds();
            }
        });
    }



    /**
     * Recalculate the state of all cards.
     */
    public void recalcAll()
    {
        long debugTimeStart = 0L;
        if (LOG.isLoggable(Level.FINE)) {
            debugTimeStart = System.currentTimeMillis();
        }
        StatesForSpecial = null;
        final CcCardCurrent[] cardsCurrent = iPresenter.getCardsCurrent();
        for (CcCardCurrent card : cardsCurrent)
        {
            final CcCardConfig cardConfig = card.getConfig();
            final CcState currentState = card.getState();
            CcState newState = null;
            String reason = ""; //$NON-NLS-1$

            if (currentState.isAffectingCredit()) {
                continue;   // Owned and Planned
            }

            if (cardConfig.hasPrereq()
                && !cardsCurrent[cardConfig.getPrereq()].getState().isAffectingCredit())
            {
                newState = CcState.PrereqFailed;
                String prn = cardsCurrent[cardConfig.getPrereq()].getConfig().getLocalizedName();
                reason = CcConstants.MESSAGES.prereqFailed(prn);
            }
            else if (iFundsEnabled
                && (iFundsTotal - iPresenter.getPlannedInvestment() - card.getCostCurrent()) < 0)
            {
                newState = CcState.Unaffordable;
                reason = CcConstants.STRINGS.noFunds();
            }
            else {
                final int pointsAchievable = computePointsAchievable(card.getMyIdx());
                if (isDiscouraged(pointsAchievable)) {
                    newState = CcState.DiscouragedBuy;
                    reason = CcConstants.MESSAGES.discouraged(iTargetPoints - pointsAchievable);
                }
                else {
                    newState = CcState.Absent;
                }
            }
            // TODO: erst alle states berechnen, dann anzeigen
            // TODO: falls alle verbleibenden karten rot sind, anders anzeigen
            if (currentState != newState) {
                iPresenter.setState(card, newState, reason);
            }
        }
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("recalcAll() - TIME: " //$NON-NLS-1$
                + (System.currentTimeMillis() - debugTimeStart) + " ms"); //$NON-NLS-1$
        }
    }



    private boolean isDiscouraged(final int pPointsAchievable)
    {
        boolean result = false;
        if (iVariant.getNumCardsLimit() > 0) {
            result = pPointsAchievable < iTargetPoints;
        }
        return result;
    }



    /**
     * Converts the real index of a card into that card's index in the specially
     * sorted array (which is used on the path).
     * @param pRealIdx real index
     * @return index into the specially sorted array, never <code>null</code>
     */
    private Integer realIdx2SpecialIdx(final int pRealIdx)
    {
        if (Real2SpecialIdxCache == null) {
            final CcCardConfig[] specialCards = iVariant.getCardsSpeciallySorted();
            Real2SpecialIdxCache = new Integer[specialCards.length];
            for (int i = 0; i < specialCards.length; i++) {
                Real2SpecialIdxCache[specialCards[i].getMyIdx()] = Integer.valueOf(i);
            }
            
            if (LOG.isLoggable(Level.FINER)) {
                StringBuilder sb = new StringBuilder();
                sb.append('[');
                for (int i = 0; i < Real2SpecialIdxCache.length; i++) {
                    sb.append(Real2SpecialIdxCache[i].intValue());
                    if (i < Real2SpecialIdxCache.length - 1) {
                        sb.append(", "); //$NON-NLS-1$
                    }
                }
                sb.append(']');
                if (LOG.isLoggable(Level.FINER)) {
                    LOG.finer("realIdx2SpecialIdx = " + sb.toString());  //$NON-NLS-1$
                }
            }
        }
        return Real2SpecialIdxCache[pRealIdx];
    }


    
    private CcState getStateFromSpecial(final CcCardCurrent[] pCards4State,
        final Integer pSecialIdx)
    {
        if (StatesForSpecial == null) {
            final CcCardConfig[] specialCards = iVariant.getCardsSpeciallySorted();
            StatesForSpecial = new CcState[specialCards.length];
            for (int i = 0; i < specialCards.length; i++)
            {
                StatesForSpecial[i] = pCards4State[specialCards[i].getMyIdx()].getState();
            }

        }
        return StatesForSpecial[pSecialIdx.intValue()];
    }


    
    private int computePointsAchievable(final int pRowIdx)
    {
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("========= Computing '" //$NON-NLS-1$
                + iPresenter.getCardsCurrent()[pRowIdx].getConfig().getLocalizedName()
                + "' ===================================="); //$NON-NLS-1$
        }
        int result = 0;
        if (iVariant.getNumCardsLimit() > 0) {
            final int remainingSteps = Math.max(0,
                iVariant.getNumCardsLimit() - iPresenter.getNumCardsAffectingCredit());
            final List<Integer> path = new ArrayList<Integer>();
            path.add(realIdx2SpecialIdx(pRowIdx));
            CcCardConfig[] specialCards = iVariant.getCardsSpeciallySorted();
            Integer startingPoint = null;
            for (int i = 0; i < specialCards.length; i++) {
                if (!getStateFromSpecial(iPresenter.getCardsCurrent(),
                        Integer.valueOf(i)).isAffectingCredit()
                    && specialCards[i].getMyIdx() != pRowIdx
                    && isPrereqMet(iPresenter.getCardsCurrent(), specialCards[i], path))
                {
                    startingPoint = Integer.valueOf(i);
                    break;
                }
            }
            // FIXME: In einer Situation, wo wegen des Kartenlimits nur noch
            //        Democracy und Philosophy gekauft werden dürfen, wird irrtümlich
            //        Democracy als discouraged angezeigt, wenn man Philosophy zuerst
            //        wählt. Andersrum ist alles ok.
            if (startingPoint != null) {
                result = isDiscouragedInternal(startingPoint, path,
                    iPresenter.getNominalSumInclPlan()
                    + iVariant.getCards()[pRowIdx].getCostNominal(),
                    remainingSteps - 1);
            }
        }
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("Done. Result=" + result);  //$NON-NLS-1$
        }
        return result;
    }



    /**
     * Computes if there is a combination of cards which the player could buy in the
     * future, which would still make it possible to reach the required winning points.
     * @param pSpecialIdx index of the next step to take (which may be an invalid step)
     * @param pPath the list of steps previously taken
     * @param pSum the current sum
     * @param pRemainingSteps the number of steps which may still be taken
     * @return an arbitrary value &gt;= {@link #iTargetPoints}, or, if that was impossible,
     *      the highest score still achievable
     */
    private int isDiscouragedInternal(final Integer pSpecialIdx,
        final List<Integer> pPath, final int pSum, final int pRemainingSteps)
    {
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("isDiscouragedInternal() - ENTER - pSpecialIdx=" //$NON-NLS-1$
                + pSpecialIdx + ", pPath=" + pPath                     //$NON-NLS-1$
                + ", pSum=" + pSum                                     //$NON-NLS-1$
                + ", pRemainingSteps=" + pRemainingSteps);             //$NON-NLS-1$
        }
        int result = pSum;
        final CcCardConfig[] specialCards = iVariant.getCardsSpeciallySorted();
        if (pRemainingSteps > 0 && result < iTargetPoints
            && pSpecialIdx.intValue() < specialCards.length)
        {
            final CcCardCurrent[] cardsCurrent = iPresenter.getCardsCurrent();
            for (int i = pSpecialIdx.intValue(); i < specialCards.length; i++)
            {
                final Integer specialIdx = Integer.valueOf(i);
                final CcCardConfig card = specialCards[i];
                
                if (!getStateFromSpecial(cardsCurrent, specialIdx).isAffectingCredit()
                    && !pPath.contains(specialIdx)
                    && isPrereqMet(cardsCurrent, card, pPath))
                {
                    pPath.add(specialIdx);
                    final int newSum = isDiscouragedInternal(
                        Integer.valueOf(specialIdx.intValue() + 1),
                        pPath, pSum + card.getCostNominal(), pRemainingSteps - 1);
                    pPath.remove(pPath.size() - 1);
                    if (newSum >= iTargetPoints) {
                        result = newSum;
                        if (LOG.isLoggable(Level.FINER)) {
                            LOG.finer("OK, path = " + pPath); //$NON-NLS-1$
                        }
                        break;  // great, we're ok
                    }
                    if (newSum > result) {
                        result = newSum;
                    }
                }
            }
        }
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("isDiscouragedInternal() - EXIT - result = " + result); //$NON-NLS-1$
        }
        return result;
    }
    
    
    
    private boolean isPrereqMet(final CcCardCurrent[] pCardsCurrent, final CcCardConfig pCard,
        final List<Integer> pPath)
    {
        boolean result = true;
        if (pCard.hasPrereq()) {
            final CcCardConfig[] cards = iVariant.getCardsSpeciallySorted();
            boolean foundOnPath = false;
            for (Iterator<Integer> iter = pPath.iterator(); iter.hasNext();) {
                if (cards[iter.next().intValue()].getMyIdx() == pCard.getPrereq()) {
                    foundOnPath = true;
                    break;
                }
            }
            if (!foundOnPath) {
                // not on path, so look in cards owned/planned
                CcCardCurrent prereqCard = pCardsCurrent[pCard.getPrereq()];
                if (!prereqCard.getState().isAffectingCredit()) {
                    result = false;
                }
            }
        }
        return result;
    }
}
