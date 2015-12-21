/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messegingSystem;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import creditBureau.CreditBureau;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Daniel
 */
public class Agreggator {

    private final static String RECEIVING_QUEUE = "aggregatorQueue";
    private final static String FINISH_EXCHANGE = "finish";
//    private final static String SENDING_QUEUE = "finishingQueue";
    private static Map<Long, JSONObject> waitingList = new HashMap<>();

    public static void main(String[] args) throws Exception {

        JSONParser jsonParster = new JSONParser();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel recvChannel = connection.createChannel();
        final Channel sendChannel = connection.createChannel();

        recvChannel.queueDeclare(RECEIVING_QUEUE, false, false, false, null);
//        sendChannel.queueDeclare(SENDING_QUEUE, false, false, false, null);
        sendChannel.exchangeDeclare(FINISH_EXCHANGE, "fanout", true);
        
        //to be deleted
        System.out.println(" [*] Waiting for messages. To exit press CTRL-C");

        QueueingConsumer consumer = new QueueingConsumer(recvChannel);
        recvChannel.basicConsume(RECEIVING_QUEUE, true, consumer);

        

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());

            //to be deleted after testing
            System.out.println(" [x] Received '" + message + "'");

            JSONObject jsonObject = (JSONObject) jsonParster.parse(message);

            if (waitingList.containsKey((Long) jsonObject.get("ssn"))) {
                if ((Double) jsonObject.get("interestRate") < (Double) waitingList.get((Long) jsonObject.get("ssn")).get("interestRate")) {
                    waitingList.put((Long) jsonObject.get("ssn"), jsonObject);
                }
            } else {
                waitingList.put((Long) jsonObject.get("ssn"), jsonObject);
                Timer timer = new Timer();
                timer.schedule(new MyTimerTask((Long) jsonObject.get("ssn")){
                
                    @Override
                    public void run() {
                        JSONObject obj = waitingList.get(this.param);
                        waitingList.remove(this.param);
                        System.out.println("Answer: " + obj);
                        try {
                            sendChannel.basicPublish(FINISH_EXCHANGE, "", null, obj.toJSONString().getBytes());
                        } catch (IOException ex) {
                            Logger.getLogger(Agreggator.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }, 5000);
            }
        }
    }

}
