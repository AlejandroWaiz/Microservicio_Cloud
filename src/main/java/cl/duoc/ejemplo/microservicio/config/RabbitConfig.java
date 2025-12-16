package cl.duoc.ejemplo.microservicio.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE_OK = "tickets.queue.ok";
    public static final String QUEUE_ERROR = "tickets.queue.error";

    @Bean
    public Queue ticketsOkQueue() {
        return new Queue(QUEUE_OK, true);
    }

    @Bean
    public Queue ticketsErrorQueue() {
        return new Queue(QUEUE_ERROR, true);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
