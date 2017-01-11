package si.test.jmsclientapp.queue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class QueueSender {
    private static final Logger LOGGER = Logger.getLogger(QueueSender.class.getName());

    @Resource(name = "queueConnectionFactory")
    private ConnectionFactory qcf;

    @Resource(name = "queue")
    private Queue queue;

    private Connection connection;

    @PostConstruct
    public void init() {
        try {
            LOGGER.log(Level.INFO, "Initializing bean {0}", QueueSender.class.getSimpleName());

            if (qcf != null) {
                connection = qcf.createConnection();
            }

            LOGGER.info("Initialization of bean successfully finished");
        } catch (JMSException jmse) {
            LOGGER.log(Level.SEVERE, "Error while trying to connect to queue.", jmse);
        }
    }

    @PreDestroy
    public void teardown() {
        try {
            LOGGER.log(Level.INFO, "Tearing down {0}", QueueSender.class.getSimpleName());

            if (connection != null) {
                connection.close();
            }
            LOGGER.info("Queue connection successfully closed.");
        } catch (JMSException jmse) {
            LOGGER.log(Level.SEVERE, "Error while trying to close queue connection.", jmse);
        }
    }

    public void sendMessage(String message) throws Exception {
        Session session = null;
        MessageProducer producer = null;
        try {
            // Create a session over the queue connection
            session = connection.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);
            LOGGER.info("Queue session created...");
            // Create a producer on top of the session for sending messages
            producer = session.createProducer(queue);
            LOGGER.info("Queue producer created...");
            // Create message
            TextMessage textMessage = session.createTextMessage();
            LOGGER.log(Level.INFO, "Sending the following message to queue: {0}", message);
            textMessage.setText(message);

            // Send the message using the producer
            producer.send(textMessage);
            LOGGER.info("Message successfully sent to queue.");
        } finally {
            if (producer != null) {
                try {
                    producer.close();
                } catch (JMSException jmse) {
                    LOGGER.log(Level.SEVERE, "Error wile closing queue producer!", jmse);
                }
            }

            if (session != null) {
                try {
                    session.close();
                } catch (JMSException jmse) {
                    LOGGER.log(Level.SEVERE, "Error wile closing queue session!", jmse);
                }
            }
        }
    }
}
