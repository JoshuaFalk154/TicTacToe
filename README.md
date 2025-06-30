# Projekt

Simples Tic Tac Toe Spiel, das mit anderen im Browser gespielt werden kann.
## Beschreibung
Tic Tac Toe Spiel, mit den folgenden Eigenschaften
- Zwei Spieler können über den Browser gegeneinander spielen.
- Es gibt eine einfache Spielersuche. Ist kein volles Spiel verfügbar, so wird ein Raum eröffnet in dem gewartet wird, bis ein weiterer Spieler beitritt.
- Während des Spiels wird angezeigt, wer an der Reihe ist und welche Zuweisung (X/O) er hat.
- Am Ende des Spiels wird der Gewinner bekanntgegeben, falls er existiert.
- Das Spiel kann neu gestartet werden.
- Darüber hinaus kann zu jedem Zeitpunkt das Spiel verlassen werden. Der andere Spieler bleibt im Raum und wartet, bis ein neuer Spieler beitritt.

Mögliche Erweiterungen
- Spieler können Namen und (X/O) festlegen.

### Spiel Beitreten
<img src="./images/join-game.png" style="width:60%; height:auto;">

### Auf Spieler warten
<img src="./images/wait-players.png" style="width:60%; height:auto;">

### Spiel
<img src="./images/game-running.png" style="width:60%; height:auto;">

### Spiel vorbei
<img src="./images/end-screen.png" style="width:60%; height:auto;">

## Verwendete Technologien
- Spring Boot
    - Web Socket
- HTML
- CSS
- JavaScript
- Maven

## Programm verwenden
1. Projekt herunterladen und in einer IDE (z.B. Intellij IDEA) öffnen. Das Projekt muss als Maven-Projekt erkannt werden.
2. Da Lombok verwendet wird, muss dies in der IDE konfiguriert werden.
3. Programm ausführen.
4. Öffnen in http://localhost:8080/


