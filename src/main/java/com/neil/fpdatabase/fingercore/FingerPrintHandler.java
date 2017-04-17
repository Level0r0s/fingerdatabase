package com.neil.fpdatabase.fingercore;

import com.zkteco.biometric.FingerprintSensor;
import com.zkteco.biometric.ZKFPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nhu on 4/17/2017.
 * pool to return stored finger print
 */
public class FingerPrintHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FingerPrintHandler.class);

    private Map<Integer, CachedFingerPrint> cachePool = new HashMap<>();
    private static int currentFingerIndex = 1;
    public static int currentRegIndex = 0;
    private static CachedFingerPrint currentRegistering = new CachedFingerPrint();
    private FingerprintSensor fingerprintSensor;

    public FingerPrintHandler(FingerprintSensor sensor) {
        this.fingerprintSensor = sensor;
    }


    private int getFingerIndex(byte[] content) {
        int[] fid = new int[1];
        int[] score = new int[1];
        int ret = fingerprintSensor.IdentifyFP(content, fid, score);
        if (ret == 0) {
            return fid[0];
        }
        return -1;
    }

    public void writeToPic(byte[] currentFinger) throws IOException {
        //haven't register
        writeBitmap(currentFinger, AcceptFingerPrint.width, AcceptFingerPrint.height);
    }

    public void handleRegister(byte[] reg) {
        int index = getFingerIndex(reg);
        if (index != -1) {
            LOGGER.info("found:" + index);
            return;
        }
        System.arraycopy(reg, 0, currentRegistering.getImg(currentRegIndex), 0, 2048);
        currentRegIndex++;
        if (currentRegIndex == 3) {
            int[] _retLen = new int[1];
            _retLen[0] = 2048;
            int ret;
            byte[] regTemp = new byte[_retLen[0]];

            ret = ZKFPService.GenRegFPTemplate(currentRegistering.getImg(0),
                    currentRegistering.getImg(1),
                    currentRegistering.getImg(2), regTemp, _retLen);
            if (ret == 0) {
                ret = ZKFPService.DBAdd(currentFingerIndex, regTemp);
                if(ret == 0){
                    cachePool.put(currentRegIndex, currentRegistering);
                    currentRegIndex = 0;
                    currentRegistering = new CachedFingerPrint();
                    currentFingerIndex++;
                }
            }
        }
    }


    public void setCurrentPrisonCode(String code) {
        currentRegistering.setPrisonCode(code);
    }

    public void writeBitmap(byte[] imageBuf, int nWidth, int nHeight) throws IOException {
        FileOutputStream fos = new FileOutputStream("D:\\project\\fpdatabase\\src\\main\\resources\\static\\img\\finger" + currentRegIndex + ".png");
        java.io.DataOutputStream dos = new java.io.DataOutputStream(fos);

        int bfType = 0x424d; // 位图文件类型（0—1字节）
        int bfSize = 54 + 1024 + nWidth * nHeight;// bmp文件的大小（2—5字节）
        int bfReserved1 = 0;// 位图文件保留字，必须为0（6-7字节）
        int bfReserved2 = 0;// 位图文件保留字，必须为0（8-9字节）
        int bfOffBits = 54 + 1024;// 文件头开始到位图实际数据之间的字节的偏移量（10-13字节）

        dos.writeShort(bfType); // 输入位图文件类型'BM'
        dos.write(changeByte(bfSize), 0, 4); // 输入位图文件大小
        dos.write(changeByte(bfReserved1), 0, 2);// 输入位图文件保留字
        dos.write(changeByte(bfReserved2), 0, 2);// 输入位图文件保留字
        dos.write(changeByte(bfOffBits), 0, 4);// 输入位图文件偏移量

        int biSize = 40;// 信息头所需的字节数（14-17字节）
        int biWidth = nWidth;// 位图的宽（18-21字节）
        int biHeight = nHeight;// 位图的高（22-25字节）
        int biPlanes = 1; // 目标设备的级别，必须是1（26-27字节）
        int biBitcount = 8;// 每个像素所需的位数（28-29字节），必须是1位（双色）、4位（16色）、8位（256色）或者24位（真彩色）之一。
        int biCompression = 0;// 位图压缩类型，必须是0（不压缩）（30-33字节）、1（BI_RLEB压缩类型）或2（BI_RLE4压缩类型）之一。
        int biSizeImage = nWidth * nHeight;// 实际位图图像的大小，即整个实际绘制的图像大小（34-37字节）
        int biXPelsPerMeter = 0;// 位图水平分辨率，每米像素数（38-41字节）这个数是系统默认值
        int biYPelsPerMeter = 0;// 位图垂直分辨率，每米像素数（42-45字节）这个数是系统默认值
        int biClrUsed = 0;// 位图实际使用的颜色表中的颜色数（46-49字节），如果为0的话，说明全部使用了
        int biClrImportant = 0;// 位图显示过程中重要的颜色数(50-53字节)，如果为0的话，说明全部重要

        dos.write(changeByte(biSize), 0, 4);// 输入信息头数据的总字节数
        dos.write(changeByte(biWidth), 0, 4);// 输入位图的宽
        dos.write(changeByte(biHeight), 0, 4);// 输入位图的高
        dos.write(changeByte(biPlanes), 0, 2);// 输入位图的目标设备级别
        dos.write(changeByte(biBitcount), 0, 2);// 输入每个像素占据的字节数
        dos.write(changeByte(biCompression), 0, 4);// 输入位图的压缩类型
        dos.write(changeByte(biSizeImage), 0, 4);// 输入位图的实际大小
        dos.write(changeByte(biXPelsPerMeter), 0, 4);// 输入位图的水平分辨率
        dos.write(changeByte(biYPelsPerMeter), 0, 4);// 输入位图的垂直分辨率
        dos.write(changeByte(biClrUsed), 0, 4);// 输入位图使用的总颜色数
        dos.write(changeByte(biClrImportant), 0, 4);// 输入位图使用过程中重要的颜色数

        for (int i = 0; i < 256; i++) {
            dos.writeByte(i);
            dos.writeByte(i);
            dos.writeByte(i);
            dos.writeByte(0);
        }

        for (int i = 0; i < nHeight; i++)
            dos.write(imageBuf, (nHeight - 1 - i) * nWidth, nWidth);
        dos.flush();
        dos.close();
        fos.close();
    }

    public static byte[] changeByte(int data) {
        byte b4 = (byte) ((data) >> 24);
        byte b3 = (byte) (((data) << 8) >> 24);
        byte b2 = (byte) (((data) << 16) >> 24);
        byte b1 = (byte) (((data) << 24) >> 24);
        byte[] bytes = {b1, b2, b3, b4};
        return bytes;
    }

}
