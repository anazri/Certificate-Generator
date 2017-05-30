package certificate.com.webservice;

import java.io.IOException;

import org.bouncycastle.cert.ocsp.OCSPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.certificate.ocsp.GetOCSPRequest;
import com.certificate.ocsp.GetOCSPResponse;
import com.certificate.service.X509RevokedCertificateService;

@Endpoint
public class OCSPEndpoint {

	private static final String NAMESPACE_URI = "http://certificate.com/ocsp";

	@Autowired
	X509RevokedCertificateService x509revokedCertificateService;

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getOCSPRequest")
	@ResponsePayload
	public GetOCSPResponse getOCSPResponse(@RequestPayload GetOCSPRequest request)
			throws OCSPException, IOException, ClassNotFoundException {
		if (x509revokedCertificateService.findBySerialNumber(Long.parseLong(request.getSerial())) != null) {
			GetOCSPResponse resp = new GetOCSPResponse();
			resp.setVerified(true);
			return resp;
		}
		GetOCSPResponse resp = new GetOCSPResponse();
		resp.setVerified(false);
		return resp;
	}
}
