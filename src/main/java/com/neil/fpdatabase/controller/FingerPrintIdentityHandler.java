package com.neil.fpdatabase.controller;

import com.alibaba.fastjson.JSONObject;
import com.neil.fpdatabase.fingercore.FingerPrintHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

/**
 * Created by nhu on 4/18/2017.
 */
public class FingerPrintIdentityHandler extends TextWebSocketHandler{

    private static final Logger LOGGER = LoggerFactory.getLogger(FingerPrintIdentityHandler.class);

    private WebSocketSession connectingSession;

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        this.connectingSession = session;
    }

    public void sendIdentity(String code, String identity){
        JSONObject idJSON = new JSONObject().fluentPut("code", code).fluentPut("identity", identity);
        LOGGER.info("sending identity:" + idJSON.toJSONString());
        TextMessage idInfo = new TextMessage(idJSON.toJSONString());
        try {
            if(connectingSession != null) {
                connectingSession.sendMessage(idInfo);
            }
        } catch (IOException e) {
            LOGGER.error("catch error during sending:", e);
        }
    }

}
