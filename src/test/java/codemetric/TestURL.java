package codemetric;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class TestURL {
	@Test
	public void testURLEncoding() throws UnsupportedEncodingException {
		String url = URLEncoder.encode(
				"https://github.com/xetorthio/jedis.git", "UTF-8");
		UriBuilder base = UriBuilder
				.fromPath("http://localhost:8080/codemetric/rest/metric/basicInfo");
		WebResource resource = Client.create().resource(
				base.matrixParam("url", url).matrixParam("rule", 0).build());
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		
		System.out.println(response.getEntity(String.class));
	}
	
	@Test
	public void testRevInfo() throws UnsupportedEncodingException{
		String url = URLEncoder.encode(
				"https://github.com/xiansong/codemetric.git", "UTF-8");
		UriBuilder base = UriBuilder
				.fromPath("http://localhost:8080/codemetric/rest/metric/advancedInfo");
		WebResource resource = Client.create().resource(
				base.matrixParam("url", url).matrixParam("rule", 0).build());
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		
		System.out.println(response.getEntity(String.class));
	}
	

}
