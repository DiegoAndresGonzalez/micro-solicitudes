package co.com.pragma.sqs.sender;

import co.com.pragma.model.notification.Notification;
import co.com.pragma.model.notification.gateways.NotificationPublisher;
import co.com.pragma.sqs.sender.config.SQSSenderProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements NotificationPublisher {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;

    @Override
    public Mono<Void> publishNotification(Notification payload) {
        return Mono.fromRunnable(() -> {
            String message = String.format(
                    "{" +
                            "\"requestId\":%d, " +
                            "\"status\":\"%s\", " +
                            "\"email\":\"%s\", " +
                            "\"name\":\"%s\", " +
                            "\"loanType\":\"%s\", " +
                            "\"amount\":%d" +
                            "}",
                    payload.getRequestId(),
                    payload.getStatus(),
                    payload.getEmail(),
                    payload.getFullName(),
                    payload.getLoanType(),
                    payload.getAmount()
            );

            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(properties.queueUrl())
                    .messageBody(message)
                    .build();

            client.sendMessage(sendMessageRequest);
        });
    }

}
