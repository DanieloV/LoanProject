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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Daniel
 */
public class RecepientList {
    
    private final static String RECEIVING_QUEUE = "recipListChannel";
    
    public static void main(String[] args) throws Exception {
        
        JSONParser jsonParster = new JSONParser();
        
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel recvChannel = connection.createChannel();
        recvChannel.queueDeclare(RECEIVING_QUEUE, false, false, false, null);
        
        //to be deleted
        System.out.println(" [*] Waiting for messages. To exit press CTRL-C");
        
        QueueingConsumer consumer = new QueueingConsumer(recvChannel);
        recvChannel.basicConsume(RECEIVING_QUEUE, true, consumer);
        
        while(true){
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody(),"UTF-8");
            
            //to be deleted after testing
            System.out.println(" [x] Received '" + message + "'");
            
            JSONObject obj = (JSONObject)jsonParster.parse(message);
            JSONArray bankTranslatorQueue = (JSONArray) obj.get("Banks");
            
            System.out.println(bankTranslatorQueue);
            
            obj.remove("Banks");
            for (Object bankTranslatorQueue1 : bankTranslatorQueue) {
                Channel sendChannel = connection.createChannel();
                String queue = (String) bankTranslatorQueue1;
                sendChannel.queueDeclare(queue, false, false, false, null);
                sendChannel.basicPublish("", queue, null, obj.toJSONString().getBytes());
            }
            
        }
    }
    
}
