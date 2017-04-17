package com.neil.fpdatabase.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.neil.fpdatabase.fingercore.AcceptFingerPrint;
import com.neil.fpdatabase.fingercore.FingerPrintHandler;
import org.apache.commons.codec.binary.Base64;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

/**
 * Created by nhu on 4/6/2017.
 * push the result of Finger Print scan to front
 */
@Component
public class FingerPrintRecognitionResultHandler extends TextWebSocketHandler {
    
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
        fingerPrintHandler.setCurrentRegisteringCode(identity.getString("code"),
                identity.getString("identity"));
    }
    
    public void sendImage(String imgLoc) throws IOException {
        WebSocketMessage<String> imgMessage = new TextMessage(imgLoc);
        connectingSession.sendMessage(imgMessage) ;
    }
    
}
