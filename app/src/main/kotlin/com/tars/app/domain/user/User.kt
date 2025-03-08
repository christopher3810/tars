package com.tars.app.domain.user

import com.tars.common.error.ErrorMessage
import com.tars.app.domain.user.vo.UserCredentials
import com.tars.common.util.idGenerator.UserIdGeneratorCas

class User private constructor(
    val id: Long,
    val credentials: UserCredentials,
    private var address: String?,
    private var roles: Set<String>
) {
    companion object {
        const val ROLE_USER = "ROLE_USER"
        const val ROLE_ADMIN = "ROLE_ADMIN"

        /**
         * 신규 User 생성
         * Factory 를 통해서만 호출되어야 한다.
         * UserIdGenerator 를 사용하여 10자리 고유 ID를 생성.
         */
        internal fun create(
            credentials: UserCredentials,
            address: String?,
            roles: Set<String>
        ): User {
            val userId = UserIdGeneratorCas.generate()
            return User(
                id = userId,
                credentials = credentials,
                address = address,
                roles = roles
            )
        }

        /**
         * 영속성 저장소에서 로드된 데이터로 User 재구성
         */
        internal fun reconstitute(
            id: Long,
            credentials: UserCredentials,
            address: String?,
            roles: Set<String>
        ): User {
            return User(
                id = id,
                credentials = credentials,
                address = address,
                roles = roles
            )
        }
    }

    init {
        require(roles.isNotEmpty()) { ErrorMessage.EMPTY_ROLES.message }
    }

    fun changeAddress(newAddress: String?) {
        this.address = newAddress
    }

    fun addRole(role: String) {
        require(role.startsWith("ROLE_")) { ErrorMessage.INVALID_ROLE_FORMAT.message }
        this.roles = this.roles + role
    }

    fun getAddress(): String? = address
    fun getRoles(): Set<String> = roles.toSet()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as User
        return credentials.email == other.credentials.email
    }

    override fun hashCode(): Int {
        return credentials.email.hashCode()
    }
}