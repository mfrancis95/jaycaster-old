package com.amf.jaycaster;

public class ControllableEntity extends Entity {
    
    public static final int CONTROLS_FORWARD = 0;
    public static final int CONTROLS_BACKWARD = 1;
    public static final int CONTROLS_STRAFELEFT = 2;
    public static final int CONTROLS_STRAFERIGHT = 3;
    public static final int CONTROLS_TURNLEFT = 4;
    public static final int CONTROLS_TURNRIGHT = 5;
    
    public final boolean[] controls;
    
    public double moveSpeed, turnSpeed;
    
    public ControllableEntity(int controls, double moveSpeed, double turnSpeed) {
        this.controls = new boolean[controls < 6 ? 6 : controls];
        this.moveSpeed = moveSpeed;
        this.turnSpeed = turnSpeed;
    }
    
    public void update(Game game) {
        if (controls[CONTROLS_FORWARD]) {
            move(game.map, Direction.FORWARD, moveSpeed);
        } 
        else if (controls[CONTROLS_BACKWARD]) {
            move(game.map, Direction.BACKWARD, moveSpeed);
        }
        if (controls[CONTROLS_STRAFELEFT]) {
            move(game.map, Direction.LEFT, moveSpeed);
        }
        else if (controls[CONTROLS_STRAFERIGHT]) {
            move(game.map, Direction.RIGHT, moveSpeed);
        }
        if (controls[CONTROLS_TURNLEFT]) {
            direction.rotate(-turnSpeed);
            direction.normalise();
        } 
        else if (controls[CONTROLS_TURNRIGHT]) {
            direction.rotate(turnSpeed);
            direction.normalise();
        }
        super.update(game);
    }
    
}