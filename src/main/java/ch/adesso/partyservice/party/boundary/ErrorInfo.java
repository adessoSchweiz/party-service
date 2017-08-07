package ch.adesso.partyservice.party.boundary;

import javax.json.JsonObjectBuilder;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import lombok.Data;

@Data
public class ErrorInfo {
	private JsonObjectBuilder error;

	private ResponseBuilder responseBuilder;

	public ErrorInfo(Throwable ex) {
		handleError(ex);

	}

	private void handleError(Throwable ex) {
		if (ex instanceof EntityNotFoundException) {
			responseBuilder.status(412).entity(ex.getMessage());
		} else {
			responseBuilder.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage());
		}
	}

	public Response build() {
		return responseBuilder.build();
	}
}
