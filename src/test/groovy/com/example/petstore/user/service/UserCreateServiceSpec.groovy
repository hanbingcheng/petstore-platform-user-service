package com.example.petstore.user.service

import org.springframework.security.crypto.password.PasswordEncoder

import com.example.petstore.common.exception.DuplicateResourceException
import com.example.petstore.user.entity.UserEntity
import com.example.petstore.user.mapper.UserMapper
import com.example.petstore.user.model.CreateUserRequest
import com.example.petstore.user.model.User

import spock.lang.Specification
import spock.lang.Subject

class UserCreateServiceSpec extends Specification {

	@Subject
	UserCreateService userCreateService

	UserMapper userMapper = Mock()
	PasswordEncoder passwordEncoder = Mock()

	def setup() {
		userCreateService = new UserCreateService(userMapper, passwordEncoder)
	}

	def "ユーザーを登録できること"() {
		given:
		def request = new CreateUserRequest()
				.name("Taro Yamada")
				.email("taro@example.com")
				.password("password123")

		when:
		passwordEncoder.encode("password123") >> "hashed_password"
		userMapper.existsByEmail("taro@example.com") >> false
		userMapper.insert(_ as UserEntity) >> { UserEntity u -> u.setId(1L) }
		def result = userCreateService.execute(request)

		then:
		result.id == 1L
		result.name == "Taro Yamada"
		result.email == "taro@example.com"
	}

	def "パスワードがハッシュ化されること"() {
		given:
		def request = new CreateUserRequest()
				.name("Test")
				.email("test@example.com")
				.password("raw_password")

		when:
		passwordEncoder.encode("raw_password") >> "hashed_value"
		userMapper.existsByEmail("test@example.com") >> false
		userMapper.insert(_ as UserEntity) >> { UserEntity u -> u.setId(1L) }
		userCreateService.execute(request)

		then:
		1 * passwordEncoder.encode("raw_password")
	}

	def "メールアドレスが重複するとDuplicateResourceExceptionが発生すること"() {
		given:
		def request = new CreateUserRequest()
				.name("Taro")
				.email("duplicate@example.com")
				.password("password123")

		when:
		userMapper.existsByEmail("duplicate@example.com") >> true
		userCreateService.execute(request)

		then:
		thrown(DuplicateResourceException)
	}
}
