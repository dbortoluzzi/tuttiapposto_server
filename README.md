# Tuttiapposto Server

Before to start:
- add json credentials to application.properties with key *firebase.serviceAccount.json* (replacing \n with \\n)

## To run in local mode
`mvn spring-boot:run -Dspring-boot.run.profiles=local`

## To run in prod mode
`mvn spring-boot:run -Dspring-boot.run.profiles=prod`

## Deploy 

### Heroku
`git push heroku master`
