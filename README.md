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
Ta se tudi nahaja v `./jetty-home-10.0.13`.

Napisal sem dva razreda, ki delata s podatki. To sta `Data`
in `FlightsServlet`. Najdemo jih v `./api/`. Razred `Data` je namenjen za branje
podatkov iz same baze. Ta direkno dela z njo. Medtem ko
razred `FlightsServlet` deluje kot API.

Ta razred ima dve
glavni metodi `doGet` in `doPost`. Metoda `doGet` je
namenjena za poizvedbe tipa `GET` in pridobiva podatke vn
iz baze. Metoda `doPost` je namenjena za poizvedbe tipa
`POST` in dodaja podatke v bazo.

Api ne omogoča spreminjanje podatkov. Samo dodajanje in
branje le teh.

URL za api je http://localhost:8080/api/flights. Ko pa
delamo različne poizvedbe ali ko dodajamo podatke pa
dopolnimo naš URL.
- **GET**
  - /airlines
    - /name
      - pridobi podatke o letalski družbi po imenu
    - /id
      - pridobi podatke o letalski družbi po id
    - /
      - pridobi podatke o vseh letalskih družbah
  - /airports
    - /abbreviation
      - pridobi podatke o letališču po kratici
    - /id
      - pridobi podatke o letališču po id
    - /
      - pridobi podatke o vseh letališčih
  - /flights
    - /id
      - pridobi podatke o letu po id
    - /
      - pridobi podatke o vseh letih
- **POST**
  - /airlines
    - pošlje podatke o letalskih družbah
  - /airports
    - pošlje podatke o letališčih
  - /
    - pošlje podatke o letih

### Converter

Converter je namenjen za pretvorbo podatkov iz CSV datoteke
v podatkovno bazo preko API-ja, ki smo ga ravnokar
napisali.

Razred `Converter` in podatki se nahajajo v `./converter/`.

Ta prebere datoteko in prvo shrani letališča in letalske
družbe, nato pa se še loti shranjevanja letov. 

Tak pristop sem izbral, ker lahko potem takem podatkovna
baza avtomatsko dodeli id-je. In kasneje, ko dodajam lete
tudi kličem moj api, da mi vrača id-je letališč in
letalskih družb. To pa delam zato, ker v tabeli za polete
potrebujem id-je letališč in letalskih družb.