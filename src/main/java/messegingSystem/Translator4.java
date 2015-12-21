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
import webServiceBank.WebServiceBank;


/**
 *
 * @author Daniel
 */
public class Translator4 {
    
    private final static String RECEIVING_QUEUE = "bank4Queue";
    private final static String SENDING_QUEUE = "aggregatorQueue";
    
    public static void main(String[] args) throws Exception {
        
        JSONParser jsonParser = new JSONParser();
        
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel recvChannel = connection.createChannel();
        Channel sendChannel = connection.createChannel();
        
        recvChannel.queueDeclare(RECEIVING_QUEUE, false, false, false, null);
        recvChannel.queueDeclare(SENDING_QUEUE, false, false, false, null);
        
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

            double loan = WebServiceBank.getOffer(ssnLong, ((Double)obj.get("loanAmount")).longValue(), (Long)obj.get("loanDuration"), ((Long)obj.get("creditScore")).intValue());
            
            JSONObject sendObj = new JSONObject();
            sendObj.put("ssn", ssnLong);
            sendObj.put("interestRate", loan);
            sendObj.put("bank", "Bank4");
            
            sendChannel.basicPublish("", SENDING_QUEUE, null, sendObj.toJSONString().getBytes());
        }
    }
    
}
