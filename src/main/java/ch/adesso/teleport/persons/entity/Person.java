package ch.adesso.teleport.persons.entity;

import ch.adesso.teleport.AggregateRoot;
import ch.adesso.teleport.persons.event.PersonChangedEvent;
import ch.adesso.teleport.persons.event.PersonContactChangedEvent;
import ch.adesso.teleport.persons.event.PersonCreatedEvent;
import ch.adesso.teleport.persons.event.PersonStatusChangedEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Person extends AggregateRoot {

	private String firstname;
	private String lastname;
	private String birthday;
	private PersonStatus status;
	private String mobil;
	private String email;

	public Person(String id) {
		super();
		applyChange(new PersonCreatedEvent(id));
	}

	public void updateFrom(Person person) {
		updatePersonData(person.getFirstname(), person.getLastname(), person.getBirthday());
		updatePersonContactData(person.getMobil(), person.getEmail());
		updatePersonStatus(person.getStatus());
	}

	public void updatePersonData(String firstname, String lastname, String birthday) {
		if (wasChanged(getFirstname(), firstname) || wasChanged(getLastname(), lastname)
				|| wasChanged(getBirthday(), birthday)) {
			applyChange(new PersonChangedEvent(getId(), getNextVersion(), firstname, lastname, birthday));
		}
	}

	public void updatePersonContactData(String mobil, String email) {
		if (wasChanged(getMobil(), mobil) || wasChanged(getEmail(), email)) {
			applyChange(new PersonContactChangedEvent(getId(), getNextVersion(), mobil, email));
		}
	}

	public void updatePersonStatus(PersonStatus status) {
		if (wasChanged(getStatus(), status)) {
			applyChange(new PersonStatusChangedEvent(getId(), getNextVersion(), status));
		}
	}

	private void on(PersonCreatedEvent event) {
		setId(event.getAggregateId());
	}

	private void on(PersonChangedEvent event) {
		setFirstname(event.getFirstname());
		setLastname(event.getLastname());
		setBirthday(event.getBirthday());
	}

	private void on(PersonContactChangedEvent event) {
		setMobil(event.getMobil());
		setEmail(event.getEmail());
	}

	private void on(PersonStatusChangedEvent event) {
		setStatus(event.getStatus() != null ? PersonStatus.valueOf(event.getStatus()) : null);
	}

}
