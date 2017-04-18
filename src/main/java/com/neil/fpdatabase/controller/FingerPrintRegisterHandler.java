package com.neil.fpdatabase.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.neil.fpdatabase.fingercore.FingerPrintHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

/**
 * Created by nhu on 4/6/2017.
 * push the result of Finger Print scan to front
 */
public class FingerPrintRegisterHandler extends TextWebSocketHandler {
    
    private static WebSocketSession connectingSession ;

    @Autowired
    private FingerPrintHandler fingerPrintHandler;
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        this.connectingSession = session;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
        String identityInfoJSON = message.getPayload();
        JSONObject identity = JSON.parseObject(identityInfoJSON);
        if(identity.getString("op").equals("reg")) {
            fingerPrintHandler.setCurrentRegisteringCode(identity.getString("code"),
                    identity.getString("identity"));
        }else if(identity.getString("op").equals("clr")){
            fingerPrintHandler.reset();
        }
    }
    
    public void sendImage(String imgLoc) throws IOException {
        WebSocketMessage<String> imgMessage = new TextMessage(imgLoc);
        connectingSession.sendMessage(imgMessage) ;
    }
    
}
