package cl.duoc.ejemplo.microservicio.tickets.controller;

import cl.duoc.ejemplo.microservicio.tickets.model.Ticket;
import cl.duoc.ejemplo.microservicio.tickets.model.TicketEstadisticasDto;
import cl.duoc.ejemplo.microservicio.tickets.service.S3TicketStorageService;
import cl.duoc.ejemplo.microservicio.tickets.service.TicketService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final S3TicketStorageService s3Service;

    public TicketController(TicketService ticketService,
                            S3TicketStorageService s3Service) {
        this.ticketService = ticketService;
        this.s3Service = s3Service;
    }

    /**
     * Generar ticket
     */
    @PostMapping
    public ResponseEntity<Ticket> crearTicket(@RequestBody Ticket ticket) {
        Ticket creado = ticketService.crearTicket(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Obtener ticket por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> obtenerPorId(@PathVariable Long id) {
        return ticketService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Listar tickets por usuario
     */
    @GetMapping(params = "usuarioId")
    public ResponseEntity<List<Ticket>> listarPorUsuario(@RequestParam Long usuarioId) {
        List<Ticket> tickets = ticketService.buscarPorUsuario(usuarioId);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Listar tickets por evento
     */
    @GetMapping(params = "eventoId")
    public ResponseEntity<List<Ticket>> listarPorEvento(@RequestParam Long eventoId) {
        List<Ticket> tickets = ticketService.buscarPorEvento(eventoId);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Modificar detalles del ticket
     */
    @PutMapping("/{id}")
    public ResponseEntity<Ticket> actualizarTicket(@PathVariable Long id,
                                                   @RequestBody Ticket cambios) {
        Ticket actualizado = ticketService.actualizarTicket(id, cambios);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Eliminar ticket
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTicket(@PathVariable Long id) {
        boolean eliminado = ticketService.eliminarTicket(id);
        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Subir archivo del ticket a S3
     */
    @PostMapping("/{id}/upload")
    public ResponseEntity<String> subirArchivoTicket(@PathVariable Long id,
                                                     @RequestParam("file") MultipartFile file) {
        try {
            if (ticketService.obtenerPorId(id).isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            String key = s3Service.subirArchivoTicket(id, file);
            ticketService.actualizarS3Key(id, key);
            return ResponseEntity.ok(key);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error subiendo archivo: " + e.getMessage());
        }
    }

    /**
     * Descargar archivo del ticket desde S3
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> descargarArchivoTicket(@PathVariable Long id) {
        return ticketService.obtenerPorId(id)
                .filter(t -> t.getS3Key() != null)
                .map(ticket -> {
                    byte[] data = s3Service.descargarArchivo(ticket.getS3Key());
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    headers.setContentDispositionFormData("attachment", "ticket-" + id + ".bin");
                    return new ResponseEntity<>(data, headers, HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Estadísticas de ventas / reservas por evento
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<TicketEstadisticasDto> estadisticas(@RequestParam Long eventoId) {
        TicketEstadisticasDto dto = ticketService.calcularEstadisticasPorEvento(eventoId);
        return ResponseEntity.ok(dto);
    }
}
