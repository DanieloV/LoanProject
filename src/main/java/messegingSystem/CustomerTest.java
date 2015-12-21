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
public class CustomerTest {

    private final static String QUEUE_NAME = "bureauChannel";
    private final static String RESPONSE_EXCHANGE = "finish";

    public static void main(String[] args) throws Exception {
        JSONParser jsonParster = new JSONParser();
        
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        Channel recvChannel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        recvChannel.exchangeDeclare(RESPONSE_EXCHANGE, "fanout", true);
        String queueName = recvChannel.queueDeclare().getQueue();
        recvChannel.queueBind(queueName, RESPONSE_EXCHANGE, "");

        int loanDuration = 300;

        JSONObject obj = new JSONObject();
        obj.put("ssn", "123456-1234");
        obj.put("loanAmount", 1000.0);
        obj.put("loanDuration", loanDuration);

        Long ssnLong = Long.parseLong(((String)obj.get("ssn")).replaceAll("[-]", ""));
        
        channel.basicPublish("", QUEUE_NAME, null, obj.toJSONString().getBytes());
        System.out.println(" [x] Sent '" + obj.toString() + "'");

        QueueingConsumer consumer = new QueueingConsumer(recvChannel);
        recvChannel.basicConsume(queueName, true, consumer);
        boolean ok = false;
        while (!ok) {
            
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            
            System.out.println(message);
            
            JSONObject recvobj = (JSONObject) jsonParster.parse(message);
            if(((Long)recvobj.get("ssn")).equals(ssnLong)){
                ok = true;
                System.out.println(recvobj.toJSONString());
            }
        }
        recvChannel.queueUnbind(queueName, RESPONSE_EXCHANGE, "");
        recvChannel.queueDelete(queueName);
        recvChannel.close();
        channel.close();
        connection.close();

    }

}
