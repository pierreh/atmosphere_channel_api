
if (typeof org == 'undefined') {
    org = {}
}
if (typeof org.atmosphere == 'undefined') {
    org.atmosphere = {}
}
if (typeof org.atmosphere.ChannelAPITransport == 'undefined') {
    
    org.atmosphere.ChannelAPITransport = function(listener) {
    
        if (typeof listener == 'undefined') {
            throw "listener must not be empty";
        }
        // private members
        var _self = this;
        var _listener = listener;
        var _socket = false;
        var _method = 'GET';
        
        // private method
        function connectChannel(token) {
            var channel = new goog.appengine.Channel(token);
            _socket = channel.open();
            _socket.onopen = listener.onOpened;
            _socket.onmessage = onMessage;
            _socket.onerror = function (err) { error(err.code + ": " + err.description)};
            _socket.onclose = _listener.onClosed;
        }
        
        function onMessage(msg) {
            var data = msg.data;
            var i = data.indexOf(";");
            if (i == -1) {
                throw "Corrupt message: " + data;
            }
            var j = data.indexOf(":");
            var action;
            var params = {};
            var payload;
            if (j == -1) {
                action = data.substring(0, i);
            } else {
                action = data.substring(0, j);
                var p = data.substring(j+1, i).split(",");
                for (var nv in p) {
                    var nva = p[nv].split("=");
                    params[nva[0]] = nva[1];
                }
            }
            if (data.length > i + 1) {
                payload = data.substr(i+1);
            }
            if (action == "connected") {
                _listener.onConnected(params.id);
            } else if (action == "disconnect") {
                _self.close();
            } else if (action == "message") {
                if (payload.length < params.l) {
                    throw "incomplete message data received";
                }
                _listener.onMessage(payload.substr(0, params.l));
            }
        }
        
        // private method
        function error(msg) {
            if (_listener.onError) {
                _listener.onError(msg);
            } else {
                console.log("[ERROR]" + msg);
            }
        }
        
        // private method
        this.open = function open(url) {
            if (_socket) {
                throw "Socket is already open";
            }
            var xhr = new XMLHttpRequest();
            xhr.open(_method, url, true); 
            xhr.onreadystatechange = function() {
                if (xhr.readyState == 4 && xhr.status==200) {
                    var response = eval('('+xhr.responseText+')');
                    if (response && response.token) {
                        connectChannel(response.token);
                    } else {
                        error('failed to get token');
                    }
                } else if (xhr.readyState == 4){
                    error('error code ' + xhr.status);
                }
            }
            xhr.send(); 
        }
        
        this.close = function () {
            if (_socket) {
                _socket.close();
                _socket = false;
            }
        }
    }
    
    org.atmosphere.ChannelAPIListener = function() {}
    org.atmosphere.ChannelAPIListener.prototype.onOpened = function() {}
    org.atmosphere.ChannelAPIListener.prototype.onConnected = function(id) {}
    org.atmosphere.ChannelAPIListener.prototype.onMessage = function(payload) {}
    org.atmosphere.ChannelAPIListener.prototype.onError = function(msg) {}
    org.atmosphere.ChannelAPIListener.prototype.onClosed = function() {}
}
