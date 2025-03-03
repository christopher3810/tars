package com.tars.app.application.validator

import com.tars.app.application.validator.policy.ValidationPolicy
import org.springframework.stereotype.Component

@Component
class PolicyValidator (
    private val policies: List<ValidationPolicy>
) : ValidationPolicy {
    override fun validate(input: Map<String, Any?>) {
        policies.forEach { it.validate(input) }
    }
}