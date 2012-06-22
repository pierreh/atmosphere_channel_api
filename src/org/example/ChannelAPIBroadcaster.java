package org.example;

import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.util.SimpleBroadcaster;

/**
 *
 * @author p.havelaar
 */
public class ChannelAPIBroadcaster extends SimpleBroadcaster {

    public ChannelAPIBroadcaster(String id, AtmosphereConfig config) {
        super(id, config);
    }
    
}
