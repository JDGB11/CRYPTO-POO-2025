package entities;

import utils.Constants;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Luchador {
    private String name;
    private int maxHealth, currentHealth;
    private int x, y;
    private Color colorPersonaje;
    private List<Constants.AttackData> attacks;

    public Luchador(String name, int maxHealth, int x, int y, Color color) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.x = x;
        this.y = y;
        this.colorPersonaje = color;
        this.attacks = new ArrayList<>();
        
        // Cargar ataques
        attacks.add(new Constants.AttackData("Golpe Alto", 15, Color.YELLOW));
        attacks.add(new Constants.AttackData("Golpe Bajo", 10, Color.ORANGE));
        attacks.add(new Constants.AttackData("Especial", 30, Color.MAGENTA));
    }

    public void takeDamage(int dmg) {
        this.currentHealth -= dmg;
        if (currentHealth < 0) currentHealth = 0;
    }

    public boolean isDead() { return currentHealth <= 0; }
    public List<Constants.AttackData> getAttacks() { return attacks; }
    public String getName() { return name; }

    public void render(Graphics2D g2) {
        // Cuerpo
        g2.setColor(colorPersonaje);
        g2.fillRoundRect(x, y, 90, 130, 15, 15);
        
        // Sombra
        g2.setColor(new Color(0,0,0,50));
        g2.fillOval(x, y+120, 90, 20);

        // Barra de Vida
        int barW = 120;
        int barX = x - 15;
        int barY = y - 30;
        
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(barX, barY, barW, 10); // Fondo
        
        float hpPercent = (float)currentHealth / maxHealth;
        if(hpPercent > 0.5) g2.setColor(Color.GREEN);
        else if(hpPercent > 0.2) g2.setColor(Color.ORANGE);
        else g2.setColor(Color.RED);
        
        g2.fillRect(barX, barY, (int)(barW * hpPercent), 10); // Vida
        
        g2.setColor(Color.BLACK);
        g2.drawRect(barX, barY, barW, 10); // Borde
        
        // Texto Info
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString(name + " (" + currentHealth + ")", barX, barY - 5);
    }
}
