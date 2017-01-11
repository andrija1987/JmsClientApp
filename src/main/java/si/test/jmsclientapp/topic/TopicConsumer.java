package si.test.jmsclientapp.topic;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic")
})
public class TopicConsumer implements MessageListener {
    private static final Logger LOGGER = Logger.getLogger(TopicConsumer.class.getName());

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;

            LOGGER.log(Level.INFO, "Received new publication: {0}", textMessage.getText());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error consuming the message.", e);
        }
    }
}
