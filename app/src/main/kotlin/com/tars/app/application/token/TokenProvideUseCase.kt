package com.tars.app.application.token

import com.tars.app.domain.user.User
import io.jsonwebtoken.Jwts

interface TokenProvideUseCase {

    fun generateAccessToken(userDomain: User): String

}