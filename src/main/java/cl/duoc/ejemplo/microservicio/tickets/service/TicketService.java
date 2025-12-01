package cl.duoc.ejemplo.microservicio.tickets.service;

import cl.duoc.ejemplo.microservicio.tickets.model.Ticket;
import cl.duoc.ejemplo.microservicio.tickets.model.TicketEstadisticasDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final Map<Long, Ticket> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1L);

    public Ticket crearTicket(Ticket ticket) {
        Long id = sequence.getAndIncrement();
        ticket.setId(id);

        if (ticket.getFechaReserva() == null) {
            ticket.setFechaReserva(LocalDateTime.now());
        }
        if (ticket.getEstado() == null) {
            ticket.setEstado("RESERVADO");
        }

        storage.put(id, ticket);
        return ticket;
    }

    public Optional<Ticket> obtenerPorId(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Ticket> buscarPorUsuario(Long usuarioId) {
        return storage.values().stream()
                .filter(t -> t.getUsuarioId() != null && t.getUsuarioId().equals(usuarioId))
                .collect(Collectors.toList());
    }

    public List<Ticket> buscarPorEvento(Long eventoId) {
        return storage.values().stream()
                .filter(t -> t.getEventoId() != null && t.getEventoId().equals(eventoId))
                .collect(Collectors.toList());
    }

    public Ticket actualizarTicket(Long id, Ticket cambios) {
        Ticket existente = storage.get(id);
        if (existente == null) {
            return null;
        }

        if (cambios.getEventoId() != null) {
            existente.setEventoId(cambios.getEventoId());
        }
        if (cambios.getUsuarioId() != null) {
            existente.setUsuarioId(cambios.getUsuarioId());
        }
        if (cambios.getPrecio() != null) {
            existente.setPrecio(cambios.getPrecio());
        }
        if (cambios.getEstado() != null) {
            existente.setEstado(cambios.getEstado());
        }

        storage.put(id, existente);
        return existente;
    }

    public boolean eliminarTicket(Long id) {
        return storage.remove(id) != null;
    }

    public void actualizarS3Key(Long id, String s3Key) {
        Ticket ticket = storage.get(id);
        if (ticket != null) {
            ticket.setS3Key(s3Key);
        }
    }

    public TicketEstadisticasDto calcularEstadisticasPorEvento(Long eventoId) {
        List<Ticket> ticketsEvento = buscarPorEvento(eventoId);

        long totalReservas = ticketsEvento.size();
        long totalPagados = ticketsEvento.stream()
                .filter(t -> "PAGADO".equalsIgnoreCase(t.getEstado()))
                .count();
        long totalCancelados = ticketsEvento.stream()
                .filter(t -> "CANCELADO".equalsIgnoreCase(t.getEstado()))
                .count();

        BigDecimal montoTotal = ticketsEvento.stream()
                .map(Ticket::getPrecio)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TicketEstadisticasDto(
                eventoId,
                totalReservas,
                totalPagados,
                totalCancelados,
                montoTotal
        );
    }
}
