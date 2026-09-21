package com.example.petstore.user.service

import com.example.petstore.common.exception.UnauthorizedException
import com.example.petstore.user.model.LoginResponse
import com.example.petstore.user.model.RefreshTokenRequest

import spock.lang.Specification
import spock.lang.Subject

class RefreshTokenServiceSpec extends Specification {

	@Subject
	RefreshTokenService refreshTokenService

	JwtService jwtService = Mock()

	def setup() {
		refreshTokenService = new RefreshTokenService(jwtService)
	}

	def "有効なトークンで新しいトークンを再発行できること"() {
		given:
		def request = new RefreshTokenRequest().token("old-token")
		def claims = new JwtService.UserClaims(1L, "taro@example.com")

		when:
		jwtService.parseForRefresh("old-token") >> claims
		jwtService.generateToken(1L, "taro@example.com") >> "new-token"
		def result = refreshTokenService.execute(request)

		then:
		result.token == "new-token"
		result.userId == 1L
	}

	def "無効なトークンでUnauthorizedExceptionが発生すること"() {
		given:
		def request = new RefreshTokenRequest().token("invalid-token")

		when:
		jwtService.parseForRefresh("invalid-token") >> { throw new UnauthorizedException("Invalid or expired token") }
		refreshTokenService.execute(request)

		then:
		thrown(UnauthorizedException)
	}
}
