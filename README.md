# Tab Canvas (Fabric, Minecraft 26.3)

Ein Block, der wie eine Leinwand an der Wand haengt. Baust du 3 x 3 Bloecke
nebeneinander/uebereinander (alle mit gleicher Ausrichtung), zeigt jede Kachel
einen Spieler aus dem Tab-Menue mit Skin-Kopf und Namen (alphabetisch, bis zu 9).

## Bauen
Voraussetzung: JDK 25.

    ./gradlew wrapper --gradle-version 9.6.0   # einmalig, erzeugt den Gradle-Wrapper
    ./gradlew build                            # Jar liegt in build/libs/
    ./gradlew runClient                        # zum Testen

## Benutzen
Crafting: Eisenbarren-Rahmen (8x) um eine Glasscheibe. Auch im Kreativmenue
unter "Funktionale Bloecke". An eine Wand klicken zum Platzieren.
Kachel-Position = Position in der zusammenhaengenden Wand modulo 3,
oben links ist Spieler 1.

## Hinweis
Der Code wurde ohne Compiler-Test geschrieben (kein Netzwerk fuer Gradle in der
Entwicklungsumgebung). Kleine API-Namensabweichungen in 26.3 sind moeglich -
Fehlermeldungen von `./gradlew build` zeigen genau, was anzupassen ist.
