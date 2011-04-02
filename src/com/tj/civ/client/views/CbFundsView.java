/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 31.03.2011
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
package com.tj.civ.client.views;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.LazyPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.tj.civ.client.event.CcCommSpinnerPayload;
import com.tj.civ.client.model.jso.CcCommodityConfigJSO;
import com.tj.civ.client.model.jso.CcFundsJSO;
import com.tj.civ.client.resources.CcConstants;
import com.tj.civ.client.widgets.CcCommoditySpinner;
import com.tj.civ.client.widgets.CcLabel;
import com.tj.civ.client.widgets.CcMessageBox;
import com.tj.civ.client.widgets.CcMessageBox.CcResultCallbackIF;
import com.tj.civ.client.widgets.CcStatsIndicator;


/**
 * Implementation of the 'Funds' view.
 *
 * @author Thomas Jensen
 */
public class CbFundsView
    extends Composite
    implements CbFundsViewIF, HasEnabled
{
    /** logger for this class */
    private static final Logger LOG = Logger.getLogger(CbFundsView.class.getName());

    /** this view's presenter */
    private CbFundsViewIF.CbPresenterIF iPresenter;

    /** The text box containing the total funds value. Only editable when detailed
     *  tracking is turned off. */
    private IntegerBox iTotalFundsBox;

    /** text box containing the treasury value */
    private IntegerBox iTreasuryBox;

    /** text box containing the bonus value */
    private IntegerBox iBonusBox;

    /** Stats row entry about the current total funds */
    private CcStatsIndicator iTotalFundsIndicator;

    /** Stats row entry about the number of commodity cards */
    private CcStatsIndicator iNumCommIndicator;

    /** the 'clear all funds' button */
    private Button iBtnClear;

    /** the button for overall enabling/disabling of funds tracking */
    private ToggleButton iBtnToggleFunds;

    /** the button for enabling/disabling detail tracking */
    private ToggleButton iBtnToggleDetail;

    /** Panel including all the widgets for detailed funds tracking */
    private LazyPanel iDetailPanel;

    /** Grid including the commodity spinners */
    private Grid iSpinnersGrid;

    /** Panel including all the widgets for coarse funds tracking */
    private Panel iCoarsePanel;

    /** List of all widgets containing information on the funds details, used for
     *  recalculating the total funds */
    private List<HasValue<?>> iDetailWidgets = new ArrayList<HasValue<?>>();

    /** List of all widgets which can be enabled/disabled. Used in connection with
     *  {@link #iBtnToggleFunds} */
    private List<HasEnabled> iActivatableWidgets = new ArrayList<HasEnabled>();

    /** focus handler which selects the entire input text when the box receives focus */
    private static final FocusHandler TXTFOCUSHANDLER = new FocusHandler() {
        @Override
        public void onFocus(final FocusEvent pEvent)
        {
            ValueBoxBase<?> source = (ValueBoxBase<?>) pEvent.getSource();
            if (source.getValue() != null) {
                source.setSelectionRange(0, source.getText().length());
            }
        }
    };



//  /**
//  * Wrapper that adds the {@link HasEnabled} interface to the unspeakable
//  * {@link SliderBar} which already implements it without declaring it.
//  * @author Thomas Jensen
//  */
// private class CcSliderBarEnabler extends SliderBar implements HasEnabled
// {
//     CcSliderBarEnabler(final double pMinValue, final double pMaxValue,
//         final LabelFormatter pLabelFormatter, final SliderBarImages pImages)
//     {
//         super(pMinValue, pMaxValue, pLabelFormatter, pImages);
//     }
//
//     CcSliderBarEnabler(final double pMinValue, final double pMaxValue,
//         final LabelFormatter pLabelFormatter)
//     {
//         super(pMinValue, pMaxValue, pLabelFormatter);
//     }
//
//     CcSliderBarEnabler(final double pMinValue, final double pMaxValue)
//     {
//         super(pMinValue, pMaxValue);
//     }
// }



    private Panel createFundsButtonPanel()
    {
        iBtnClear = new Button(CcConstants.STRINGS.clearFunds());
        iBtnClear.setStyleName(CcConstants.CSS.ccButton());
        iBtnClear.setTitle(CcConstants.STRINGS.clearFundsDesc());
        iBtnClear.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                CcMessageBox.showOkCancel(CcConstants.STRINGS.askAreYouSure(),
                    SafeHtmlUtils.fromString(CcConstants.STRINGS.askClearFunds()),
                    CbFundsView.this, new CcResultCallbackIF() {
                        @Override
                        public void onResultAvailable(final boolean pOkPressed)
                        {
                            if (pOkPressed) {
                                iPresenter.reset();
                                reset();
                    }   }   }
                );
            }
        });
        iActivatableWidgets.add(iBtnClear);

        iBtnToggleFunds = new ToggleButton(CcConstants.STRINGS.off(), CcConstants.STRINGS.on());
        //iBtnToggleFunds.setStyleName(CcConstants.CSS.ccButton());
        iBtnToggleFunds.setTitle(CcConstants.STRINGS.enableFunds());
        iBtnToggleFunds.setEnabled(true);
        iBtnToggleFunds.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                ToggleButton source = (ToggleButton) pEvent.getSource();
                boolean enabled = source.getValue().booleanValue();
                iPresenter.onEnableToggled(enabled);
            }
        });

        HorizontalPanel result = new HorizontalPanel();
        result.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        result.add(iBtnClear);
        result.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        result.add(iBtnToggleFunds);
        result.setStyleName(CcConstants.CSS.ccButtonPanel());
        result.addStyleName(CcConstants.CSS_BLUEGRADIENT);
        return result;
    }



    /**
     * Constructor.
     */
    public CbFundsView()
    {
        super();

        // TODO Back Button HERE
        VerticalPanel workaround = new VerticalPanel();
        workaround.setStyleName(CcConstants.CSS.ccStats());
        HorizontalPanel statsHp = new HorizontalPanel();
        statsHp.setStyleName(CcConstants.CSS.ccStatsInner() + " " //$NON-NLS-1$
            + CcConstants.CSS_BLUEGRADIENT);
        iTotalFundsIndicator = new CcStatsIndicator(CcConstants.STRINGS.statsFunds(), null, true);
        iActivatableWidgets.add(iTotalFundsIndicator);
        statsHp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        statsHp.add(iTotalFundsIndicator);
        iNumCommIndicator = new CcStatsIndicator(
            CcConstants.STRINGS.fundsCommodities(), null, false);
        iActivatableWidgets.add(iNumCommIndicator);
        statsHp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        statsHp.add(iNumCommIndicator);
        workaround.add(statsHp);

        CcLabel label = new CcLabel(CcConstants.STRINGS.fundsTotalLabel());
        iActivatableWidgets.add(label);
        // TODO extract into a widget (with bonus box) and add validation
        iTotalFundsBox = new IntegerBox();
        final int maxLen = String.valueOf(CcFundsJSO.MAX_TOTAL_FUNDS).length();
        iTotalFundsBox.setMaxLength(maxLen);
        iTotalFundsBox.setVisibleLength(maxLen);
        iTotalFundsBox.setAlignment(TextAlignment.RIGHT);
        iTotalFundsBox.addFocusHandler(TXTFOCUSHANDLER);
        // use numerical input pad on iPhone
        iTotalFundsBox.getElement().setAttribute("pattern", "[0-9]*"); //$NON-NLS-1$ //$NON-NLS-2$
        iTotalFundsBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Integer> pEvent)
            {
                iPresenter.onTotalFundsBoxChanged(pEvent.getValue());
            }
        });
        iActivatableWidgets.add(iTotalFundsBox);
        iCoarsePanel = new HorizontalPanel();
        iCoarsePanel.add(label);
        iCoarsePanel.add(iTotalFundsBox);
        
        CcLabel detLabel = new CcLabel(CcConstants.STRINGS.fundsDetailed());
        iActivatableWidgets.add(detLabel);
        iBtnToggleDetail = new ToggleButton(CcConstants.STRINGS.off(), CcConstants.STRINGS.on());
        iBtnToggleDetail.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Boolean> pEvent)
            {
                iPresenter.onDetailToggled(pEvent.getValue().booleanValue());
            }
        });
        iBtnToggleDetail.setValue(Boolean.TRUE, false);  // no event yet
        iActivatableWidgets.add(iBtnToggleDetail);
        HorizontalPanel detHp = new HorizontalPanel();
        detHp.add(detLabel);
        detHp.add(iBtnToggleDetail);
        
        final CcLabel treasuryLabel = new CcLabel(CcConstants.STRINGS.treasury());
        iActivatableWidgets.add(treasuryLabel);
        iTreasuryBox = new IntegerBox();
//        final CcSliderBarEnabler sb = new CcSliderBarEnabler(TREASURY_MIN, TREASURY_MAX);
        iTreasuryBox.setMaxLength(2);
        iTreasuryBox.setVisibleLength(2);
        iTreasuryBox.setValue(Integer.valueOf(0));
        iTreasuryBox.setAlignment(TextAlignment.RIGHT);
        iTreasuryBox.addFocusHandler(TXTFOCUSHANDLER);
        iTreasuryBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Integer> pEvent)
            {
//                IntegerBox source = (IntegerBox) pEvent.getSource();
//                onTreasuryBoxChange(source, sb, pEvent.getValue());
            }});
        iActivatableWidgets.add(iTreasuryBox);
        iDetailWidgets.add(iTreasuryBox);
//        sb.setWidth("250px");
//        sb.setNumLabels(CcFundsJso.TREASURY_NUM_TICKS);
//        sb.setNumTicks(CcFundsJso.TREASURY_NUM_TICKS);
//        sb.setStepSize(1);
//        sb.setCurrentValue(0.0d, false);
//        sb.setLabelFormatter(new LabelFormatter()
//        {
//            @Override
//            public String formatLabel(final SliderBar pSlider, final double pValue)
//            {
//                // show labels as ints
//                return String.valueOf((int) pValue);
//            }
//        });
//        sb.addValueChangeHandler(new ValueChangeHandler<Double>() {
//            @Override
//            public void onValueChange(final ValueChangeEvent<Double> pEvent)
//            {
//                Integer v = txtTreasury.getValue();
//                int oldValue = 0;
//                if (v != null) {
//                    oldValue = v.intValue();
//                }
//                int newValue = (int) pEvent.getValue().doubleValue();
//                txtTreasury.setValue(Integer.valueOf(newValue));
//                updateTotalFunds(iTotalFunds + newValue - oldValue);
//            }
//        });
//        // TODO touch* events to enable dragging on iPhone --> it's own widget
//        iActivatableWidgets.add(sb);

        final HorizontalPanel treasuryHp = new HorizontalPanel();
        treasuryHp.add(iTreasuryBox);
//        treasuryHp.add(sb);

        iSpinnersGrid = new Grid(1, 2);  // dummy with only the bonus box
        iSpinnersGrid.setWidget(0, 1, createBonusBox());
        iDetailPanel = new LazyPanel() {
            @Override
            protected Widget createWidget()
            {
                VerticalPanel lvp = new VerticalPanel();
                lvp.add(treasuryLabel);
                lvp.add(treasuryHp);
                lvp.add(iSpinnersGrid);
                return lvp;
            }
        };

        VerticalPanel fp2 = new VerticalPanel();
        fp2.add(createFundsButtonPanel());
        fp2.add(workaround);
        fp2.add(detHp);
        fp2.add(iCoarsePanel);
        fp2.add(iDetailPanel);
//        Label delme = new Label("ON");
//        delme.getElement().setAttribute("style", "font-size: x-small; color:#FF0000;"
//            + " font-weight: normal; text-shadow: 0 0 0.2em #F87, 0 0 0.2em #F87");
//        fp2.add(delme);
        initWidget(fp2);
    }



    private Panel createBonusBox()
    {
        boolean firstCall = iBonusBox == null;

        CcLabel lblBonus = new CcLabel(CcConstants.STRINGS.fundsBonus());
        lblBonus.setTitle(CcConstants.STRINGS.fundsBonusTitle());

        if (firstCall) {
            iBonusBox = new IntegerBox();
            iBonusBox.setValue(Integer.valueOf(0));
            final int maxLen = String.valueOf(CcFundsJSO.MAX_BONUS).length();
            iBonusBox.setMaxLength(maxLen);
            iBonusBox.setVisibleLength(maxLen);
            iBonusBox.setAlignment(TextAlignment.RIGHT);
            iBonusBox.setTitle(CcConstants.STRINGS.fundsBonusTitle());
            iBonusBox.addFocusHandler(TXTFOCUSHANDLER);
            iBonusBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
                @Override
                public void onValueChange(final ValueChangeEvent<Integer> pEvent)
                {
                    iPresenter.onBonusChanged(pEvent.getValue());
                }
            });
            // use numerical input pad on iPhone
            iBonusBox.getElement().setAttribute("pattern", "[0-9]*"); //$NON-NLS-1$ //$NON-NLS-2$

            iActivatableWidgets.add(lblBonus);
            iActivatableWidgets.add(iBonusBox);
            iDetailWidgets.add(iBonusBox);
        }

        VerticalPanel vp = new VerticalPanel();
        vp.add(lblBonus);
        vp.add(iBonusBox);
        return vp;
    }



    @Override
    public void setDetailTracking(final boolean pDetailed)
    {
        iTotalFundsBox.setEnabled(!pDetailed);
        iCoarsePanel.setVisible(!pDetailed);
        iDetailPanel.setVisible(pDetailed);
        iNumCommIndicator.setEnabled(pDetailed);
    }



//    private void onTreasuryBoxChange(final IntegerBox pSource, final SliderBar pSb,
//        final Integer pNewValue)
//    {
//        // TODO part of this will have to move to the presenter
//        if (isIntBetween(pNewValue, CcFundsJso.TREASURY_MIN, CcFundsJso.TREASURY_MAX)) {
//            int newValue = pNewValue.intValue();
//            updateTotalFunds(iTotalFunds + newValue - ((int) pSb.getCurrentValue()));
//            pSb.setCurrentValue(newValue);
//        }
//        else {
//            Integer oldValue = Integer.valueOf((int) pSb.getCurrentValue());
//            pSource.setValue(oldValue);
//            pSource.setSelectionRange(0, pSource.getText().length());
//        }
//    }



    @Override
    public boolean isEnabled()
    {
        return iBtnClear.isEnabled();
    }

    /**
     * Enable or disable the entire view.
     * @param pEnabled <code>true</code> to enable
     * @see com.google.gwt.user.client.ui.HasEnabled#setEnabled(boolean)
     */
    @Override
    public void setEnabled(final boolean pEnabled)
    {
        for (HasEnabled w : iActivatableWidgets) {
            w.setEnabled(pEnabled);
        }
        if (pEnabled) {
            iBtnToggleFunds.setTitle(CcConstants.STRINGS.disableFunds());
        } else {
            iBtnToggleFunds.setTitle(CcConstants.STRINGS.enableFunds());
        }
    }



    @Override
    public void setPresenter(final CbPresenterIF pPresenter)
    {
        iPresenter = pPresenter;
    }



    @Override
    public void initialize(final CcCommodityConfigJSO[] pCommodities,
        final CcFundsJSO pFundsJso)
    {
        final ValueChangeHandler<CcCommSpinnerPayload> vch =
            new ValueChangeHandler<CcCommSpinnerPayload>()
        {
            @Override
            public void onValueChange(final ValueChangeEvent<CcCommSpinnerPayload> pEvent)
            {
                iPresenter.onSpinnerChanged(pEvent.getValue());
            }
        };

        final int numCells = pCommodities.length + 1;
        iSpinnersGrid.resize(Math.round(numCells / 2), 2);
        for (int row = 0, c = 0; row < iSpinnersGrid.getRowCount(); row++) {
            for (int col = 0; col < iSpinnersGrid.getColumnCount(); col++)
            {
                if (c < numCells - 1) {
                    CcCommoditySpinner cs = new CcCommoditySpinner(c, pCommodities[c]);
                    c++;
                    cs.addValueChangeHandler(vch);
                    iSpinnersGrid.setWidget(row, col, cs);
                    iDetailWidgets.add(cs);
                    iActivatableWidgets.add(cs);
                }
                else if (c < numCells) {
                    iSpinnersGrid.setWidget(row, col, createBonusBox());
                }
            }
        }

        iTreasuryBox.setValue(Integer.valueOf(pFundsJso.getTreasury()), true);
        iBonusBox.setValue(Integer.valueOf(pFundsJso.getBonus()), false);
        iBtnToggleDetail.setValue(Boolean.valueOf(pFundsJso.isDetailed()), true);
        iBtnToggleFunds.setValue(Boolean.valueOf(pFundsJso.isEnabled()), true);
    }



    @Override
    public void setTotalFundsBoxOnly(final int pNewValue)
    {
        iTotalFundsBox.setValue(Integer.valueOf(pNewValue));
        iTotalFundsBox.setSelectionRange(0, iTotalFundsBox.getText().length());
    }



    @Override
    public void setTotalFunds(final int pNewValue)
    {
        iTotalFundsBox.setValue(Integer.valueOf(pNewValue));
        iTotalFundsIndicator.setValue(pNewValue);
    }



    @Override
    public void setNumCommodities(final int pNewValue)
    {
        iNumCommIndicator.setValue(pNewValue);
    }



    @Override
    public void setBonusBoxOnly(final int pBonus)
    {
        iBonusBox.setValue(Integer.valueOf(pBonus));
        iBonusBox.setSelectionRange(0, iBonusBox.getText().length());
    }



    /**
     * Reset all widget values in the funds view to zero.
     */
    private void reset()
    {
        final CcCommSpinnerPayload csZero = new CcCommSpinnerPayload(0, 0, 0);
        for (HasValue<?> w : iDetailWidgets) {
            if (w instanceof CcCommoditySpinner) {
                ((CcCommoditySpinner) w).setValue(csZero, false);
            } else if (w instanceof IntegerBox) {
                ((IntegerBox) w).setValue(Integer.valueOf(0), true);  // with events!
            } else if (LOG.isLoggable(Level.WARNING)) {
                LOG.warning("Unknown detail widget type " //$NON-NLS-1$
                    + (w != null ? w.getClass().getName() : "null") //$NON-NLS-1$
                    + " - BUG!"); //$NON-NLS-1$
            }
        }
        setNumCommodities(0);
        setTotalFunds(0);
    }
}
