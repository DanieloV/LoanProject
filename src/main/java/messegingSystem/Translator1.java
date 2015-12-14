/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messegingSystem;

import com.rabbitmq.client.AMQP.BasicProperties;
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
public class Translator1 {
    
    private final static String RECEIVING_QUEUE = "translator1Channel";
    private final static String EXCHANGE_NAME = "cphbusiness.bankJSON";
    
    public static void main(String[] args) throws Exception {
        
        JSONParser jsonParser = new JSONParser();
        
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel recvChannel = connection.createChannel();
        
        ConnectionFactory connfac = new ConnectionFactory();
        connfac.setHost("datdb.cphbusiness.dk");
        connfac.setUsername("student");
        connfac.setPassword("cph");
        Connection bankConnection = connfac.newConnection();
        Channel bankChannel = connection.createChannel();

        bankChannel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        
        //to be deleted
        System.out.println(" [*] Waiting for messages. To exit press CTRL-C");
        
        QueueingConsumer consumer = new QueueingConsumer(recvChannel);
        recvChannel.basicConsume(RECEIVING_QUEUE, true, consumer);
        
        while(true){
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            
            //to be deleted after testing
            System.out.println(" [x] Received '" + message + "'");
            
            JSONObject obj = (JSONObject)jsonParser.parse(message);
            String ssn = (String) obj.get("ssn");
            ssn = ssn.replaceAll("[-]", "");
            long ssnLong = Long.parseLong(ssn);
            obj.put("ssn", ssnLong);
                        
            System.out.println("[x] Sending '" + obj.toJSONString() + "'");
            
            BasicProperties bp = new BasicProperties.Builder()
                        .replyTo("normalizerChannel")
                        .build();
            bankChannel.basicPublish(EXCHANGE_NAME, "", bp, obj.toJSONString().getBytes());
//            sendChannel.basicPublish("", SENDING_QUEUE, null, obj.toJSONString().getBytes());
        }
    }
    
}
