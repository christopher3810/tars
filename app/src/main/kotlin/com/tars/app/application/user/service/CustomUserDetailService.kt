package com.tars.app.application.user.service

import com.tars.app.outport.user.UserRepositoryPort
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepositoryPort: UserRepositoryPort
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val domainUser = userRepositoryPort.findByEmail(username)
            ?: throw UsernameNotFoundException("사용자를 찾을 수 없습니다: $username")
        return User.builder()
            .username(domainUser.email)
            .password(domainUser.password)
            .roles(*domainUser.roles.toTypedArray())
            .build()
    }
}