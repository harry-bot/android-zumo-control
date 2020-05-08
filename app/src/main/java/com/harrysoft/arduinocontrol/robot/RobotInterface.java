package com.harrysoft.arduinocontrol.robot;

import io.reactivex.Completable;

public interface RobotInterface {
    Completable connect();
    void setMotorSpeeds(float leftMotorSpeed, float rightMotorSpeed) throws Exception;
    void disconnect() throws Exception;

    static enum Protocol {
        TCPIP,
        UDPIP,
        BLUETOOTH
    }

    static RobotInterface open(Protocol protocol, String address) {
        switch (protocol) {
            case TCPIP:
                return new TcpipRobotInterface(address);
            case UDPIP:
                return new UdpipRobotInterface(address);
            case BLUETOOTH:
                return new BluetoothRobotInterface(address);
        }
        throw new IllegalStateException("unreachable");
    }
}
