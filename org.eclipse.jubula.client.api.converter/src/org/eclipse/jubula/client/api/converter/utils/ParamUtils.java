/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.api.converter.utils;

import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.ui.rcp.views.dataset.AbstractDataSetPage;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * @created 05.11.2014
 */
public class ParamUtils {
    
    /** Pattern for detecting parameters like =PARAM */
    private static Pattern parameter = Pattern.compile("^=([a-zA-Z0-9_]+)"); //$NON-NLS-1$

    /** Pattern for detecting parameters like ={PARAM} */
    private static Pattern parameterWithBrackets = Pattern.compile(
            "^=\\{([a-zA-Z0-9_]+)\\}"); //$NON-NLS-1$
    
    /** Pattern for detecting parameters like =PARAM1 =PARAM2 */
    private static Pattern multipleParameters = Pattern.compile(".*=.*=.*"); //$NON-NLS-1$
    
    /** Pattern for detecting variables like $VAR */
    private static Pattern variable = Pattern.compile(".*\\$.*"); //$NON-NLS-1$
    
    /** Pattern for detecting functions like ?add(1,2) */
    private static Pattern function = Pattern.compile(".*\\?[a-zA-Z_]+\\(.*?"); //$NON-NLS-1$
    
    /**
     * private constructor
     */
    private ParamUtils() {
        // private
    }

    /**
     * Returns a parameter value for a node
     * @param node the node
     * @param param the parameter
     * @param row the row
     * @param locale the language
     * @return the value
     */
    public static String getValueForParam(IParameterInterfacePO node,
            IParamDescriptionPO param, int row, Locale locale) {
        String paramType = param.getType();
        String value = AbstractDataSetPage.getGuiStringForParamValue(
                node, param, row, locale);
        //CHECKSTYLE:OFF
        if (value == null) {
            value = "null /*TODO: check*/"; //$NON-NLS-1$
        } else {
            value = executeEscapes(value);
            if (StringUtils.isBlank(value)) {
                value = StringConstants.QUOTE + StringConstants.QUOTE;
            } else if (multipleParameters.matcher(value).matches()) {
                return "null // TODO: Parameter concatenation: \"" //$NON-NLS-1$
                        + value + "\"\n"; //$NON-NLS-1$
            } else if (variable.matcher(value).matches()) {
                return "null /* TODO: Variable: \"" //$NON-NLS-1$
                        + value + "\" */"; //$NON-NLS-1$
            } else if (function.matcher(value).matches()) {
                return "null /* TODO: Function: \"" //$NON-NLS-1$
                        + value + "\" */"; //$NON-NLS-1$
            } else if (parameterWithBrackets.matcher(value).matches()) {
                value = value.replaceAll(parameterWithBrackets.pattern(), "$1"); //$NON-NLS-1$
            } else if (parameter.matcher(value).matches()) {
                value = value.replaceAll(parameter.pattern(), "$1"); //$NON-NLS-1$
            } else if (paramType.equals("java.lang.String") ) { //$NON-NLS-1$
                value = StringConstants.QUOTE + value + StringConstants.QUOTE;
            } else if (paramType.equals("guidancer.datatype.Variable") ) { //$NON-NLS-1$
                value = "null /* TODO: Potential Variable assignment: " //$NON-NLS-1$
                        + value + "*/"; //$NON-NLS-1$
            }
        }
        return value;
        //CHECKSTYLE:ON
    }

    /**
     * escapes characters in a string
     * @param value the string
     * @return the adjusted string
     */
    private static String executeEscapes(String value) {
        String adjustedValue = value;
        adjustedValue = adjustedValue.replace(StringConstants.BACKSLASH,
                StringConstants.BACKSLASH + StringConstants.BACKSLASH);
        adjustedValue = adjustedValue.replace(StringConstants.QUOTE, "\\\""); //$NON-NLS-1$
        adjustedValue = adjustedValue.replace(StringConstants.APOSTROPHE, "\\'"); //$NON-NLS-1$
        return adjustedValue;
    }
    
    
}