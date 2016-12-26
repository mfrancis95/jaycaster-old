package com.amf.jaycaster.core;

import com.amf.jaycaster.graphics.Bitmap;
import com.amf.jaycaster.graphics.Color;
import com.amf.jaycaster.entity.Entity;
import com.amf.jaycaster.tile.Tile;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class Renderer {
    
    public boolean onlyWalls;
    
    public int raySkip;
    
    private final double[] cameraTable, zBuffer;
    
    private final boolean[] rendered;
    
    private final Bitmap screen;
    
    private final RenderTask[] renderTasks;
    
    private final Vector plane, translated;
    
    private Camera camera;
    
    private Map map;
    
    public Renderer(BufferedImage image, int renderTasks) {
        screen = new Bitmap(image);
        cameraTable = new double[screen.width];
        zBuffer = new double[screen.width];
        for (int i = 0; i < screen.width; i++) {
            cameraTable[i] = 2.0 * i / screen.width - 1;
        }
        this.renderTasks = new RenderTask[renderTasks];
        int pixels = screen.width / renderTasks;
        for (int i = 0; i < renderTasks - 1; i++) {
            this.renderTasks[i] = new RenderTask(i * pixels, i * pixels + pixels);
        }
        this.renderTasks[renderTasks - 1] = new RenderTask((renderTasks - 1) * pixels, screen.width);
        plane = new Vector();
        rendered = new boolean[screen.width];
        translated = new Vector();
    }

    private boolean isShaded(boolean side, double rayDirX, double rayDirY, int lightDirection) {
        switch (lightDirection) {
            case Map.DIRECTION_NORTH:
                return side && rayDirY > 0 || !side;
            case Map.DIRECTION_SOUTH:
                return side && rayDirY < 0 || !side;
            case Map.DIRECTION_EAST:
                return !side && rayDirX < 0 || side;
            case Map.DIRECTION_WEST:
                return !side && rayDirX > 0 || side;
            case Map.DIRECTION_NORTHEAST:
                return side && rayDirY > 0 || !side && rayDirX < 0;
            case Map.DIRECTION_NORTHWEST:
                return side && rayDirY > 0 || !side && rayDirX > 0;
            case Map.DIRECTION_SOUTHEAST:
                return side && rayDirY < 0 || !side && rayDirX < 0;
            default:
                return side && rayDirY < 0 || !side && rayDirX > 0;
        }
    }

    public void render(Map map, List<Entity> entities, Camera camera) {
        this.map = map;
        this.camera = camera;
        plane.x = -camera.direction.y;
        plane.y = camera.direction.x;
        double planeMagnitude = FastMath.tan(camera.halfFov);
        plane.scale(planeMagnitude);
        for (RenderTask renderTask : renderTasks) {
            renderTask.reinitialize();
            renderTask.fork();
        }
        for (Entity entity : entities) {
            if (!entity.visible) {
                continue;
            }
            translated.set(entity.position);
            translated.subtract(camera.position);
            if (translated.x + translated.y == 0) {
                continue;
            }
            double distanceSquared = translated.lengthSquared();
            if (camera.nearClippingSquared > distanceSquared || distanceSquared > entity.viewDistanceSquared) {
                continue;
            }
            double invDet = 1.0 / (plane.x * camera.direction.y - camera.direction.x * plane.y);

            double transformX = invDet * (camera.direction.y * translated.x - camera.direction.x * translated.y);
            double transformY = invDet * (-plane.y * translated.x + plane.x * translated.y);

            int entityScreenX = (int) (screen.width / 2 * (1 + transformX / transformY));

            int entityHeight = (int) (screen.height / transformY * entity.scale.y);
            Bitmap bitmap = entity.currentAnimation.getFrame();
            double yOffset = entity.yOffset + camera.height;
            int drawStartY = (int) (screen.height * camera.pitch - entityHeight * (1 - yOffset));
            int drawEndY = (int) (entityHeight * yOffset + screen.height * camera.pitch);
            int entityWidth = (int) (screen.height / transformY * entity.scale.x);
            int drawStartX = -entityWidth / 2 + entityScreenX;
            if (drawStartX < 0) {
                drawStartX = 0;
            }
            int drawEndX = entityWidth / 2 + entityScreenX;
            if (drawEndX >= screen.width) {
                drawEndX = screen.width - 1;
            }
            for (int x = drawStartX; x < drawEndX; x++) {
                while (!rendered[x]) {
                    Thread.yield();
                }
                if (transformY > 0 && transformY < zBuffer[x]) {
                    for (int y = Math.max(0, drawStartY); y < Math.min(screen.height, drawEndY); y++) {
                        int texX = (x - (-entityWidth / 2 + entityScreenX)) * bitmap.width / entityWidth;
                        int texY = (int) (bitmap.height * ((y - drawStartY) / (entityHeight + 1.0)));
                        int pixel = entity.effect.affect(bitmap, texX, texY);
                        if (pixel != 0) {
                            Tile tile = map.getTile(entity.position);
                            pixel = Color.blend(pixel, tile.lighting.color, tile.lighting.alpha);
                            pixel = map.fog.blendSquared(pixel, distanceSquared);
                            if (entity.opacity < 1) {
                                pixel = Color.blend(screen.getPixel(x, y), pixel, entity.opacity);
                            }                            
                            for (int i = 0; i <= raySkip; i++) {
                                screen.setPixel(x + i, y, pixel);
                            }
                        }
                    }
                }
            }
        }
        for (RenderTask renderTask : renderTasks) {
            renderTask.join();
        }
        Arrays.fill(rendered, false);
    }
    
    private class RenderTask extends RecursiveAction {
        
        final Vector direction;
        
        final Ray ray;
        
        final int startX, endX;
        
        RenderTask(int startX, int endX) {
            this.startX = startX;
            this.endX = endX;
            direction = new Vector();
            ray = new Ray();
        }

        protected void compute() {
            for (int x = startX; x < endX; x += 1 + raySkip) {
                direction.set(plane);
                direction.scale(cameraTable[x]);
                direction.add(camera.direction);
                ray.cast(map, camera.position, direction);
                zBuffer[x] = ray.distance;
                int lineHeight = (int) (screen.height / ray.distance);
                int drawStart = (int) (screen.height * camera.pitch - lineHeight * (1 - camera.height));
                int drawEnd = (int) (screen.height * camera.pitch + lineHeight * camera.height);
                //Render walls
                if (ray.tile.backgroundWall) {
                    Bitmap bitmap = map.background;
                    int bitmapX = x * bitmap.width / screen.width;
                    for (int y = drawStart; y < drawEnd; y++) {
                        for (int i = 0; i <= raySkip; i++) {
                            for (int j = 1; j <= map.experimentalHeight; j++) {
                                int heightY = y - (lineHeight - 1) * (j - 1);
                                if (heightY >= 0) {
                                    int pixel = bitmap.getPixel(bitmapX, heightY * bitmap.height / screen.height);
                                    screen.setPixel(x + i, heightY, pixel);
                                }
                            }
                        }
                    }
                }
                else {
                    boolean shaded = isShaded(ray.side, direction.x, direction.y, map.lightDirection);
                    Bitmap bitmap = ray.tile.wallBitmap;
                    int bitmapX = (int) (bitmap.width * ray.wallX);
                    if (!ray.side && direction.x > 0 || ray.side && direction.y < 0) {
                        bitmapX = bitmap.width - bitmapX - 1;
                    }
                    for (int y = drawStart; y < drawEnd; y++) {
                        int pixel = ray.tile.effect.affect(bitmap, bitmapX, (int) (bitmap.height * ((y - drawStart) / (lineHeight + 1.0))));
                        if (shaded) {
                            pixel = Color.blend(pixel, 0, 0.5);
                        }
                        pixel = Color.blend(pixel, ray.tile.lighting.color, ray.tile.lighting.alpha);
                        pixel = map.fog.blend(pixel, ray.distance);
                        for (int i = 0; i <= raySkip; i++) {
                            for (int j = 1; j <= map.experimentalHeight; j++) {
                                int heightY = y - (lineHeight - 1) * (j - 1);
                                if (heightY >= 0 && heightY < screen.height) {
                                    screen.setPixel(x + i, heightY, pixel);
                                }
                            }
                        }
                    }
                }
                //Render floors and ceilings
                double floorXWall, floorYWall;
                if (!ray.side && direction.x > 0) {
                    floorXWall = ray.mapX;
                    floorYWall = ray.mapY + ray.wallX;
                } 
                else if (!ray.side && direction.x < 0) {
                    floorXWall = ray.mapX + 1.0;
                    floorYWall = ray.mapY + ray.wallX;
                } 
                else if (ray.side && direction.y > 0) {
                    floorXWall = ray.mapX + ray.wallX;
                    floorYWall = ray.mapY;
                } 
                else {
                    floorXWall = ray.mapX + ray.wallX;
                    floorYWall = ray.mapY + 1.0;
                }
                double diffY = screen.height * camera.height;
                diffY -= screen.height * camera.pitch;
                for (int y = drawEnd + 1; y < screen.height; y++) {
                    double distance = screen.height / ((y + diffY) / camera.height - screen.height);
                    double weight = distance / ray.distance;
                    weight -= Math.floor(weight);
                    double currentFloorX = weight * floorXWall + (1 - weight) * camera.position.x;
                    double currentFloorY = weight * floorYWall + (1 - weight) * camera.position.y;
                    Tile tile = map.getTile(currentFloorX, currentFloorY);
                    int pixel;
                    if (onlyWalls || tile.backgroundFloor) {
                        Bitmap bitmap = map.background;
                        pixel = bitmap.getPixel(x * bitmap.width / screen.width, y * bitmap.height / screen.height);
                    } 
                    else {
                        Bitmap bitmap = tile.floorBitmap;
                        int floorTexX = (int) (currentFloorX * bitmap.width) % bitmap.width;
                        int floorTexY = (int) (currentFloorY * bitmap.height) % bitmap.height;
                        pixel = tile.effect.affect(bitmap, floorTexX, floorTexY);
                        pixel = Color.blend(pixel, tile.lighting.color, tile.lighting.alpha);
                        if (map.experimentalEffect) {
                            pixel = Color.blend(pixel, 0, weight);
                        }
                        pixel = map.fog.blend(pixel, Math.abs(distance));
                    }
                    for (int i = 0; i <= raySkip; i++) {
                        int heightY = y - 1;
                        if (heightY >= 0 && heightY < screen.height) {
                            screen.setPixel(x + i, heightY, pixel);
                        }
                    }
                }            
                for (int y = 0; y <= drawStart - lineHeight * (map.experimentalHeight - 1); y++) {
                    double distance = screen.height / ((screen.height - (y + diffY)) / (1 - camera.height) - screen.height);
                    double weight = distance / ray.distance;
                    weight -= Math.floor(weight);
                    double currentCeilingX = weight * floorXWall + (1 - weight) * camera.position.x;
                    double currentCeilingY = weight * floorYWall + (1 - weight) * camera.position.y;
                    Tile tile = map.getTile(currentCeilingX, currentCeilingY);
                    int pixel;
                    if (onlyWalls || tile.backgroundCeiling) {
                        Bitmap bitmap = map.background;
                        pixel = bitmap.getPixel(x * bitmap.width / screen.width, y * bitmap.height / screen.height);
                    } 
                    else {
                        Bitmap bitmap = tile.ceilingBitmap;
                        int floorTexX = (int) (currentCeilingX * bitmap.width) % bitmap.width;
                        int floorTexY = (int) (currentCeilingY * bitmap.height) % bitmap.height;
                        pixel = tile.effect.affect(bitmap, floorTexX, floorTexY);
                        pixel = Color.blend(pixel, tile.lighting.color, tile.lighting.alpha);
                        if (map.experimentalEffect) {
                            pixel = Color.blend(pixel, 0, weight);
                        }
                        pixel = map.fog.blend(pixel, Math.abs(distance));
                    }
                    for (int i = 0; i <= raySkip; i++) {
                        int heightY = y - 1;
                        if (heightY >= 0 && heightY < screen.height) {
                            screen.setPixel(x + i, heightY, pixel);
                        }
                    }
                }
                rendered[x] = true;
            }
        }

    }
    
}