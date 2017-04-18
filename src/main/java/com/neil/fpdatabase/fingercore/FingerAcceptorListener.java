package com.neil.fpdatabase.fingercore;

import com.neil.fpdatabase.controller.FingerPrintIdentityHandler;
import com.neil.fpdatabase.controller.FingerPrintRegisterHandler;
import com.zkteco.biometric.FingerprintCaptureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by nhu on 4/17/2017.
 * Listener that invoke caching and sending img
 */
public class FingerAcceptorListener implements FingerprintCaptureListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FingerAcceptorListener.class);

    private static FingerPrintHandler handler;

    private static FingerPrintRegisterHandler fingerPrintRegisterHandler;

    private static FingerPrintIdentityHandler fingerPrintIdentityHandler;

    @Override
    public void captureOK(byte[] bytes) {
        LOGGER.info("capturing data.");
        try {
            handler.preparePic(bytes);
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
        try {
            CachedFingerPrint cachedFingerPrint = handler.handleScan(bytes);
            if(cachedFingerPrint != null){
                //find matching
                fingerPrintIdentityHandler.sendIdentity(cachedFingerPrint.getIdentityCode(),
                        cachedFingerPrint.getIdentity());
            }else{
                //registering
                handler.writeToPic();
                fingerPrintRegisterHandler.sendImage("img/finger" + FingerPrintHandler.currentRegIndex + ".png");
            }
        } catch (Exception o) {
            LOGGER.error("unable to register due to ", o);
        }

    }

    @Autowired
    public void setHandler(FingerPrintHandler handler) {
        this.handler = handler;
    }

    @Autowired
    public void setFingerPrintRecognitionResultHandler(FingerPrintRegisterHandler fingerPrintRegisterHandler) {
        this.fingerPrintRegisterHandler = fingerPrintRegisterHandler;
    }

    @Autowired
    public void setFingerPrintIdentityHandler(FingerPrintIdentityHandler fingerPrintIdentityHandler) {
        this.fingerPrintIdentityHandler = fingerPrintIdentityHandler;
    }
}
