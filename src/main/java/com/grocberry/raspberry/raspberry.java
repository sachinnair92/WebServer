package com.grocberry.raspberry;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.swagger.annotations.*;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;

import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by Voodoo on 29/11/15.
 */
@Api(value = "Raspberry")
@Path("/raspberry/")
public class raspberry {
    MongoClientURI connectionString = new MongoClientURI("mongodb://voodoo:722446@ds059804.mongolab.com:59804/grocberry");
    MongoClient mongoClient = new MongoClient(connectionString);
    MongoDatabase db = mongoClient.getDatabase(connectionString.getDatabase());
    MongoCollection<Document> collection = db.getCollection("raspberry");
    boolean israspberryadded=false;
    JSONObject obj;
    int is_added=0,flag=0;





    @GET
    @Path("/addraspberry")
    @Produces("application/json")
    @ApiOperation(value = "This api adds new raspberry pi for a specific user")
    public String addRaspberry(@QueryParam("user_id") String user_id,@QueryParam("rasp_id") Long rasp_serial_no,@QueryParam("rasp_name") String rasp_name){

        try {
               obj = new JSONObject();
                FindIterable<Document> iterable = collection.find(new Document("user_id", user_id).append("rasp_serial_no", rasp_serial_no));
                iterable.forEach(new Block<Document>() {
                    @Override
                    public void apply(final Document document) {
                        is_added=1;
                    }

                });



                if(is_added==0) {
                    if (user_id != null && rasp_serial_no != null && rasp_name != null) {
                        Document doc = new Document("user_id", user_id)
                                .append("rasp_serial_no", rasp_serial_no)
                                .append("rasp_name", rasp_name);
                        collection.insertOne(doc);
                        obj.put("message", "true");
                        return String.valueOf(obj);
                    }
                }
                else
                {
                    obj.put("message", "already added");
                    return String.valueOf(obj);
                }
            obj.put("message", "false");
            return String.valueOf(obj);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        obj.put("message", "false");
        return String.valueOf(obj);
    }

    @GET
    @Path("/removeraspberry")
    @Produces("application/json")
    @ApiOperation(value = "This api removes raspberry pi")
    public String removeRaspberry(@QueryParam("user_id") String user_id,@QueryParam("rasp_id") Long rasp_serial_no) {
        try {
            obj = new JSONObject();
            if (user_id != null && rasp_serial_no != null) {
                DeleteResult dr = collection.deleteOne(new Document("user_id", user_id).append("rasp_serial_no", rasp_serial_no));
                if (dr.getDeletedCount() != 0) {
                    obj.put("message", "true");
                    return String.valueOf(obj);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        obj.put("message", "false");
        return String.valueOf(obj);
    }


    @GET
    @Path("/editraspberry")
    @Produces("application/json")
    @ApiOperation(value = "This api edits the name of a particular raspberry pi")
    public String editRaspberry(@QueryParam("user_id") String user_id,@QueryParam("rasp_id") Long rasp_serial_no,@QueryParam("rasp_name") String rasp_name){
        try {
            obj = new JSONObject();
            if (user_id != null && rasp_serial_no != null && rasp_name !=null) {
                UpdateResult ur = collection.updateOne(new Document("user_id", user_id).append("rasp_serial_no", rasp_serial_no), new Document("$set", new Document("rasp_name", rasp_name)));

                if (ur.getModifiedCount() != 0) {
                    obj.put("message", "true");
                    return String.valueOf(obj);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        obj.put("message", "false");
        return String.valueOf(obj);
    }


        public String fetchRaspberry(String user_id) {
        try {
            FindIterable<Document> iterable = collection.find(new Document("user_id", user_id));
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
                    obj.put("rasp" + (flag+1) + "_serial_no", document.get("rasp_serial_no"));
                    obj.put("rasp" + (flag+1) + "_name", document.get("rasp_name"));

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
