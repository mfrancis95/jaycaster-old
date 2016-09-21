package com.amf.jaycaster;

import com.amf.jaycaster.Map.LightDirection;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class Renderer {
    
    public boolean onlyWalls;
    
    public int raySkip;
    
    private final double[] cameraTable, floorTable, zBuffer;
    
    private final Bitmap screen;
    
    private final RenderTask[] renderTasks;
    
    private final Vector translated;
    
    private Camera camera;
    
    private Map map;
    
    private double planeX, planeY;
    
    public Renderer(BufferedImage image, int renderTasks) {
        screen = new Bitmap(image);
        cameraTable = new double[screen.width];
        zBuffer = new double[screen.width];
        for (int i = 0; i < screen.width; i++) {
            cameraTable[i] = 2.0 * i / screen.width - 1;
        }
        floorTable = new double[screen.height];
        for (int i = 0; i < screen.height; i++) {
            floorTable[i] = screen.height / (2.0 * i - screen.height);
        }
        this.renderTasks = new RenderTask[renderTasks];
        int pixels = screen.width / renderTasks;
        for (int i = 0; i < renderTasks; i++) {
            this.renderTasks[i] = new RenderTask(i * pixels, i * pixels + pixels);
        }
        translated = new Vector();
    }

    private boolean isShaded(boolean side, double rayDirX, double rayDirY, LightDirection lightDirection) {
        switch (lightDirection) {
            case NORTH:
                return side && rayDirY > 0 || !side;
            case SOUTH:
                return side && rayDirY < 0 || !side;
            case EAST:
                return !side && rayDirX < 0 || side;
            case WEST:
                return !side && rayDirX > 0 || side;
            case NORTHEAST:
                return side && rayDirY > 0 || !side && rayDirX < 0;
            case NORTHWEST:
                return side && rayDirY > 0 || !side && rayDirX > 0;
            case SOUTHEAST:
                return side && rayDirY < 0 || !side && rayDirX < 0;
            default:
                return side && rayDirY < 0 || !side && rayDirX > 0;
        }
    }

    public void render(Map map, List<Entity> entities, Camera camera) {
        this.map = map;
        this.camera = camera;
        if (onlyWalls) {
            Bitmap background = map.background;
            for (int x = 0; x < screen.width; x++) {
                for (int y = 0; y < screen.height; y++) {
                    screen.setPixel(x, y, background.getPixel(x * background.width / screen.width, y * background.height / screen.height));
                }
            }
        }
        planeX = -camera.direction.y;
        planeY = camera.direction.x;
        double planeMagnitude = FastMath.tan(camera.halfFov);
        planeX *= planeMagnitude;
        planeY *= planeMagnitude;
        for (RenderTask renderTask : renderTasks) {
            renderTask.reinitialize();
            renderTask.fork();
        }
        for (RenderTask renderTask : renderTasks) {
            renderTask.join();
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
            double invDet = 1.0 / (planeX * camera.direction.y - camera.direction.x * planeY);

            double transformX = invDet * (camera.direction.y * translated.x - camera.direction.x * translated.y);
            double transformY = invDet * (-planeY * translated.x + planeX * translated.y);

            int entityScreenX = (int) (screen.width / 2 * (1 + transformX / transformY));

            int entityHeight = (int) (screen.height / transformY * entity.scale.y);
            Bitmap sprite = entity.currentAnimation.getFrame();
            int yMove = (int) (entity.yOffset * sprite.height / transformY);
            int drawStartY = -entityHeight / 2 + screen.height / 2 + yMove;
            if (drawStartY < 0) {
                drawStartY = 0;
            }
            int drawEndY = entityHeight / 2 + screen.height / 2 + yMove;
            if (drawEndY >= screen.height) {
                drawEndY = screen.height - 1;
            }
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
                if (transformY > 0 && transformY < zBuffer[x]) {
                    for (int y = drawStartY; y < drawEndY; y++) {
                        int texX = (x - (-entityWidth / 2 + entityScreenX)) * sprite.width / entityWidth;
                        int texY = ((y - yMove) * 2 - screen.height + entityHeight) * sprite.height / 2 / entityHeight;
                        int pixel = entity.effect.affect(sprite, texX, texY);
                        if (pixel != 0) {
                            pixel = Color.blend(pixel, map.ambientColor, map.ambientFactor);
                            if (entity.opacity < 1) {
                                pixel = Color.blend(screen.getPixel(x, y), pixel, entity.opacity);
                            }
                            pixel = map.fog.blendSquared(pixel, distanceSquared);
                            for (int i = 0; i <= raySkip; i++) {
                                screen.setPixel(x + i, y, pixel);
                            }
                        }
                    }
                }
            }
        }
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
                double cameraX = cameraTable[x];
                direction.x = camera.direction.x + planeX * cameraX;
                direction.y = camera.direction.y + planeY * cameraX;
                ray.cast(map, camera.position, direction);
                int lineHeight = (int) (screen.height / ray.distance);
                int drawStart = screen.height / 2 - lineHeight / 2;
                if (drawStart < 0) {
                    drawStart = 0;
                }
                int drawEnd = lineHeight / 2 + screen.height / 2;
                if (drawEnd >= screen.height) {
                    drawEnd = screen.height - 1;
                }
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
                        int pixel = ray.tile.effect.affect(bitmap, bitmapX, (y * 2 - screen.height + lineHeight) * bitmap.height / 2 / lineHeight);
                        if (shaded) {
                            pixel = Color.blend(pixel, 0, 0.5);
                        }
                        pixel = Color.blend(pixel, map.ambientColor, map.ambientFactor);
                        pixel = map.fog.blend(pixel, ray.distance);
                        for (int i = 0; i <= raySkip; i++) {
                            for (int j = 1; j <= map.experimentalHeight; j++) {
                                int heightY = y - (lineHeight - 1) * (j - 1);
                                if (heightY >= 0) {
                                    screen.setPixel(x + i, heightY, pixel);
                                }
                            }
                        }
                    }
                }
                zBuffer[x] = ray.distance;
                if (!onlyWalls) {
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
                    if (drawEnd < 0) {
                        drawEnd = screen.height;
                    }
                    for (int y = drawEnd + 1; y < screen.height; y++) {
                        double weight = floorTable[y] / ray.distance;
                        double currentFloorX = weight * floorXWall + (1 - weight) * camera.position.x;
                        double currentFloorY = weight * floorYWall + (1 - weight) * camera.position.y;
                        Tile tile = map.getTile(currentFloorX, currentFloorY);
                        int pixel;
                        if (tile.backgroundFloor) {
                            Bitmap bitmap = map.background;
                            pixel = bitmap.getPixel(x * bitmap.width / screen.width, y * bitmap.height / screen.height);
                        } 
                        else {
                            Bitmap bitmap = tile.floorBitmap;
                            int floorTexX = (int) (currentFloorX * bitmap.width) % bitmap.width;
                            int floorTexY = (int) (currentFloorY * bitmap.height) % bitmap.height;
                            pixel = tile.effect.affect(bitmap, floorTexX, floorTexY);
                            pixel = Color.blend(pixel, map.ambientColor, map.ambientFactor);
                            if (map.experimentalEffect) {
                                pixel = Color.blend(pixel, 0, weight);
                            }
                            pixel = map.fog.blend(pixel, floorTable[y]);
                        }
                        for (int i = 0; i <= raySkip; i++) {
                            screen.setPixel(x + i, y - 1, pixel);
                        }
                        if (tile.backgroundCeiling) {
                            Bitmap bitmap = map.background;
                            pixel = bitmap.getPixel(x * bitmap.width / screen.width, (screen.height - y) * bitmap.height / screen.height);
                        } 
                        else {
                            Bitmap bitmap = tile.ceilingBitmap;
                            int floorTexX = (int) (currentFloorX * bitmap.width) % bitmap.width;
                            int floorTexY = (int) (currentFloorY * bitmap.height) % bitmap.height;
                            pixel = tile.effect.affect(bitmap, floorTexX, floorTexY);
                            pixel = Color.blend(pixel, map.ambientColor, map.ambientFactor);
                            if (map.experimentalEffect) {
                                pixel = Color.blend(pixel, 0, weight);
                            }
                            pixel = map.fog.blend(pixel, floorTable[y]);
                        }
                        for (int i = 0; i <= raySkip; i++) {
                            int heightY = screen.height - y - lineHeight * (map.experimentalHeight - 1);
                            if (heightY >= 0) {
                                screen.setPixel(x + i, heightY, pixel);
                            }
                        }
                    }
                }
            }
        }

    }
    
}