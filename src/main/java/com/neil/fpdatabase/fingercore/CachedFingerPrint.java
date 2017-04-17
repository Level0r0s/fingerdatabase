package com.neil.fpdatabase.fingercore;

/**
 * Created by nhu on 4/17/2017.
 * prison finger print which will be stored in memory
 */
public class CachedFingerPrint {

    private int index;
    private  byte[][] regTempArray = new byte[3][2048];
    private int currentRegIndex = 0;
    private String prisonCode;
    private String identity;


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void registering(byte[] img){
        regTempArray[currentRegIndex] = img;
        currentRegIndex++;
    }

    public Boolean isReady(){
        return currentRegIndex == 3;
    }

    public String getIdentityCode() {
        return prisonCode;
    }

    public void setPrisonCode(String prisonCode) {
        this.prisonCode = prisonCode;
    }

    public String getIdentity() {
        return identity;
    }

    public boolean isIdentityValidate(){
        return this.identity.equals("police") || this.identity.equals("prison");
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public byte[] getImg(int index){
        return regTempArray[index];
    }
}
