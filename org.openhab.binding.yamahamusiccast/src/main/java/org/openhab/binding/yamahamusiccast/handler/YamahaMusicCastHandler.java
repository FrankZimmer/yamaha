/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.yamahamusiccast.handler;

import static org.openhab.binding.yamahamusiccast.YamahaMusicCastBindingConstants.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link YamahaMusicCastHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Frank Zimmer - Initial contribution
 */
public class YamahaMusicCastHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(YamahaMusicCastHandler.class);
    private String host;
    private static String ON = "on";
    private static String OFF = "standby";

    public YamahaMusicCastHandler(Thing thing) {
        super(thing);
        host = (String) getConfig().get("host");
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(POWER)) {
            if (command.equals(OnOffType.ON)) {
                postPowerState(ON);
            } else if (command.equals(OnOffType.OFF)) {
                postPowerState(OFF);
            }
        }
        if (channelUID.getId().equals(VOLUME)) {
            postVolumeState(command.toString());
        }
    }

    @Override
    public void initialize() {
        // TODO: Initialize the thing. If done set status to ONLINE to indicate proper working.
        // Long running initialization should be done asynchronously in background.
        updateStatus(ThingStatus.ONLINE);

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work
        // as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }

    private void postPowerState(String state) {

        String urlString = "http://" + host + "/YamahaExtendedControl/v1/main/setPower?power=" + state;
        logger.debug(urlString);
        try {

            // Create HTTP GET request
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            logger.debug(response.toString());

            logger.debug("YAMAHA: " + responseCode + "; " + connection.getResponseMessage());
            // Process response
            if (responseCode == 200) {
                if (state.equals(ON)) {
                    updateState(POWER, OnOffType.ON);
                } else if (state.equals(OFF)) {
                    updateState(POWER, OnOffType.OFF);
                    updateStatus(ThingStatus.ONLINE);
                }
            }
            logger.debug("Update of {} : {} to {}", this.getThing().getUID(), POWER, state);

        } catch (Exception e) {
            logger.warn("Unable to post state: " + e.getMessage(), e);
            updateStatus(ThingStatus.OFFLINE);
        }

    }

    private void postVolumeState(String volume) {

        String urlString = "http://" + host + "/YamahaExtendedControl/v1/main/setVolume?volume=" + volume;
        logger.debug(urlString);
        try {

            // Create HTTP GET request
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            logger.debug("YAMAHA: " + responseCode);
            // Process response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            logger.debug(response.toString());

            updateState(VOLUME, new PercentType(volume));

            logger.debug("Update of {} : {} to {}", this.getThing().getUID(), POWER, volume);

            updateStatus(ThingStatus.ONLINE);

        } catch (Exception e) {
            logger.warn("Unable to post state: " + e.getMessage(), e);
            updateStatus(ThingStatus.OFFLINE);
        }

    }
}
