package entities;

import combat.Ataque;
import utils.Constants;
import utils.Constants.Controls;
import utils.Constants.State;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Luchador extends Personaje {

    private Controls controls;
    private Ataque currentAttack;
    private Color bodyColor;

    public Luchador(float x, float y, Controls controls, Color color) {
        super(x, y, 50, 100); // Tamaño del luchador 50x100
        this.controls = controls;
        this.bodyColor = color;
        this.currentAttack = new Ataque();
    }

    @Override
    public void update() {
        super.update();
        updateCombat();
    }

    private void updateCombat() {
        // Si está atacando, actualizamos la lógica del ataque
        if (state == State.ATTACKING) {
            currentAttack.update(x, y, direction);
            if (!currentAttack.isAttacking()) {
                state = State.IDLE; // Volver a IDLE cuando termina el ataque
            }
        }
    }

    // Sistema de input para este luchador específico
    public void processInput(boolean up, boolean down, boolean left, boolean right, boolean atkH, boolean atkL, boolean atkS) {
        if (state == State.DEAD || state == State.HIT) return;

        // Prioridad al ataque. Si ataca, no se mueve.
        if (state != State.ATTACKING) {
            if (left) move(-Constants.WALK_SPEED);
            if (right) move(Constants.WALK_SPEED);
            if (up) jump();
            if (!left && !right && !inAir) state = State.IDLE;

            // Trigger de ataques
            if (atkH) performAttack(Constants.ATK_HIGH);
            else if (atkL) performAttack(Constants.ATK_LOW);
            else if (atkS) performAttack(Constants.ATK_SPECIAL);
        }
    }

    private void performAttack(Constants.AttackData data) {
        state = State.ATTACKING;
        currentAttack.startAttack(data);
    }

    // --- RENDERIZADO (Matriz Gráfica Básica) ---
    @Override
    public void render(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        // 1. Dibujar cuerpo (Hurtbox)
        g2.setColor(bodyColor);
        if(state == State.HIT) g2.setColor(Color.WHITE); // Flash blanco al ser golpeado
        g2.fill(hitbox);

        // 2. Dibujar indicador de dirección (ojo)
        g2.setColor(Color.GREEN);
        int eyeX = (direction == 1) ? (int)(x + width - 10) : (int)x;
        g2.fillRect(eyeX, (int)y + 10, 10, 10);

        // 3. Dibujar Hitbox de ataque activo (Si existe)
        if (currentAttack.isActiveFrame()) {
            g2.setColor(Color.YELLOW);
            g2.fill(currentAttack.getHitbox());
        }
        
        // 4. Barra de vida simple encima
        g2.setColor(Color.RED);
        g2.fillRect((int)x, (int)y - 20, 50, 5);
        g2.setColor(Color.GREEN);
        g2.fillRect((int)x, (int)y - 20, (int)(50 * ((float)currentHealth/maxHealth)), 5);
    }

    public Controls getControls() { return controls; }
    @Override
    public Ataque getCurrentAttack() { return currentAttack; }
}
