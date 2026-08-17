package com.example.petstore.user.message;

/**
 * user-service（機能別コード: 002）のメッセージコード定義。
 *
 * <p>形式: {メッセージ種別}{機能別コード}{コード枝番}（計7桁）
 */
public enum UserMessageCode {
  USER_DUPLICATE("E002001"),
  LOGIN_FAILED("E002002"),
  USER_NOT_FOUND("E002003");

  private final String code;

  UserMessageCode(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
