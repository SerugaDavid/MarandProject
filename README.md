# MarandProject
 
## Navodila za uporabo

Implementirana sta podatkovna baza in API.
Narejen je tudi converter, ki bazo napolni.

Naredil sem tudi GUI aplikacijo, ki je namenjena za
pregled letov ter rezervacijo teh.

V aplikaciji lahko izberemo letališče, iz katerega
letimo in letališče, kam letimo. Nato s klikom na gumb
`Najdi polete` dobimo vse lete, ki so na voljo.

Ko vidimo vse polete, ki so na voljo, lahko izberemo
enega od teh in ga rezerviramo s klikom na gumb
`Rezerviraj polet`.


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

Shemo lahko najdemo shranjeno kot model v
`./database/models/FlyByNight.mwb`

### API server
Da sem postavil API server sem uporabil Jetty.
Ta se tudi nahaja v `./jetty-home-10.0.13`.

Napisal sem dva razreda, ki delata s podatki. To sta `Data`
in `FlightsServlet`. Najdemo jih v `./api/`. Razred `Data`
je namenjen za branje podatkov iz same baze. Ta direkno
dela z njo. Medtem ko razred `FlightsServlet` deluje kot API.

Ta razred ima tri glavne metodi `doGet`, `doPost` in `doPut`.
Metoda `doGet` je namenjena za poizvedbe tipa `GET` in pridobiva
podatke vn iz baze. Metoda `doPost` je namenjena za poizvedbe
tipa `POST` in dodaja podatke v bazo. Metoda `doPut` je namenjena
za poizvedbe tipa `PUT` in posodablja podatke v bazi.

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
- **PUT**
  - /booking
    - naredi rezervacijo leta
    - potrebuje, da mu v telesu pošljemo json array s številko leta npr: `["PA001"]`

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

### GUI aplikacija

Aplikacija je namenjena temu, da pogledamo, kateri leti
obstajajo in katere od teh lahko rezerviramo.
Aplikacija se pogovarja s prej narejenim API-jem inž
tako pridobiva in posodablja podatke iz baze.

To lahko najdemo v `./app/`, kjer je njen glavni razred
`FlightsApp`. Notri so vse metode za prikazovanje
elementov in posodabljanje teh.

GUI je narejen z elementi Java Swing.