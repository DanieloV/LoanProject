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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Daniel
 */
public class CreditBureauMessageComponent {
    
    private final static String RECEIVING_QUEUE = "bureauChannel";
    private final static String SENDING_QUEUE = "ruleBaseChannel";
    
    public static void main(String[] args) throws Exception {
        
        JSONParser jsonParster = new JSONParser();
        
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel recvChannel = connection.createChannel();
        Channel sendChannel = connection.createChannel();
        
        recvChannel.queueDeclare(RECEIVING_QUEUE, false, false, false, null);
        sendChannel.queueDeclare(SENDING_QUEUE, false, false, false, null);
        
        //to be deleted
        System.out.println(" [*] Waiting for messages. To exit press CTRL-C");
        
        QueueingConsumer consumer = new QueueingConsumer(recvChannel);
        recvChannel.basicConsume(RECEIVING_QUEUE, true, consumer);
        
        while(true){
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            
            //to be deleted after testing
            System.out.println(" [x] Received '" + message + "'");
            
            JSONObject obj = (JSONObject)jsonParster.parse(message);
            String ssn = (String) obj.get("ssn");
            int result = CreditBureau.getScore(ssn);
            obj.put("creditScore", result);
            
            sendChannel.basicPublish("", SENDING_QUEUE, null, obj.toJSONString().getBytes());
        }
    }
    
}
