package entities;

import utils.Constants;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Luchador {
    private String name;
    private Constants.CharType type;
    private Color color;
    private float x, y, velY;
    private int direction = 1;

    private int maxHealth, currentHealth;
    private int maxEnergy = 200, currentEnergy = 200;
    private int currentRage = 0;

    private Constants.PlayerState state = Constants.PlayerState.IDLE;
    private boolean inAir = false;
    private int animTick = 0;
    private int blinkTimer = 0;
    private int blockTimer = 0;
    private boolean blockCooldown = false;

    private Rectangle2D.Float hitbox = new Rectangle2D.Float();
    private Rectangle2D.Float hurtbox = new Rectangle2D.Float();
    private List<Projectile> projectiles = new ArrayList<>();
    private Rectangle2D.Float specialBox = new Rectangle2D.Float();

    public class Projectile {
        public float px, py; public int dir; public boolean active=true;
        public Projectile(float x, float y, int dir) { px=x; py=y; this.dir=dir; }
        public void update() { px += 25 * dir; }
        public void render(Graphics2D g2) {
            g2.setColor(type == Constants.CharType.ORION ? Color.CYAN : Color.YELLOW);
            g2.fillOval((int)px, (int)py, 20, 20);
            g2.setColor(Color.WHITE); g2.fillOval((int)px+5, (int)py+5, 10, 10);
        }
        public Rectangle2D.Float getRect() { return new Rectangle2D.Float(px, py, 20, 20); }
    }

    public Luchador(Constants.CharType type, int x, int y, int playerNum) {
        this.type = type;
        this.name = type.name();
        this.x = x; this.y = y;
        this.maxHealth = type.maxHp;
        this.currentHealth = this.maxHealth;
        this.color = (playerNum == 1) ? Constants.NEON_CYAN : Constants.NEON_PURPLE;
        if(type == Constants.CharType.MAX) this.color = Color.LIGHT_GRAY;
        if(type == Constants.CharType.SILVER) this.color = new Color(0, 180, 120);
    }

    public void update() {
        if (state == Constants.PlayerState.DEAD) return;
        updatePhysics();
        updateCombat();
        updateProjectiles();

        // Regenerar energía pasiva
        if (state != Constants.PlayerState.BLOCKING && state != Constants.PlayerState.SHOOTING && animTick%10==0 && currentEnergy < maxEnergy) {
            currentEnergy++;
        }

        if (state == Constants.PlayerState.BLOCKING) {
            blockTimer++;
            if (blockTimer >= Constants.BLOCK_MAX_DURATION) {
                blockCooldown = true; state = Constants.PlayerState.STUNNED; animTick = 0;
            }
        } else {
            if (blockTimer > 0) blockTimer--;
            if (blockTimer == 0) blockCooldown = false;
        }
        hurtbox.setRect(x - 25, y - 90, 50, 90);
    }

    private void updatePhysics() {
        if (y < Constants.FLOOR_Y) { velY += Constants.GRAVITY; inAir = true; }
        else if (y >= Constants.FLOOR_Y && velY > 0) {
            y = Constants.FLOOR_Y; velY = 0; inAir = false;
            if(state == Constants.PlayerState.JUMPING) state = Constants.PlayerState.IDLE;
        }
        y += velY;
        if (x < 30) x = 30; if (x > Constants.GAME_WIDTH - 30) x = Constants.GAME_WIDTH - 30;
    }

    private void updateCombat() {
        animTick++;
        hitbox.setRect(0,0,0,0);
        specialBox.setRect(0,0,0,0);

        if (state == Constants.PlayerState.PUNCHING) handleMelee(Constants.ATK_PUNCH);
        else if (state == Constants.PlayerState.KICKING) handleMelee(Constants.ATK_KICK);
        else if (state == Constants.PlayerState.SHOOTING && animTick > 20) state = Constants.PlayerState.IDLE;
        else if (state == Constants.PlayerState.SPECIAL) {
            if (animTick > 30 && animTick < 60) {
                float w = 800;
                float bx = (direction == 1) ? x + 30 : x - 30 - w;
                specialBox.setRect(bx, y - 100, w, 100);
            }
            if (animTick > 80) { state = Constants.PlayerState.IDLE; currentRage = 0; }
        }
        else if ((state == Constants.PlayerState.HIT || state == Constants.PlayerState.STUNNED) && animTick > 25) state = Constants.PlayerState.IDLE;
    }

    private void handleMelee(Constants.AttackData atk) {
        if (animTick >= atk.start && animTick <= atk.end) {
            float hx = (direction == 1) ? x + 20 : x - 20 - atk.range;
            hitbox.setRect(hx, y - 70, atk.range, 50);
        }
        if (animTick >= atk.dur) state = Constants.PlayerState.IDLE;
    }

    private void updateProjectiles() {
        projectiles.removeIf(p -> { p.update(); return p.px < 0 || p.px > Constants.GAME_WIDTH || !p.active; });
    }

    // --- ACCIONES BLINDADAS ---
    public void move(int dir) {
        if(canAct()) {
            direction = dir; x += dir * type.speed;
            // Solo cambiamos a WALKING si estamos en IDLE (para no interrumpir ataques)
            if(!inAir && state == Constants.PlayerState.IDLE) state = Constants.PlayerState.WALKING;
        }
    }

    public void stopMoving() {
        if (!inAir && state == Constants.PlayerState.WALKING) {
            state = Constants.PlayerState.IDLE;
        }
    }

    public void jump() { if(!inAir && canAct()) { velY = type.jumpForce; state = Constants.PlayerState.JUMPING; inAir=true; } }

    public void punch() {
        if(canAct()) {
            System.out.println(name + " PUNCH!"); // DEBUG
            state=Constants.PlayerState.PUNCHING; currentEnergy-=5; animTick=0;
        }
    }

    public void kick() {
        if(canAct()) {
            System.out.println(name + " KICK!"); // DEBUG
            state=Constants.PlayerState.KICKING; currentEnergy-=10; animTick=0;
        }
    }

    public void shoot() { if(canAct() && currentEnergy>=20) { state=Constants.PlayerState.SHOOTING; currentEnergy-=20; animTick=0; projectiles.add(new Projectile(x+(direction*30), y-60, direction)); } }
    public void special() { if(canAct() && currentRage>=Constants.MAX_RAGE) { state=Constants.PlayerState.SPECIAL; animTick=0; } }

    public void setBlocking(boolean b) {
        if(state != Constants.PlayerState.STUNNED && state != Constants.PlayerState.DEAD && !isAttacking()) {
            state = b ? Constants.PlayerState.BLOCKING : Constants.PlayerState.IDLE;
        }
    }

    public void takeDamage(int dmg, boolean unblockable) {
        if (state == Constants.PlayerState.DEAD) return;
        if (currentRage < Constants.MAX_RAGE) currentRage += Constants.RAGE_GAIN_PER_HIT;
        if (currentRage > Constants.MAX_RAGE) currentRage = Constants.MAX_RAGE;
        if (!unblockable && state == Constants.PlayerState.BLOCKING) { dmg = (int)(dmg * (1.0f-Constants.BLOCK_DAMAGE_REDUCTION)); }
        else { state = Constants.PlayerState.HIT; animTick = 0; blinkTimer = 10; x -= direction * 10; }
        currentHealth -= dmg;
        if (currentHealth <= 0) { currentHealth = 0; state = Constants.PlayerState.DEAD; }
    }

    // Simplificado para evitar bloqueos
    public boolean canAct() {
        return state == Constants.PlayerState.IDLE || state == Constants.PlayerState.WALKING;
    }

    public boolean isAttacking() {
        return state == Constants.PlayerState.PUNCHING || state == Constants.PlayerState.KICKING || state == Constants.PlayerState.SHOOTING || state == Constants.PlayerState.SPECIAL;
    }

    // --- RENDERIZADO ---
    public void render(Graphics2D g2) {
        for(Projectile p : projectiles) p.render(g2);

        if(state == Constants.PlayerState.SPECIAL && animTick > 30 && animTick < 60) {
            g2.setColor(new Color(255, 255, 255, 200));
            g2.fill(specialBox);
            g2.setColor(type == Constants.CharType.ORION ? Color.CYAN : Color.RED);
            g2.draw(specialBox);
        }

        if(blinkTimer > 0) { g2.setColor(Color.WHITE); blinkTimer--; } else g2.setColor(color);
        drawStickman(g2, (int)x, (int)y, 1.0f);
        drawHUD(g2);
    }

    public static void drawPreview(Graphics2D g2, Constants.CharType cType, int px, int py, int scale) {
        g2.setColor(Color.WHITE);
        if(cType == Constants.CharType.MAX) g2.setColor(Color.LIGHT_GRAY);
        if(cType == Constants.CharType.SILVER) g2.setColor(new Color(0, 180, 120));
        Luchador temp = new Luchador(cType, px, py, 1);
        temp.drawStickman(g2, px, py, scale);
    }

    private void drawStickman(Graphics2D g2, int cx, int cy, float scale) {
        Stroke mainStroke = new BasicStroke(5 * scale, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(mainStroke);

        int headY = cy - (int)(90 * scale);
        int headSize = (int)(30 * scale);
        int bodyTopY = headY + (int)(15 * scale);
        int hipY = cy - (int)(40 * scale);

        double time = System.currentTimeMillis() * 0.015;
        int walkSwing = (inAir || state == Constants.PlayerState.WALKING) ? (int)(Math.sin(time) * 15 * scale) : 0;
        int armSwing = (state == Constants.PlayerState.WALKING) ? (int)(Math.cos(time) * 15 * scale) : 0;

        // MIEMBROS TRASEROS
        Color baseC = g2.getColor();
        g2.setColor(baseC.darker().darker());
        g2.drawLine(cx, hipY, cx - (int)(10*scale) - walkSwing, cy);
        int shoY = headY + (int)(20*scale);
        if (state != Constants.PlayerState.PUNCHING) {
            g2.drawLine(cx, shoY, cx + (int)(direction*10*scale) - armSwing, shoY + (int)(25*scale));
        }

        // CUERPO Y CABEZA
        g2.setColor(baseC);
        g2.drawLine(cx, bodyTopY, cx, hipY);
        g2.drawOval(cx - headSize/2, headY - headSize/2, headSize, headSize);
        if(type==Constants.CharType.MAX) { g2.drawLine(cx-(int)(10*scale), headY-(int)(10*scale), cx-(int)(15*scale), headY-(int)(25*scale)); g2.drawLine(cx+(int)(10*scale), headY-(int)(10*scale), cx+(int)(15*scale), headY-(int)(25*scale)); }
        if(type==Constants.CharType.LANCELOT) g2.drawLine(cx-(int)(15*scale), headY, cx+(int)(15*scale), headY);

        // MIEMBROS DELANTEROS
        if(state==Constants.PlayerState.KICKING) {
            g2.drawLine(cx, hipY, cx + (int)(direction*50*scale), cy - (int)(40*scale));
        } else {
            g2.drawLine(cx, hipY, cx + (int)(10*scale) + walkSwing, cy);
        }

        if(type == Constants.CharType.LANCELOT) {
            g2.setColor(Color.GREEN); g2.setStroke(new BasicStroke(3*scale));
            if(state==Constants.PlayerState.PUNCHING) g2.drawLine(cx, shoY, cx+(int)(direction*80*scale), shoY);
            else g2.drawLine(cx, shoY, cx+(int)(direction*30*scale), shoY-(int)(30*scale));
            g2.setStroke(mainStroke);
        } else {
            if(state==Constants.PlayerState.PUNCHING) g2.drawLine(cx, shoY, cx+(int)(direction*40*scale), shoY);
            else g2.drawLine(cx, shoY, cx + (int)(direction*10*scale) + armSwing, shoY + (int)(25*scale));
        }
    }

    private void drawHUD(Graphics2D g2) {
        int bx = (int)x-50; int by = (int)y-130;
        g2.setColor(Color.RED); g2.fillRect(bx, by, 100, 8);
        g2.setColor(Color.GREEN); g2.fillRect(bx, by, (int)(100*((float)currentHealth/maxHealth)), 8);
        g2.setColor(Color.BLACK); g2.drawRect(bx, by, 100, 8);
        g2.setColor(Color.GRAY); g2.fillRect(bx, by+10, 100, 4);
        g2.setColor(Color.MAGENTA); g2.fillRect(bx, by+10, (int)(100*((float)currentRage/Constants.MAX_RAGE)), 4);
        g2.setColor(Color.WHITE); g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString(name, bx, by-5);
    }

    public Rectangle2D.Float getHitbox() { return hitbox; }
    public Rectangle2D.Float getSpecialBox() { return specialBox; }
    public Rectangle2D.Float getHurtbox() { return hurtbox; }
    public List<Projectile> getProjectiles() { return projectiles; }
    public Constants.PlayerState getState() { return state; }
    public boolean isDead() { return state == Constants.PlayerState.DEAD; }
    public String getName() { return name; }
}