# Booking
booking project
**DEMO:** *http://195.133.49.102:8081/*

## Начало работы
#### Должна быть установлена Java 17, IntelliJ Idea Ultimate (желательно), Maven и PostgreSQL
- Склонировать репозиторий
- Перейти в File->Project Structure... и убедиться, что установлена JDK версии 17
- Перейти в Run->Edit Configurations... И в спринговой конфигурации нажать alt+R и в появившемся 
инпуте ввести следующую строку: `--spring.profiles.active=dev`
- Далее перейти к файлу `application-dev.properties` который находится в папке с ресурсами.
В этом файле вы должны подключить свой postgreSQL сервер, который вы должны создать зарание
  ```Properties
  spring.datasource.url = jdbc:postgresql://localhost:5432/{db_name}
  spring.datasource.username = {db_username}
  spring.datasource.password = {user_password}
  ```
- После этого можно запускать проект

