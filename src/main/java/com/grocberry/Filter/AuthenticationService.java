package com.grocberry.Filter;

import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by Voodoo on 13/12/15.
 */
public class AuthenticationService {
    public boolean authenticate(String credential) {
        if (null == credential) {
            return false;
        }
        // header value format will be "Basic encodedstring" for Basic
        // authentication. Example "Basic YWRtaW46YWRtaW4="
        final String encodedUserPassword = credential.replaceFirst("Basic" + " ", "");
        String usernameAndPassword = null;
        try {
            byte[] decodedBytes = new BASE64Decoder().decodeBuffer(encodedUserPassword);
            usernameAndPassword = new String(decodedBytes, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        final String username = tokenizer.nextToken();
        final String password = tokenizer.nextToken();

        // we have fixed the userid and password as admin
        // call some UserService/LDAP here
        boolean authenticationStatus = "admin".equals(username) && "admin".equals(password);
        return authenticationStatus;
    }
}