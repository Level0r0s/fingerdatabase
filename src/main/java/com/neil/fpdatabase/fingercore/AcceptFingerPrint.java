package com.neil.fpdatabase.fingercore;

import com.zkteco.biometric.FingerprintSensor;
import com.zkteco.biometric.FingerprintSensorErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by nhu on 4/17/2017.
 * component that accept the finger print
 */
@Component
public class AcceptFingerPrint {

    private FingerprintSensor fingerprintSensor = new FingerprintSensor();
    private static final Logger LOGGER = LoggerFactory.getLogger(AcceptFingerPrint.class);

    public static int width = 0;
    public static int height = 0;

    @PostConstruct
    public void startFingerAcceptor() {
        LOGGER.info("starting finger acceptor");
        int ret = fingerprintSensor.getDeviceCount();
        if (ret < 0){
            LOGGER.error("no device detected!");
        }
        if (FingerprintSensorErrorCode.ERROR_SUCCESS != (ret = fingerprintSensor.openDevice(0))){
            LOGGER.error("device open failed!");
        }
        width = fingerprintSensor.getImageWidth();
        height = fingerprintSensor.getImageHeight();
        fingerprintSensor.setFingerprintCaptureListener(getListener());
        if (!fingerprintSensor.startCapture()) {
            freeSensor();
        }
    }

    private void freeSensor() {
        LOGGER.info("freeing finger acceptor");
        if (null != fingerprintSensor) {
            fingerprintSensor.stopCapture();
            fingerprintSensor.closeDevice();
            fingerprintSensor.destroy();
            fingerprintSensor = null;
        }
    }

    @Bean
    public FingerAcceptorListener getListener() {
        return new FingerAcceptorListener();
    }

    @Bean
    FingerPrintHandler getHandler() {
        return new FingerPrintHandler(fingerprintSensor);
    }

}
