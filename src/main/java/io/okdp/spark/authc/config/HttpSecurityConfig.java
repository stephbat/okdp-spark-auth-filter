/*
 *    Copyright 2024 tosit.io
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.okdp.spark.authc.config;

import static java.util.Arrays.stream;

import io.okdp.spark.authc.provider.AuthProvider;
import io.okdp.spark.authc.provider.OidcAuthProvider;
import io.okdp.spark.authc.provider.TokenStore;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor(staticName = "create")
@Getter
@Accessors(fluent = true)
public class HttpSecurityConfig {

  private final List<Pattern> patterns = new ArrayList<>();
  @NonNull private OidcConfig oidcConfig;
  private TokenStore tokenStore;

  /**
   * Skip authentication for the requests with the provided URL patterns
   *
   * @param patterns the URL patterns to skip authentication for
   */
  public HttpSecurityConfig authorizeRequests(String... patterns) {
    this.patterns.addAll(stream(patterns).map(Pattern::compile).collect(Collectors.toList()));
    return this;
  }

  /**
   * The token store {@link TokenStore} implementation managing the access token persistence
   *
   * @see io.okdp.spark.authc.provider.TokenStore
   */
  public HttpSecurityConfig tokenStore(TokenStore tokenStore) {
    this.tokenStore = tokenStore;
    return this;
  }

  /** Configure the security rules */
  public AuthProvider configure() {
    return new OidcAuthProvider(this);
  }
}
