package ch.adesso.partyservice.party.command;

import ch.adesso.partyservice.party.entity.CreditCardTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString
@NoArgsConstructor
public class CreditCardChangedCommand extends CoreCommand {

	private String cardNumber;
	private CreditCardTypeEnum cardType;
	private String nameOnCard;
	private int validToMonth;
	private int validToYear;
	private int secretNumber;

	public CreditCardChangedCommand(String aggregateId, long sequence,
			String cardNumber, CreditCardTypeEnum cardType, String nameOnCard, int validToMonth, int validToYear,
			int secretNumber) {
		super(aggregateId, sequence);
		this.cardNumber = cardNumber;
		this.cardType = cardType;
		this.nameOnCard = nameOnCard;
		this.validToMonth = validToMonth;
		this.validToYear = validToYear;
		this.secretNumber = secretNumber;
	}
}
