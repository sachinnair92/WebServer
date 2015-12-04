package com.grocberry.raspberry;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.swagger.annotations.*;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import java.math.BigInteger;

/**
 * Created by Voodoo on 29/11/15.
 */
@Api(value = "raspberry")
@Path("/raspberry/")
public class raspberry {
    MongoClientURI connectionString = new MongoClientURI("mongodb://voodoo:722446@ds059804.mongolab.com:59804/grocberry");
    MongoClient mongoClient = new MongoClient(connectionString);
    MongoDatabase db = mongoClient.getDatabase(connectionString.getDatabase());
    boolean israspberryadded=false;
    JSONObject obj;






    @GET
    @Path("/addraspberry")
    @Produces("application/json")
    @ApiOperation(value = "Use this to add new raspberry pi for a specific user")
    public String addRaspberry(@QueryParam("user_id") BigInteger user_id,@QueryParam("rasp_id") Long rasp_id){

        try {
            MongoCollection<Document> collection = db.getCollection("raspberry");
            if (user_id != null && rasp_id != null) {
                Document doc = new Document("user_id", user_id)
                        .append("no_of_raspberry", 1)
                        .append("rasp1_id", rasp_id)
                        .append("rasp1_name","null");
                collection.insertOne(doc);
                return "true";
            }
            return "false";
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "false";
    }


    public String fetchRaspberry(BigInteger user_id) {
        try {
            FindIterable<Document> iterable = db.getCollection("raspberry").find(new Document("user_id", user_id));
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    israspberryadded = true;
                    int no_of_raspberry = (int) document.get("no_of_raspberry");

                    obj = new JSONObject();
                    obj.put("user_id", document.get("user_id"));
                    obj.put("no_of_raspberry", no_of_raspberry);
                    System.out.println(obj.toString());
                    for (int i = 1; i <= no_of_raspberry; i++) {
                        obj.put("rasp" + i + "_id", document.get("rasp" + i + "_id"));
                        obj.put("rasp" + i + "_name", document.get("rasp" + i + "_name"));
                    }

                }

            });

            if (israspberryadded) {
                return String.valueOf(obj);
            } else {
                return "No raspberry added";
            }
        } catch (Exception e) {
            return "Some error occurred";
        }
    }




}
