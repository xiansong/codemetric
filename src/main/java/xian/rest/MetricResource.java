package xian.rest;

import java.net.URLDecoder;

import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import xian.git.RepositoryAccess;
import xian.git.RepositoryAccess.Rule;
import xian.rest.model.RepoInfo;
import xian.visitor.RepositoryVisitor;

@Path("/metric")
public class MetricResource {

	@GET
	@Path("basicInfo")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getBasicInfo(@MatrixParam("url") final String url,
			@MatrixParam("rule") final int rule) {

		try {
			String decodedUrl = URLDecoder.decode(url, "UTF-8");
			RepositoryAccess rm = new RepositoryAccess(decodedUrl,
					Rule.fromInt(rule));
			int size = rm.getCommits().size();
			if (size != 0) {
				RepoInfo repoinfo = new RepoInfo.Builder(rm.getAuthor(),
						rm.getName()).numOfCommit(size)
						.startOn(rm.firstCommitTime())
						.lastUpdate(rm.lastCommitTime()).build();
				return Response.ok(repoinfo).build();

			} else {
				RepoInfo repoinfo = new RepoInfo.Builder(rm.getAuthor(),
						rm.getName()).build();
				return Response.ok(repoinfo).build();
			}

		} catch (Exception e) {
			return Response.serverError().build();
		}

	}

	@GET
	@Path("advancedInfo")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getAdvancedInfo(@MatrixParam("url") final String url,
			@MatrixParam("rule") final int rule) {
		try {
			String decodedUrl = URLDecoder.decode(url, "UTF-8");
			RepositoryAccess rm = new RepositoryAccess(decodedUrl,
					Rule.fromInt(rule));
			RepositoryVisitor vistor = new RepositoryVisitor(rm);
			return Response.ok(vistor.getCommitData()).build();

		} catch (Exception e) {
			return Response.serverError().build();
		}
	}

}
