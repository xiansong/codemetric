package xian.rest;

import java.net.URLDecoder;

import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.jgit.api.errors.InvalidRemoteException;

import xian.git.RepositoryAccess;
import xian.git.RepositoryAccess.Rule;
import xian.rest.model.RepoInfo;

@Path("/metric")
public class MetricResource {

	@GET
	@Path("/basic")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getHello(@MatrixParam("url") String url,
			@MatrixParam("rule") int rule) {
		
		try {
			String decodedUrl = URLDecoder.decode(url, "UTF-8");
			RepositoryAccess rm = new RepositoryAccess(decodedUrl,
					Rule.fromInt(rule));
			RepoInfo repoinfo = new RepoInfo();
			int size = rm.getCommits().size();
			repoinfo.setAuthor(rm.getCommits().get(0).getAuthorIdent()
					.getName());
			repoinfo.setName(rm.getRepositoryName());
			repoinfo.setNumberOfCommit(size);
			repoinfo.setStartOn(rm.getCommits().get(size - 1)
					.getCommitterIdent().getWhen().getTime());
			repoinfo.setLastUpdate(rm.getCommits().get(0).getCommitterIdent()
					.getWhen().getTime());

			return Response.ok(repoinfo).build();
			
		} catch (InvalidRemoteException e) {
			return Response.status(Status.NOT_FOUND).build();
		} catch (Exception e) {
			return Response.serverError().build();
		}

	}

	@GET
	@Path("/advance")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getSecond() {
		return Response.ok("not working").build();

	}

}
