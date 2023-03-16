# MarandProject
 
## Navodila za uporabo


## Potek izdelave

### Podatkovna baza
Za podatkovno bazo sem izbral MySQL, saj smo s tem že delali na faksu.
Naložil sem si MySQL Server in MySQL Workbench.
Za podatkovno bazo sem ustvari shemo z imenom FlyByNight.
Ta baza ima tri tabele:
- Airlines
  - idAirlines
  - Name
  - Tag
- Airports
  - idAirports
  - Name
  - Abbreviation
- Flights
  - idFlights
  - FlightNumber
  - OriginAirportID
  - DestinationAirportID
  - Airlines_idAirlines
  - Price
  - Day
  - Time
  - Duration
  - AvailableSeats

Shemo lahko najdemo shranjeno kot model v `./database/models/FlyByNight.mwb`

### API server
Da sem postavil API server sem uporabil Jetty.
Ta se tudi nahaja v `./jetty-home-10.0.13`