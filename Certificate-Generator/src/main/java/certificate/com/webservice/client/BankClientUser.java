package certificate.com.webservice.client;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.stream.StreamResult;

import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import com.certificate.user.GetUserRequest;
import com.certificate.user.GetUserResponse;

public class BankClientUser extends WebServiceGatewaySupport{

	public GetUserResponse getUserResponse(GetUserRequest request) throws XmlMappingException, IOException {
		
		final StringWriter out = new StringWriter();
		getWebServiceTemplate().getMarshaller().marshal(request, new StreamResult(out));
		String xml = out.toString();
		
		System.out.println(xml);
		
		
		getWebServiceTemplate().setDefaultUri("https://localhost:8080/ws/userRequestService");
		
		GetUserResponse response = (GetUserResponse) getWebServiceTemplate()
				.marshalSendAndReceive("https://localhost:8080/ws/userRequestService",
						request, new SoapActionCallback("http://certificate.com/getUser"));
		System.out.println(response);
		return response;
	}
}
