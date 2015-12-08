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

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by Voodoo on 29/11/15.
 */
@Api(value = "raspberry")
@Path("/raspberry/")
public class raspberry {
    MongoClientURI connectionString = new MongoClientURI("mongodb://voodoo:722446@ds059804.mongolab.com:59804/grocberry");
    MongoClient mongoClient = new MongoClient(connectionString);
    MongoDatabase db = mongoClient.getDatabase(connectionString.getDatabase());
    MongoCollection<Document> collection = db.getCollection("raspberry");
    boolean israspberryadded=false;
    JSONObject obj;
    int is_registered=0,flag=0;





    @GET
    @Path("/addraspberry")
    @Produces("application/json")
    @ApiOperation(value = "Use this to add new raspberry pi for a specific user")
    public String addRaspberry(@QueryParam("user_id") String user_id,@QueryParam("rasp_id") Long rasp_id,@QueryParam("rasp_name") String rasp_name){

        try {

                MongoCollection<Document> collection = db.getCollection("raspberry");
                if (user_id != null && rasp_id != null && rasp_name !=null) {
                    Document doc = new Document("user_id", user_id)
                                .append("rasp_serial", rasp_id)
                                .append("rasp_name",rasp_name);
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


    public String fetchRaspberry(String user_id) {
        try {
            FindIterable<Document> iterable = db.getCollection("raspberry").find(new Document("user_id", user_id));
            flag=0;
            obj = new JSONObject();
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    israspberryadded = true;
                    if(flag==0)
                    {
                        obj.put("user_id", document.get("user_id"));
                    }
                    obj.put("rasp" + (flag+1) + "_id", document.get("rasp" + (flag+1) + "_id"));
                    obj.put("rasp" + (flag+1) + "_name", document.get("rasp" + (flag+1) + "_name"));

                    flag++;
                }

            });
            obj.put("no_of_raspberry",flag);
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
