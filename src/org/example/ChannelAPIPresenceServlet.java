package org.example;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Chunked messages
 * 
 * @author p.havelaar
 */
public class ChannelAPIPresenceServlet extends HttpServlet {
    
    private final Logger logger = LoggerFactory.getLogger(ChannelAPIPresenceServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        ChannelPresence presence = channelService.parsePresence(req);
        if (presence != null) {
            logger.info("Detected channel presense notification connected:"+ presence.isConnected()
                    + " id:"+ presence.clientId());
            AtmosphereResource resource = AtmosphereResourceFactory.find(presence.clientId());
            if (presence.isConnected()) {
                if (resource != null) {
                    connected(resource, presence);
                    ChannelApiAsyncSupport.sendToClient(presence.clientId(), 
                        "connected:id=" + presence.clientId() + ";");
                } else {
                    throw new IllegalStateException("Failed to find atmosphere resource for: " + presence.clientId());
                }
            } else {
                if (resource != null) {
                    disconnected(resource, presence);
                } else {
                    logger.warn("Failed to find resource for disconnecting channel: " + presence.clientId());
                }
            }
        }
    }
        
    protected void connected(AtmosphereResource resource, ChannelPresence presence) {
        // TODO we need to sort out which http request/response to use for the atmosphereresource
         
        final String uuid = presence.clientId();
        resource.getResponse().setResponse(new HttpServletResponseWrapper(resource.getResponse()) {
            ServletOutputStream out = new ServletOutputStream() {
                private StringBuilder buffer = new StringBuilder();
                @Override
                public void flush() throws IOException {
                    if (buffer.length() > 0) {
                        ChannelApiAsyncSupport.sendToClient(uuid, "message:l=" + buffer.length()
                                + ";" + buffer.toString());
                        buffer.setLength(0);
                    }
                }
                @Override
                public void write(int b) throws IOException {
                    buffer.append(String.valueOf((char)b));
                }
                @Override
                public void write(byte[] b) throws IOException {
                    buffer.append(new String(b, getCharacterEncoding()));
                }
                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    buffer.append(new String(b, off, len, getCharacterEncoding()));
                }
            };
            @Override
            public ServletOutputStream getOutputStream() throws IOException {
                return out;
            }

        });
    }

    protected void disconnected(AtmosphereResource resource, ChannelPresence presence) {
        resource.getRequest().setRequest(null);
        resource.getResponse().setResponse(null);
        resource.resume();
    }
}
