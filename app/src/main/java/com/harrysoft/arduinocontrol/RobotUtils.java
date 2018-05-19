package com.harrysoft.arduinocontrol;

public class RobotUtils {

    public static MotorSpeedConfig calculateMotorSpeeds(float x, float y) {
        float lBasedOnY = y * -100;
        float rBasedOnY = y * -100;

        float lBasedOnX = x * 100;
        float rBasedOnX = x * -100;

        float lPre = lBasedOnX + lBasedOnY;
        float rPre = rBasedOnX + rBasedOnY;

        if (lPre > 100) {
            lPre = 100;
        }

        if (lPre < -100) {
            lPre = -100;
        }

        if (rPre > 100) {
            rPre = 100;
        }

        if (rPre < -100) {
            rPre = -100;
        }

        return new MotorSpeedConfig(lPre, rPre);
    }

    static class MotorSpeedConfig {
        public final float leftSpeed;
        public final float rightSpeed;

        MotorSpeedConfig(float leftSpeed, float rightSpeed) {
            this.leftSpeed = leftSpeed;
            this.rightSpeed = rightSpeed;
        }
    }

}
