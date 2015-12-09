/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messegingSystem;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class CustomerTest {
    
    private final static String QUEUE_NAME = "bureauChannel";
    
    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        
        JSONObject obj = new JSONObject();
        obj.put("ssn", "123456-1234");
        obj.put("loanAmount", 1000.0);
        obj.put("loanDuration", 300);
        
        String message = "123456-1234";
        channel.basicPublish("", QUEUE_NAME, null, obj.toJSONString().getBytes());
        System.out.println(" [x] Sent '" + obj.toString() + "'");
        
        channel.close();
        connection.close();
        
    }
    
}
