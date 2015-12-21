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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Daniel
 */
public class Normalizer3 {
    
    private final static String RECEIVING_QUEUE = "normalizer3QUEUEWWW";
    private final static String SENDING_QUEUE = "aggregatorQueue";
    
    public static void main(String[] args) throws Exception {
        
        JSONParser jsonParser = new JSONParser();
        
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel sendChannel = connection.createChannel();
        Channel bankChannel = connection.createChannel();

        bankChannel.queueDeclare(RECEIVING_QUEUE, false, false, false, null);
        sendChannel.queueDeclare(SENDING_QUEUE, false, false, false, null);
        
        //to be deleted
        System.out.println(" [*] Waiting for messages. To exit press CTRL-C");
        
        QueueingConsumer consumer = new QueueingConsumer(bankChannel);
        bankChannel.basicConsume(RECEIVING_QUEUE, true, consumer);
        
        int counter = 0;
        
        while(true){
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            
            //to be deleted after testing
            System.out.println(" ["+ counter++ +"] Received '" + message + "'");
            
            JSONObject obj = (JSONObject)jsonParser.parse(message);
            obj.put("bank", "Bank3");
            
            sendChannel.basicPublish("", SENDING_QUEUE, null, obj.toJSONString().getBytes());
        }
    }
    
}
