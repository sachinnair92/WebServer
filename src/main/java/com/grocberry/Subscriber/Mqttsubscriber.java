package com.grocberry.Subscriber;

import com.grocberry.container.container;
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
                msgList=new Float[2];
                msgList[0]=-1f;
                msgList[1]=-2f;
                logger.info("Subscribed");
                Cont_id1=new String[1];
                rasp_id1=new String[1];
                Quan1=new String[1];
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

    static Float msgList[];
    int index=0;
    static String Cont_id1[];
    static String rasp_id1[];
    static String Quan1[];
    static int No_of_Container=0;



    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {


        String msg=new String(mqttMessage.getPayload());
       logger.info("Topic: " + s + " Message: " + msg);



        logger.info("msg is "+msg);        String val[]=msg.split(" ");


        if(!msg.contains("Publishing Sensor Data") ){


            String Contid=val[1].replaceAll(":","");
            logger.info("Contid: "+Contid);
            String Percentage="0";
            logger.info("size is"+val.length);
            for(int k=0;k<val.length;k++)
            {
               logger.info("k:"+k+" val is "+val[k]);
            }
            String temp=val[3].replaceAll("cm", "");
            logger.info("Temp: "+temp);
            float x=Float.parseFloat(temp);
            logger.info("x: "+x);
            float per;
            if (val[4].equals("small")) {

                x=(10.30f-x);
                per=((x / 10.14f) * 100);
                Percentage=String.valueOf(per);
                if(per>100)
               {
                    Percentage="100";
                }else if(per<3)
                {
                    Percentage="0";
                }

                logger.info("per1= "+per);

            } else if (val[4].equals("medium")) {

                logger.info("Medium: ");
                x=(14.37f-x);
                per=((x / 14.37f) * 100);
                logger.info("perrrrrrrrr= "+per);
                Percentage=String.valueOf(per);
                if(per>100)
                {
                    Percentage="100";
                }else if(per<5)
                {
                    Percentage="0";
                }

                logger.info("per2= "+per);
            } else if (val[4].equals("large")) {

                x=(21.68f-x);
                per=((x / 21.68f) * 100);
                Percentage=String.valueOf(per);
                if(per>100)
                {
                    Percentage="100";
               }else if(per<3)
                {
                    Percentage="0";
                }
                logger.info("per3= "+per);
            } else {
                return;
            }


            addContainer(Contid, rasp_id, Percentage);
        //    addContainer1(Contid, rasp_id, Percentage);
//             msgList=new Float[2];
//               msgList[0]=-1f;
//               msgList[1]=-2f;
//                index=0;
        }


    }



    int is_registered=0;



    public void addContainer1(String Cont_id,String rasp_id,String Quan){

        int flag=0;
        for(int i=0;i<No_of_Container;i++)
        {
            if(Cont_id1[i].equals(Cont_id))
            {
                Quan1[i]=Quan;
                flag=1;
            }
        }

        if(flag==0)
        {
            String Cont_id2[]=new String[No_of_Container+1];
            String rasp_id2[]=new String[No_of_Container+1];
            String Quan2[]=new String[No_of_Container+1];
            for(int j=0;j<No_of_Container;j++)
            {
                Cont_id2[j]=Cont_id1[j];
                rasp_id2[j]=rasp_id1[j];
                Quan2[j]=rasp_id1[j];
            }
            Cont_id2[No_of_Container]=Cont_id;
            rasp_id2[No_of_Container]=rasp_id;
            Quan2[No_of_Container]=Quan;

            Cont_id1=Cont_id2;
            rasp_id1=rasp_id2;
            Quan1=Quan2;
            No_of_Container++;


        }

        container cnt=new container();
        cnt.set_details(Cont_id1,rasp_id1,Quan1,No_of_Container);


    }




    public void addContainer(String Cont_id,String rasp_id,String Quan){


        logger.info("helloooozzzzzzzzz INnnnnnnnnnndfjkndsfikbnafskobefwoeswhnlohlophjlohjglor4hjnlohgto;htw4ihyoho;i4hjohyrw4ioofrw4hhop");
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

            logger.info("is added " + is_registered);

            if(is_registered==1)
            {
                //UpdateResult ur = collection.updateOne(new Document("rasp_serial_no", rasp_id), new Document("$set", new Document("quantity", Quan )));

                UpdateResult ur = collection.updateOne(new Document("rasp_serial_no", rasp_id).append("container_id", Cont_id), new Document("$set", new Document("quantity", Quan )));

                logger.info("is registered inn");
            }else{

                logger.info("is registered else");

                Document doc = new Document("rasp_serial_no", rasp_id)
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

            logger.info("Helllllllloooooooozzzzzzzzzzzzzz-------------------1111111");
            MongoCollection<Document> collection1 = db.getCollection("raspberryList");
            FindIterable<Document> iterable = collection1.find(new Document("rasp_serial_no", rasp_id));
            is_added=0;
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    is_added = 1;
                }

            });
            logger.info("Helllllllloooooooozzzzzzzzzzz4zzzz-------------------44444444444444444444");
            if(is_added==1) {
                logger.info("Helllllllloooooooozzzzzzzzzzzzzzz-------------------12222222");
                if (rasp_id!= null && rasp_ip!=null) {
                    UpdateResult ur = collection1.updateOne(new Document("rasp_serial_no", rasp_id), new Document("$set", new Document("rasp_ip", rasp_ip)));
                    ur = collection1.updateOne(new Document("rasp_serial_no", rasp_id), new Document("$set", new Document("Thread_id",Thread.currentThread().getId())));

                }
            }
            else
            {
                logger.info("Helllllllloooooooozzzzzzzzzzzzzzz-------------------3333333333333");
                if (rasp_id != null && rasp_ip!=null) {
                    Document doc = new Document("rasp_serial_no", rasp_id)
                            .append("rasp_ip", rasp_ip).append("Thread_id", String.valueOf(Thread.currentThread().getId()));
                    collection1.insertOne(doc);

                }

            }


        }
        catch (Exception e)
        {
            logger.info("Helllllllloooooooozzzzzzzzzzzzzzz-------------------6656666555656565656565656");
            e.printStackTrace();
        }
    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
