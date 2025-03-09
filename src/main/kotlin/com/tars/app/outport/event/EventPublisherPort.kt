package com.tars.app.outport.event

import com.tars.app.event.DomainEvent

interface EventPublisherPort {
    fun publish(event: DomainEvent)
} 