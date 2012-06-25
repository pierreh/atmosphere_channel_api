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
        <script type="text/javascript" src="/_ah/channel/jsapi"></script>
    </head>
    <body>
        
        <a href="#" onclick="sendMessage('hello')">send</a>
        
        <script>
        function connectChannel(token) {
            var channel = new goog.appengine.Channel(token);
            var socket = channel.open();
            socket.onopen = onOpened;
            socket.onmessage = onMessage;
            socket.onerror = onError;
            socket.onclose = onClose;

            function onOpened() {
                alert('opened');
            }
            function onMessage(msg) {
                alert('message: ' + msg.data);
            }
            function onError(err) {
                alert('error: [' + err.code + '] ' + err.description);
            }
            function onClose() {
                alert('close');
            }
        }
        function requestToken() {
          var xhr = new XMLHttpRequest();
          xhr.open('GET', 'atmosphere', true); 
          xhr.onreadystatechange = function() {
              if (xhr.readyState == 4 && xhr.status==200) {
                  var response = eval('('+xhr.responseText+')');
                  if (response.token) {
                      console.log('received token: ' + response.token);
                      connectChannel(response.token);
                  } else {
                      alert('failed to get token');
                  }
              } else if (xhr.readyState == 4){
                  alert('error code ' + xhr.status);
              }
          }
          xhr.send(); 
        }
        function sendMessage(message) {
          var xhr = new XMLHttpRequest();
          xhr.open('POST', document.URL, true);
          xhr.send();
        };
        
        requestToken();
      </script>
    </body>
</html>
