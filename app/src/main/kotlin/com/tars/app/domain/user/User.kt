package com.tars.app.domain.user

import java.time.LocalDate

data class User(
    val id: Long? = null,

    val email: String,
    val password: String,

    val ssn: String,
    val phoneNumber: String,
    val name: String,
    val address: String?,
    val birthDate: LocalDate?,

    val roles: Set<String> = setOf("ROLE_USER")
) {

    fun changeAddress(newAddress: String?): User =
        this.copy(address = newAddress)

    fun addRole(role: String): User =
        this.copy(roles = this.roles + role)

}