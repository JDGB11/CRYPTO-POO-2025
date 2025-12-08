package main;

import entities.Luchador;
import utils.Constants;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class GamePanel extends JPanel implements Runnable {
    private Thread thread;
    private Constants.GameState state = Constants.GameState.MENU;
    private Set<Integer> keys = new HashSet<>();
    private Luchador p1, p2;
    private BufferedImage logo;

    private Constants.CharType p1Char, p2Char;
    private Constants.CharType[] chars = Constants.CharType.values();
    private int p1SelIdx = 0, p2SelIdx = 1;
    private boolean p1Ready = false, p2Ready = false;

    private Constants.Stage[] stages = Constants.Stage.values();
    private int stageIdx = 0;
    private Constants.Stage currentStage = Constants.Stage.CYBER_CITY;

    private String[] mainOpts = {"VS MODE", "EXIT"};
    private int menuIdx = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(Constants.GAME_WIDTH, Constants.GAME_HEIGHT));
        setFocusable(true);
        setBackground(Constants.COLOR_BG);

        addKeyListener(new InputHandler());
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { requestFocusInWindow(); }
        });

        try { logo = ImageIO.read(new File("src/logo.png")); } catch (Exception e) {}
    }

    public void startGameThread() { thread = new Thread(this); thread.start(); }

    public void run() {
        while (thread != null) {
            update();
            repaint();
            try { Thread.sleep(1000/Constants.FPS); } catch (Exception e) {}
        }
    }

    private void update() {
        if (state == Constants.GameState.PLAYING) {
            handleInput();
            p1.update();
            p2.update();
            checkCombat();
            if (p1.isDead() || p2.isDead()) state = Constants.GameState.GAME_OVER;
        }
    }

    private void handleInput() {
        // P1
        boolean m1 = false;
        if(keys.contains(KeyEvent.VK_A)) { p1.move(-1); m1=true; } else if(keys.contains(KeyEvent.VK_D)) { p1.move(1); m1=true; }
        if(!m1) p1.stopMoving();
        if(keys.contains(KeyEvent.VK_W)) p1.jump();
        if(keys.contains(KeyEvent.VK_J)) p1.punch(); if(keys.contains(KeyEvent.VK_K)) p1.kick();
        if(keys.contains(KeyEvent.VK_U)) p1.shoot(); if(keys.contains(KeyEvent.VK_I)) p1.special();
        p1.setBlocking(keys.contains(KeyEvent.VK_L));

        // P2
        boolean m2 = false;
        if(keys.contains(KeyEvent.VK_LEFT)) { p2.move(-1); m2=true; } else if(keys.contains(KeyEvent.VK_RIGHT)) { p2.move(1); m2=true; }
        if(!m2) p2.stopMoving();
        if(keys.contains(KeyEvent.VK_UP)) p2.jump();
        if(keys.contains(KeyEvent.VK_NUMPAD1)) p2.punch(); if(keys.contains(KeyEvent.VK_NUMPAD2)) p2.kick();
        if(keys.contains(KeyEvent.VK_NUMPAD4)) p2.shoot(); if(keys.contains(KeyEvent.VK_NUMPAD5)) p2.special();
        p2.setBlocking(keys.contains(KeyEvent.VK_NUMPAD3));
    }

    private void checkCombat() {
        checkHit(p1, p2); checkHit(p2, p1);
        for(Luchador.Projectile p : p1.getProjectiles()) if(p.active && p.getRect().intersects(p2.getHurtbox())) { p2.takeDamage(Constants.PROJ_DMG, false); p.active=false; }
        for(Luchador.Projectile p : p2.getProjectiles()) if(p.active && p.getRect().intersects(p1.getHurtbox())) { p1.takeDamage(Constants.PROJ_DMG, false); p.active=false; }
    }

    private void checkHit(Luchador att, Luchador def) {
        if(!att.getHitbox().isEmpty() && att.getHitbox().intersects(def.getHurtbox())) {
            int dmg = (att.getState()==Constants.PlayerState.KICKING) ? Constants.ATK_KICK.damage : Constants.ATK_PUNCH.damage;
            def.takeDamage(dmg, false);
        }
        if(!att.getSpecialBox().isEmpty() && att.getSpecialBox().intersects(def.getHurtbox())) def.takeDamage(Constants.SPECIAL_DAMAGE, true);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (state == Constants.GameState.MENU) drawMenu(g2);
        else if (state == Constants.GameState.CHAR_SELECT) drawCharSelect(g2);
        else if (state == Constants.GameState.STAGE_SELECT) drawStageSelect(g2);
        else if (state == Constants.GameState.CONTROLS_INFO) drawControlsInfo(g2);
        else {
            drawStageBackground(g2);
            p1.render(g2);
            p2.render(g2);
            drawInGameHUD(g2);
            if(state == Constants.GameState.PAUSED) drawOverlay(g2, "PAUSED");
            if(state == Constants.GameState.GAME_OVER) drawOverlay(g2, p1.isDead() ? p2.getName()+" WINS!" : p1.getName()+" WINS!");
        }
    }

    // --- ESCENARIOS ---
    private void drawStageBackground(Graphics2D g2) {
        if (currentStage == Constants.Stage.CYBER_CITY) {
            g2.setColor(new Color(15, 10, 35)); g2.fillRect(0,0,getWidth(), getHeight());
            g2.setColor(new Color(30, 30, 60));
            for(int i=50; i<1200; i+=150) {
                int h = 200 + (i%300); g2.fillRect(i, Constants.FLOOR_Y - h, 100, h);
                g2.setColor(new Color(255, 255, 0, 100));
                for(int w=0; w<h; w+=40) if((i+w)%3==0) g2.fillRect(i+20, Constants.FLOOR_Y - h + w, 10, 20);
                g2.setColor(new Color(30, 30, 60));
            }
        } else if (currentStage == Constants.Stage.FOREST_OF_NODES) {
            g2.setColor(new Color(5, 25, 10)); g2.fillRect(0,0,getWidth(), getHeight());
            g2.setColor(new Color(0, 80, 40));
            for(int i=20; i<1200; i+=200) { g2.fillPolygon(new int[]{i, i+60, i+120}, new int[]{Constants.FLOOR_Y, Constants.FLOOR_Y-300, Constants.FLOOR_Y}, 3); }
        } else {
            g2.setColor(new Color(40, 0, 0)); g2.fillRect(0,0,getWidth(), getHeight());
            g2.setColor(new Color(80, 40, 0)); g2.fillRect(200, 200, 800, 400);
            g2.setColor(Color.ORANGE); g2.drawRect(250, 250, 700, 350);
        }
        if(logo != null) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            g2.drawImage(logo, 300, 100, 600, 300, null);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
        g2.setColor(Color.BLACK); g2.fillRect(0, Constants.FLOOR_Y, getWidth(), getHeight()-Constants.FLOOR_Y);
        g2.setColor(Constants.NEON_CYAN); g2.drawLine(0, Constants.FLOOR_Y, getWidth(), Constants.FLOOR_Y);
    }

    private void drawInGameHUD(Graphics2D g2) {
        g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(3));
        g2.drawOval(getWidth()-50, 20, 30, 30);
        g2.setFont(new Font("Arial", Font.BOLD, 10)); g2.drawString("ESC", getWidth()-45, 65);
    }

    private void drawMenu(Graphics2D g2) {
        if(logo != null) g2.drawImage(logo, 400, 50, 400, 200, null);
        else drawCenteredText(g2, "CRYPTO FIGHTERS", 150, 60, Constants.NEON_CYAN);
        for(int i=0; i<mainOpts.length; i++) {
            g2.setColor(i==menuIdx ? Color.YELLOW : Color.GRAY);
            drawCenteredText(g2, mainOpts[i], 350 + i*60, 40, null);
        }
    }

    private void drawCharSelect(Graphics2D g2) {
        g2.setColor(Constants.COLOR_BG); g2.fillRect(0,0,getWidth(), getHeight());
        drawCenteredText(g2, "SELECT YOUR HERO", 50, 40, Color.WHITE);
        drawCharCard(g2, p1SelIdx, 100, 100, Constants.NEON_CYAN, "P1", p1Ready);
        drawCharCard(g2, p2SelIdx, 700, 100, Constants.NEON_PURPLE, "P2", p2Ready);
    }

    private void drawCharCard(Graphics2D g2, int idx, int x, int y, Color c, String label, boolean ready) {
        Constants.CharType ch = chars[idx];
        g2.setColor(Constants.TEXT_BOX_BG); g2.fillRoundRect(x, y, 400, 500, 20, 20);
        g2.setColor(c); g2.setStroke(new BasicStroke(3)); g2.drawRoundRect(x, y, 400, 500, 20, 20);
        g2.setFont(new Font("Impact", Font.PLAIN, 30)); g2.drawString(label + ": " + ch.name(), x+20, y+40);
        if(ready) { g2.setColor(Color.GREEN); g2.drawString("READY!", x+300, y+40); }
        Luchador.drawPreview(g2, ch, x + 200, y + 250, 3);

        g2.setColor(Color.WHITE); g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.drawString("Ult: " + ch.ultName, x+20, y+300);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2.setColor(Color.LIGHT_GRAY);
        // Lore
        drawStringMultiLine(g2, ch.lore, 360, x+20, y+330);

        g2.setColor(Constants.NEON_GREEN);
        g2.drawString("CLASS: " + ch.role, x+20, y+420);
    }

    // Función para escribir Lore en varias líneas
    private void drawStringMultiLine(Graphics2D g, String text, int lineWidth, int x, int y) {
        FontMetrics m = g.getFontMetrics();
        if(m.stringWidth(text) < lineWidth) { g.drawString(text, x, y); }
        else {
            String[] words = text.split(" ");
            String currentLine = words[0];
            for(int i = 1; i < words.length; i++) {
                if(m.stringWidth(currentLine + " " + words[i]) < lineWidth) {
                    currentLine += " " + words[i];
                } else {
                    g.drawString(currentLine, x, y);
                    y += m.getHeight();
                    currentLine = words[i];
                }
            }
            g.drawString(currentLine, x, y);
        }
    }

    private void drawStageSelect(Graphics2D g2) {
        g2.setColor(Constants.COLOR_BG); g2.fillRect(0,0,getWidth(), getHeight());
        drawCenteredText(g2, "SELECT STAGE", 100, 40, Color.WHITE);
        g2.setColor(Color.YELLOW); drawCenteredText(g2, "< " + stages[stageIdx].name + " >", 300, 50, null);
        g2.setColor(Color.LIGHT_GRAY); g2.setFont(new Font("SansSerif", Font.PLAIN, 20));
        drawCenteredText(g2, stages[stageIdx].desc, 350, 20, null);
    }

    private void drawControlsInfo(Graphics2D g2) {
        g2.setColor(Constants.COLOR_BG); g2.fillRect(0,0,getWidth(), getHeight());
        drawCenteredText(g2, "BATTLE CONTROLS", 100, 50, Color.WHITE);
        int col1 = 300, col2 = 800;
        g2.setFont(new Font("Arial", Font.BOLD, 25));
        g2.setColor(Constants.NEON_CYAN); g2.drawString("PLAYER 1", col1, 200);
        g2.setColor(Constants.NEON_PURPLE); g2.drawString("PLAYER 2", col2, 200);
        g2.setColor(Color.WHITE); g2.setFont(new Font("Consolas", Font.PLAIN, 18));
        int y = 250, gap = 40;
        g2.drawString("WASD", col1, y); g2.drawString("ARROWS", col2, y);
        g2.drawString("J - PUNCH", col1, y+gap); g2.drawString("NUM 1 - PUNCH", col2, y+gap);
        g2.drawString("K - KICK", col1, y+gap*2); g2.drawString("NUM 2 - KICK", col2, y+gap*2);
        g2.drawString("U - SHOOT", col1, y+gap*3); g2.drawString("NUM 4 - SHOOT", col2, y+gap*3);
        g2.drawString("L - BLOCK", col1, y+gap*4); g2.drawString("NUM 3 - BLOCK", col2, y+gap*4);
        g2.setColor(Color.YELLOW);
        g2.drawString("I - ULTIMATE", col1, y+gap*5); g2.drawString("NUM 5 - ULTIMATE", col2, y+gap*5);
        drawCenteredText(g2, "PRESS [SPACE] TO FIGHT", 600, 30, Color.GREEN);
    }

    private void drawOverlay(Graphics2D g2, String txt) {
        g2.setColor(new Color(0,0,0,200)); g2.fillRect(0,0,getWidth(), getHeight());
        drawCenteredText(g2, txt, 300, 60, Color.WHITE);
        drawCenteredText(g2, "[R] RESTART   [Q] MENU", 400, 20, Color.LIGHT_GRAY);
    }

    private void drawCenteredText(Graphics2D g, String text, int y, int size, Color c) {
        if(c!=null) g.setColor(c);
        g.setFont(new Font("Impact", Font.PLAIN, size));
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    private class InputHandler extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            int k = e.getKeyCode();
            keys.add(k);
            if (state == Constants.GameState.MENU) {
                if(k==KeyEvent.VK_W || k==KeyEvent.VK_UP) menuIdx = (menuIdx-1+mainOpts.length)%mainOpts.length;
                if(k==KeyEvent.VK_S || k==KeyEvent.VK_DOWN) menuIdx = (menuIdx+1)%mainOpts.length;
                if(k==KeyEvent.VK_ENTER) {
                    if(menuIdx==0) state = Constants.GameState.CHAR_SELECT;
                    else System.exit(0);
                }
            }
            else if (state == Constants.GameState.CHAR_SELECT) {
                if(!p1Ready) {
                    if(k==KeyEvent.VK_A) p1SelIdx = (p1SelIdx-1+chars.length)%chars.length;
                    if(k==KeyEvent.VK_D) p1SelIdx = (p1SelIdx+1)%chars.length;
                    if(k==KeyEvent.VK_SPACE) { p1Ready=true; p1Char=chars[p1SelIdx]; }
                }
                if(!p2Ready) {
                    if(k==KeyEvent.VK_LEFT) p2SelIdx = (p2SelIdx-1+chars.length)%chars.length;
                    if(k==KeyEvent.VK_RIGHT) p2SelIdx = (p2SelIdx+1)%chars.length;
                    if(k==KeyEvent.VK_ENTER) { p2Ready=true; p2Char=chars[p2SelIdx]; }
                }
                if(p1Ready && p2Ready) state = Constants.GameState.STAGE_SELECT;
            }
            else if (state == Constants.GameState.STAGE_SELECT) {
                if(k==KeyEvent.VK_A || k==KeyEvent.VK_LEFT) stageIdx = (stageIdx-1+stages.length)%stages.length;
                if(k==KeyEvent.VK_D || k==KeyEvent.VK_RIGHT) stageIdx = (stageIdx+1)%stages.length;
                if(k==KeyEvent.VK_ENTER || k==KeyEvent.VK_SPACE) {
                    currentStage = stages[stageIdx];
                    state = Constants.GameState.CONTROLS_INFO;
                }
            }
            else if (state == Constants.GameState.CONTROLS_INFO) {
                if(k==KeyEvent.VK_SPACE || k==KeyEvent.VK_ENTER) {
                    p1 = new Luchador(p1Char, 200, Constants.FLOOR_Y, 1);
                    p2 = new Luchador(p2Char, 900, Constants.FLOOR_Y, 2);
                    p2.move(-1);
                    state = Constants.GameState.PLAYING;
                }
            }
            else if (state == Constants.GameState.PAUSED || state == Constants.GameState.GAME_OVER) {
                if(k==KeyEvent.VK_R) {
                    p1 = new Luchador(p1Char, 200, Constants.FLOOR_Y, 1);
                    p2 = new Luchador(p2Char, 900, Constants.FLOOR_Y, 2);
                    p2.move(-1);
                    state = Constants.GameState.PLAYING;
                }
                if(k==KeyEvent.VK_Q) { state = Constants.GameState.MENU; p1Ready=false; p2Ready=false; }
                if(k==KeyEvent.VK_ESCAPE && state==Constants.GameState.PAUSED) state=Constants.GameState.PLAYING;
            }
            else if (state == Constants.GameState.PLAYING) {
                if(k==KeyEvent.VK_ESCAPE) state = Constants.GameState.PAUSED;
            }
        }
        public void keyReleased(KeyEvent e) { keys.remove(e.getKeyCode()); }
    }
}