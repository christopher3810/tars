package com.tars.app.event

sealed class UserEvent : DomainEvent {
    data class UserCreated(
        val email: String,
        val name: String
    ) : UserEvent()

    data class AddressChanged(
        val email: String,
        val newAddress: String?
    ) : UserEvent()

    data class RoleAdded(
        val email: String,
        val role: String
    ) : UserEvent()
} 