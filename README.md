# Say Hi API

### Requirements
- Java 21
- Docker

### Design Considerations
- Added `get user by nickName` operation(in case user id gets lost) 
- Message sent at datetime stored in UTC timezone
- Functional: Search for messages sent by user to particular user and received by user from a particular user implemented.
- Used artemis spring jms library to simulate an embedded(Ideally, the queue should be out of the application) JMS queue

### What else can be done?
- Monitoring
- DLQ setup
- Retries setup in queue
- Actuator enable
- More documentation for describing other possible endpoint responses

### Steps to run(`The steps have been tried on windows powershell and may require changes if executed on unix`)
- Run command `docker-compose down`
- Run command `docker-compose up -d`
- Build application using command
  `./mvnw clean package`
- Go to target directory by running command `cd target`
- run `java --enable-preview -jar sayhi-0.0.1-SNAPSHOT.jar`. This should start the application
- Open browser of your choice and go to URL `http://localhost:8080/`
- The web page will have the supported operations.

<b>NOTE</b>: the web page is built to support only happy path. To verify other scenarios, use curls mentioned below.

### Steps to build docker image
- run `docker build -t sayhi:1.0 .` from within the project root directory

P.S. - Working on windows system so cannot test any build/shell script, hence provided individual steps


### REST API
NOTE: In case the application is running on a different host and port, replace `localhost:8080` with correct `<host>:<port>`
Swagger: `localhost:8080/swagger-ui/index.html` (only accessible once the project is running)

### Create User
#### Request
`POST /api/v1/users`
    
    curl --location 'localhost:8080/api/v1/users' --header 'Content-Type: application/json' --data '{"nickName": "user-foo"}'

#### Response
    {"user":{"id":26,"nickName":"user-foo"}}

### Get User by nickname
#### Request
`GET /api/v1/users/<nickName>`

    curl --location 'localhost:8080/api/v1/users/user-foo'

#### Response
    {"id":10,"nickName":"user-foo"}

### Send messages
#### Request(x-user-id in header should be populated with the user id of user performing the action/curl)
`POST /api/v1/messages`

    curl --location 'localhost:8080/api/v1/messages' --header 'x-user-id: 1' --header 'Content-Type: application/json' --data '{"receiverUserId": 2,"messageBody": "Hello"}'

#### Response
    200 OK

### Get all received messages 
#### Request(x-user-id in header should be populated with the user id of user performing the action/curl)
`GET localhost:8080/api/v1/messages?transfer_type=received`

    curl --location 'localhost:8080/api/v1/messages?transfer_type=received' --header 'x-user-id: 2' --data ''

#### Response
    [{"id":15,"body":"Hello","senderUserId":1,"receiverUserId":2,"sentAt":"2024-01-31T19:25:41.566956Z"}]

### Get all sent messages
#### Request(x-user-id in header should be populated with the user id of user performing the action/curl)
`GET localhost:8080/api/v1/messages?transfer_type=sent`

    curl --location 'localhost:8080/api/v1/messages?transfer_type=sent' --header 'x-user-id: 1' --data ''

#### Response
    [{"id":15,"body":"Hello","senderUserId":1,"receiverUserId":2,"sentAt":"2024-01-31T19:25:41.566956Z"}]


### Get messages received by a user sent from a particular user
#### Request(x-user-id in header should be populated with the user id of user performing the action/curl)
`GET localhost:8080/api/v1/messages?transfer_type=received`

    curl --location 'localhost:8080/api/v1/messages?fellow_user_id=1&transfer_type=received' --header 'x-user-id: 2' --data ''

#### Response
    [{"id":15,"body":"Hello","senderUserId":1,"receiverUserId":2,"sentAt":"2024-01-31T19:25:41.566956Z"}]

### Get messages sent by a user to a particular user
#### Request(x-user-id in header should be populated with the user id of user performing the action/curl)
`GET localhost:8080/api/v1/messages?transfer_type=sent`

    curl --location 'localhost:8080/api/v1/messages?fellow_user_id=2&transfer_type=sent' --header 'x-user-id: 1' --data ''

#### Response
    [{"id":15,"body":"Hello","senderUserId":1,"receiverUserId":2,"sentAt":"2024-01-31T19:25:41.566956Z"}]

