# CRYPTO-POO-2025

# ⚔️ CRYPTO LEGENDS: Ultimate Edition

![Version](https://img.shields.io/badge/Version-v6.0-neon)
![Java](https://img.shields.io/badge/Java-JDK%2017%2B-orange)
![Status](https://img.shields.io/badge/Status-Finalizado-success)

**Crypto Legends** es un videojuego de lucha 2D (Fighting Game) desarrollado en Java puro (Swing/AWT). Ambientado en un futuro Cyberpunk donde las transacciones definen el poder, el juego ofrece combates frenéticos 1v1 local, personajes con lore profundo, mecánicas de bloqueo, energía, proyectiles y ataques definitivos.

![Logo Preview](src/logo.png)
*(Asegúrate de que tu imagen logo.png esté en la carpeta src para verla aquí)*

---

## 🎮 Características Principales

* **Combate en Tiempo Real:** Físicas de gravedad, colisiones precisas (Hitbox/Hurtbox) y sistema de combos.
* **5 Luchadores Únicos:** Cada uno con estadísticas diferenciadas (Vida, Velocidad, Salto) y roles específicos (Tanque, Asesino, Rusher, etc.).
* **Sistema de Energía y Furia:**
    * **Energía (Azul):** Se gasta al golpear, disparar o correr. Se regenera con el tiempo.
    * **Furia/Rage (Morada):** Se llena al recibir daño. Al 100% permite lanzar el **ULTIMATE**.
* **Mecánica de Bloqueo Avanzada:** Reduce el 90% del daño, pero tiene "Fatiga". Si bloqueas por más de 3 segundos, quedas aturdido (Stunned).
* **Escenarios Dinámicos:** Fondos animados generados proceduralmente (Cyber City, Forest of Nodes, Blockchain Temple).
* **Audio Espacial:** Sistema de sonido para golpes, saltos, selección y música de fondo.
* **Interfaz (UI) Moderna:** Menús interactivos, selección de personajes con Lore y barras de vida estilo neón.

---

## 🕹️ Controles (Teclado)

El juego está diseñado para **Multijugador Local (1 PC, 2 Jugadores)**.

| Acción | Jugador 1 (Azul) | Jugador 2 (Morado) |
| :--- | :---: | :---: |
| **Moverse** | `W` `A` `S` `D` | `Flechas Dirección` |
| **Saltar** | `W` | `Flecha Arriba` |
| **Puñetazo** | `J` | `Numpad 1` |
| **Patada** | `K` | `Numpad 2` |
| **Bloqueo** | `L` | `Numpad 3` |
| **Proyectil** | `U` | `Numpad 4` |
| **ULTIMATE** | `I` | `Numpad 5` |

* **Pausa:** `ESC` o `P`
* **Reiniciar:** `R` (En pantalla de Game Over o Pausa)
* **Salir al Menú:** `Q` (En pantalla de Game Over o Pausa)

---

## 🏆 Personajes (Roster)

| Personaje | Rol | Habilidad Definitiva (Ultimate) | Descripción |
| :--- | :--- | :--- | :--- |
| **KATT** | *Asesina* | **Glitch Storm** | Hacker veloz con poca vida pero daño y movilidad extremos. |
| **SILVER** | *Tanque* | **Ledger Crush** | Ogra guardiana con 3000 HP. Lenta pero imposible de derribar. |
| **ORION** | *Fighter* | **Plasma Nova** | Soldado del futuro. Balance perfecto entre ataque y defensa. |
| **MAX** | *Rusher* | **Moon Howl** | Hombre lobo agresivo. Saltos muy altos para dominar el aire. |
| **LANCELOT**| *Defensor* | **Excalibur Protocol**| Caballero digital con gran alcance de espada y defensa sólida. |

---

## 📂 Estructura del Proyecto

Para compilar y ejecutar, asegura la siguiente estructura de carpetas:

```text
CryptoLegends/
├── src/
│   ├── main/           # Bucle principal y Ventana
│   ├── entities/       # Lógica del Luchador y Físicas
│   ├── utils/          # Constantes, Audio y Configuración
│   ├── res/            # Archivos de Audio (.wav)
│   └── logo.png        # Imagen del Logo
└── README.md
