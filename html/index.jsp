<%-- 
    Document   : index
    Created on : 21-jun-2012, 10:39:05
    Author     : p.havelaar
--%>
<%@page contentType="text/html" pageEncoding="windows-1252"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
        <title>Atmosphere Integration With Channel API</title>
        <script type="text/javascript" src="channel_api_transport.js"></script>
        
        <script type="text/javascript" src="/_ah/channel/jsapi"></script>
        
    </head>
    <body>
        
        <a href="#" onclick="sendMessage('hello')">send</a>
        
        <script>
            var listener = new org.atmosphere.ChannelAPIListener();
            listener.onConnected = function(id) {
                alert("connected: " + id);
            }
            listener.onMessage = function(payload) {
                alert("received message: " + payload);
            }
            listener.onError = function(msg) {
                alert("[ERROR]: " + msg);
            }
            
            var chapi = new org.atmosphere.ChannelAPITransport(listener);
            
            chapi.open('atmosphere');
        
            function sendMessage(message) {
              var xhr = new XMLHttpRequest();
              xhr.open('POST', document.URL, true);
              xhr.send();
            };
      </script>
    </body>
</html>
