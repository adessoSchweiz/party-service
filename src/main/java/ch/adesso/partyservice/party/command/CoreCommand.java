package ch.adesso.partyservice.party.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoreCommand {
	private String aggregateId;
	private long aggregateVersion;
}
