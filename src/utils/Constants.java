package utils;

import java.awt.Color;

public class Constants {
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;
    
    // TIEMPO POR TURNO (Blitz)
    public static final int TURN_TIME_LIMIT = 5; 

    public enum GameState {
        MENU, PLAYING, GAME_OVER
    }

    public enum BattleTurn {
        PLAYER_TURN, ENEMY_TURN
    }

    // Datos visuales del ataque
    public static class AttackData {
        public String name;
        public int damage;
        public Color color;

        public AttackData(String name, int damage, Color color) {
            this.name = name;
            this.damage = damage;
            this.color = color;
        }
    }
}
