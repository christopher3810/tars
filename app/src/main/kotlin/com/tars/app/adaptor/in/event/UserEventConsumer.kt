package com.tars.app.adaptor.`in`.event

import com.tars.app.event.DomainEvent
import com.tars.app.event.UserEvent
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class UserEventConsumer {
    private val log = LoggerFactory.getLogger(javaClass)
    
    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleUserEvent(event: DomainEvent) {
        try {
            when (event) {
                is UserEvent.UserCreated -> handleUserCreated(event)
                is UserEvent.AddressChanged -> handleAddressChanged(event)
                is UserEvent.RoleAdded -> handleRoleAdded(event)
                else -> {} // 처리하지 않는 이벤트
            }
        } catch (e: Exception) {
            log.error("Failed to process event: $event", e)
            // 여기서 실패한 이벤트를 재시도 큐에 넣거나, 추적을위해 저장 고민.
        }
    }

    private fun handleUserCreated(event: UserEvent.UserCreated) {
        log.info("Processing UserCreated event asynchronously for user: ${event.email}")
        // 사용자 생성 후처리
        // ex) 웰컴 이메일 발송, 초기 설정 등
    }

    private fun handleAddressChanged(event: UserEvent.AddressChanged) {
        log.info("Processing AddressChanged event asynchronously for user: ${event.email}")
        // 주소 변경 후처리
        // ex) 주소 변경 알림 발송 등
    }

    private fun handleRoleAdded(event: UserEvent.RoleAdded) {
        log.info("Processing RoleAdded event asynchronously for user: ${event.email}")
        // 역할 추가 후처리
        // ex) 권한 변경 알림 등
    }
} 