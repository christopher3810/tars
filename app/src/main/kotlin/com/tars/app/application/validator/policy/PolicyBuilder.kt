package com.tars.app.application.validator.policy

import com.tars.app.application.validator.PolicyValidator

/**
 * pojo builder 로 validator 반환.
 */
class PolicyBuilder{

    private val policies = mutableListOf<ValidationPolicy>()

    fun addPolicy(policy: ValidationPolicy): PolicyBuilder {
        policies.add(policy)
        return this
    }

    fun build(): PolicyValidator = PolicyValidator(policies)
}