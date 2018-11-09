package com.vinod.spring.integration.integrationexample.router;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.router.AbstractMessageRouter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RetryTypeRouter extends AbstractMessageRouter {

    @Autowired
    @Qualifier("channelIntegestion")
    private MessageChannel channelIntegestion;

    @Autowired
    @Qualifier("channelProcessing")
    private MessageChannel  channelProcessing;

    @Override
    protected Collection<MessageChannel> determineTargetChannels(Message<?> message) {
        String payload = message.getPayload().toString();

        List<MessageChannel> channels = new ArrayList<>();
        if("ingestion".equals(payload)) {
            channels.add(channelIntegestion);
        } else {
            channels.add(channelProcessing);
        }
        return null;
    }
}
