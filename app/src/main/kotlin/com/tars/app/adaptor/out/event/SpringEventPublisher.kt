package com.tars.app.adaptor.out.event

import com.tars.app.event.DomainEvent
import com.tars.app.outport.event.EventPublisherPort
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class SpringEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) : EventPublisherPort {

    @Transactional
    override fun publish(event: DomainEvent) {
        applicationEventPublisher.publishEvent(event)
    }
} 