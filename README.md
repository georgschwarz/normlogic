![NormLogic logo](media/normlogic.png "NormLogic logo")

### Introduction

The project NormLogic explores facilities to give computer aided assistance in the application of law. 

The main objectives of the tool "NormLogic Navigator" are to

 * give orientation in respect to the complexity of legal regulations, such as contracts and other legal statements
 * guide the user (computer aided) through the regulation of a legal case
 * find the applicable legal regulations in respect to a given case

The current state of this tool evaluates the technics and possibilities of semantic technologies in the legal area. It is a playground to experiment and evaluate.

The Project NormLogic uses description logic (DL) to represent the legal regulation as well as the given Situation which forms the legal case. Specifically the Web Ontology Language OWL-DL is used. The given examples can be viewed an edited with generally available OWL-Editors, for instance the Protege ( http://protege.stanford.edu ).

### How to build

Due to maven tycho, a two steps are needed to build the "NormLogic Navigator":

```
cd ./org.normlogic.3rd
mvn clean build
cd ../org.normlogic.navigator
mvn clean build
```

The build produces an eclipse rcp application which is a kind of prototype. After the build, you can find the application in the folder `./org.normlogic.navigator.product/target/products`.

A first example of an legal content (German terms of an insurance contract) can be found in the folder `./examples`. Just open it and try to construct your legal case by a right mouse click in of the Situation View. I will give further information soon...

The project stands at its beginning. If you are interested to contribute, don't hesitate to contact me.
