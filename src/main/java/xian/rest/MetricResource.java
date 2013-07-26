package xian.rest;

import java.net.URLDecoder;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import xian.git.RepositoryAccess;
import xian.git.RepositoryAccess.Rule;

@Path("/metric")
public class MetricResource {

	@Context
	ServletContext ctx;

	@GET
	@Path("/all")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getHello(@MatrixParam("url") String url, @MatrixParam("rule") int rule) {
		try {
			String decodedUrl = URLDecoder.decode(url, "UTF-8");
		
			RepositoryAccess rm = new RepositoryAccess(decodedUrl, Rule.fromInt(rule));
			return Response.ok(String.valueOf(rm.getCommits().size())).build();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.ok("error").build();
	}

	@GET
	@Path("second")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getSecond() {
		try {
			RepositoryAccess rm = new RepositoryAccess(
					"https://github.com/xetorthio/jedis.git", Rule.OLD);
			return Response
					.ok(String.valueOf(rm.getJavaInputStream(
							rm.getCommits().get(0)).size())).build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.ok("not working").build();

	}

}
