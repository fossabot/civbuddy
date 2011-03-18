/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-01-09
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
package com.tj.civ.client.widgets;

import java.util.SortedSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.tj.civ.client.model.jso.CcPlayerJSO;
import com.tj.civ.client.resources.CcConstants;


/**
 * Shows a modal message box for editing the player settings.
 * <p>Since all GWT actions are asynchronous, we cannot return directly with a result.
 * Instead, a callback is invoked when the result is available.
 *
 * @author Thomas Jensen
 */
public final class CcPlayerSettingsBox
    extends DialogBox
{
    // TODO caching of message boxes (important performance aspect)

    /**
     * Callback for getting the result from the player settings box.
     * @author Thomas Jensen
     */
    public interface CcPlayerResultCallbackIF
    {
        /**
         * Fired when the user has selected one of the buttons.
         * @param pResult <code>true</code> --&gt; OK, <code>false</code> --&gt; Cancel
         * @param pPlayerName the player name selected by the user (NOT validated)
         * @param pTargetPoints the target points selected by the user
         */
        void onResultAvailable(final boolean pResult, final String pPlayerName,
            final int pTargetPoints);
    }



    /**
     * Click handler for all buttons on the player settings message box.
     * @author Thomas Jensen
     */
    private class CcMsgBoxPlayerClickHandler implements ClickHandler
    {
        /** the value we pass to the callback handler when the button is pressed */
        private boolean iExpectedResult;

        /** the callback handler */
        private CcPlayerResultCallbackIF iCallback;

        /**
         * Constructor.
         * @param pResult the value we pass to the callback handler when the button is pressed
         * @param pCallback the callback handler, or <code>null</code> for no callback
         */
        public CcMsgBoxPlayerClickHandler(final boolean pResult,
            final CcPlayerResultCallbackIF pCallback)
        {
            iExpectedResult = pResult;
            iCallback = pCallback;
        }

        @Override
        public void onClick(final ClickEvent pEvent)
        {
            if (iCallback != null) {
                iCallback.onResultAvailable(iExpectedResult, iPlayerNameBox.getText(),
                    Integer.parseInt(iPointsSelector.getValue(iPointsSelector.getSelectedIndex())));
            }
            CcPlayerSettingsBox.this.hide();
        }
    }



    /** flag if the appearance/disappearance of the message box should be animated */
    private static final boolean ANIMATED = true;

    /** reference to the player name text box */
    private TextBox iPlayerNameBox = null;

    /** reference to the target points selector dropdown */
    private ListBox iPointsSelector = null;



    /**
     * Constructor.
     */
    private CcPlayerSettingsBox()
    {
        super(false, true);
    }



    /**
     * Display a modal message box with an OK/Cancel selection which allows
     * entering the player settings. This is used for adding a new player, and for
     * changing a player's settings. Since all GWT action is asynchronous, the result
     * is reported by callback.<br/>
     * The message box displayed by this method looks like this:<br/>
     * TODO: screenshot
     *
     * @param pTitle title of the message box
     * @param pTargetPointsSelection the set of target points allowed by the game
     *          variant
     * @param pBackObject the object that we will center the message box above;
     *              if <code>null</code>, we center on the {@link RootPanel}
     * @param pCallback where we report when the result is available
     */
    public static void showPlayerSettings(final String pTitle,
        final SortedSet<Integer> pTargetPointsSelection, final UIObject pBackObject,
        final CcPlayerResultCallbackIF pCallback)
    {
        showPlayerSettings(pTitle, "", 0, //$NON-NLS-1$
            pTargetPointsSelection, pBackObject, pCallback);
    }



    /**
     * Display a modal message box with an OK/Cancel selection which allows
     * entering the player settings. This is used for adding a new player, and for
     * changing a player's settings. Since all GWT action is asynchronous, the result
     * is reported by callback.<br/>
     * The message box displayed by this method looks like this:<br/>
     * TODO: screenshot
     *
     * @param pTitle title of the message box
     * @param pPlayerName preset player name
     * @param pPointsSelected preset target points selection
     * @param pTargetPointsSelection the set of target points allowed by the game
     *          variant
     * @param pBackObject the object that we will center the message box above;
     *              if <code>null</code>, we center on the {@link RootPanel}
     * @param pCallback where we report when the result is available
     */
    public static void showPlayerSettings(final String pTitle,
        final String pPlayerName, final int pPointsSelected,
        final SortedSet<Integer> pTargetPointsSelection, final UIObject pBackObject,
        final CcPlayerResultCallbackIF pCallback)
    {
        final CcPlayerSettingsBox msgBox = new CcPlayerSettingsBox();
        msgBox.setGlassEnabled(true);
        msgBox.setText(pTitle);
        msgBox.setAnimationEnabled(ANIMATED);

        Button btnOk = new Button(CcConstants.STRINGS.ok());
        btnOk.addClickHandler(msgBox.new CcMsgBoxPlayerClickHandler(true, pCallback));
        btnOk.setStyleName(CcConstants.CSS.ccButton());
        Button btnCancel = new Button(CcConstants.STRINGS.cancel());
        btnCancel.addClickHandler(msgBox.new CcMsgBoxPlayerClickHandler(false, pCallback));
        btnCancel.setStyleName(CcConstants.CSS.ccButton());

        HorizontalPanel buttons = new HorizontalPanel();
        buttons.setStyleName(CcConstants.CSS.ccButtonPanel());
        buttons.addStyleName(CcConstants.CSS_BLUEGRADIENT);
        buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        buttons.add(btnOk);
        buttons.add(btnCancel);

        Label nameLbl = new Label("Name:");
        msgBox.iPlayerNameBox = new TextBox();
        msgBox.iPlayerNameBox.setText(pPlayerName);
        msgBox.iPlayerNameBox.setMaxLength(CcPlayerJSO.PLAYER_NAME_MAXLEN);
        msgBox.iPlayerNameBox.addStyleName(CcConstants.CSS.ccMarginBottom10());
        Label pointsLbl = new Label("Target Points:");
        msgBox.iPointsSelector = new ListBox();
        for (Integer pts : pTargetPointsSelection) {
            msgBox.iPointsSelector.addItem(String.valueOf(pts.intValue()));
            if (pPointsSelected > 0 && pPointsSelected == pts.intValue()) {
                msgBox.iPointsSelector.setSelectedIndex(
                    msgBox.iPointsSelector.getItemCount() - 1);
            }
        }
        if (msgBox.iPointsSelector.getSelectedIndex() < 0) {
            msgBox.iPointsSelector.setSelectedIndex(0);
        }
        msgBox.iPointsSelector.addStyleName(CcConstants.CSS.ccMarginBottom10());

        VerticalPanel vp = new VerticalPanel();
        vp.add(nameLbl);
        vp.add(msgBox.iPlayerNameBox);
        vp.add(pointsLbl);
        vp.add(msgBox.iPointsSelector);
        vp.add(buttons);
        msgBox.setWidget(vp);
        // TODO: player name textbox should have input focus
        // TODO: keypress handler for hitting return/escape
        
        msgBox.setPopupPositionAndShow(new CcPositionCallback(msgBox, pBackObject));
    }
}
