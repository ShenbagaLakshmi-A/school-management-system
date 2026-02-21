package com.hotel.reservation.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Configuration
public class RabbitConfig {

    public static final String RESERVATION_EXCHANGE = "reservation-exchange";
    public static final String RESERVATION_QUEUE = "notification-queue";
    public static final String RESERVATION_ROUTING_KEY = "reservation.created";

    @Bean
    public Queue reservationQueue() {
        return new Queue(RESERVATION_QUEUE, true);
    }

    @Bean
    public TopicExchange reservationExchange() {
        return new TopicExchange(RESERVATION_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue reservationQueue, TopicExchange reservationExchange) {
        return BindingBuilder.bind(reservationQueue)
                             .to(reservationExchange)
                             .with(RESERVATION_ROUTING_KEY);
    }
	
	@Bean
	public Jackson2JsonMessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory,
										 Jackson2JsonMessageConverter jsonMessageConverter) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(jsonMessageConverter);
		return template;
	}
}