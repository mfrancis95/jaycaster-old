package com.amf.jaycaster.entity;

import com.amf.jaycaster.core.Game;
import com.amf.jaycaster.core.Vector;

public class ControllableEntity extends Entity {
    
    public static final int CONTROLS_FORWARD = 0;
    public static final int CONTROLS_BACKWARD = 1;
    public static final int CONTROLS_STRAFELEFT = 2;
    public static final int CONTROLS_STRAFERIGHT = 3;
    public static final int CONTROLS_TURNLEFT = 4;
    public static final int CONTROLS_TURNRIGHT = 5;
    
    public final boolean[] controls;
    
    public double acceleration, friction, maxSpeed, turnSpeed;
    
    private final Vector forward = new Vector(), strafe = new Vector();
    
    private double forwardThrust, strafeThrust;
    
    public ControllableEntity(int controls, double moveSpeed, double turnSpeed) {
        this(controls, moveSpeed, 0, moveSpeed, turnSpeed);
    }
    
    public ControllableEntity(int controls, double acceleration, double friction, double maxSpeed, double turnSpeed) {
        this.controls = new boolean[controls < 6 ? 6 : controls];
        this.acceleration = acceleration;
        this.friction = friction;
        this.maxSpeed = maxSpeed;
        this.turnSpeed = turnSpeed;
    }
    
    public void update(Game game) {
        if (controls[CONTROLS_FORWARD]) {
            forwardThrust = Math.min(maxSpeed, forwardThrust + acceleration);
        } 
        else if (controls[CONTROLS_BACKWARD]) {
            forwardThrust = Math.max(-maxSpeed, forwardThrust - acceleration);
        }
        else {
            forwardThrust = Math.abs(forwardThrust) < 0.0001 ? 0 : forwardThrust * friction;
        }
        if (controls[CONTROLS_STRAFELEFT]) {
            strafeThrust = Math.max(-maxSpeed, strafeThrust - acceleration);
        }
        else if (controls[CONTROLS_STRAFERIGHT]) {
            strafeThrust = Math.min(maxSpeed, strafeThrust + acceleration);
        }
        else {
            strafeThrust = Math.abs(strafeThrust) < 0.0001 ? 0 : strafeThrust * friction;
        }
        if (controls[CONTROLS_TURNLEFT]) {
            direction.rotate(-turnSpeed);
            direction.normalise();
        } 
        else if (controls[CONTROLS_TURNRIGHT]) {
            direction.rotate(turnSpeed);
            direction.normalise();
        }
        forward.set(direction);
        forward.scale(forwardThrust);
        strafe.set(-direction.y, direction.x);
        strafe.scale(strafeThrust);
        velocity.set(forward);
        velocity.add(strafe);
        super.update(game);
    }
    
}