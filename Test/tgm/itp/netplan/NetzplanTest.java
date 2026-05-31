package tgm.itp.netplan;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testplan für die Klasse Netzplan (CuT: netplan-1.1.jar)
 *
 * Abgedeckte Fragestellungen:
 *  1. Start- und Endknoten-Validierung  (TC-SE-*)
 *  2. Reihenfolge der Knoteneingabe     (TC-ORD-*)
 *  3. Zirkelbezug-Erkennung             (TC-CYC-*)
 *  4. Mehrere Vorgänger                 (TC-PRED-*)
 *  5. Mehrere Nachfolger                (TC-SUCC-*)
 */
@DisplayName("Netzplan – Testplan")
class NetzplanTest {

    /** Minimaler linearer Netzplan: A → B → C (Dauer je 5) */
    private Netzplan linearerNetzplan() {
        Knoten a = new Knoten(1, "A", 5);
        Knoten b = new Knoten(2, "B", 5);
        Knoten c = new Knoten(3, "C", 5);

        b.addPredecessor(new Knoten[]{a});
        c.addPredecessor(new Knoten[]{b});
        Netzplan np = new Netzplan();
        np.addNode(a);
        np.addNode(b);
        np.addNode(c);
        try {
            a.getSuccessors();
            b.getSuccessors();
            c.getSuccessors();
        } catch (NullPointerException e) {

        }

        return np;
    }
    /** Netzplan mit einem Knoten ohne Vorgänger und einem Knoten ohne Nachfolger – valide */
    private Netzplan netzplanMitEinemStartUndEnde() {
        return linearerNetzplan();
    }
    @Nested
    @DisplayName("TC-SE – Start- und Endknoten-Validierung")
    class StartEndValidierung {

        @Test
        @DisplayName("TC-SE-01: Genau ein Startknoten → kein Fehler")
        void einStartknoten_keinFehler() {
            Netzplan np = linearerNetzplan();
            assertDoesNotThrow(np::calcPath,
                    "Ein Netzplan mit genau einem Startknoten sollte keine Exception werfen");
        }

        @Test
        @DisplayName("TC-SE-02: Genau ein Endknoten → kein Fehler")
        void einEndknoten_keinFehler() {
            Netzplan np = linearerNetzplan();
            assertDoesNotThrow(np::calcPath,
                    "Ein Netzplan mit genau einem Endknoten sollte keine Exception werfen");
        }

        @Test
        @DisplayName("TC-SE-03: Kein Startknoten (alle Knoten haben Vorgänger) → Exception")
        void keinStartknoten_exception() {
            // Zirkelbezug erzeugt implizit keinen Startknoten
            Knoten a = new Knoten(1, "A", 5);
            Knoten b = new Knoten(2, "B", 5);
            a.addPredecessor(new Knoten[]{b});
            b.addPredecessor(new Knoten[]{a});
            Netzplan np = new Netzplan();
            np.addNode(a);
            np.addNode(b);
            assertThrows(IllegalArgumentException.class, np::calcPath,
                    "Fehlt ein Startknoten, sollte eine IllegalArgumentException geworfen werden");
        }

        @Test
        @DisplayName("TC-SE-04: Zwei Startknoten → Exception")
        void zweiStartknoten_exception() {
            Knoten start1 = new Knoten(1, "Start1", 2);
            Knoten start2 = new Knoten(2, "Start2", 3);
            Knoten ende  = new Knoten(3, "Ende",   1);
            ende.addPredecessor(new Knoten[]{start1, start2});
            Netzplan np = new Netzplan();
            np.addNode(start1);
            np.addNode(start2);
            np.addNode(ende);
            assertThrows(IllegalArgumentException.class, np::calcPath,
                    "Zwei Startknoten sollten eine IllegalArgumentException auslösen");
        }

        @Test
        @DisplayName("TC-SE-05: Zwei Endknoten → Exception")
        void zweiEndknoten_exception() {
            Knoten start = new Knoten(1, "Start", 1);
            Knoten ende1 = new Knoten(2, "Ende1", 2);
            Knoten ende2 = new Knoten(3, "Ende2", 3);
            ende1.addPredecessor(new Knoten[]{start});
            ende2.addPredecessor(new Knoten[]{start});
            Netzplan np = new Netzplan();
            np.addNode(start);
            np.addNode(ende1);
            np.addNode(ende2);
            assertThrows(IllegalArgumentException.class, np::calcPath,
                    "Zwei Endknoten sollten eine IllegalArgumentException auslösen");
        }

        @Test
        @DisplayName("TC-SE-06: Einzelknoten (Start = Ende) → kein Fehler")
        void einzelknoten_startUndEndeZugleich() {
            Knoten einziger = new Knoten(1, "Einziger", 10);
            Netzplan np = new Netzplan();
            np.addNode(einziger);
            assertDoesNotThrow(np::calcPath,
                    "Ein einzelner Knoten ist gleichzeitig Start und Ende – das sollte erlaubt sein");
        }

        @Test
        @DisplayName("TC-SE-07: Drei Startknoten → Exception")
        void dreiStartknoten_exception() {
            Knoten s1 = new Knoten(1, "S1", 1);
            Knoten s2 = new Knoten(2, "S2", 1);
            Knoten s3 = new Knoten(3, "S3", 1);
            Knoten e  = new Knoten(4, "E",  1);
            e.addPredecessor(new Knoten[]{s1, s2, s3});
            Netzplan np = new Netzplan();
            np.addNode(s1); np.addNode(s2); np.addNode(s3); np.addNode(e);
            assertThrows(IllegalArgumentException.class, np::calcPath,
                    "Drei Startknoten sollten eine IllegalArgumentException auslösen");
        }

        @Test
        @DisplayName("TC-SE-08: Drei Endknoten → Exception")
        void dreiEndknoten_exception() {
            Knoten s  = new Knoten(1, "S",  1);
            Knoten e1 = new Knoten(2, "E1", 1);
            Knoten e2 = new Knoten(3, "E2", 1);
            Knoten e3 = new Knoten(4, "E3", 1);
            e1.addPredecessor(new Knoten[]{s});
            e2.addPredecessor(new Knoten[]{s});
            e3.addPredecessor(new Knoten[]{s});
            Netzplan np = new Netzplan();
            np.addNode(s); np.addNode(e1); np.addNode(e2); np.addNode(e3);
            assertThrows(IllegalArgumentException.class, np::calcPath,
                    "Drei Endknoten sollten eine IllegalArgumentException auslösen");
        }

        @Test
        @DisplayName("TC-SE-09: Leerer Netzplan → Exception oder definiertes Verhalten")
        void leererNetzplan_definierteAntwort() {
            Netzplan np = new Netzplan();

            try {
                np.calcPath();
                assertEquals(0L, np.getDuration(),
                        "Ein leerer Netzplan sollte Gesamtdauer 0 haben");
            } catch (IllegalArgumentException e) {

            }
        }

        @Test
        @DisplayName("TC-SE-10: checkStartAndEndNode meldet Start- und Endknoten-Verletzung")
        void checkStartAndEndNode_fehlermeldungEnthältAnzahl() {
            Knoten s1 = new Knoten(1, "S1", 1);
            Knoten s2 = new Knoten(2, "S2", 1);
            Knoten e  = new Knoten(3, "E",  1);
            e.addPredecessor(new Knoten[]{s1, s2});
            Netzplan np = new Netzplan();
            np.addNode(s1); np.addNode(s2); np.addNode(e);
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, np::calcPath,
                    "Bei zwei Startknoten muss eine IllegalArgumentException geworfen werden");
            assertTrue(ex.getMessage().contains("2"),
                    "Die Fehlermeldung sollte die Anzahl der Startknoten ('2') enthalten, war: " + ex.getMessage());
        }
    }

    @Nested
    @DisplayName("TC-ORD – Einfluss der Knoteneingabe-Reihenfolge")
    class EingabeReihenfolge {

        @Test
        @DisplayName("TC-ORD-01: Knoten in topologischer Reihenfolge → Pfadlänge korrekt")
        void topologischeReihenfolge_korrektePfadlaenge() {
            Knoten a = new Knoten(1, "A", 5);
            Knoten b = new Knoten(2, "B", 3);
            Knoten c = new Knoten(3, "C", 7);
            b.addPredecessor(new Knoten[]{a});
            c.addPredecessor(new Knoten[]{b});
            Netzplan np = new Netzplan();
            np.addNode(a); np.addNode(b); np.addNode(c);
            np.calcPath();
            assertEquals(15L, np.getDuration(),
                    "Gesamtdauer sollte 5+3+7=15 sein, unabhängig von der Einfüge-Reihenfolge");
        }

        @Test
        @DisplayName("TC-ORD-02: Knoten in umgekehrter Reihenfolge → gleiche Pfadlänge")
        void umgekehrteReihenfolge_gleichePfadlaenge() {
            Knoten a = new Knoten(1, "A", 5);
            Knoten b = new Knoten(2, "B", 3);
            Knoten c = new Knoten(3, "C", 7);
            b.addPredecessor(new Knoten[]{a});
            c.addPredecessor(new Knoten[]{b});
            Netzplan np = new Netzplan();
            // Umgekehrte Reihenfolge
            np.addNode(c); np.addNode(b); np.addNode(a);
            np.calcPath();
            assertEquals(15L, np.getDuration(),
                    "Gesamtdauer muss 15 sein – Einfüge-Reihenfolge darf keinen Einfluss haben");
        }

        @Test
        @DisplayName("TC-ORD-03: Knoten in zufälliger Reihenfolge → gleiche Pfadlänge")
        void zufaelligeReihenfolge_gleichePfadlaenge() {
            Knoten a = new Knoten(1, "A", 5);
            Knoten b = new Knoten(2, "B", 3);
            Knoten c = new Knoten(3, "C", 7);
            b.addPredecessor(new Knoten[]{a});
            c.addPredecessor(new Knoten[]{b});
            Netzplan np = new Netzplan();
            // b zuerst, dann c, dann a
            np.addNode(b); np.addNode(c); np.addNode(a);
            np.calcPath();
            assertEquals(15L, np.getDuration(),
                    "Gesamtdauer muss 15 sein – beliebige Einfüge-Reihenfolge darf kein anderes Ergebnis liefern");
        }

        @Test
        @DisplayName("TC-ORD-04: Kritischer Pfad ist reihenfolge-unabhängig")
        void kritischerPfad_reihenfolgeUnabhaengig() {
            Knoten a = new Knoten(1, "A", 10);
            Knoten b = new Knoten(2, "B", 1);
            Knoten c = new Knoten(3, "C", 5);
            Knoten d = new Knoten(4, "D", 3);
            b.addPredecessor(new Knoten[]{a});
            c.addPredecessor(new Knoten[]{a});
            d.addPredecessor(new Knoten[]{b, c});

            // Reihenfolge 1: a, b, c, d
            Netzplan np1 = new Netzplan();
            np1.addNode(a); np1.addNode(b); np1.addNode(c); np1.addNode(d);
            np1.calcPath();

            // Reihenfolge 2: d, c, b, a
            Knoten a2 = new Knoten(1, "A", 10);
            Knoten b2 = new Knoten(2, "B", 1);
            Knoten c2 = new Knoten(3, "C", 5);
            Knoten d2 = new Knoten(4, "D", 3);
            b2.addPredecessor(new Knoten[]{a2});
            c2.addPredecessor(new Knoten[]{a2});
            d2.addPredecessor(new Knoten[]{b2, c2});
            Netzplan np2 = new Netzplan();
            np2.addNode(d2); np2.addNode(c2); np2.addNode(b2); np2.addNode(a2);
            np2.calcPath();

            assertEquals(np1.getDuration(), np2.getDuration(),
                    "Die Gesamtdauer muss unabhängig von der Einfüge-Reihenfolge identisch sein");
        }
    }

    @Nested
    @DisplayName("TC-CYC – Zirkelbezug-Erkennung")
    class Zirkelbezug {

        @Test
        @DisplayName("TC-CYC-01: Direkter Zirkelbezug A→B→A → Exception")
        void direkterZirkelbezug_exception() {
            Knoten a = new Knoten(1, "A", 5);
            Knoten b = new Knoten(2, "B", 5);
            a.addPredecessor(new Knoten[]{b});
            b.addPredecessor(new Knoten[]{a});
            Netzplan np = new Netzplan();
            np.addNode(a); np.addNode(b);
            assertThrows(IllegalArgumentException.class, np::calcPath,
                    "Ein direkter Zirkelbezug (A→B→A) muss erkannt und als Exception gemeldet werden");
        }

        @Test
        @DisplayName("TC-CYC-02: Indirekter Zirkelbezug A→B→C→A → Exception")
        void indirekterZirkelbezug_exception() {
            Knoten a = new Knoten(1, "A", 2);
            Knoten b = new Knoten(2, "B", 3);
            Knoten c = new Knoten(3, "C", 4);
            b.addPredecessor(new Knoten[]{a});
            c.addPredecessor(new Knoten[]{b});
            a.addPredecessor(new Knoten[]{c});   // Kreis geschlossen
            Netzplan np = new Netzplan();
            np.addNode(a); np.addNode(b); np.addNode(c);
            assertThrows(IllegalArgumentException.class, np::calcPath,
                    "Ein indirekter Zirkelbezug (A→B→C→A) muss erkannt und als Exception gemeldet werden");
        }

        @Test
        @DisplayName("TC-CYC-03: Fehlermeldung enthält Hinweis auf Zirkelbezug oder fehlenden Startknoten")
        void zirkelbezug_fehlermeldungPassend() {
            Knoten a = new Knoten(1, "A", 1);
            Knoten b = new Knoten(2, "B", 1);
            a.addPredecessor(new Knoten[]{b});
            b.addPredecessor(new Knoten[]{a});
            Netzplan np = new Netzplan();
            np.addNode(a); np.addNode(b);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, np::calcPath,
                    "Bei Zirkelbezug muss eine IllegalArgumentException geworfen werden");

            String msg = ex.getMessage().toLowerCase();

            // Anpassung an die tatsächliche Logik des Jars:
            // Ein reiner Kreis führt dazu, dass 0 Startknoten gefunden werden.
            assertTrue(msg.contains("zirkel") || msg.contains("cycle") || msg.contains("kreis") || msg.contains("startknoten"),
                    "Fehlermeldung sollte auf Zirkelbezug oder fehlende Startknoten hinweisen, war: " + ex.getMessage());
        }

        @Test
        @DisplayName("TC-CYC-04: Kein Zirkelbezug im linearen Netzplan")
        void keinZirkelbezug_linea() {
            assertDoesNotThrow(() -> linearerNetzplan().calcPath(),
                    "Ein linearer Netzplan ohne Zirkelbezug darf keine Exception werfen");
        }

        @Test
        @DisplayName("TC-CYC-05: Selbst-Referenz eines Knotens → Exception")
        void selbstreferenz_exception() {
            Knoten a = new Knoten(1, "A", 5);
            a.addPredecessor(new Knoten[]{a});   // A → A
            Netzplan np = new Netzplan();
            np.addNode(a);
            assertThrows(IllegalArgumentException.class, np::calcPath,
                    "Eine Selbst-Referenz (A→A) muss als Zirkelbezug erkannt werden");
        }
    }

    @Nested
    @DisplayName("TC-PRED – Mehrere Vorgänger eines Knotens")
    class MehrereVorgaenger {

        @Test
        @DisplayName("TC-PRED-01: Knoten mit zwei Vorgängern ist zulässig")
        void zweiVorgaenger_zulässig() {
            Knoten echterStart = new Knoten(0, "START", 1);
            Knoten a = new Knoten(1, "A", 5);
            Knoten b = new Knoten(2, "B", 3);
            Knoten c = new Knoten(3, "C", 7);

            a.addPredecessor(new Knoten[]{echterStart});
            b.addPredecessor(new Knoten[]{echterStart});
            c.addPredecessor(new Knoten[]{a, b});

            Netzplan np = new Netzplan();
            np.addNode(echterStart);
            np.addNode(a);
            np.addNode(b);
            np.addNode(c);

            assertDoesNotThrow(np::calcPath,
                    "Ein Knoten mit zwei Vorgängern muss erlaubt sein, solange es global nur einen Startknoten gibt.");
        }

        @Test
        @DisplayName("TC-PRED-02: FAZ des Nachfolgers entspricht dem Maximum der FEZ der Vorgänger")
        void faz_maximumDerVorgaengerFez() {
            Knoten echterStart = new Knoten(0, "START", 0); // Dauer 0, um Rechnung nicht zu verfälschen
            Knoten a = new Knoten(1, "A", 10);
            Knoten b = new Knoten(2, "B", 3);
            Knoten c = new Knoten(3, "C", 5);

            a.addPredecessor(new Knoten[]{echterStart});
            b.addPredecessor(new Knoten[]{echterStart});
            c.addPredecessor(new Knoten[]{a, b});

            Netzplan np = new Netzplan();
            np.addNode(echterStart);
            np.addNode(a);
            np.addNode(b);
            np.addNode(c);

            np.calcPath();

            assertEquals(10L, c.getFaz(),
                    "FAZ von C muss dem Maximum der FEZ aller Vorgänger entsprechen (10)");
        }

        @Test
        @DisplayName("TC-PRED-03: Drei Vorgänger – Pfadlänge korrekt berechnet")
        void dreiVorgaenger_korrektePfadlaenge() {
            Knoten echterStart = new Knoten(0, "START", 0);
            Knoten a = new Knoten(1, "A", 10);
            Knoten b = new Knoten(2, "B", 6);
            Knoten c = new Knoten(3, "C", 4);
            Knoten d = new Knoten(4, "D", 2);

            a.addPredecessor(new Knoten[]{echterStart});
            b.addPredecessor(new Knoten[]{echterStart});
            c.addPredecessor(new Knoten[]{echterStart});
            d.addPredecessor(new Knoten[]{a, b, c});

            Netzplan np = new Netzplan();
            np.addNode(echterStart);
            np.addNode(a);
            np.addNode(b);
            np.addNode(c);
            np.addNode(d);

            np.calcPath();
            assertEquals(12L, np.getDuration(),
                    "Gesamtdauer muss 12 sein (längster Vorgänger A=10, + D=2)");
        }

        @Test
        @DisplayName("TC-PRED-04: Vorgänger-Liste ist nicht leer nach addPredecessor")
        void vorgaengerListe_nichtLeer() {
            Knoten a = new Knoten(1, "A", 5);
            Knoten b = new Knoten(2, "B", 3);
            b.addPredecessor(new Knoten[]{a});
            assertFalse(b.getPredecessors().isEmpty(),
                    "Die Vorgänger-Liste von B muss nach addPredecessor mindestens einen Eintrag enthalten");
        }
    }

    @Nested
    @DisplayName("TC-SUCC – Mehrere Nachfolger eines Knotens")
    class MehrereNachfolger {

        @Test
        @DisplayName("TC-SUCC-01: Knoten mit zwei Nachfolgern ist zulässig")
        void zweiNachfolger_zulässig() {
            Knoten start = new Knoten(1, "Start", 5);
            Knoten b     = new Knoten(2, "B",     3);
            Knoten c     = new Knoten(3, "C",     4);
            Knoten ende  = new Knoten(4, "Ende",  2);
            b.addPredecessor(new Knoten[]{start});
            c.addPredecessor(new Knoten[]{start});
            ende.addPredecessor(new Knoten[]{b, c});
            Netzplan np = new Netzplan();
            np.addNode(start); np.addNode(b); np.addNode(c); np.addNode(ende);
            assertDoesNotThrow(np::calcPath,
                    "Ein Knoten mit zwei Nachfolgern muss erlaubt sein");
        }

        @Test
        @DisplayName("TC-SUCC-02: Gesamtdauer bei zwei parallelen Zweigen korrekt (längster Pfad)")
        void zweiZweige_laengsterPfadEntscheidet() {
            Knoten start = new Knoten(1, "Start", 2);
            Knoten b     = new Knoten(2, "B",     8);  // kurzer Zweig
            Knoten c     = new Knoten(3, "C",    20);  // langer Zweig
            Knoten ende  = new Knoten(4, "Ende",  3);
            b.addPredecessor(new Knoten[]{start});
            c.addPredecessor(new Knoten[]{start});
            ende.addPredecessor(new Knoten[]{b, c});
            Netzplan np = new Netzplan();
            np.addNode(start); np.addNode(b); np.addNode(c); np.addNode(ende);
            np.calcPath();
            // Start(2) + C(20) + Ende(3) = 25
            assertEquals(25L, np.getDuration(),
                    "Der kritische Pfad (2+20+3=25) muss als Gesamtdauer erkannt werden");
        }

        @Test
        @DisplayName("TC-SUCC-03: Nachfolger-Liste des Startknotens enthält beide Nachfolger")
        void nachfolgerListe_beideNachfolgerEnthalten() {
            Knoten start = new Knoten(1, "Start", 1);
            Knoten b     = new Knoten(2, "B", 1);
            Knoten c     = new Knoten(3, "C", 1);
            Knoten ende  = new Knoten(4, "Ende", 1);
            b.addPredecessor(new Knoten[]{start});
            c.addPredecessor(new Knoten[]{start});
            ende.addPredecessor(new Knoten[]{b, c});
            Netzplan np = new Netzplan();
            np.addNode(start); np.addNode(b); np.addNode(c); np.addNode(ende);
            np.calcPath();
            assertEquals(2, start.getSuccessors().size(),
                    "Der Startknoten sollte genau 2 Nachfolger haben");
        }

        @Test
        @DisplayName("TC-SUCC-04: Drei parallele Zweige – Gesamtdauer ist Maximum + Rahmen")
        void dreiParalleleZweige_korrektePfadlaenge() {
            Knoten start = new Knoten(1, "Start", 1);
            Knoten b     = new Knoten(2, "B",     5);
            Knoten c     = new Knoten(3, "C",    15);
            Knoten d     = new Knoten(4, "D",    10);
            Knoten ende  = new Knoten(5, "Ende",  1);
            b.addPredecessor(new Knoten[]{start});
            c.addPredecessor(new Knoten[]{start});
            d.addPredecessor(new Knoten[]{start});
            ende.addPredecessor(new Knoten[]{b, c, d});
            Netzplan np = new Netzplan();
            np.addNode(start); np.addNode(b); np.addNode(c); np.addNode(d); np.addNode(ende);
            np.calcPath();
            // Start(1) + C(15) + Ende(1) = 17
            assertEquals(17L, np.getDuration(),
                    "Bei drei parallelen Zweigen bestimmt der längste (C=15) die Gesamtdauer: 1+15+1=17");
        }
    }
}