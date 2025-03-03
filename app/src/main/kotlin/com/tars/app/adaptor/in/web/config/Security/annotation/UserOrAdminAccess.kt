package com.tars.app.adaptor.`in`.web.config.Security.annotation

import org.springframework.security.access.prepost.PreAuthorize

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAnyRole('USER','ADMIN')")
annotation class UserOrAdminAccess {
}