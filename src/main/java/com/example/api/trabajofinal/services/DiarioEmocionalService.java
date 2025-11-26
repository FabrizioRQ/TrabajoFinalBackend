package com.example.api.trabajofinal.services;

import com.example.api.trabajofinal.DTO.*;
import com.example.api.trabajofinal.entities.DiarioEmocional;
import com.example.api.trabajofinal.entities.Niño;
import com.example.api.trabajofinal.interfaces.DiarioEmocionalInterface;
import com.example.api.trabajofinal.repositories.DiarioEmocionalRepository;
import com.example.api.trabajofinal.repositories.NiñoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class DiarioEmocionalService implements DiarioEmocionalInterface {

    @Autowired
    private DiarioEmocionalRepository diarioEmocionalRepository;

    @Autowired
    private NiñoRepository niñoRepository;

    @Autowired
    private EmotionAIService emotionAIService;

    @Autowired
    private ModelMapper modelMapper;

    private final Map<Long, List<RegistroEmocionalDTO>> cacheOffline = new ConcurrentHashMap<>();

    private final List<PreguntaEmocionalDTO> preguntasBase = Arrays.asList(
            new PreguntaEmocionalDTO(
                    "¿Cómo te sentiste hoy en una escala del 1 al 5?",
                    Arrays.asList("1 - Muy mal", "2 - Mal", "3 - Regular", "4 - Bien", "5 - Muy bien"),
                    "escala",
                    "Evaluación general del día"
            ),
            new PreguntaEmocionalDTO(
                    "¿Qué te hizo sentir así hoy?",
                    null,
                    "texto",
                    "Contexto emocional"
            ),
            new PreguntaEmocionalDTO(
                    "¿Hubo algún momento especial que quieras recordar?",
                    null,
                    "texto",
                    "Reflexión positiva"
            ),
            new PreguntaEmocionalDTO(
                    "¿Cómo te sentiste en la escuela/hogar hoy?",
                    Arrays.asList("Feliz", "Triste", "Enojado", "Asustado", "Tranquilo"),
                    "seleccion",
                    "Contexto específico"
            )
    );

    // Métodos existentes
    @Override
    public DiarioEmocional registrarEmocion(Long niñoId, String emocion, String contexto) {
        Optional<Niño> niñoOpt = niñoRepository.findById(niñoId);
        if (niñoOpt.isEmpty()) {
            throw new IllegalArgumentException("Niño no encontrado con ID: " + niñoId);
        }

        Niño niño = niñoOpt.get();

        DiarioEmocional registro = new DiarioEmocional();
        registro.setFecha(LocalDate.now());
        registro.setEmocionRegistrada(emocion);
        registro.setIdNiño(niño);

        System.out.println("Registrando emoción: " + emocion + " para niño ID: " + niñoId);
        return diarioEmocionalRepository.save(registro);
    }

    @Override
    public List<DiarioEmocional> obtenerHistorialPorNiño(Long niñoId) {
        System.out.println("Obteniendo historial emocional para niño ID: " + niñoId);
        return diarioEmocionalRepository.findByIdNiñoIdOrderByFechaDesc(niñoId);
    }

    @Override
    public List<DiarioEmocional> obtenerUltimasEmociones(Long niñoId, int limite) {
        System.out.println("Obteniendo últimas " + limite + " emociones para niño ID: " + niñoId);
        List<DiarioEmocional> todas = diarioEmocionalRepository.findByIdNiñoIdOrderByFechaDesc(niñoId);
        return todas.stream().limit(limite).collect(Collectors.toList());
    }

    @Override
    public List<DiarioEmocional> obtenerEmocionesPorFecha(Long niñoId, String fecha) {
        LocalDate fechaBusqueda = LocalDate.parse(fecha);
        System.out.println("Obteniendo emociones para niño ID: " + niñoId + " en fecha: " + fecha);
        return diarioEmocionalRepository.findByIdNiñoIdAndFecha(niñoId, fechaBusqueda);
    }


    @Override
    public List<PreguntaEmocionalDTO> generarPreguntasDiarias(Long niñoId) {
        try {
            System.out.println("Generando preguntas diarias para niño ID: " + niñoId);

            Optional<Niño> niñoOpt = niñoRepository.findById(niñoId);
            if (niñoOpt.isEmpty()) {
                throw new IllegalArgumentException("Niño no encontrado");
            }

            String contextoPersonalizado = obtenerContextoParaPreguntas(niñoId);

            List<PreguntaEmocionalDTO> preguntasSeleccionadas = new ArrayList<>();
            List<PreguntaEmocionalDTO> preguntasDisponibles = new ArrayList<>(preguntasBase);

            Collections.shuffle(preguntasDisponibles);
            int numPreguntas = new Random().nextInt(2) + 2;

            for (int i = 0; i < numPreguntas && i < preguntasDisponibles.size(); i++) {
                PreguntaEmocionalDTO pregunta = preguntasDisponibles.get(i);

                if (contextoPersonalizado != null && pregunta.getTipo().equals("texto")) {
                    pregunta.setPregunta(pregunta.getPregunta() + " " + contextoPersonalizado);
                }

                preguntasSeleccionadas.add(pregunta);
            }

            System.out.println("Preguntas generadas: " + preguntasSeleccionadas.size());
            return preguntasSeleccionadas;

        } catch (Exception e) {
            System.err.println("Error generando preguntas: " + e.getMessage());
            return preguntasBase.subList(0, Math.min(2, preguntasBase.size()));
        }
    }

    @Override
    public RespuestaEmocionalDTO procesarRespuestaEmocional(RegistroEmocionalDTO registroDTO) {
        try {
            System.out.println(" Procesando respuesta emocional para niño ID: " + registroDTO.getNiñoId());

            Optional<Niño> niñoOpt = niñoRepository.findById(registroDTO.getNiñoId());
            if (niñoOpt.isEmpty()) {
                return new RespuestaEmocionalDTO(false, "Niño no encontrado", null, null, false, LocalDateTime.now());
            }

            String emocionFinal = determinarEmocion(registroDTO);

            EmotionAIService.AnalisisEmocional analisis = null;
            if (registroDTO.getRespuestaTexto() != null && !registroDTO.getRespuestaTexto().trim().isEmpty()) {
                analisis = emotionAIService.analizarTexto(registroDTO.getRespuestaTexto());
                emocionFinal = analisis.getEmocionDetectada();
            }

            registrarEmocionIdempotente(registroDTO, emocionFinal);

            guardarEnCacheOffline(registroDTO);

            String recomendacion = (analisis != null) ? analisis.getRecomendacion() :
                    "Gracias por compartir tus emociones. Recuerda que es importante expresar lo que sientes.";

            System.out.println(" Respuesta procesada - Emoción: " + emocionFinal);

            return new RespuestaEmocionalDTO(
                    true,
                    "Respuesta guardada exitosamente",
                    emocionFinal,
                    recomendacion,
                    (analisis != null) ? analisis.isCritico() : false,
                    LocalDateTime.now()
            );

        } catch (Exception e) {
            System.err.println("Error procesando respuesta: " + e.getMessage());

            try {
                guardarEnCacheOffline(registroDTO);
                return new RespuestaEmocionalDTO(
                        false,
                        "Error de conexión. Respuesta guardada localmente para sincronizar después.",
                        null, null, false, LocalDateTime.now()
                );
            } catch (Exception cacheError) {
                return new RespuestaEmocionalDTO(
                        false,
                        "Error del sistema: " + e.getMessage(),
                        null, null, false, LocalDateTime.now()
                );
            }
        }
    }

    @Override
    public RegistroEmocionalDTO obtenerRegistroDelDia(Long niñoId) {
        try {
            System.out.println("Obteniendo registro del día para niño ID: " + niñoId);

            LocalDate hoy = LocalDate.now();
            List<DiarioEmocional> registrosHoy = diarioEmocionalRepository
                    .findByIdNiñoIdAndFecha(niñoId, hoy);

            if (registrosHoy.isEmpty()) {
                return null;
            }

            DiarioEmocional ultimoRegistro = registrosHoy.get(0);

            return new RegistroEmocionalDTO(
                    niñoId,
                    ultimoRegistro.getEmocionRegistrada(),
                    "Registro del día",
                    convertirEmocionAEscala(ultimoRegistro.getEmocionRegistrada()),
                    "Respuesta guardada anteriormente",
                    hoy
            );

        } catch (Exception e) {
            System.err.println("Error obteniendo registro del día: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Boolean sincronizarDatosOffline(Long niñoId) {
        try {
            System.out.println("Sincronizando datos offline para niño ID: " + niñoId);

            List<RegistroEmocionalDTO> datosOffline = cacheOffline.get(niñoId);
            if (datosOffline == null || datosOffline.isEmpty()) {
                System.out.println("No hay datos offline para sincronizar");
                return true;
            }

            int sincronizados = 0;
            for (RegistroEmocionalDTO registro : datosOffline) {
                try {
                    procesarRespuestaEmocional(registro);
                    sincronizados++;
                } catch (Exception e) {
                    System.err.println("Error sincronizando registro: " + e.getMessage());
                }
            }

            cacheOffline.remove(niñoId);

            System.out.println("Datos sincronizados: " + sincronizados + " de " + datosOffline.size());
            return sincronizados == datosOffline.size();

        } catch (Exception e) {
            System.err.println("Error en sincronización offline: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<DiarioEmocionalDTO> findAllByNiñoId(Long niñoId) {
        List<DiarioEmocional> diarios = diarioEmocionalRepository.findAllByNiñoId(niñoId);

        return diarios.stream()
                .map(diario -> modelMapper.map(diario, DiarioEmocionalDTO.class))
                .collect(Collectors.toList());
    }

    private String obtenerContextoParaPreguntas(Long niñoId) {
        List<DiarioEmocional> ultimasEmociones = obtenerUltimasEmociones(niñoId, 3);

        if (ultimasEmociones.isEmpty()) {
            return null;
        }

        String ultimaEmocion = ultimasEmociones.get(0).getEmocionRegistrada();

        switch (ultimaEmocion.toUpperCase()) {
            case "TRISTEZA":
                return "(Parece que ayer fue un día difícil)";
            case "FELICIDAD":
                return "(Me alegra que ayer hayas tenido un buen día)";
            case "ANSIEDAD":
                return "(Sé que ayer fue un día de preocupaciones)";
            default:
                return null;
        }
    }

    private String determinarEmocion(RegistroEmocionalDTO registro) {
        if (registro.getEscalaEmocional() != null) {
            switch (registro.getEscalaEmocional()) {
                case 1: case 2: return "TRISTEZA";
                case 3: return "NEUTRAL";
                case 4: case 5: return "FELICIDAD";
            }
        }

        if (registro.getRespuestaTexto() != null && !registro.getRespuestaTexto().trim().isEmpty()) {
            EmotionAIService.AnalisisEmocional analisis = emotionAIService.analizarTexto(registro.getRespuestaTexto());
            return analisis.getEmocionDetectada();
        }

        return "NEUTRAL";
    }

    private void registrarEmocionIdempotente(RegistroEmocionalDTO registro, String emocion) {
        LocalDate hoy = LocalDate.now();

        List<DiarioEmocional> registrosExistentes = diarioEmocionalRepository
                .findByIdNiñoIdAndFecha(registro.getNiñoId(), hoy);

        DiarioEmocional diario;

        if (registrosExistentes.isEmpty()) {
            Optional<Niño> niñoOpt = niñoRepository.findById(registro.getNiñoId());
            if (niñoOpt.isEmpty()) return;

            diario = new DiarioEmocional();
            diario.setFecha(hoy);
            diario.setIdNiño(niñoOpt.get());
        } else {
            diario = registrosExistentes.get(0);
        }

        diario.setEmocionRegistrada(emocion);
        diarioEmocionalRepository.save(diario);

        System.out.println("Emoción registrada idempotentemente: " + emocion);
    }

    private void guardarEnCacheOffline(RegistroEmocionalDTO registro) {
        cacheOffline.computeIfAbsent(registro.getNiñoId(), k -> new ArrayList<>())
                .add(registro);
        System.out.println("Registro guardado en cache offline");
    }

    private Integer convertirEmocionAEscala(String emocion) {
        switch (emocion.toUpperCase()) {
            case "FELICIDAD": return 5;
            case "CALMA": return 4;
            case "NEUTRAL": return 3;
            case "TRISTEZA": return 2;
            case "ENOJO": case "ANSIEDAD": case "MIEDO": return 1;
            default: return 3;
        }
    }


    // ... (todos tus métodos existentes se mantienen igual)

    // MÉTODO NUEVO PARA LA USER STORY 21: Obtener entradas por rango de fechas
    public List<DiarioEmocionalDTO> obtenerEntradasPorRangoFechas(Long niñoId, LocalDate startDate, LocalDate endDate) {
        // Validar que el niño exista
        Optional<Niño> niñoOpt = niñoRepository.findById(niñoId);
        if (niñoOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Niño no encontrado con ID: " + niñoId);
        }

        // Validar rango de fechas
        if (startDate == null || endDate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Las fechas startDate y endDate son requeridas");
        }

        if (endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha endDate no puede ser anterior a startDate");
        }

        // Validar que las fechas no sean futuras (opcional)
        LocalDate hoy = LocalDate.now();
        if (startDate.isAfter(hoy) || endDate.isAfter(hoy)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Las fechas no pueden ser futuras");
        }

        System.out.println("Consultando entradas para niño ID: " + niñoId +
                " desde " + startDate + " hasta " + endDate);

        // Obtener las entradas del repositorio
        List<DiarioEmocional> entradas = diarioEmocionalRepository
                .findByIdNiñoIdAndFechaBetweenOrderByFechaDesc(niñoId, startDate, endDate);

        // Mapear a DTO usando ModelMapper
        List<DiarioEmocionalDTO> entradasDTO = entradas.stream()
                .map(entrada -> {
                    DiarioEmocionalDTO dto = modelMapper.map(entrada, DiarioEmocionalDTO.class);
                    dto.setNiñoId(entrada.getIdNiño().getId());
                    return dto;
                })
                .collect(Collectors.toList());

        System.out.println("Encontradas " + entradasDTO.size() + " entradas en el rango especificado");

        return entradasDTO;
    }

    // MÉTODO ALTERNATIVO: Obtener entradas por mes específico
    public List<DiarioEmocionalDTO> obtenerEntradasPorMes(Long niñoId, int año, int mes) {
        LocalDate startDate = LocalDate.of(año, mes, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return obtenerEntradasPorRangoFechas(niñoId, startDate, endDate);
    }

    // MÉTODO PARA VERIFICAR SI EXISTEN ENTRADAS EN UN RANGO
    public boolean existenEntradasEnRango(Long niñoId, LocalDate startDate, LocalDate endDate) {
        try {
            List<DiarioEmocionalDTO> entradas = obtenerEntradasPorRangoFechas(niñoId, startDate, endDate);
            return !entradas.isEmpty();
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw e; // Re-lanzar el error 404
            }
            return false;
        }
    }
}