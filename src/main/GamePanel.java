package main;

import entities.Luchador;
import inputs.KeyboardInputs;
import utils.CollisionManager;
import utils.Constants;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class GamePanel extends JPanel implements Runnable {

    private Thread gameThread;
    private KeyboardInputs keyInputs;
    private Luchador player1;
    private Luchador player2;

    public GamePanel() {
        keyInputs = new KeyboardInputs();
        addKeyListener(keyInputs);
        setFocusable(true);
        requestFocusInWindow();
        setPreferredSize(new Dimension(Constants.GAME_WIDTH, Constants.GAME_HEIGHT));
        initClasses();
    }

    private void initClasses() {
        // Inicializamos dos luchadores con diferentes controles y colores
        player1 = new Luchador(100, 200, Constants.P1_CONTROLS, Color.BLUE);
        player2 = new Luchador(600, 200, Constants.P2_CONTROLS, Color.RED);
        // Invertir dirección inicial del P2
        player2.move(-1); 
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    // --- BUCLE PRINCIPAL DEL JUEGO (GAME LOOP) ---
    @Override
    public void run() {
        double timePerFrame = 1000000000.0 / Constants.FPS_SET;
        long lastFrame = System.nanoTime();
        long now = System.nanoTime();

        while (true) {
            now = System.nanoTime();
            if (now - lastFrame >= timePerFrame) {
                update();
                repaint();
                lastFrame = now;
            }
        }
    }

    // --- UPDATE: Lógica del juego ---
    public void update() {
        // 1. Procesar Inputs para cada jugador según su configuración
        handlePlayerInput(player1);
        handlePlayerInput(player2);

        // 2. Actualizar estados individuales (movimiento, gravedad, ataque)
        player1.update();
        player2.update();

        // 3. Resolver Colisiones Físicas (Entorno y empuje entre jugadores)
        CollisionManager.checkFloorCollision(player1);
        CollisionManager.checkFloorCollision(player2);
        CollisionManager.checkWallCollision(player1);
        CollisionManager.checkWallCollision(player2);
        CollisionManager.checkPlayerPushCollision(player1, player2);

        // 4. Resolver Combate (Hitboxes vs Hurtboxes)
        checkCombatHits();
    }
    
    private void handlePlayerInput(Luchador p) {
        p.processInput(
            keyInputs.isPressed(p.getControls().UP),
            keyInputs.isPressed(p.getControls().DOWN),
            keyInputs.isPressed(p.getControls().LEFT),
            keyInputs.isPressed(p.getControls().RIGHT),
            keyInputs.isPressed(p.getControls().ATK_H),
            keyInputs.isPressed(p.getControls().ATK_L),
            keyInputs.isPressed(p.getControls().ATK_S)
        );
    }

    private void checkCombatHits() {
        // Verificar si P1 golpea a P2
        if(CollisionManager.checkAttackHit(player1, player2)) {
            if(player2.getState() != Constants.State.HIT && player2.getState() != Constants.State.DEAD) {
                player2.takeDamage(player1.getCurrentAttack().getDamage());
            }
        }
        // Verificar si P2 golpea a P1
        if(CollisionManager.checkAttackHit(player2, player1)) {
             if(player1.getState() != Constants.State.HIT && player1.getState() != Constants.State.DEAD) {
                player1.takeDamage(player2.getCurrentAttack().getDamage());
             }
        }
    }

    // --- DRAW: Renderizado ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Fondo y suelo básico
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, (int)Constants.FLOOR_Y, Constants.GAME_WIDTH, (int)(GAME_HEIGHT - Constants.FLOOR_Y));

        // Dibujar personajes
        player1.render(g);
        player2.render(g);
    }
}
