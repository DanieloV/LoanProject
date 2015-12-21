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
public class Translator3 {
    
    private final static String RECEIVING_QUEUE = "bank3Queue";
    private final static String EXCHANGE_NAME = "local.bank";
    private final static String REPLYTO_QUEUE = "normalizer3QUEUEWWW";
    
    public static void main(String[] args) throws Exception {
        
        JSONParser jsonParser = new JSONParser();
        
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel recvChannel = connection.createChannel();
        Channel bankChannel = connection.createChannel();

        recvChannel.queueDeclare(RECEIVING_QUEUE, false, false, false, null);
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
            
            System.out.println("[x] Sending '" + obj.toString() + "'");
            
            BasicProperties.Builder bp = new BasicProperties.Builder()
                        .replyTo(REPLYTO_QUEUE);
                    
            bankChannel.basicPublish(EXCHANGE_NAME, "", bp.build(), obj.toString().getBytes());
//            sendChannel.basicPublish("", SENDING_QUEUE, null, obj.toJSONString().getBytes());
        }
    }
    
}
