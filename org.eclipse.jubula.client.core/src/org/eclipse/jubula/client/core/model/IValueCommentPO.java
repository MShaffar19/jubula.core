/*******************************************************************************
 * Copyright (c) 2018 BREDEX GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: BREDEX GmbH - initial API and implementation and/or initial
 * documentation
 *******************************************************************************/

package org.eclipse.jubula.client.core.model;

/**
 * @author BREDEX GmbH
 *
 */
public interface IValueCommentPO {

    /**
     * @return the persistence id
     */
    Long getId();

    /**
     * @return the value
     */
    String getValue();

    /**
     * @param value the value
     */
    void setValue(String value);

    /**
     * @return the comment
     */
    String getComment();

    /**
     * @param comment the comment
     */
    void setComment(String comment);

}