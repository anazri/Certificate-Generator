package certificate.com.webservice;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.certificate.user.GetUserRequest;

import certificate.com.webservice.client.BankClientUser;

@Configuration
public class BankClientConfig {
	
	@Bean
	@Qualifier("marshaller")
	public Jaxb2Marshaller marshallerForMt103() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("com.user");
		System.out.println(marshaller.supports(GetUserRequest.class));
		return marshaller;
	}
	
	
	@Bean
	public BankClientUser bankClientUser(@Qualifier("marshaller") Jaxb2Marshaller marshaller) {
		BankClientUser client = new BankClientUser();
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		return client;
	}

}