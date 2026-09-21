package com.example.petstore.user.service

import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.Date

import com.example.petstore.common.exception.UnauthorizedException
import com.example.petstore.user.config.JwtProperties

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys

import spock.lang.Specification
import spock.lang.Subject

class JwtServiceSpec extends Specification {

	@Subject
	JwtService jwtService

	JwtProperties properties

	def setup() {
		properties = new JwtProperties()
		properties.setSecret("test-secret-key-for-jwt-signing-0123456789abcdef")
		properties.setExpiration(Duration.ofHours(1))
		jwtService = new JwtService(properties)
	}

	def "ユーザーIDとメールアドレスを含むJWTが発行できること"() {
		when:
		def token = jwtService.generateToken(1L, "taro@example.com")

		then:
		token != null
		token.count(".") == 2 // header.payload.signature の3セグメント構成

		and: "同じ秘密鍵で検証し、クレームを取り出せること"
		def key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8))
		def claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload()

		claims.getSubject() == "1"
		claims.get("email", String.class) == "taro@example.com"
		claims.getExpiration() != null
		claims.getIssuedAt() != null
	}

	def "parseForRefreshで有効なトークンのクレームを取り出せること"() {
		given:
		def token = jwtService.generateToken(1L, "taro@example.com")

		when:
		def claims = jwtService.parseForRefresh(token)

		then:
		claims.userId() == 1L
		claims.email() == "taro@example.com"
	}

	def "parseForRefreshで有効期限切れのトークンでもクレームを取り出せること"() {
		given:
		def key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8))
		def expiredToken = Jwts.builder()
			.subject("1")
			.claim("email", "taro@example.com")
			.issuedAt(new Date(System.currentTimeMillis() - 7200000L))
			.expiration(new Date(System.currentTimeMillis() - 3600000L))
			.signWith(key)
			.compact()

		when:
		def claims = jwtService.parseForRefresh(expiredToken)

		then:
		claims.userId() == 1L
		claims.email() == "taro@example.com"
	}

	def "parseForRefreshで署名が不正なトークンの場合UnauthorizedExceptionが発生すること"() {
		given:
		def wrongKey = Keys.hmacShaKeyFor("wrong-secret-key-for-jwt-signing-0123456789".getBytes(StandardCharsets.UTF_8))
		def invalidToken = Jwts.builder()
			.subject("1")
			.signWith(wrongKey)
			.compact()

		when:
		jwtService.parseForRefresh(invalidToken)

		then:
		thrown(UnauthorizedException)
	}
}
