package com.grocberry.Subscriber;

import com.grocberry.raspberry.raspberry;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

/**
 * Created by sachin on 13/11/15.
 */
public class Mqttsubscriber implements Runnable,MqttCallback {

    private final Logger logger = LoggerFactory.getLogger(Mqttsubscriber.class);

    String rasp_id;    String rasp_ip;

    public Mqttsubscriber(String raspi_id,String raspi_ip){
         rasp_id=raspi_id;
        rasp_ip=raspi_ip;

    }


    @Override
    public void run() {
        String topic        = "SensorData";
        String broker       = "tcp://"+rasp_ip+":1883";
        String clientId     = "WebServer";
        MemoryPersistence persistence = new MemoryPersistence();

        UpdateRaspberry();
        msgList=new String[3];
        while(!Thread.currentThread().isInterrupted()) {
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
                while (!Thread.currentThread().isInterrupted()) {

                }
                System.out.print("xited");
                //System.out.println("Message published");
            } catch (MqttException me) {
//                logger.info("reason " + me.getReasonCode());
//                logger.info("msg " + me.getMessage());
//                logger.info("loc " + me.getLocalizedMessage());
//                logger.info("cause " + me.getCause());
//                logger.info("excep " + me);
//                me.printStackTrace();
            }
       }
        System.out.println("Interupted");
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    String msgList[];
    int index=0;

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {


        String msg=new String(mqttMessage.getPayload());
       logger.info("Topic: " + s + " Message: " + msg);


        String val[]=msg.split(" ");


        if(!msg.contains("Sending") ){

            String Contid=val[1].replaceAll(":","");

            String Percentage="0";

            String temp=val[3].replaceAll("cm","");
            float x=Float.parseFloat(temp);
            if (val[4].equals("small")) {

                x=(10.30f-x);
                float per=((x / 10.14f) * 100);
                Percentage=String.valueOf(per);
                if(per>100)
               {
                    Percentage="100";
                }else if(per<1)
                {
                    Percentage="0";
                }



            } else if (val[4].equals("medium")) {

                x=(14.37f-x);
                float per=((x / 14.37f) * 100);
                Percentage=String.valueOf(per);
                if(per>100)
                {
                    Percentage="100";
                }else if(per<1)
                {
                    Percentage="0";
                }

            } else if (val[4].equals("large")) {

                x=(21.68f-x);
                float per=((x / 21.68f) * 100);
                Percentage=String.valueOf(per);
                if(per>100)
                {
                    Percentage="100";
                }else if(per<1)
                {
                    Percentage="0";
                }

            } else {
                return;
            }

            if(msgList[0].equals(msgList[1]) && msgList[1].equals(msgList[2]))
            {
             addContainer(Contid,rasp_id,Percentage);
             msgList=new String[3];
                index=0;
            }else
            {

                msgList[index]=Percentage;
                index=((index+1)%3);
            }



        }


    }



    int is_registered=0;

    public void addContainer(String Cont_id,String rasp_id,String Quan){

        MongoCollection<Document> collection = db.getCollection("containers");
        try {
            obj= new JSONObject();


            is_registered=0;
            FindIterable<Document> iterable = db.getCollection("containers").find(new Document("container_id", Cont_id));
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    is_registered =1 ;
                }

            });



            if(is_registered==1)
            {
                UpdateResult ur = collection.updateOne(new Document("rasp_id", rasp_id), new Document("$set", new Document("quantity", Quan )));

            }else{

                Document doc = new Document("rasp_id", rasp_id)
                        .append("container_id",Cont_id)
                        .append("container_name", "null")
                        .append("quantity", Quan)
                        .append("container_type", "null");
                collection.insertOne(doc);
                obj.put("message", "true");
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }









    MongoClientURI connectionString = new MongoClientURI("mongodb://voodoo:722446@ds059804.mongolab.com:59804/grocberry");
    MongoClient mongoClient = new MongoClient(connectionString);
    MongoDatabase db = mongoClient.getDatabase(connectionString.getDatabase());
    JSONObject obj;
    int is_added=0;

    public void UpdateRaspberry(){
        try {


            MongoCollection<Document> collection1 = db.getCollection("raspberryList");
            FindIterable<Document> iterable = collection1.find(new Document("rasp_id", rasp_id));
            is_added=0;
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    is_added = 1;
                }

            });

            if(is_added==1) {

                if (rasp_id!= null && rasp_ip!=null) {
                    UpdateResult ur = collection1.updateOne(new Document("rasp_id", rasp_id), new Document("$set", new Document("rasp_ip", rasp_ip)));
                    ur = collection1.updateOne(new Document("rasp_id", rasp_id), new Document("$set", new Document("Thread_id",Thread.currentThread().getId())));

                }
            }
            else
            {

                if (rasp_id != null && rasp_ip!=null) {
                    Document doc = new Document("rasp_id", rasp_id)
                            .append("rasp_ip", rasp_ip).append("Thread_id", String.valueOf(Thread.currentThread().getId()));
                    collection1.insertOne(doc);

                }

            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
