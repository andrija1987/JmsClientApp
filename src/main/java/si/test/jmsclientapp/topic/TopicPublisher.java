package si.test.jmsclientapp.topic;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSession;

@Stateless
public class TopicPublisher {
    private static final Logger LOGGER = Logger.getLogger(TopicPublisher.class.getName());
    
    @Resource(name = "connectorConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(name = "sampleTopic")
    private Topic lockTopic;
    
    private Connection connection;
    
    @PostConstruct
    public void init() {
        try {
            LOGGER.log(Level.INFO, "Initializing bean {0}", TopicPublisher.class.getSimpleName());

            if (connectionFactory != null) {
                connection = connectionFactory.createConnection();
            }

            LOGGER.info("Initialization of bean successfully finished");
        } catch (JMSException jmse) {
            LOGGER.log(Level.SEVERE, "Error while trying to connect to topic.", jmse);
        }
    }

    @PreDestroy
    public void teardown() {
        try {
            LOGGER.log(Level.INFO, "Tearing down {0}", TopicPublisher.class.getSimpleName());

            if (connection != null) {
                connection.close();
            }
            LOGGER.info("Queue connection successfully closed.");
        } catch (JMSException jmse) {
            LOGGER.log(Level.SEVERE, "Error while trying to close connection.", jmse);
        }
    }
    
    public void publishMessage() {
        Session session = null;
        MessageProducer producer = null;
        try {
            // Create a session over the queue connection
            session = connection.createSession(false, TopicSession.AUTO_ACKNOWLEDGE);
            LOGGER.info("Topic session created...");
            // Create a producer on top of the session for sending acknowledgeMessages
            producer = session.createProducer(lockTopic);
            LOGGER.info("Queue producer created...");
            // Create message
            String message = "Finished";
            TextMessage textMessage = session.createTextMessage();
            LOGGER.log(Level.INFO, "Publishing the following message to topic: {0}", message);
            textMessage.setText(message);

            // Send the message using the producer
            producer.send(textMessage);
            LOGGER.info("Message successfully published.");
        } catch (JMSException jmse) {
            LOGGER.log(Level.SEVERE, "Error sending message to topic", jmse);
        } finally {
            if (producer != null) {
                try {
                    producer.close();
                } catch (JMSException jmse) {
                    LOGGER.log(Level.SEVERE, "Error wile closing topic producer!", jmse);
                }
            }

            if (session != null) {
                try {
                    session.close();
                } catch (JMSException jmse) {
                    LOGGER.log(Level.SEVERE, "Error wile closing topic session!", jmse);
                }
            }
        }
    }
}
