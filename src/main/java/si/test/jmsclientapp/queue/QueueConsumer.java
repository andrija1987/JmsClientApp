package si.test.jmsclientapp.queue;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.TextMessage;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class QueueConsumer implements MessageListener {
    private static final Logger LOGGER = Logger.getLogger(QueueConsumer.class.getName());

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;

            LOGGER.log(Level.INFO, "Received new message: {0}", textMessage.getText());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred consuming message from queue.", e);
        }
    }
}
