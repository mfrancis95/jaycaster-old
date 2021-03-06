package com.amf.jaycaster.core;

import com.amf.jaycaster.entity.Entity;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class Game implements KeyListener {
    
    public static long tick;
    
    public final long maxTicks;
    
    public final int targetFPS;
    
    public BufferedImage buffer;
    
    public Camera currentCamera;
    
    public List<Entity> entities = new LinkedList<>();
    
    public World world;
    
    public Renderer renderer;
    
    private final Comparator<Entity> comparator = new Comparator<Entity>() {
        
        Vector temp1 = new Vector();
        
        Vector temp2 = new Vector();

        public int compare(Entity e1, Entity e2) {
            temp1.set(e1.position);
            temp1.subtract(currentCamera.position);
            temp2.set(e2.position);
            temp2.subtract(currentCamera.position);
            return (int) (temp2.lengthSquared() - temp1.lengthSquared());
        }
    };
    
    private final Map<String, Object> objects = new HashMap<>();
    
    private final Screen screen = new Screen();
    
    private final Set<ScheduledGameTask> tasks = new TreeSet<>();
    
    private final JFrame window;
    
    private boolean running;
    
    public Game(String title, int windowWidth, int windowHeight, int targetFPS) {
        screen.setPreferredSize(new Dimension(windowWidth, windowHeight));
        window = new JFrame(title);
        window.add(screen);
        window.addKeyListener(this);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.targetFPS = targetFPS;
        maxTicks = 1000 / targetFPS;
    }
    
    public void addObject(String name, Object object) {
        objects.put(name, object);
    }  
    
    public <T> T getObject(String name) {
        return (T) objects.get(name);
    }
    
    public void render(Graphics2D g) {
        g.drawImage(buffer, 0, 0, window.getWidth(), window.getHeight(), null);
    }
    
    public void schedule(GameTask task, long delay, TimeUnit unit) {
        tasks.add(new ScheduledGameTask(task, tick + unit.convert(delay, TimeUnit.SECONDS) * targetFPS));
    }
    
    public void start() {
        if (!running) {
            running = true;
            window.pack();
            Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
            window.setLocation((size.width - window.getWidth()) / 2, (size.height - window.getHeight()) / 2);
            window.setVisible(true);
            new Thread(new Loop()).start();
        }
    }
    
    public void update() {
        for (Iterator<ScheduledGameTask> iterator = tasks.iterator(); iterator.hasNext();) {
            ScheduledGameTask task = iterator.next();
            if (tick < task.tick) {
                break;
            }
            iterator.remove();
            task.task.run(this);
        }
        for (Iterator<Entity> iterator = entities.iterator(); iterator.hasNext();) {
            Entity entity = iterator.next();
            if (entity.removed) {
                world.getTile(entity.position).triggerLeave(this, entity);
                iterator.remove();
            }
            else {
                entity.update(this);
            }
        }
        entities.sort(comparator);
        tick++;
    }
    
    private class Loop implements Runnable {
        
        public void run() {
            while (running) {
                long ticks = System.currentTimeMillis();
                update();
                renderer.render(world, entities, currentCamera);
                screen.repaint();
                ticks = System.currentTimeMillis() - ticks;
                if (ticks < maxTicks) {
                    try {
                        Thread.sleep(maxTicks - ticks);
                    } 
                    catch (InterruptedException ex) {}
                }
            }
        }
        
    }
    
    private class ScheduledGameTask implements Comparable<ScheduledGameTask> {
        
        GameTask task;
        
        long tick;
        
        ScheduledGameTask(GameTask task, long tick) {
            this.task = task;
            this.tick = tick;
        }

        public int compareTo(ScheduledGameTask task) {
            long result = tick - task.tick;
            return result == 0 ? 1 : (int) result;
        }
        
    }
    
    private class Screen extends JPanel {
        
        public void paintComponent(Graphics g) {
            render((Graphics2D) g);
        }
        
    }
    
}