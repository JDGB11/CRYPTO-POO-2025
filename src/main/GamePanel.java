package main;

import entities.Luchador;
import utils.Constants;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {

    private Thread gameThread;
    private Constants.GameState gameState = Constants.GameState.MENU;
    private Constants.BattleTurn turn = Constants.BattleTurn.PLAYER_TURN;
    
    private Luchador player;
    private Luchador enemy;
    private BufferedImage logo;
    
    // Temporizador
    private long turnStart;
    private int timeLeft;
    private String log = "¡A PELEAR!";

    // Botones (Zonas de clic)
    private Rectangle btnPlay = new Rectangle(300, 400, 200, 50);
    private Rectangle[] btnAtk = new Rectangle[3];

    public GamePanel() {
        this.setPreferredSize(new Dimension(Constants.GAME_WIDTH, Constants.GAME_HEIGHT));
        this.setFocusable(true);
        
        // Cargar Logo
        try { logo = ImageIO.read(new File("src/logo.png")); } 
        catch (IOException e) { System.out.println("No se halló logo.png"); }

        initBattle();

        // Mouse Listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                checkClick(e.getX(), e.getY());
            }
        });
    }

    private void initBattle() {
        player = new Luchador("CryptoHero", 100, 150, 300, Color.BLUE);
        enemy = new Luchador("BearMarket", 100, 550, 150, Color.RED);
        
        for(int i=0; i<3; i++) {
            btnAtk[i] = new Rectangle(50 + (i*220), 500, 200, 60);
        }
        resetTimer();
    }

    private void resetTimer() {
        turnStart = System.currentTimeMillis();
        timeLeft = Constants.TURN_TIME_LIMIT;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while(true) {
            update();
            repaint();
            try { Thread.sleep(16); } catch(Exception e) {}
        }
    }

    private void update() {
        if(gameState == Constants.GameState.PLAYING) {
            long now = System.currentTimeMillis();
            timeLeft = Constants.TURN_TIME_LIMIT - (int)((now - turnStart)/1000);
            
            if(timeLeft <= 0) {
                log = "¡Tiempo fuera! Cambio de turno.";
                changeTurn();
            }

            if(turn == Constants.BattleTurn.ENEMY_TURN) {
                if(Math.random() > 0.98) {
                    int dmg = 15;
                    player.takeDamage(dmg);
                    log = "Enemigo ataca: -" + dmg + " HP";
                    changeTurn();
                }
            }

            if(player.isDead()) { log = "PERDISTE..."; gameState = Constants.GameState.GAME_OVER; }
            if(enemy.isDead()) { log = "¡GANASTE!"; gameState = Constants.GameState.GAME_OVER; }
        }
    }

    private void changeTurn() {
        turn = (turn == Constants.BattleTurn.PLAYER_TURN) ? Constants.BattleTurn.ENEMY_TURN : Constants.BattleTurn.PLAYER_TURN;
        resetTimer();
    }

    private void checkClick(int x, int y) {
        if(gameState == Constants.GameState.MENU) {
            if(btnPlay.contains(x,y)) {
                gameState = Constants.GameState.PLAYING;
                resetTimer();
            }
        } 
        else if(gameState == Constants.GameState.PLAYING && turn == Constants.BattleTurn.PLAYER_TURN) {
            for(int i=0; i<3; i++) {
                if(btnAtk[i].contains(x,y)) {
                    int dmg = player.getAttacks().get(i).damage;
                    enemy.takeDamage(dmg);
                    log = "Usaste Atq " + (i+1) + ": -" + dmg + " HP";
                    changeTurn();
                }
            }
        }
        else if(gameState == Constants.GameState.GAME_OVER) {
            initBattle();
            gameState = Constants.GameState.MENU;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(gameState == Constants.GameState.MENU) {
            g2.setColor(new Color(20,20,50));
            g2.fillRect(0,0, getWidth(), getHeight());
            if(logo != null) g2.drawImage(logo, 200, 50, 400, 250, null);
            
            g2.setColor(Color.GREEN);
            g2.fill(btnPlay);
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 30));
            g2.drawString("JUGAR", 350, 435);
        } 
        else if(gameState == Constants.GameState.PLAYING) {
            player.render(g2);
            enemy.render(g2);
            
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 480, 800, 120);
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString(log, 50, 450);
            
            g2.setColor(timeLeft < 2 ? Color.RED : Color.CYAN);
            g2.fillOval(350, 20, 80, 80);
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 40));
            g2.drawString(""+timeLeft, 375, 75);

            if(turn == Constants.BattleTurn.PLAYER_TURN) {
                for(int i=0; i<3; i++) {
                    g2.setColor(player.getAttacks().get(i).color);
                    g2.fill(btnAtk[i]);
                    g2.setColor(Color.BLACK);
                    g2.draw(btnAtk[i]);
                    g2.drawString(player.getAttacks().get(i).name, btnAtk[i].x+20, btnAtk[i].y+35);
                }
            }
        } 
        else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0,0,800,600);
            g2.setColor(Color.WHITE);
            g2.drawString(log, 300, 300);
            g2.drawString("Clic para Menu", 300, 350);
        }
    }
}
