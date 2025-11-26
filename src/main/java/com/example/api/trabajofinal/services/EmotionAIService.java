package com.example.api.trabajofinal.services;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EmotionAIService {

    private final Map<String, String> emocionesPalabrasClave = Map.ofEntries(
            Map.entry("estres", "ESTRES"),
            Map.entry("estrés", "ESTRES"),
            Map.entry("estresado", "ESTRES"),
            Map.entry("estresada", "ESTRES"),
            Map.entry("agobiado", "ESTRES"),
            Map.entry("agobiada", "ESTRES"),
            Map.entry("presion", "ESTRES"),
            Map.entry("presión", "ESTRES"),
            Map.entry("tensión", "ESTRES"),
            Map.entry("tension", "ESTRES"),
            Map.entry("ansiedad", "ANSIEDAD"),
            Map.entry("ansioso", "ANSIEDAD"),
            Map.entry("ansiosa", "ANSIEDAD"),
            Map.entry("nervioso", "ANSIEDAD"),
            Map.entry("nerviosa", "ANSIEDAD"),
            Map.entry("inquieto", "ANSIEDAD"),
            Map.entry("inquieta", "ANSIEDAD"),
            Map.entry("preocupado", "ANSIEDAD"),
            Map.entry("preocupada", "ANSIEDAD"),
            Map.entry("triste", "TRISTEZA"),
            Map.entry("tristeza", "TRISTEZA"),
            Map.entry("deprimido", "TRISTEZA"),
            Map.entry("deprimida", "TRISTEZA"),
            Map.entry("melancolía", "TRISTEZA"),
            Map.entry("melancolia", "TRISTEZA"),
            Map.entry("desanimado", "TRISTEZA"),
            Map.entry("desanimada", "TRISTEZA"),
            Map.entry("desesperanzado", "TRISTEZA"),
            Map.entry("desesperanzada", "TRISTEZA"),
            Map.entry("enojo", "ENOJO"),
            Map.entry("enojado", "ENOJO"),
            Map.entry("enojada", "ENOJO"),
            Map.entry("furioso", "ENOJO"),
            Map.entry("furiosa", "ENOJO"),
            Map.entry("ira", "ENOJO"),
            Map.entry("irritado", "ENOJO"),
            Map.entry("irritada", "ENOJO"),
            Map.entry("molesto", "ENOJO"),
            Map.entry("molesta", "ENOJO"),
            Map.entry("miedo", "MIEDO"),
            Map.entry("asustado", "MIEDO"),
            Map.entry("asustada", "MIEDO"),
            Map.entry("atemorizado", "MIEDO"),
            Map.entry("atemorizada", "MIEDO"),
            Map.entry("temeroso", "MIEDO"),
            Map.entry("temerosa", "MIEDO"),
            Map.entry("pánico", "MIEDO"),
            Map.entry("panico", "MIEDO"),
            Map.entry("aterrado", "MIEDO"),
            Map.entry("aterrada", "MIEDO"),
            Map.entry("feliz", "FELICIDAD"),
            Map.entry("contento", "FELICIDAD"),
            Map.entry("contenta", "FELICIDAD"),
            Map.entry("alegre", "FELICIDAD"),
            Map.entry("alegría", "FELICIDAD"),
            Map.entry("alegria", "FELICIDAD"),
            Map.entry("gozo", "FELICIDAD"),
            Map.entry("euforia", "FELICIDAD"),
            Map.entry("entusiasmo", "FELICIDAD"),
            Map.entry("calma", "CALMA"),
            Map.entry("tranquilo", "CALMA"),
            Map.entry("tranquila", "CALMA"),
            Map.entry("sereno", "CALMA"),
            Map.entry("serena", "CALMA"),
            Map.entry("paz", "CALMA"),
            Map.entry("pacífico", "CALMA"),
            Map.entry("pacifica", "CALMA")
    );


    private final Set<String> palabrasCriticas = Set.of(
            "suicidio", "suicidar", "suicidarme", "suicidarse", "suicidarnos", "suicidarte",
            "matar", "matarme", "matarse", "matarnos", "matarte", "asesinar", "asesinarme",
            "me quiero morir", "quiero morirme", "deseo morir", "anhelo morir", "espero morir",
            "morir", "morirme", "morirse", "muerte", "muerto", "muerta", "muertos", "muertas",
            "quiero morir", "no quiero vivir", "no deseo vivir", "no quiero seguir", "no deseo seguir",
            "acabar con todo", "acabar conmigo", "acabar contigo", "acabar con nosotros",
            "quitarme la vida", "quitarse la vida", "quitarnos la vida", "quitarte la vida",
            "terminar con mi vida", "terminar con su vida", "terminar con nuestra vida",
            "hacerme daño", "hacerse daño", "hacernos daño", "hacerte daño",
            "lastimarme", "lastimarse", "lastimarnos", "lastimarte",
            "cortarme", "cortarse", "cortarnos", "cortarte", "cortadas", "cortados",
            "dañarme", "dañarse", "dañarnos", "dañarte",
            "colgarme", "colgarse", "colgarnos", "colgarte", "ahorcarme", "ahorcarse",
            "desaparecer", "desaparecerme", "desaparecerse", "desaparezco",
            "quitarme del medio", "quitarse del medio", "quitarnos del medio",
            "no tengo salida", "no hay salida", "sin salida", "callejón sin salida",
            "no tengo esperanza", "sin esperanza", "esperanza perdida", "sin futuro",
            "no puedo más", "no aguanto más", "no resisto más", "no soporto más",
            "ya no aguanto", "ya no resisto", "ya no soporto", "llegué al límite",
            "no vale la pena", "nada vale la pena", "todo es inútil", "inutilidad",
            "no sirvo para nada", "no valgo para nada", "soy un inútil", "soy inútil",
            "estoy cansado de todo", "cansado de la vida", "harto de todo", "harto de la vida",
            "odio mi vida", "detesto mi vida", "aborrezco mi vida", "repudio mi vida",
            "me quiero rendir", "quiero rendirme", "me rindo", "rendición",
            "no encuentro sentido", "vida sin sentido", "existencia vacía",
            "no valgo nada", "no merezco nada", "soy nada", "no soy nadie",
            "depresión", "depresivo", "deprimido", "deprimida", "depresivos", "deprimidos",
            "tristeza", "tristeza profunda", "tristeza eterna", "tristeza infinita",
            "angustia", "angustiado", "angustiada", "angustioso", "angustiosa",
            "ansiedad", "ansiedad extrema", "ansiedad paralizante", "ataque de ansiedad",
            "vacío", "vacío existencial", "vacío interior", "sentimiento de vacío",
            "culpa", "culpable", "culpabilidad", "remordimiento", "arrepentimiento",
            "soledad", "solo", "sola", "solos", "soledad absoluta", "aislamiento",
            "dolor", "dolor emocional", "dolor interno", "dolor del alma",
            "sufrimiento", "sufrimiento eterno", "sufrir", "padecer", "padecimiento",
            "desesperado", "desesperada", "desesperación", "desesperanza",
            "fracaso", "fracasado", "fracasada", "fracasados", "fracasadas", "fallido",
            "infeliz", "infelicidad", "desdichado", "desdichada", "desgraciado",
            "sin sentido", "sin propósito", "sin razón", "sin motivo", "sin valor","incendiar"
    );

    private final Map<String, List<String>> tecnicasPorEmocion = Map.ofEntries(
            Map.entry("ESTRES", Arrays.asList("Ejercicio de respiración 4-7-8", "Visualización guiada", "Relajación muscular progresiva", "Meditación de atención plena", "Ejercicio de respiración diafragmática", "Técnica de relajación rápida")),
            Map.entry("ANSIEDAD", Arrays.asList("Técnica 5-4-3-2-1", "Ejercicio de grounding", "Meditación mindfulness", "Respiración cuadrada", "Técnica de anclaje", "Ejercicio de focalización sensorial")),
            Map.entry("TRISTEZA", Arrays.asList("Ejercicio de gratitud", "Actividad física suave", "Escritura expresiva", "Técnica de reestructuración cognitiva", "Ejercicio de conexión social", "Técnica de activación conductual")),
            Map.entry("ENOJO", Arrays.asList("Técnica del semáforo", "Respiración profunda", "Tiempo fuera", "Técnica de pausa activa", "Ejercicio de perspectiva", "Método de comunicación asertiva")),
            Map.entry("MIEDO", Arrays.asList("Exposición gradual", "Diálogo interno positivo", "Técnica de la caja", "Ejercicio de respiración calmante", "Técnica de visualización positiva", "Método de afrontamiento progresivo"))
    );

    public AnalisisEmocional analizarTexto(String texto) {
        String textoLower = texto.toLowerCase();

        for (String palabra : palabrasCriticas) {
            if (textoLower.contains(palabra)) {
                return new AnalisisEmocional("CRITICO", 90,
                        "Es importante que hables con un profesional de inmediato. Contacta a tu psicólogo.",
                        true, LocalDateTime.now());
            }
        }

        String emocionDetectada = "NEUTRAL";
        Integer confianza = 0;

        for (Map.Entry<String, String> entry : emocionesPalabrasClave.entrySet()) {
            if (textoLower.contains(entry.getKey())) {
                emocionDetectada = entry.getValue();
                confianza = 75;
                break;
            }
        }

        if (emocionDetectada.equals("NEUTRAL")) {
            emocionDetectada = analizarContexto(textoLower);
            confianza = 50;
        }

        return new AnalisisEmocional(emocionDetectada, confianza,
                generarRecomendacion(emocionDetectada),
                false, LocalDateTime.now());
    }

    private String analizarContexto(String texto) {
        if (texto.contains("no puedo") || texto.contains("difícil") || texto.contains("problema")) {
            return "ESTRES";
        } else if (texto.contains("preocupado") || texto.contains("nervioso") || texto.contains("qué pasará")) {
            return "ANSIEDAD";
        } else if (texto.contains("solo") || texto.contains("vacío") || texto.contains("llorar")) {
            return "TRISTEZA";
        }
        return "NEUTRAL";
    }

    private String generarRecomendacion(String emocion) {
        if (emocion.equals("NEUTRAL")) {
            return "Continúa con la sesión estándar. Mantén la comunicación abierta.";
        }

        List<String> tecnicas = tecnicasPorEmocion.getOrDefault(emocion,
                Arrays.asList("Respira profundamente", "Habla sobre lo que sientes"));

        Random random = new Random();
        return tecnicas.get(random.nextInt(tecnicas.size()));
    }

    public static class AnalisisEmocional {
        private String emocionDetectada;
        private Integer confianza;
        private String recomendacion;
        private boolean critico;
        private LocalDateTime timestamp;

        public AnalisisEmocional(String emocionDetectada, Integer confianza, String recomendacion, boolean critico, LocalDateTime timestamp) {
            this.emocionDetectada = emocionDetectada;
            this.confianza = confianza;
            this.recomendacion = recomendacion;
            this.critico = critico;
            this.timestamp = timestamp;
        }

        public String getEmocionDetectada() { return emocionDetectada; }
        public Integer getConfianza() { return confianza; }
        public String getRecomendacion() { return recomendacion; }
        public boolean isCritico() { return critico; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}