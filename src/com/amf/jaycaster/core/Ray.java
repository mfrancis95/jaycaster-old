package com.amf.jaycaster.core;

import com.amf.jaycaster.tile.Tile;

public class Ray {
    
    public double distance, wallX;
    
    private boolean hit;
    
    public int mapX, mapY;
    
    public boolean side;
    
    public Tile tile;
    
    private double deltaDistX, deltaDistY, sideDistX, sideDistY;
    
    private int stepX, stepY;
    
    public void cast(Map map, Vector position, Vector direction) {
        mapX = (int) position.x;
        mapY = (int) position.y;
        deltaDistX = Math.sqrt(1 + (direction.y * direction.y) / (direction.x * direction.x));
        deltaDistY = Math.sqrt(1 + (direction.x * direction.x) / (direction.y * direction.y));
        hit = false;
        if (direction.x < 0) {
            stepX = -1;
            sideDistX = (position.x - mapX) * deltaDistX;
        }
        else {
            stepX = 1;
            sideDistX = (mapX + 1f - position.x) * deltaDistX;
        }
        if (direction.y < 0) {
            stepY = -1;
            sideDistY = (position.y - mapY) * deltaDistY;
        } 
        else {
            stepY = 1;
            sideDistY = (mapY + 1f - position.y) * deltaDistY;
        }
        while (!hit) {
            if (sideDistX < sideDistY) {
                sideDistX += deltaDistX;
                mapX += stepX;
                side = false;
            } 
            else {
                sideDistY += deltaDistY;
                mapY += stepY;
                side = true;
            }
            if ((tile = map.getTile(mapX, mapY)).isRaised()) {
                hit = true;
            }
        }
        if (side) {
            distance = (mapY - position.y + (1 - stepY) / 2) / direction.y;
            wallX = position.x + distance * direction.x;
        } 
        else {
            distance = (mapX - position.x + (1 - stepX) / 2) / direction.x;
            wallX = position.y + distance * direction.y;
        }
        wallX -= (int) wallX;
    }
    
}