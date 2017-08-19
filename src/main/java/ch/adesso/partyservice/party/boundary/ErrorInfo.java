package ch.adesso.partyservice.party.boundary;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import lombok.Data;

@Data
public class ErrorInfo {

	private ResponseBuilder responseBuilder;

	public ErrorInfo(Throwable ex) {
		handleError(ex);

	}

	private void handleError(Throwable ex) {
		if (ex instanceof EntityNotFoundException) {
			responseBuilder = Response.status(412).entity(toJson(ex));
		} else {
			responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(toJson(ex));
		}
	}

	public Response build() {
		return responseBuilder.build();
	}

	private String toJson(Throwable t) {
		return String.format("{\"error\": \"%s\"}", t.getMessage());
	}
}
