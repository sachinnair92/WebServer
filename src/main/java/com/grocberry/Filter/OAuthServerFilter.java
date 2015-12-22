package com.grocberry.Filter;


import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Base64;
import java.util.StringTokenizer;

/**
 * Created by sachin on 15/10/15.
 */

@Provider
public class OAuthServerFilter implements ContainerRequestFilter {



    private boolean isAuthValid(String authCredentials){

        if (null == authCredentials)
            return false;
        // header value format will be "Basic encodedstring" for Basic
        // authentication. Example "Basic YWRtaW46YWRtaW4="

        final String encodedUserPassword = authCredentials.replaceFirst("Basic"
                + " ", "");
        System.out.println("encoded userpass is "+encodedUserPassword);
        String usernameAndPassword = null;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedUserPassword);
            usernameAndPassword = new String(decodedBytes, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("userpass is "+usernameAndPassword);
        if(usernameAndPassword != null) {
            final StringTokenizer tokenizer = new StringTokenizer(
                    usernameAndPassword, ":");
            final String username = tokenizer.nextToken();
            final String password = tokenizer.nextToken();
            System.out.println("U:" + username + "--P:" + password);

            // we have fixed the userid and password as admin
            // call some UserService/LDAP here
            boolean authenticationStatus = "swagdocuser".equals(username)
                    && "WoopSwag123".equals(password);
            return authenticationStatus;
        }
        return false;
    }

    public static final String AUTHENTICATION_HEADER = "Authorization";

    @Override
    public void filter(ContainerRequestContext containerRequest) throws IOException {
        String authCredentials = containerRequest
                .getHeaderString(AUTHENTICATION_HEADER);
        if (!isAuthValid(authCredentials)) {
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
    }
}
