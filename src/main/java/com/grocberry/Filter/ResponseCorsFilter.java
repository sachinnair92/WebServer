package com.grocberry.Filter;



import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;


/**
 * Created by Voodoo on 03/12/15.
 */
@Provider
public class ResponseCorsFilter implements ContainerResponseFilter {


    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {

        containerResponseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        containerResponseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        containerResponseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        containerResponseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        containerResponseContext.getHeaders().add("Access-Control-Max-Age", "1209600");

       // cres.getHttpHeaders().add("Access-Control-Allow-Origin", "*");
        //cres.getHttpHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
       // cres.getHttpHeaders().add("Access-Control-Allow-Credentials", "true");
       // cres.getHttpHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        //cres.getHttpHeaders().add("Access-Control-Max-Age", "1209600");
    }
}
