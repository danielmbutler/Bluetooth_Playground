package com.dbtechprojects.bluetoothplayground;

public class Device{
    private String name;
    private String mac;

    public Device(){
    }

    public Device(String deviceName, String deviceHardwareAddress) {
        setName(deviceName);
        setMac(deviceHardwareAddress);
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getMac(){
        return mac;
    }
    public void setMac(String mac){
        this.mac = mac;
    }
}
