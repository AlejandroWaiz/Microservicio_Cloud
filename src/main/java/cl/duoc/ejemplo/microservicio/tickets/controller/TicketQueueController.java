package cl.duoc.ejemplo.microservicio.tickets.controller;

import cl.duoc.ejemplo.microservicio.config.RabbitConfig;
import cl.duoc.ejemplo.microservicio.tickets.model.Ticket;
import cl.duoc.ejemplo.microservicio.tickets.service.TicketAsyncService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets/queue")
public class TicketQueueController {

    private final RabbitTemplate rabbitTemplate;
    private final TicketAsyncService asyncService;

    public TicketQueueController(RabbitTemplate rabbitTemplate,
                                 TicketAsyncService asyncService) {
        this.rabbitTemplate = rabbitTemplate;
        this.asyncService = asyncService;
    }

    @PostMapping("/consume")
    public ResponseEntity<String> consumirCola() {

        int procesados = 0;

        while (true) {
            Ticket ticket = (Ticket) rabbitTemplate.receiveAndConvert(RabbitConfig.QUEUE_OK);
            if (ticket == null) break;

            

            asyncService.guardar(ticket);
            procesados++;
        }

        return ResponseEntity.ok(
                "Mensajes procesados desde cola OK: " + procesados
        );
    }
}
