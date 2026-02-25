package com.school.student.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


@Configuration
public class RabbitMQConfig {

    public static final String STUDENT_EXCHANGE = "student.exchange";
    public static final String STUDENT_QUEUE = "student.queue";
    public static final String STUDENT_ROUTING_KEY = "student.created";

    @Bean
    public TopicExchange studentExchange() {
        return new TopicExchange(STUDENT_EXCHANGE);
    }

    @Bean
    public Queue studentQueue() {
        return new Queue(STUDENT_QUEUE, true); // durable queue
    }

    @Bean
    public Binding binding(Queue studentQueue, TopicExchange studentExchange) {
        return BindingBuilder.bind(studentQueue).to(studentExchange).with(STUDENT_ROUTING_KEY);
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