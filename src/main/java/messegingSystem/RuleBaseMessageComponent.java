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
import ruleBase.RuleBase;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Daniel
 */
public class RuleBaseMessageComponent {
    
    private final static String RECEIVING_QUEUE = "ruleBaseChannel";
    private final static String SENDING_QUEUE = "recipListChannel";
    
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
            long creditScore = (long) obj.get("creditScore");
            String[] result = RuleBase.getBanksForCreditScore(creditScore);
            JSONArray array = new JSONArray();
            for(String queue : result){
                array.add(queue);
                System.out.println(queue);
            }
            obj.put("Banks", array);
            
            System.out.println(" [x] == '" + array.toJSONString() + "'");
            
            sendChannel.basicPublish("", SENDING_QUEUE, null, obj.toJSONString().getBytes());
        }
    }
    
}
