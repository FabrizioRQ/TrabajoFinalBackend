package com.example.api.trabajofinal.controllers;

import com.example.api.trabajofinal.DTO.*;
import com.example.api.trabajofinal.services.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pagos")
public class PagoController {
    @Autowired
    private PagoService pagoService;

    @PostMapping
    public PagoDTO crearPago(@RequestBody PagoCreateDTO pagoDTO) {
        return pagoService.crearPago(pagoDTO);
    }

    @GetMapping("/{id}")
    public PagoDTO obtenerPagoPorId(@PathVariable Long id) {
        return pagoService.obtenerPagoPorId(id);
    }

    @GetMapping
    public List<PagoDTO> listarPagos() {
        return pagoService.listarPagos();
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<PagoDTO> listarPagosPorUsuario(@PathVariable Long usuarioId) {
        return pagoService.listarPagosPorUsuario(usuarioId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoDTO> actualizarPago(@PathVariable Long id, @RequestBody PagoCreateDTO pagoDTO)
    {
        PagoDTO actualizado = pagoService.actualizarPago(id, pagoDTO);
        return actualizado != null
                ? ResponseEntity.ok(actualizado)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPago(@PathVariable Long id) {
        pagoService.eliminarPago(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/planes")
    public ResponseEntity<List<PlanSuscripcionDTO>> obtenerPlanesDisponibles() {
        try {
            List<PlanSuscripcionDTO> planes = pagoService.obtenerPlanesDisponibles();
            return ResponseEntity.ok(planes);
        } catch (Exception e) {
            System.err.println("Error obteniendo planes: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/seleccionar-plan")
    public ResponseEntity<RespuestaPlanDTO> seleccionarPlan(@RequestBody SeleccionPlanDTO seleccionDTO) {
        RespuestaPlanDTO respuesta = pagoService.seleccionarPlan(seleccionDTO);

        if (respuesta.getExito()) {
            return ResponseEntity.ok(respuesta);
        } else {
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    @GetMapping("/plan-actual/{usuarioId}")
    public ResponseEntity<PlanSuscripcionDTO> obtenerPlanActual(@PathVariable Long usuarioId) {
        PlanSuscripcionDTO planActual = pagoService.obtenerPlanActual(usuarioId);
        return ResponseEntity.ok(planActual);
    }

    @GetMapping("/metodos-pago/{usuarioId}")
    public ResponseEntity<List<MetodoPagoDTO>> obtenerMetodosPagoUsuario(@PathVariable Long usuarioId) {
        try {
            List<MetodoPagoDTO> metodos = pagoService.obtenerMetodosPagoUsuario(usuarioId);
            return ResponseEntity.ok(metodos);
        } catch (Exception e) {
            System.err.println("Error obteniendo métodos de pago: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/metodos-pago")
    public ResponseEntity<RespuestaMetodoPagoDTO> agregarMetodoPago(@RequestBody CrearMetodoPagoDTO metodoDTO) {
        RespuestaMetodoPagoDTO respuesta = pagoService.agregarMetodoPago(metodoDTO);

        if (respuesta.getExito()) {
            return ResponseEntity.ok(respuesta);
        } else {
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    @DeleteMapping("/metodos-pago/{metodoPagoId}/usuario/{usuarioId}")
    public ResponseEntity<RespuestaMetodoPagoDTO> eliminarMetodoPago(
            @PathVariable Long metodoPagoId,
            @PathVariable Long usuarioId) {
        RespuestaMetodoPagoDTO respuesta = pagoService.eliminarMetodoPago(metodoPagoId, usuarioId);

        if (respuesta.getExito()) {
            return ResponseEntity.ok(respuesta);
        } else {
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    @GetMapping("/estado-suscripcion/{usuarioId}")
    public ResponseEntity<EstadoSuscripcionDTO> obtenerEstadoSuscripcion(@PathVariable Long usuarioId) {
        try {
            EstadoSuscripcionDTO estado = pagoService.obtenerEstadoSuscripcion(usuarioId);
            return ResponseEntity.ok(estado);
        } catch (Exception e) {
            System.err.println("Error obteniendo estado de suscripción: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/pausar-suscripcion")
    public ResponseEntity<RespuestaSimpleDTO> pausarSuscripcion(@RequestBody CancelacionSuscripcionDTO cancelacionDTO) {
        RespuestaSimpleDTO respuesta = pagoService.pausarSuscripcion(cancelacionDTO);
        if (respuesta.getExito()) {
            return ResponseEntity.ok(respuesta);
        } else {
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    @PostMapping("/cancelar-suscripcion")
    public ResponseEntity<RespuestaSimpleDTO> cancelarSuscripcion(@RequestBody CancelacionSuscripcionDTO cancelacionDTO) {
        RespuestaSimpleDTO respuesta = pagoService.cancelarSuscripcion(cancelacionDTO);
        if (respuesta.getExito()) {
            return ResponseEntity.ok(respuesta);
        } else {
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    @PostMapping("/reactivar-suscripcion/{usuarioId}")
    public ResponseEntity<RespuestaSimpleDTO> reactivarSuscripcion(@PathVariable Long usuarioId) {
        RespuestaSimpleDTO respuesta = pagoService.reactivarSuscripcion(usuarioId);
        if (respuesta.getExito()) {
            return ResponseEntity.ok(respuesta);
        } else {
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    @GetMapping("/total-rango")
    public List<Object[]> totalPorUsuario(
            @RequestParam Instant inicio,
            @RequestParam Instant fin
    ) {
        return pagoService.totalPagadoPorUsuarioEnRango(inicio, fin);
    }

    // 2) Conteo de pagos por estado y método
    @GetMapping("/count-estado-metodo")
    public List<Object[]> countEstadoMetodo() {
        return pagoService.countPagosPorEstadoYMetodo();
    }

    // 3) Reporte ingresos mensuales últimos 12 meses
    @GetMapping("/ingresos-mensuales")
    public List<Object[]> ingresosMensuales() {
        return pagoService.ingresosMensualesUltimoAnio();
    }

    // 4) Top usuarios con más pagos
    @GetMapping("/top-usuarios")
    public List<Object[]> topUsuarios() {
        return pagoService.topUsuariosConMasPagos();
    }
}