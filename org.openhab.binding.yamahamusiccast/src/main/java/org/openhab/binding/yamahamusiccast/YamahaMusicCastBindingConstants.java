/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.yamahamusiccast;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link YamahaMusicCastBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Frank Zimmer - Initial contribution
 */
public class YamahaMusicCastBindingConstants {

    public static final String BINDING_ID = "yamahamusiccast";

    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_SAMPLE = new ThingTypeUID(BINDING_ID, "speaker");

    // List of all Channel ids
    public final static String POWER = "power";
    public final static String VOLUME = "volume";

}
