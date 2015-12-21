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
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Daniel
 */
public class Translator2 {

    private final static String RECEIVING_QUEUE = "bank2Queue";
    private final static String EXCHANGE_NAME = "cphbusiness.bankXML";
    private final static String REPLYTO_QUEUE = "normalizer2QUEUEWWW";

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
        Channel bankChannel = bankConnection.createChannel();

        recvChannel.queueDeclare(RECEIVING_QUEUE, false, false, false, null);
        bankChannel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        //to be deleted
        System.out.println(" [*] Waiting for messages. To exit press CTRL-C");

        QueueingConsumer consumer = new QueueingConsumer(recvChannel);
        recvChannel.basicConsume(RECEIVING_QUEUE, true, consumer);

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());

            //to be deleted after testing
            System.out.println(" [x] Received '" + message + "'");

            JSONObject obj = (JSONObject) jsonParser.parse(message);
            String ssn = (String) obj.get("ssn");
            ssn = ssn.replaceAll("[-]", "");
            long ssnLong = Long.parseLong(ssn);
            obj.put("ssn", ssnLong);

            StringWriter stringWriter = new StringWriter();

            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);

            xMLStreamWriter.writeStartDocument();
            xMLStreamWriter.writeStartElement("LoanRequest");

            xMLStreamWriter.writeStartElement("ssn");
            xMLStreamWriter.writeCharacters("" + ssnLong);
            xMLStreamWriter.writeEndElement();

            xMLStreamWriter.writeStartElement("creditScore");
            xMLStreamWriter.writeCharacters("" + obj.get("creditScore"));
            xMLStreamWriter.writeEndElement();

            xMLStreamWriter.writeStartElement("loanAmount");
            xMLStreamWriter.writeCharacters("" + obj.get("loanAmount"));
            xMLStreamWriter.writeEndElement();

            xMLStreamWriter.writeStartElement("loanDuration");
            Long loanDuration = (Long) obj.get("loanDuration");
            Date date = new Date(70, loanDuration.intValue(), 1);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S z");
            xMLStreamWriter.writeCharacters(df.format(date));
            xMLStreamWriter.writeEndElement();

            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeEndDocument();

            xMLStreamWriter.flush();
            xMLStreamWriter.close();

            String xmlString = stringWriter.getBuffer().toString();

            stringWriter.close();

            System.out.println(xmlString);

            System.out.println("[x] Sending '" + xmlString + "'");

            BasicProperties.Builder bp = new BasicProperties.Builder()
                    .replyTo(REPLYTO_QUEUE);

            bankChannel.basicPublish(EXCHANGE_NAME, "", bp.build(), xmlString.getBytes());
//            sendChannel.basicPublish("", SENDING_QUEUE, null, obj.toJSONString().getBytes());
        }
    }

}
