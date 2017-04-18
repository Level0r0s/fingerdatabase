package com.neil.fpdatabase.fingercore;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.zkteco.biometric.FingerprintSensor;
import com.zkteco.biometric.FingerprintSensorErrorCode;
import com.zkteco.biometric.ZKFPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by nhu on 4/17/2017.
 * component that accept the finger print
 */
@Component
public class FingerPrintConfig {

    public static final String COLLECTION_NAME = "persistent";
    private static final Logger LOGGER = LoggerFactory.getLogger(FingerPrintConfig.class);
    public static int width = 0;
    public static int height = 0;
    private FingerprintSensor fingerprintSensor = new FingerprintSensor();
    private CachedFingerPrintPool cachedFingerPrintPool = new CachedFingerPrintPool();
    @Value("${file.location}")
    private String fileLocation;
    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void startFingerAcceptor() {
        LOGGER.info("starting finger acceptor");
        initFingerSensor();
        loadFromDatabase();
    }

    private void initFingerSensor() {
        int ret = fingerprintSensor.getDeviceCount();
        if (ret < 0) {
            LOGGER.error("no device detected!");
        }
        if (FingerprintSensorErrorCode.ERROR_SUCCESS != (ret = fingerprintSensor.openDevice(0))) {
            LOGGER.error("device open failed!");
        }
        width = fingerprintSensor.getImageWidth();
        height = fingerprintSensor.getImageHeight();
        fingerprintSensor.setFingerprintCaptureListener(getListener());
        if (!fingerprintSensor.startCapture()) {
            freeSensor();
        }
    }

    private void loadFromDatabase() {
        DBCursor cursor = mongoTemplate.getCollection(COLLECTION_NAME).find();
        int index = 1;
        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            int ret = ZKFPService.DBAdd(index, (byte[]) object.get("generated"));

            if (ret == 0) {
                CachedFingerPrint cachedFingerPrint = new CachedFingerPrint();
                cachedFingerPrint.setIdentity(object.get("identity").toString());
                cachedFingerPrint.setPrisonCode(object.get("code").toString());
                cachedFingerPrintPool.set(index, cachedFingerPrint);
                index++;
            }

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
        FingerPrintHandler handler = new FingerPrintHandler(fingerprintSensor);
        handler.setFileLocation(fileLocation);
        handler.setMongoTemplate(mongoTemplate);
        return handler;
    }

}
