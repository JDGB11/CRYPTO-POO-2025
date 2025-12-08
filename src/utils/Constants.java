package utils;

import java.awt.Color;

public class Constants {
    // --- CONFIGURACIÓN GENERAL ---
    public static final String GAME_VERSION = "vFinal - Crypto Legends";
    public static final int GAME_WIDTH = 1200;
    public static final int GAME_HEIGHT = 700;
    public static final int FLOOR_Y = 600;
    public static final int FPS = 60;

    // --- COLORES ---
    public static final Color COLOR_BG = new Color(10, 15, 30);
    public static final Color NEON_CYAN = new Color(0, 255, 255);
    public static final Color NEON_PURPLE = new Color(180, 0, 255);
    public static final Color NEON_GREEN = new Color(50, 255, 50);
    public static final Color TEXT_BOX_BG = new Color(0, 0, 0, 220);

    // --- FÍSICAS ---
    public static final float GRAVITY = 0.5f;

    // --- ESTADOS ---
    public enum GameState { MENU, CHAR_SELECT, STAGE_SELECT, CONTROLS_INFO, PLAYING, PAUSED, GAME_OVER }
    public enum PlayerState { IDLE, WALKING, JUMPING, PUNCHING, KICKING, SHOOTING, SPECIAL, BLOCKING, HIT, STUNNED, DEAD }

    // --- ESCENARIOS ---
    public enum Stage {
        CYBER_CITY("Cyber City", "La capital financiera donde las transacciones nunca duermen."),
        FOREST_OF_NODES("Forest of Nodes", "Un bosque antiguo reclamado por la fibra óptica."),
        BLOCKCHAIN_TEMPLE("Blockchain Temple", "Ruinas sagradas que guardan el bloque génesis.");

        public String name, desc;
        Stage(String n, String d) { name=n; desc=d; }
    }

    // --- PERSONAJES (LORE COMPLETO) ---
    public enum CharType {
        KATT(1700, 9, 17, "Glitch Storm",
                "Una hacker 'Sombrero Blanco' que descubrió una conspiración en la red. Lucha para liberar el código fuente de las megacorporaciones.",
                "ASESINA: Muy rápida, doble salto, pero poca salud."),

        SILVER(3000, 3, 10, "Ledger Crush",
                "La Ogra Guardiana de la Bóveda Central. Fue mutada por un virus de minería de datos y ahora protege los activos con fuerza bruta.",
                "TANQUE: Lenta, gigantesca y muy difícil de derribar."),

        ORION(2200, 6, 14, "Plasma Nova",
                "Un soldado enviado desde el año 2099 para asegurar la estabilidad de la moneda del futuro. Su tecnología no es de este tiempo.",
                "LUCHADOR: Balance perfecto entre ataque y defensa."),

        MAX(1900, 8, 16, "Moon Howl",
                "Un experimento genético financiado con Dogecoin que salió mal. Escapó del laboratorio y busca su manada en la arena digital.",
                "RUSHER: Agresivo, saltos altos y ataques salvajes."),

        LANCELOT(2400, 5, 12, "Excalibur Protocol",
                "Un antiguo antivirus que cobró conciencia y adoptó la forma de un caballero digital. Busca honor y justicia en cada byte.",
                "DEFENSOR: Gran alcance con su espada y gran bloqueo.");

        public int maxHp, speed;
        public float jumpForce;
        public String ultName, lore, role;

        CharType(int hp, int spd, float jmp, String ult, String l, String r) {
            maxHp=hp; speed=spd; jumpForce=-jmp; ultName=ult; lore=l; role=r;
        }
    }

    // --- BALANCE DE COMBATE ---
    public static final int MAX_RAGE = 100;
    public static final int RAGE_GAIN_PER_HIT = 10; // Carga un poco más lento
    public static final int SPECIAL_DAMAGE = 700;   // Daño fuerte (aprox 30% HP), NO mata de una (antes 1000+)

    public static final int BLOCK_MAX_DURATION = 180;
    public static final float BLOCK_DAMAGE_REDUCTION = 0.90f;
    public static final int PROJ_DMG = 30;

    public static class AttackData {
        public int damage, cost, dur, start, end, range;
        public AttackData(int d, int c, int du, int s, int e, int r) {
            damage=d; cost=c; dur=du; start=s; end=e; range=r;
        }
    }

    public static final AttackData ATK_PUNCH = new AttackData(35, 5, 20, 5, 12, 60);
    public static final AttackData ATK_KICK = new AttackData(50, 10, 30, 10, 20, 80);
}