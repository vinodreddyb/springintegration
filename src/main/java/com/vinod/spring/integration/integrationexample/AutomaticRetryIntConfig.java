package com.vinod.spring.integration.integrationexample;

import com.vinod.spring.integration.integrationexample.router.RetryTypeRouter;
import com.vinod.spring.integration.integrationexample.service.IngetionFlowRetryService;
import com.vinod.spring.integration.integrationexample.service.ProcessingFlowRetryService;
import com.vinod.spring.integration.integrationexample.service.TestService;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
@EnableRabbit
public class AutomaticRetryIntConfig {

    @Autowired
    ConnectionFactory connectionFactory;

    @Bean
    AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    Queue queue() {
        return new Queue("test", false);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange("");
    }

    @Bean
    Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(queue.getName());
    }


    @Bean
    IngetionFlowRetryService ingetionFlowRetryService() {
        return new IngetionFlowRetryService();
    }

    @Bean
    ProcessingFlowRetryService processingFlowRetryService() {
        return new ProcessingFlowRetryService();
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    TestService testService() {
        return new TestService();
    }


    @Bean
    public IntegrationFlow routerFlow() {
        return IntegrationFlows.from(Amqp.inboundAdapter(connectionFactory,queue()).configureContainer(cont -> cont.concurrentConsumers(1)))
                .transform(new JsonToObjectTransformer(Test.class))
                .<Test,String>route(s-> s.getId(),
                        m -> m.subFlowMapping("ingestion", inflow -> inflow.handle(ingetionFlowRetryService(),"printMessage"))
                .subFlowMapping("processing", proc -> proc.handle(processingFlowRetryService(),"printMessage"))
                ).channel(c -> c.direct("test"))
                .get();
    }

   /* @Bean
    public IntegrationFlow routeFlow() {
        return IntegrationFlows.from(Amqp.inboundAdapter(connectionFactory,queue()).configureContainer(cont -> cont.concurrentConsumers(1)))
                .transform(new JsonToObjectTransformer(Test.class))
                .<Test,String>route(s-> s.getId(),
                        m -> m.channelMapping("ingestion", "ingestion")
                                .channelMapping("processing", "processing")
                )
                .get();
    }


    @Bean
    public IntegrationFlow ingestionFlow() {
        return  IntegrationFlows.from("ingestion")
                .handle(ingetionFlowRetryService(),"printMessage")
                .channel("test")
                .get();
    }

    @Bean
    public IntegrationFlow processingFlow() {
        return  IntegrationFlows.from("processing")
                .handle(processingFlowRetryService(),"printMessage")

                .channel("test")
                .get();
    }*/

    @Bean
    public IntegrationFlow testFlow() {
        return  IntegrationFlows.from("test")
                .handle(testService(),"test")
                .get();
    }








}
