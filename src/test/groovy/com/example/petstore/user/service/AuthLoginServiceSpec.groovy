package com.example.petstore.user.service

import org.springframework.security.crypto.password.PasswordEncoder

import com.example.petstore.common.exception.UnauthorizedException
import com.example.petstore.user.entity.UserEntity
import com.example.petstore.user.mapper.UserMapper
import com.example.petstore.user.model.LoginRequest
import com.example.petstore.user.model.LoginResponse

import spock.lang.Specification
import spock.lang.Subject

class AuthLoginServiceSpec extends Specification {

	@Subject
	AuthLoginService authLoginService

	UserMapper userMapper = Mock()
	PasswordEncoder passwordEncoder = Mock()
	JwtService jwtService = Mock()

	def setup() {
		authLoginService = new AuthLoginService(userMapper, passwordEncoder, jwtService)
	}

	def "正しい認証情報でログインできること"() {
		given:
		def entity = UserEntity.builder()
				.id(1L).email("taro@example.com").passwordHash("hashed_password")
				.build()
		def request = new LoginRequest()
				.email("taro@example.com")
				.password("correct_password")

		when:
		userMapper.findByEmail("taro@example.com") >> Optional.of(entity)
		passwordEncoder.matches("correct_password", "hashed_password") >> true
		jwtService.generateToken(1L, "taro@example.com") >> "jwt-token"
		def result = authLoginService.execute(request)

		then:
		result.userId == 1L
		result.token != null
		!result.token.isEmpty()
	}

	def "間違ったパスワードでUnauthorizedExceptionが発生すること"() {
		given:
		def entity = UserEntity.builder()
				.id(1L).passwordHash("hashed_password")
				.build()
		def request = new LoginRequest()
				.email("taro@example.com")
				.password("wrong_password")

		when:
		userMapper.findByEmail("taro@example.com") >> Optional.of(entity)
		passwordEncoder.matches("wrong_password", "hashed_password") >> false
		authLoginService.execute(request)

		then:
		thrown(UnauthorizedException)
	}

	def "存在しないメールアドレスでUnauthorizedExceptionが発生すること"() {
		given:
		def request = new LoginRequest()
				.email("unknown@example.com")
				.password("password")

		when:
		userMapper.findByEmail("unknown@example.com") >> Optional.empty()
		authLoginService.execute(request)

		then:
		thrown(UnauthorizedException)
	}
}
