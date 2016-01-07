package com.grocberry.raspberry;

import com.grocberry.Subscriber.Mqttsubscriber;
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
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.json.JSONObject;

import javax.ws.rs.*;

import java.util.Set;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
//import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by Voodoo on 29/11/15.
 */
@Api(value = "Raspberry")
@Path("/raspberry/")
public class raspberry implements MqttCallback {
    MongoClientURI connectionString = new MongoClientURI("mongodb://voodoo:722446@ds059804.mongolab.com:59804/grocberry");
    MongoClient mongoClient = new MongoClient(connectionString);
    MongoDatabase db = mongoClient.getDatabase(connectionString.getDatabase());
    MongoCollection<Document> collection = db.getCollection("raspberry");
    boolean israspberryadded=false;
    JSONObject obj;
    int is_added=0,flag=0;





    @POST
    @Path("/addRaspberry")
    @Produces("application/json")
    @ApiOperation(value = "This api adds new raspberry pi for a specific user (Before adding check if raspberry is added to server)")
    public String addRaspberry(@FormParam("user_id") String user_id,@FormParam("rasp_id") String rasp_serial_no,@FormParam("rasp_name") String rasp_name){

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

    @POST
    @Path("/removeRaspberry")
    @Produces("application/json")
    @ApiOperation(value = "This api removes raspberry pi")
    public String removeRaspberry(@FormParam("user_id") String user_id,@FormParam("rasp_id") String rasp_serial_no) {
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


    @POST
    @Path("/editRaspberry")
    @Produces("application/json")
    @ApiOperation(value = "This api edits the name of a particular raspberry pi")
    public String editRaspberry(@FormParam("user_id") String user_id,@FormParam("rasp_id") String rasp_serial_no,@FormParam("rasp_name") String rasp_name){
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


    @GET
    @Path("/checkRaspberry")
    @Produces("application/json")
    @ApiOperation(value = "This api checks if raspberry is added to server")
    public String checkRaspberry(@QueryParam("rasp_id") String rasp_serial_no){

        try {
            obj = new JSONObject();
            is_added=0;
            MongoCollection<Document> collection1 = db.getCollection("raspberryList");
            FindIterable<Document> iterable = collection1.find(new Document("rasp_serial_no", rasp_serial_no));
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    is_added=1;
                }

            });



            if(is_added==1) {
                    obj.put("message", "true");
                    return String.valueOf(obj);
            }
            else
            {
                obj.put("message", "false");
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

    @POST
    @Path("/updateRaspberryList")
    @Produces("application/json")
    @ApiOperation(value = "This api updates the ip address of the raspberry pi (Dont use this)")
    public String updateRaspberryList(@FormParam("rasp_id") String rasp_serial_no,@FormParam("rasp_ip") String rasp_ip){

        try {
            obj = new JSONObject();
            is_added=0;
            MongoCollection<Document> collection1 = db.getCollection("raspberryList");
            FindIterable<Document> iterable = collection1.find(new Document("rasp_serial_no", rasp_serial_no));
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    is_added = 1;
                }

            });



            if(is_added==1) {

                System.out.println("lready added");
                if (rasp_serial_no != null && rasp_ip!=null) {
                    UpdateResult ur = collection1.updateOne(new Document("rasp_serial_no", rasp_serial_no), new Document("$set", new Document("rasp_ip", rasp_ip)));

                    killExistingThread(rasp_serial_no);
                    Thread t=(new Thread(new Mqttsubscriber(rasp_serial_no,rasp_ip)));
                    t.start();



                    if (ur.getModifiedCount() != 0) {
                        obj.put("message", "true");
                        return String.valueOf(obj);
                    }else
                    {
                        obj.put("message", "NoModification");
                        return String.valueOf(obj);
                    }




                }

                obj.put("message", "false");
                return String.valueOf(obj);
            }
            else
            {
                System.out.print("new added");
                Thread t=(new Thread(new Mqttsubscriber(rasp_serial_no,rasp_ip)));
                t.start();


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

    String t_id;

    public void killExistingThread(String rasp_serial_no){

        System.out.println("going to kill");
        MongoCollection<Document> collection1 = db.getCollection("raspberryList");
        FindIterable<Document> iterable = collection1.find(new Document("rasp_serial_no", rasp_serial_no));
        iterable.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                is_added = 1;
                t_id = String.valueOf(document.get("Thread_id"));
            }
        });
        System.out.println("going to kill"+t_id);

        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread currentThread = Thread.currentThread();
        ThreadGroup threadGroup = getRootThreadGroup(currentThread);
        int allActiveThreads = threadGroup.activeCount();
        Thread[] allThreads = new Thread[allActiveThreads];
        threadGroup.enumerate(allThreads);

        for (Thread thread : threadSet) {
            String id = String.valueOf(thread.getId());
            System.out.println("matching with "+id);
            if(id.equals(t_id))
            {
                thread.interrupt();
                System.out.println("killed");
            }
        }

    }

    private static ThreadGroup getRootThreadGroup(Thread thread) {
        ThreadGroup rootGroup = thread.getThreadGroup();
        while (true) {
            ThreadGroup parentGroup = rootGroup.getParent();
            if (parentGroup == null) {
                break;
            }
            rootGroup = parentGroup;
        }
        return rootGroup;
    }

    private final Logger logger = Logger.getLogger(String.valueOf(raspberry.class));

    @GET
    @Path("/Testing")
    @Produces("application/json")

    public void test(@QueryParam("user_id") String user_id) {
        String topic        = "outTopic";
        //String content      = "hello from voodoo";
        //int qos             = 2;
        String broker       = "tcp://101.63.94.87:1883";
        String clientId     = "Voodoo Pc";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            logger.info("Connecting to broker: " + broker);
            sampleClient.setCallback(this);
            sampleClient.connect(connOpts);
            logger.info("Connected");
            //System.out.println("Publishing message: "+content);
            //MqttMessage message = new MqttMessage(content.getBytes());
            //message.setQos(qos);

            int subQoS = 0;
            sampleClient.subscribe(topic, subQoS);
            logger.info("Subscribed");
            //sampleClient.subscribe(topic);
            while(true){

            }
            //System.out.println("Message published");
        } catch(MqttException me) {
            logger.info("reason " + me.getReasonCode());
            logger.info("msg " + me.getMessage());
            logger.info("loc " + me.getLocalizedMessage());
            logger.info("cause " + me.getCause());
            logger.info("excep "+me);
            me.printStackTrace();
        }

    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        logger.info("Topic: " + s + " Message: " + new String(mqttMessage.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

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
            obj.put("no_of_raspberry", flag);
            obj.put("message", "fetched");
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
