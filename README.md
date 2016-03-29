# Dacara 2 #

Dacara is a simple tool for executing SQL commands like SELECT, INSERT, UPDATE and DELETE.
The data will be shown in a nice grid.
Commands like UPDATE show only the number of inserted or updated or deleted records.
All successfully entered commands will be stored in a history on disk.
When you start Dacara again it will use the last database and shows the last successfully executed command.

Dacara is targeted to Java developers.
It's JDBC based, so it can be used with any database, e.g. PostgreSQL, Oracle, HSQLDB.

Dacara was developed using Java 8 (u60), JavaFX, Gradle 1.9, Eclipse Mars.2, Guice 4.0 and my XML classes which are based on DOM4J.
Dacara is a spanish made-up word that means data view.
