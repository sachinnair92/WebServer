package com.grocberry.user;



import javax.ws.rs.*;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import io.swagger.annotations.*;
import org.bson.Document;
import com.grocberry.raspberry.raspberry;
import org.json.JSONObject;

import java.math.BigInteger;

/**
 * Created by Voodoo on 27/11/15.
 */
@Api(value = "User")
@Path("/user/")
public class User {

    int is_registered=0;
    MongoClientURI connectionString = new MongoClientURI("mongodb://voodoo:722446@ds059804.mongolab.com:59804/grocberry");
    MongoClient mongoClient = new MongoClient(connectionString);
    MongoDatabase db = mongoClient.getDatabase(connectionString.getDatabase());
    JSONObject obj;
    @GET
    @Path("/checkUser")
    @Produces("application/json")
    @ApiOperation(value = "check if user exists and return the raspberry pi available.If user doesn't exist this function will add a new user")
    public String checkUser(@QueryParam("user_id") String user_id,@QueryParam("name") String name,@QueryParam("email") String email,@QueryParam("platform")  String platform) {
        obj= new JSONObject();
        is_registered=0;
        FindIterable<Document> iterable = db.getCollection("user").find(new Document("user_id", user_id));
        iterable.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                is_registered=1;
            }

        });

        raspberry rs=new raspberry();
        if(is_registered==0)
        {
            Boolean is_Added=addUser(user_id,name,email,platform);
            if(is_Added==true) {
                obj.put("message", "user added");
                return String.valueOf(obj);
            }else
            {
                obj.put("message", "Some error occurred user not added");
                return String.valueOf(obj);
            }
        }
        else{
            String msg=rs.fetchRaspberry(user_id);

            if(msg.equals("No raspberry added")||msg.equals("Some error occurred"))
            {
                obj.put("message", msg);
                return String.valueOf(obj);
            }else
            {
                return msg;
            }


        }
    }

    @GET
    @Path("/getUserInfo")
    @Produces("application/json")
    @ApiOperation(value = "This api fetches the details of a particular user")
    public String getUserInfo(@QueryParam("user_id") String user_id){

        try {

            obj= new JSONObject();
            is_registered=0;
            FindIterable<Document> iterable = db.getCollection("user").find(new Document("user_id", user_id));
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    is_registered=1;
                    document.get("user_id");
                    obj.put("user_id", document.get("user_id"));
                    obj.put("name", document.get("name"));
                    obj.put("email_id", document.get("email_id"));
                    obj.put("platform", document.get("platform"));
                }
            });
            if(is_registered==1)
            {
                return String.valueOf(obj);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        obj.put("message", "user not found");
        return String.valueOf(obj);
    }


    public boolean addUser(String user_id, String name,String email, String platform ){
        MongoCollection<Document> collection = db.getCollection("user");
        try {
            if (user_id != null && name != null && platform != null) {
                Document doc = new Document("user_id", user_id)
                        .append("name", name)
                        .append("email_id", email)
                        .append("platform", platform);
                collection.insertOne(doc);
                return true;
            }
            return false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
