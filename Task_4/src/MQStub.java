import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.ibm.mq.headers.pcf.MQCFBF;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.mq.jms.MQQueueReceiver;
import com.ibm.mq.jms.MQQueueSender;
import com.ibm.mq.jms.MQQueueSession;


public class MQStub {

	public static void main(String[] args) {
		try {
			MQQueueConnection mqConn;
			MQQueueConnectionFactory mqCF;
			final MQQueueSession mqQSession;
			MQQueue mqIn, mqOut ;
			MQQueueReceiver mqReceiver;
			MQQueueSender mqSender;
			
			mqCF = new MQQueueConnectionFactory();
			mqCF.setHostName("localhost");
			
			mqCF.setPort(1410);
			
			mqCF.setQueueManager("Q_ADMIN");
			mqCF.setChannel("SYSTEM.DEF.SVRCONN");
			
			mqConn = (MQQueueConnection) mqCF.createQueueConnection();
			mqQSession = (MQQueueSession) mqConn.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
			
			mqIn = (MQQueue) mqQSession.createQueue("MQ.IN"); //in
			mqReceiver = (MQQueueReceiver) mqQSession.createReceiver(mqIn);
			
			
			mqOut = (MQQueue) mqQSession.createQueue("MQ.OUT"); //OUT
			mqSender = (MQQueueSender) mqQSession.createSender(mqOut);
			
			
			javax.jms.MessageListener Listener = new javax.jms.MessageListener() {
				@Override
				public void onMessage (Message msg) {
					System.out.println("Got Message!");
					if (msg instanceof TextMessage)
					{
						try {
							TextMessage tMsg = (TextMessage) msg;
							String msgText = tMsg.getText();
							mqSender.send(msg);
							System.out.println(msgText);
						} catch (JMSException e) {
							e.printStackTrace();
						}
					}
						
				}
				
			};
			mqReceiver.setMessageListener(Listener);
			mqConn.start();
			System.out.println("Stub started!");
		} catch (JMSException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("stop working");
	}
	
}
