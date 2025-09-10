package co.com.pragma.model.notification.gateways;

import co.com.pragma.model.notification.Notification;
import reactor.core.publisher.Mono;

public interface NotificationPublisher {

    Mono<Void> publishNotification(Notification notification);

}
