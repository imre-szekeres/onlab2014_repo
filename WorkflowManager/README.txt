Előfeltételek: Eclipse, M2E plugin

Nem kell külön alkalmazásszervert telepíteni, mert a maven letölti és embedded módban
futtatja a TomEE alkalmazásszervert.
Az alkalmazás beágyazott Derby adatbázist használ, emiatt nem kell külön DB sem.
Az adatbázis JNDI neve products, külön nem kell regisztrálni, 
ezt megoldja a a WEB-INF\resources.xml a TomEE-ben.

A projekt futtatása

1. Projekt importálása: File --> Import --> Existing Maven project

2. Futtatni debug módban érdemes, ehhez két lépés kell:

	A. A Run configok közül a "TomeeSample run" futtatása (Ez meg fog akadni, várja a csatlakozó debuggert) (mvn package tomee:debug)
	B. Debug configok közül a "debug tomee" futtatása (Ez rácsatlakozik a processzre, és tud továbbmenni a startup)
	
3. A böngészőben itt érhető el: http://localhost:8080/TomEESample/ 

    
Frissítések módosítás után.

1. Ha html, xhtml, js vagy css fájlt módosítunk, automatikusan megkapja a tomee a friss verziót.
Ezt ki is logolja. Ha más kiterjesztésekre is kell ez a viselkedés, akkor a pom.xml-ben az <updateOnlyExtensions> részt kell bővíteni

2. Ha java fájlt módosítunk, de csak pl. egy metódus törzsét, az Eclipse hot code replacement-je miatt automatikusan
az új verzió fog látszódni az alkalmazásban, de ezt a TomEE nem logolja.
Be lehetne állítani a pom.xml-ben, hogy módosult .class fájlok esetén újrainduljon az alkalmazás, de nem célszerű, mert a
JSF kontextus megszűnik, a böngészőben meg ott marad és hibát okoz. 
(+ Derby probléma is jelentkezik, mert csak egyszer nyitható meg az embedded adatbázis)   
Ha új tagváltozót, metódust veszünk fel, a hoz code replacement nem működik, ilyenkor le kell állítani a szervert és újraindítani.

A leállítás módjai: 

1. A Debug perspektíva Debug nézetében kilövünk minden folyamatot
VAGY
2. Run configok közül futtatjuk a TomeeSample stop-ot (mvn tomee:stop), lefutása utána pedig a Console-on Terminate.
(Simán Terminate a Console-on nem elég, mert bent marad a debug processz)

Tesztelés

1. unit tesztek, pl. ProductServiceTest, ProductManagerTest
launch config: TomEESample test (mvn test)

2. Integrációs tesztek, pl. ProductServiceIT, ProductServiceArqIT
launch config: TomEESample integration-test (mvn integration-test)
Egyelőre nem futnak le rendesen. 