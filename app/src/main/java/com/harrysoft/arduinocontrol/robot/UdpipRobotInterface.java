package com.harrysoft.arduinocontrol.robot;

import androidx.annotation.Nullable;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URI;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public class UdpipRobotInterface implements RobotInterface {
    private final String addressStr;

    @Nullable
    private DatagramSocket socket;
    @Nullable
    private InetAddress address;
    @Nullable
    private Integer port;

    public UdpipRobotInterface(String address) {
        this.addressStr = address;
    }

    @Override
    public Completable connect() {
        return Completable.fromAction(() -> {
            URI uri = new URI("tcp://" + addressStr);
            address = InetAddress.getByName(uri.getHost());
            port = uri.getPort();
            socket = new DatagramSocket(8000);
            socket.setBroadcast(true);
        });
    }

    @Override
    public void setMotorSpeeds(float leftMotorSpeed, float rightMotorSpeed) throws Exception {
        if (socket == null || address == null || port == null) throw new IllegalStateException();
        // TODO this is lazy. Synchronize this, not on main thread.
        Completable.fromAction(() -> {
            DatagramPacket packet = new DatagramPacket(new byte[]{(byte) leftMotorSpeed, (byte) rightMotorSpeed, ';'}, 0, 3, address, port);
            socket.send(packet);
        })
                .subscribeOn(Schedulers.io())
                .blockingAwait();
    }

    @Override
    public void disconnect() throws Exception {
        if (socket != null) {
            socket.close();
            socket = null;
        }
        address = null;
        port = null;
    }
}
