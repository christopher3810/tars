package com.tars.auth.service

import com.tars.auth.port.output.UserAuthPort
import com.tars.common.error.ErrorMessage
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
@Qualifier("authModuleUserDetailsService")
class CustomUserDetailsService(
    @Qualifier("appUserAuthAdapter") private val userAuthPort: UserAuthPort
) : UserDetailsService {

    /**
     * 이 구현에서는 username 파라미터를 이메일로 사용.
     * 
     * @param username 사용자의 이메일
     * @return Spring Security UserDetails
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    override fun loadUserByUsername(username: String): UserDetails {
        val userAuthDetails = userAuthPort.findUserByEmail(username)
            ?: throw UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.format(username))

        return User.builder()
            .username(userAuthDetails.email)
            .password(userAuthDetails.hashedPassword)
            .roles(*userAuthDetails.roles.map { it.removePrefix("ROLE_") }.toTypedArray())
            .build()
    }
} 