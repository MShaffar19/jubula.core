/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jubula.rc.javafx.tester.util.compatibility.WindowsUtil;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Provides access to all instantiated windows, by accessing a private field in
 * the <code>Stage</code> class with reflection. Whenever a <code>Stage</code>
 * is instantiated or closed a reference is stored in this field automatically
 * by JavaFX.
 *
 * @author BREDEX GmbH
 * @created 10.10.2013
 *
 */
public class CurrentStages {
    /** environment variable for polling rate */
    public static final String JUBULA_FX_POLLING_RATE = "JUBULA_FX_POLLING_RATE"; //$NON-NLS-1$
    /** environment variable for an initial wait */
    public static final String JUBULA_FX_WAIT_BEFORE_INIT = "JUBULA_FX_WAIT_BEFORE_INIT"; //$NON-NLS-1$

    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(CurrentStages.class);

    /** The Window list **/
    private static ObservableList<Window> windows = 
            FXCollections.observableArrayList();

    /** private Constructor **/
    private CurrentStages() {
        // private Constructor
    }

    static {
        String pollingEnvString = EnvironmentUtils
                .getProcessOrSystemProperty(JUBULA_FX_POLLING_RATE);
        long pollingRate = 50;
        if (pollingEnvString != null) {
            try {
                pollingRate = Long.parseLong(pollingEnvString);
            } catch (NumberFormatException nf) {
                LOG.info("Could not convert the value." //$NON-NLS-1$
                        + "using standard polling rate " + pollingRate + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        String waitEnvString = EnvironmentUtils
                .getProcessOrSystemProperty(JUBULA_FX_WAIT_BEFORE_INIT);
        long waitTimeInMs = 0;
        if (waitEnvString != null) {
            waitTimeInMs = 2000;
            try {
                waitTimeInMs = Integer.parseInt(waitEnvString);
            } catch (NumberFormatException nf) {
                LOG.info("Could not convert the value." //$NON-NLS-1$
                        + "using standard wait time " + waitTimeInMs + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        ScheduledExecutorService scheduler = 
                Executors.newScheduledThreadPool(1);
        Runnable windowCheck = new Runnable() {

            @Override
            public void run() {
                // USE OF DEPRECATED API
                Iterator<Window> it = WindowsUtil.getWindowIterator();
                List<Window> tempWin = new ArrayList<>();

                // "Convert" iterator to list for removing and add new
                // windows
                while (it.hasNext()) {
                    Window w = it.next();
                    tempWin.add(w);
                    if (!windows.contains(w)) {
                        windows.add(w);
                    }
                }
                // Now iterate over the windows list and find windows
                // which can be removed
                it = windows.listIterator();
                while (it.hasNext()) {
                    Window w = it.next();
                    if (!tempWin.contains(w)) {
                        it.remove();
                    }
                }
            }
        };
        scheduler.scheduleAtFixedRate(windowCheck, waitTimeInMs, 
                pollingRate, TimeUnit.MILLISECONDS);
    }

    /**
     * Gets the first Window in the list
     *
     * @return the Window
     */
    public static Window getfirstStage() {
        for (Window window : windows) {
            if (window instanceof Stage) {
                return window;
            }
        }
        return null;
    }

    /**
     * Returns the complete list of windows
     * 
     * @return the Window list
     */
    public static List<Window> getWindowList() {
        return windows;
    }

    /**
     * Gets the Window with focus in the list
     *
     * @return the Window
     */
    public static Window getfocusStage() {
        Window fStage = null;
        for (Window win : windows) {
            if (win.isFocused() && win instanceof Stage) {
                fStage = win;
            }
        }
        return fStage;
    }

    /**
     * Adds a <code>ListChangeListener</code> to the windows-List
     *
     * @param listener
     *            the listener
     */
    public static void addStagesListener(ListChangeListener<Window> listener) {
        windows.addListener(listener);
    }

    /**
     * Removes a <code>ListChangeListener</code> from the windows-List
     *
     * @param listener
     *            the listener
     */
    public static void removeStagesListener(
            ListChangeListener<Window> listener) {
        windows.removeListener(listener);
    }
}