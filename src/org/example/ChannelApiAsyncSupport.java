package org.example;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import java.io.IOException;
import javax.servlet.ServletException;
import org.atmosphere.cpr.Action;
import org.atmosphere.cpr.AsynchronousProcessor;
import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceImpl;
import org.atmosphere.cpr.AtmosphereResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author p.havelaar
 */
public class ChannelApiAsyncSupport extends AsynchronousProcessor {
    
    private final Logger logger = LoggerFactory.getLogger(ChannelApiAsyncSupport.class.getName());

    public ChannelApiAsyncSupport(AtmosphereConfig config) {
        super(config);
    }


    public String getContainerName() {
        return super.getContainerName() + " using channel API";
    }

    public Action service(AtmosphereRequest req, AtmosphereResponse resp) throws IOException, ServletException {
        Action action = suspended(req, resp);
        if (action.equals(Action.SUSPEND)) {
            String token = ChannelServiceFactory.getChannelService().createChannel(req.resource().uuid());
            if (token == null) {
                throw new IllegalStateException("Failed to create channel for request: " + req.resource().uuid());
            } else {
                logger.info("Suspending resource " + req.resource().uuid() + " with token " + token);
            }
            resp.setHeader("chapi_token", token);
        }
        return action;
    }

    @Override
    public void action(AtmosphereResourceImpl res) {
        super.action(res);
        
        if (res.action().equals(Action.RESUME)) {
            resume(res);
        } else if (res.action().equals(Action.SUSPEND)) {
            throw new UnsupportedOperationException("not implemented");
        }
    }

    protected void resume(AtmosphereResource res) {
        if (isAlive(res)) {        
            sendToClient(res.uuid(), "disconnect;");
        }
    }
    
    static boolean isAlive(AtmosphereResource res) {
        return res.getResponse().getResponse() != null;
    }
    
    static void sendToClient(String uuid, String message) {
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        channelService.sendMessage(
                new ChannelMessage(uuid, message));
    }
}
