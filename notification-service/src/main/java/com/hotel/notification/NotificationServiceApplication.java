package com.hotel.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;

@SpringBootApplication
public class NotificationServiceApplication {

	public static final String RESERVATION_QUEUE = "reservation.queue";
	public static final String RESERVATION_EXCHANGE = "reservation.exchange";
	public static final String RESERVATION_ROUTING_KEY = "reservation.routingkey";

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	// -------------------- RabbitMQ Configuration --------------------

	@Bean
	public Queue reservationQueue() {
		return new Queue(RESERVATION_QUEUE, true);
	}

	@Bean
	public TopicExchange reservationExchange() {
		return new TopicExchange(RESERVATION_EXCHANGE);
	}

	@Bean
	public Binding reservationBinding(Queue reservationQueue, TopicExchange reservationExchange) {
		return BindingBuilder
				.bind(reservationQueue)
				.to(reservationExchange)
				.with(RESERVATION_ROUTING_KEY);
	}
}