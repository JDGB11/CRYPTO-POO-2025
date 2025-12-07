package combat;

import utils.Constants.AttackData;
import java.awt.geom.Rectangle2D;

public class Ataque {
    private AttackData data;
    private int currentFrame;
    private boolean active; // Si el ataque se está ejecutando
    private Rectangle2D.Float hitbox; // El área de impacto actual

    public Ataque() {
        this.hitbox = new Rectangle2D.Float(0,0,0,0);
        this.active = false;
    }

    public void startAttack(AttackData data) {
        this.data = data;
        this.currentFrame = 0;
        this.active = true;
    }

    // Actualiza el frame del ataque y determina si la hitbox debe aparecer
    public void update(float xPos, float yPos, int direction) {
        if (!active) return;

        currentFrame++;
        // Verificar si terminó la animación del ataque
        if (currentFrame >= data.totalFrames) {
            active = false;
            hitbox.width = 0; // Desactivar hitbox
            return;
        }

        // Verificar si estamos en los "frames activos" donde hace daño
        if (currentFrame >= data.activeStart && currentFrame <= data.activeEnd) {
            float hitX = (direction == 1) ? xPos + data.width : xPos - data.width;
            hitbox.setRect(hitX, yPos + data.offsetY, data.width, data.height);
        } else {
            // Fuera de frames activos (windup o recovery), no hay hitbox
            hitbox.width = 0;
        }
    }

    public boolean isActiveFrame() {
        return active && hitbox.width > 0;
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }
    public boolean isAttacking() { return active; }
    public int getDamage() { return data.damage; }
}
