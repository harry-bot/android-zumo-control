package com.harrysoft.arduinocontrol.robot;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public class TcpipRobotInterface implements RobotInterface {
    private final String address;

    @Nullable
    private Socket socket;
    @Nullable
    private OutputStream outputStream;

    public TcpipRobotInterface(String address) {
        this.address = address;
    }

    @Override
    public Completable connect() {
        return Completable.fromAction(() -> {
            URI uri = new URI("tcp://" + address);
            socket = new Socket(uri.getHost(), uri.getPort());
            outputStream = socket.getOutputStream();
        });
    }

    @Override
    public void setMotorSpeeds(float leftMotorSpeed, float rightMotorSpeed) throws IOException {
        if (outputStream == null) throw new IllegalStateException();
        // TODO this is lazy. Synchronize this, not on main thread.
        Completable.fromAction(() -> outputStream.write(new byte[]{(byte) leftMotorSpeed, (byte) rightMotorSpeed, ';'}))
                .subscribeOn(Schedulers.io())
                .blockingAwait();
    }

    @Override
    public void disconnect() throws Exception {
        if (outputStream != null) {
            outputStream.close();
            outputStream = null;
        }
    }
}
