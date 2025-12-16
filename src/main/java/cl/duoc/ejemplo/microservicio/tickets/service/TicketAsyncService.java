package cl.duoc.ejemplo.microservicio.tickets.service;

import cl.duoc.ejemplo.microservicio.tickets.model.Ticket;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TicketAsyncService {

    private final Map<Long, Ticket> asyncStorage = new ConcurrentHashMap<>();

    public void guardar(Ticket ticket) {
        asyncStorage.put(ticket.getId(), ticket);
        System.out.println("Ticket guardado ASYNC: " + ticket.getId());
    }

    public int totalGuardados() {
        return asyncStorage.size();
    }
}
