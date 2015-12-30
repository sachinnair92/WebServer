package com.grocberry.container;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Created by Voodoo on 15/12/15.
 */
@Api(value = "Container")
@Path("/container/")
public class container {
    MongoClientURI connectionString = new MongoClientURI("mongodb://voodoo:722446@ds059804.mongolab.com:59804/grocberry");
    MongoClient mongoClient = new MongoClient(connectionString);
    MongoDatabase db = mongoClient.getDatabase(connectionString.getDatabase());
    MongoCollection<Document> collection = db.getCollection("containers");
    JSONObject obj;
    int flag=0;
    boolean iscontaineradded=false;


    @GET
    @Path("/addContainer")
    @Produces("application/json")
    @ApiOperation(value = "Testing purpose only (Dont use this)")
    public String addContainer(@QueryParam("rasp_id") String rasp_serial_no,@QueryParam("container_id") String container_id,@QueryParam("container_name") String container_name,@QueryParam("quantity") String quantity,@QueryParam("container_type") String container_type){

        try {
            obj= new JSONObject();
            if (rasp_serial_no != null && container_id != null && quantity != null) {
                Document doc = new Document("rasp_serial_no", rasp_serial_no)
                        .append("container_id",container_id)
                        .append("container_name", container_name)
                        .append("quantity", quantity)
                        .append("container_type", container_type);
                collection.insertOne(doc);
                obj.put("message", "true");
                return String.valueOf(obj);
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
    @Path("/getAllContainerDetails")
    @Produces("application/json")
    @ApiOperation(value = "This api returns the details of all the container connected to a particular raspberry pi")
    public String getAllContainerDetails(@QueryParam("rasp_id") String rasp_serial_no)
    {

        try {
            FindIterable<Document> iterable = collection.find(new Document("rasp_serial_no", rasp_serial_no));
            flag=0;
            obj = new JSONObject();
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    iscontaineradded = true;
                    if(flag==0)
                    {
                        obj.put("rasp_serial_no", document.get("rasp_serial_no"));
                    }
                    obj.put("container" + (flag+1) + "_id", document.get("container_id"));
                    obj.put("container" + (flag+1) + "_name", document.get("container_name"));
                    obj.put("container" + (flag+1) + "_quantity", document.get("quantity"));
                    obj.put("container" + (flag+1) + "_type", document.get("container_type"));
                    flag++;
                }

            });
            obj.put("no_of_container", flag);
            System.out.print(iscontaineradded);
            if (iscontaineradded) {
                return String.valueOf(obj);
            } else {
                obj = new JSONObject();
                obj.put("message", "No container added");
                return String.valueOf(obj);
            }
        } catch (Exception e) {
            obj = new JSONObject();
            obj.put("message", "Some error occurred");
            return String.valueOf(obj);
        }
    }


    @GET
    @Path("/updateContainerDetails")
    @Produces("application/json")
    @ApiOperation(value = "This api updates the name of the container attached to a particular raspberry")
    public String updateContainerDetails(@QueryParam("rasp_id") String rasp_serial_no,@QueryParam("container_id") String container_id,@QueryParam("container_name") String container_name)
    {

        try {
            obj = new JSONObject();
            if (rasp_serial_no != null && container_id != null && container_name !=null) {
                UpdateResult ur = collection.updateOne(new Document("rasp_serial_no", rasp_serial_no).append("container_id", container_id), new Document("$set", new Document("container_name", container_name)));

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

}
