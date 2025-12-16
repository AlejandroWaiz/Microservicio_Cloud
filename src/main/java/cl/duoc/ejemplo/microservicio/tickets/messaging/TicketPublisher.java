package cl.duoc.ejemplo.microservicio.tickets.messaging;

import cl.duoc.ejemplo.microservicio.config.RabbitConfig;
import cl.duoc.ejemplo.microservicio.tickets.model.Ticket;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TicketPublisher {

    private final RabbitTemplate rabbitTemplate;

    public TicketPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishTicket(Ticket ticket) {
        try {
            System.out.println(">>> PUBLISHER: precio=" + (ticket != null ? ticket.getPrecio() : null));

            if (ticket == null || ticket.getPrecio() == null || ticket.getPrecio().signum() <= 0) {
                System.out.println(">>> PUBLISHER: ENVIANDO A COLA ERROR");

                Map<String, Object> errorMsg = new HashMap<>();
                errorMsg.put("ticket", ticket);
                errorMsg.put("error", "Ticket inválido: precio debe ser > 0");
                rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_ERROR, errorMsg);
                return;
            }
            rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_OK, ticket);
        } catch (AmqpException ex) {
            Map<String, Object> errorMsg = new HashMap<>();
            errorMsg.put("ticket", ticket);
            errorMsg.put("error", ex.getMessage());
            rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_ERROR, errorMsg);
        }
    }
}
