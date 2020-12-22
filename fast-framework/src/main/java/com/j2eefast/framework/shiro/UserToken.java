/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.shiro;

import lombok.Data;
import org.apache.shiro.authc.UsernamePasswordToken;

@Data
public class UserToken  extends UsernamePasswordToken {

    private String loginType;

    public UserToken() {
    }

    public UserToken(final String username, final String password,
                     final String loginType,final  boolean rememberMe) {
        super(username, password,rememberMe);
        this.loginType = loginType;
    }
}
