package com.tars.app.adaptor.`in`.security.service

import com.tars.app.domain.factory.UserFactory
import com.tars.app.outport.user.UserRepositoryPort
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Spring Security 에서 사용자 인증에 필요한 UserDetails 를 로드하는 서비스
 */
@Service
class CustomUserDetailsService(
    private val userRepositoryPort: UserRepositoryPort,
    private val userFactory: UserFactory
) : UserDetailsService {
    
    private val log = LoggerFactory.getLogger(this::class.java)
    
    /**
     * 이메일로 사용자 정보를 조회하여 UserDetails 형태로 변환합니다.
     * Spring Security 의 인증 매커니즘에서 사용됩니다.
     * 
     * @param email 사용자 이메일 (username)
     * @return UserDetails 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    override fun loadUserByUsername(email: String): UserDetails {
        log.debug("사용자 정보 로드: $email")

        val userEntity = userRepositoryPort.findByEmail(email)
            ?: throw UsernameNotFoundException("사용자를 찾을 수 없습니다: $email")

        val user = userFactory.reconstitute(userEntity)

        val authorities = user.getRoles()
            .map { SimpleGrantedAuthority(it) }
            .toSet()
        
        // Spring Security 의 User 객체 생성
        return User.builder()
            .username(user.credentials.email)          // 사용자 이메일을 username으로 사용
            .password(user.credentials.hashedPassword) // 해시된 비밀번호
            .authorities(authorities)                  // 권한 정보
            .accountExpired(false)                     // 계정 만료 여부
            .accountLocked(false)                      // 계정 잠금 여부
            .credentialsExpired(false)                 // 자격 증명 만료 여부
            .disabled(false)                           // 계정 비활성화 여부
            .build()
    }
} 