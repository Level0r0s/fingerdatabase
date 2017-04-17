package com.neil.fpdatabase.fingercore;

import com.neil.fpdatabase.controller.FingerPrintRecognitionResultHandler;
import com.zkteco.biometric.FingerprintCaptureListener;
import com.zkteco.biometric.FingerprintSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by nhu on 4/17/2017.
 * Listener that invoke caching and sending img
 */
public class FingerAcceptorListener implements FingerprintCaptureListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FingerAcceptorListener.class);

    private static FingerPrintHandler handler;

    private static FingerPrintRecognitionResultHandler fingerPrintRecognitionResultHandler;

    @Override
    public void captureOK(byte[] bytes) {
        LOGGER.info("capturing data.");
        try {
            handler.writeToPic(bytes);
            fingerPrintRecognitionResultHandler.sendImage("img/finger"+ FingerPrintHandler.currentRegIndex +".png");
        } catch (Exception e) {
            LOGGER.error("error during sending capture:", e);
        }
    }

    @Override
    public void captureError(int i) {
        //LOGGER.error("error during capture:", i);
    }

    @Override
    public void extractOK(byte[] bytes) {
        LOGGER.info("extracting data.");
        handler.handleRegister(bytes);
    }

    @Autowired
    public void setHandler(FingerPrintHandler handler) {
        this.handler = handler;
    }

    @Autowired
    public void setFingerPrintRecognitionResultHandler(FingerPrintRecognitionResultHandler fingerPrintRecognitionResultHandler) {
        this.fingerPrintRecognitionResultHandler = fingerPrintRecognitionResultHandler;
    }
}
