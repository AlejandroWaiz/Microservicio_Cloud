package cl.duoc.ejemplo.microservicio.tickets.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Ticket {

    private Long id;
    private Long eventoId;
    private Long usuarioId;
    private BigDecimal precio;
    private String estado; // RESERVADO, PAGADO, CANCELADO
    private LocalDateTime fechaReserva;
    private String s3Key; // Ruta del archivo en S3

    public Ticket() {
    }

    public Ticket(Long id,
                  Long eventoId,
                  Long usuarioId,
                  BigDecimal precio,
                  String estado,
                  LocalDateTime fechaReserva,
                  String s3Key) {
        this.id = id;
        this.eventoId = eventoId;
        this.usuarioId = usuarioId;
        this.precio = precio;
        this.estado = estado;
        this.fechaReserva = fechaReserva;
        this.s3Key = s3Key;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }
}
