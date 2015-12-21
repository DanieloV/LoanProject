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
import java.io.ByteArrayInputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Daniel
 */
public class Normalizer2 {

    private final static String RECEIVING_QUEUE = "normalizer2QUEUEWWW";
    private final static String SENDING_QUEUE = "aggregatorQueue";

    public static void main(String[] args) throws Exception {

        JSONParser jsonParser = new JSONParser();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel sendChannel = connection.createChannel();

        ConnectionFactory connfac = new ConnectionFactory();
        connfac.setHost("datdb.cphbusiness.dk");
        connfac.setUsername("student");
        connfac.setPassword("cph");
        Connection bankConnection = connfac.newConnection();
        Channel bankChannel = bankConnection.createChannel();

        bankChannel.queueDeclare(RECEIVING_QUEUE, false, false, false, null);
        sendChannel.queueDeclare(SENDING_QUEUE, false, false, false, null);

        //to be deleted
        System.out.println(" [*] Waiting for messages. To exit press CTRL-C");

        QueueingConsumer consumer = new QueueingConsumer(bankChannel);
        bankChannel.basicConsume(RECEIVING_QUEUE, true, consumer);

        int counter = 0;

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());

            //to be deleted after testing
            System.out.println(" [" + counter++ + "] Received '" + message + "'");

            ByteArrayInputStream inputStream = new ByteArrayInputStream(delivery.getBody());
            XMLInputFactory xmlfactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = xmlfactory.createXMLStreamReader(inputStream);

            JSONObject obj = new JSONObject();

            int xmlPos = 0;
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {

                    if (xmlPos == 1) {
                        String text = reader.getElementText();
                        text = text.replaceAll("[^\\d.]", "");
                        obj.put("interestRate", Double.parseDouble(text));
                    } else if (xmlPos == 2) {
                        String text = reader.getElementText();
                        text = text.replaceAll("[^\\d.]", "");
                        obj.put("ssn", Long.parseLong(text));
                    }
                    xmlPos++;
                }

            }

            obj.put("bank", "Bank2");
            
            sendChannel.basicPublish("", SENDING_QUEUE, null, obj.toString().getBytes());

        }
    }

}
