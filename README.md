# DigiHotel

This project implements a simple hotel booking manager in Java.

## How to run it

This project uses Maven to build. To compile, run all unit tests and package the code, run:

```bash
mvn clean install
```

or if you don't have maven on your PATH:

```bash
mvnw clean install
```

There is no main method. To try this hotel manager, you can use the unit tests available and change values there.

## Structure

Most of the business logic is located in HotelService. You will find inside all the public methods asked for this
exercise.

Note: HotelService is thread safe, since public methods that read or modify the list of bookings are `synchronized`.
