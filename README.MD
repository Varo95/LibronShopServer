# ¡LibronShop Server App!

## What do you need?
You only need Java [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

Some Spanish words to understand the prints

## Freatures

- [x] You can register and login into the app, as Manager or as Client.

- [x] You can shell books as Manager and have control of them.

- [x] Passwords are encrypted with SHA256

- [x] As client you can buy books, see your balance, charge your account and see your historical data purchased

- [x] H2 embbeded DataBase

- [x] Base64 image storage on database, take care of the size, this is designed for small project.


## How it works
Just go on bash/terminal/cmd on the place you have download the .jar file and put the below command:
```java -jar server.jar```

Then you need to download and run the client right here ➡ [LibronShopClient](https://github.com/Varo95/LibronShopClient)

**NOTE**: That data will missing for the first time because the generated H2 DataBase.

**NOTE**: For the client Windows 10 or 11 is hardly recommended for run app because of the GUI and icons.