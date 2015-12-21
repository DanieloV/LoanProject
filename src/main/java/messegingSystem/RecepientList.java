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
    private final static String QUEUEBANK1 = "bank1Queue";
    private final static String QUEUEBANK2 = "bank2Queue";
    private final static String QUEUEBANK3 = "bank3Queue";
    private final static String QUEUEBANK4 = "bank4Queue";

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

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody(), "UTF-8");

            //to be deleted after testing
            System.out.println(" [x] Received '" + message + "'");

            JSONObject obj = (JSONObject) jsonParster.parse(message);
            JSONArray banks = (JSONArray) obj.get("Banks");

            System.out.println(banks);

            obj.remove("Banks");
            for (Object bank : banks) {
                Channel sendChannel = connection.createChannel();
                Short bankShort = ((Long) bank).shortValue();
                String queue = null;
                switch (bankShort) {
                    case 1:
                        queue = QUEUEBANK1;
                        break;
                    case 2:
                        queue = QUEUEBANK2;
                        break;
                    case 3:
                        queue = QUEUEBANK3;
                        break;
                    case 4:
                        queue = QUEUEBANK4;
                        break;
                }
                if (queue != null) {
                    sendChannel.queueDeclare(queue, false, false, false, null);
                    sendChannel.basicPublish("", queue, null, obj.toJSONString().getBytes());
                }
            }

        }
    }

}
