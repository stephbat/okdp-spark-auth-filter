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

package io.okdp.spark.authc.utils;

import static java.util.Optional.ofNullable;

import io.okdp.spark.authc.config.Constants;
import io.okdp.spark.authc.exception.AuthenticationException;
import io.okdp.spark.authc.exception.OidcClientException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/** Authentication utility methods */
@Slf4j
public class HttpAuthenticationUtils implements Constants {

  /**
   * Get the cookie content
   *
   * @param cookieName the cookie name
   * @param request the {@link ServletRequest}
   * @return the cookie content if the cookie exists or return empty if it does not exist
   */
  public static Optional<String> getCookieValue(String cookieName, ServletRequest request) {
    Optional<Cookie[]> maybeCookie = ofNullable(((HttpServletRequest) request).getCookies());
    return maybeCookie.flatMap(
        cookies ->
            Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .map(Cookie::getValue)
                .findAny());
  }

  /**
   * Get the domain from an url
   *
   * @param url the url
   * @return the url domain
   */
  public static String domain(String url) {
    try {
      return new URL(url).getHost();
    } catch (MalformedURLException e) {
      throw new OidcClientException(e.getMessage(), e);
    }
  }

  /** Sends an error response to the client using the specified status code and message */
  public static void sendError(ServletResponse response, int statusCode, String message) {
    try {
      ((HttpServletResponse) response).sendError(statusCode, message);
    } catch (IOException e) {
      throw new AuthenticationException(e.getMessage(), e);
    }
  }
}
