package com.example.petstore.user.integration

import com.example.petstore.user.model.CreateUserRequest
import com.example.petstore.user.model.User as ApiUser
import org.springframework.http.HttpStatus

import ch.qos.logback.classic.Level

class UserApiIntegrationSpec extends BaseIntegrationSpec {

    // ========================================
    // テスト: ユーザー登録
    // ========================================
    def "ユーザーを登録できること"() {
        given:
        def request = new CreateUserRequest()
            .name("Test User")
            .email("test@example.com")
            .password("password123")

        when:
        def response = null
        def logEvents = captureLogEvents {
            response = restTemplate.postForEntity("${getBaseUrl()}/users", request, ApiUser)
        }

        then:
        // レスポンス検証
        response.statusCode == HttpStatus.CREATED
        response.body.name == "Test User"
        response.body.email == "test@example.com"
        response.body.id != null

        // [DB Assert] usersテーブルに正常に登録されたことを検証
        def userId = response.body.id
        def userRows = jdbcTemplate.queryForList("SELECT * FROM users WHERE id = ?", userId)
        assert userRows.size() == 1
        assert userRows[0].name == "Test User"
        assert userRows[0].email == "test@example.com"
        assert userRows[0].password_hash != null

        // ログの検証
        logEvents[0].level == Level.INFO
        logEvents[0].formattedMessage == "[I000001] [UserCreateService]処理開始1: input={request=class CreateUserRequest {\n    name: Test User\n    email: test@example.com\n    password: password123\n}}"
        logEvents[logEvents.size() - 1].level == Level.INFO
        logEvents[logEvents.size() - 1].formattedMessage == "[I000002] [UserCreateService]処理完了: result=class User {\n    id: 1\n    name: Test User\n    email: test@example.com\n    createdAt: null\n}"
    }

    // ========================================
    // テスト: メールアドレス重複
    // ========================================
    def "既存のメールアドレスで登録しようとすると409エラーが返ること"() {
        given:
        def request = new CreateUserRequest()
            .name("Test User")
            .email("duplicate@example.com")
            .password("password123")

        // 1回目は成功
        restTemplate.postForEntity("${getBaseUrl()}/users", request, ApiUser)

        when:
        // 2回目は重複エラー
        def response = null
        def logEvents = captureLogEvents {
            response = restTemplate.postForEntity("${getBaseUrl()}/users", request, Map)
        }

        then:
        // レスポンス検証
        response.statusCode == HttpStatus.CONFLICT
        response.body.code == "E002001"

        // [DB Assert] 重複登録は発生せず、usersテーブルには1件のみ存在することを検証
        def userCount = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM users WHERE email = ?", Integer.class, "duplicate@example.com")
        assert userCount == 1

        // ログの検証
        logEvents[0].level == Level.INFO
        logEvents[0].formattedMessage == "[I000001] [UserCreateService]処理開始1: input={request=class CreateUserRequest {\n    name: Test User\n    email: duplicate@example.com\n    password: password123\n}}"
        logEvents[logEvents.size() - 2].level == Level.ERROR
        logEvents[logEvents.size() - 2].formattedMessage == "[E000017] [UserCreateService]処理異常終了: User already exists with email: duplicate@example.com"
        logEvents[logEvents.size() - 1].level == Level.WARN
        logEvents[logEvents.size() - 1].formattedMessage == "Duplicate resource: User already exists with email: duplicate@example.com"
    }
}