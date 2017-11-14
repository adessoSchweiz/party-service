# party-service

This is a service used for registration of parties (persons or organizations).
All changes are published into kafka, 

Person creation example

POST http://localhost:8092/party-service/resources/persons

		{
			"firstname": "firstname",
			"lastname" : "lastname",
			"mobil"    : "079-222-11-11"
		}

This is a supporting service for passenger application, see passenger app readme for more details.	