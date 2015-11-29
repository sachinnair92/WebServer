package com.grocberry.user;



import javax.ws.rs.*;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Created by Voodoo on 27/11/15.
 */
@Path("user")
public class User {

    @GET
    @Path("/checkuser")
    @Produces("application/json")
    public String checkuser(@QueryParam("id") Long id,@QueryParam("name") String name,@QueryParam("email") String email,@QueryParam("platform") String platform) {
        MongoClientURI connectionString = new MongoClientURI("mongodb://voodoo:722446@ds059804.mongolab.com:59804/grocberry");
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase db = mongoClient.getDatabase(connectionString.getDatabase());
        MongoCollection<Document> collection = db.getCollection("user");

        FindIterable<Document> iterable = db.getCollection("user").find(new Document("user_id", id));

        iterable.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                System.out.println("yooooo");
                System.out.println(document);
            }
        });



        /*if(id!=null && name!=null && email!=null && platform!=null){
            Document doc = new Document("user_id", id)
                    .append("name", name)
                    .append("email_id", email)
                    .append("platform", platform);
            collection.insertOne(doc);
            return String.valueOf(id);
        }*/

        return String.valueOf("something is null");
    }
}
