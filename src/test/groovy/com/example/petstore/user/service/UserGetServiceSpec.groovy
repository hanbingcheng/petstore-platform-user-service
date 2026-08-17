package com.example.petstore.user.service

import com.example.petstore.common.exception.ResourceNotFoundException
import com.example.petstore.user.entity.UserEntity
import com.example.petstore.user.mapper.UserMapper
import com.example.petstore.user.model.User

import spock.lang.Specification
import spock.lang.Subject

class UserGetServiceSpec extends Specification {

	@Subject
	UserGetService userGetService

	UserMapper userMapper = Mock()

	def setup() {
		userGetService = new UserGetService(userMapper)
	}

	def "ユーザーIDで情報を取得できること"() {
		given:
		def entity = UserEntity.builder()
				.id(1L).name("Taro Yamada").email("taro@example.com")
				.build()

		when:
		userMapper.findById(1L) >> Optional.of(entity)
		def result = userGetService.execute(1L)

		then:
		result.id == 1L
		result.name == "Taro Yamada"
		result.email == "taro@example.com"
	}

	def "存在しないユーザーIDを指定するとResourceNotFoundExceptionが発生すること"() {
		when:
		userMapper.findById(999L) >> Optional.empty()
		userGetService.execute(999L)

		then:
		thrown(ResourceNotFoundException)
	}
}
