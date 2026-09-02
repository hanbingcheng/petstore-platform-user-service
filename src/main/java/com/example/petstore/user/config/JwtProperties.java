package com.example.petstore.user.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** JWT に関する設定（application.yaml の jwt.* をバインドする）。 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {

  /** HS256 署名用の秘密鍵。32 バイト（256 ビット）以上が必須。 */
  private String secret;

  /** トークンの有効期限。 */
  private Duration expiration;
}
