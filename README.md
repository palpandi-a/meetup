# Community Meet-Up Mini Project API Documentation
This project provides a platform for community users to register and participate in meet-ups. It is built using Servlet, Maven, and Hibernate.

## API Documentation

### Authorization APIs

#### Sign Up
```text
Endpoint: POST /signup
```

##### Parameters:
| Parameter | Type     | Description |
| :-------- | :------- | :------- |
| `name` | `string` | **Required.** User's name |
| `email` | `string` | **Required.** User's email |
| `password` | `string` | **Required.** User's password |

##### Request Body
```json
{
    "name": "user-1",
    "email": "user-1@email.com",
    "password": "1234"
}
```

##### Response
```json
{
    "modifiedTime": "07-07-2024 22:07",
    "name": "user-1",
    "createdTime": "07-07-2024 22:07",
    "id": 1,
    "email": "user-1@email.com"
}
```

#### Login
```http
Endpoint: POST /login
```

##### Parameters:
| Parameter | Type     | Description |
| :-------- | :------- | :------- |
| `email` | `string` | **Required.** User's email |
| `password` | `string` | **Required.** User's password |

##### Request Body
```
{
    "email": "user-1@email.com",
    "password": "1234"
}
```

##### Response
```json
{
    "modifiedTime": "07-07-2024 13:11",
    "name": "user-1",
    "createdTime": "07-07-2024 13:11",
    "id": 1,
    "email": "user-1@email.com"
}
```


##### Note: 
Both the sign-up and login APIs return an **auth key** as the value of the Authorization header. Use this header value for performing further operations. The **Authorization** header is mandatory for all other APIs. You will receive an authorization-related error response if you do not provide it.

#### Logout
```http
Endpoint: POST /logout
```

##### Response
```text 
204 - No Content
```

### User module APIs

#### Get a user
```http
Endpoint: GET /users/{userId}
```

##### Response
```json
{
    "modifiedTime": "08-07-2024 20:41",
    "name": "user-1",
    "createdTime": "07-07-2024 13:11",
    "id": 1,
    "email": "user_1@email.com"
}
```

#### Update a user

```http
Endpoint: PUT /users/{id}
```

##### Parameters:
| Parameter | Type     | Description |
| :-------- | :------- | :------- |
| `name` | `string` | User's name |

##### Request Body
```json
{
    "name": "User - 1"
}
```

###### Response
```json
{
    "modifiedTime": "08-07-2024 20:41",
    "name": "User - 1",
    "createdTime": "07-07-2024 13:11",
    "id": 1,
    "email": "user_1@email.com"
}
```


#### Delete a user
```http
Endpoint: DELETE /users/{id}
```

##### Response
```text
204 - No Content
```

#### Get user associated groups
This API will return list of groups where the user is a member of the group or creator of the group.

```http
Endpoint: GET /users/{userId}/groups
```

##### Response
```json
{
    "groups": [{
        "modifiedTime": "08-07-2024 20:55",
        "createdBy": 2,
        "members": [1],
        "name": "user-group",
        "createdTime": "08-07-2024 20:55",
        "id": 11
    }]
}
```

##### Get user associated events
This API will return list of events where the user an attendee of the event or creator of the event.

```http
Endpoint: GET /users/{id}/events
```

##### Response
```json
{
    "events": [{
        "modifiedTime": "07-07-2024 13:51",
        "createdBy": {
            "name": "user-2",
            "id": 2
        },
        "attendees": [{
            "name": "User-2",
            "id": 1
        }],
        "eventStatus": "COMPLETED",
        "eventTime": "27-06-2024 13:42",
        "description": null,
        "createdTime": "07-07-2024 13:51",
        "id": 1,
        "title": "Event Title"
    }]
}
```

### Group module APIs

##### Create a group
```http
Endpoint: POST /groups
```

| Parameter | Type     | Description |
| :-------- | :------- | :------- |
| `name` | `string` | **Required.** The name of the group |
| `createdBy` | `int` | **Required.** Valid user ID |
| `members` | `JSON Array` | Optional. Other valid user IDs |

##### Response
```json
{
  "modifiedTime": "08-07-2024 20:57",
  "createdBy": 1,
  "members": [2],
  "name": "user-group",
  "createdTime": "08-07-2024 20:57",
  "id": 12
}
```

#### Get all groups
```http
Endpoint: GET /groups
```

##### Response
```json
{
    "groups": [{
        "modifiedTime": "08-07-2024 20:57",
        "createdBy": 2,
        "members": [
            1
        ],
        "name": "sample",
        "createdTime": "08-07-2024 20:57",
        "id": 12
    }]
}
```

#### Get a group
```http
Endpoint: GET /groups/{groupId}
```

##### Response
```json
{
    "modifiedTime": "08-07-2024 20:57",
    "createdBy": 2,
    "members": [1],
    "name": "sample",
    "createdTime": "08-07-2024 20:57",
    "id": 12
}
```

#### Update a group
```http
Endpoint: PUT /groups/{groupId}
```

| Parameter | Type     | Description |
| :-------- | :------- | :------- |
| `name` | `string` | Optional. The name of the group |
| `members` | `JSON Array` | Optional. Valid user IDs or an empty array to remove all members |

##### Request Body
```json
{
    "name": "Updated group"
}
```

##### Response
```json
{
    "modifiedTime": "08-07-2024 21:08",
    "createdBy": 2,
    "members": [],
    "name": "Updated group",
    "createdTime": "08-07-2024 20:57",
    "id": 12
}
```

#### Delete a group
```http
Endpoint: DELETE /groups/{groupId}
```

##### Response
```text
204 - No Content
```

### Event module APIs

#### Create an event
```http
Endpoint: POST /events
```

| Parameter | Type     | Description |
| :-------- | :------- | :------- |
| `title` | `string` | **Required.** Title of the event |
| `createdBy` | `int` | **Required.** Valid user or group ID |
| `eventTime` | `timestamp` | Optional. Valid timestamp value |

##### Request Body
```json
{
    "title": "Event Title",
    "createdBy": 2,
    "eventTime": "08-07-2024 13:42"
}
```

##### Response
```json
{
    "modifiedTime": "08-07-2024 21:24",
    "createdBy": {
        "name": "user-2",
        "id": 2
    },
    "attendees": [],
    "eventStatus": "NOT_STARTED",
    "eventTime": "08-07-2024 13:42",
    "description": null,
    "createdTime": "08-07-2024 21:24",
    "id": 2,
    "title": "Event Title"
}
```


#### Get an event
```http
Endpoint: GET /events/{eventId}
```

##### Response
```json
{
    "modifiedTime": "08-07-2024 21:24",
    "createdBy": {
        "name": "user-2",
        "id": 2
    },
    "attendees": [],
    "eventStatus": "NOT_STARTED",
    "eventTime": "08-07-2024 13:42",
    "description": null,
    "createdTime": "08-07-2024 21:24",
    "id": 2,
    "title": "Event Title"
}
```

##### Get all events
```http
Endpoint: GET /events
```

##### Response
```json
{
    "events": [{
        "modifiedTime": "08-07-2024 21:24",
        "createdBy": {
            "name": "user-2",
            "id": 2
        },
        "attendees": [],
        "eventStatus": "NOT_STARTED",
        "eventTime": "08-07-2024 13:42",
        "description": null,
        "createdTime": "08-07-2024 21:24",
        "id": 2,
        "title": "Event Title"
    }]
}
```


#### Delete an event
```http
Endpoint: DELETE /events/{eventId}
```

##### Response
```text
204 - No Content
```

#### Register into event
```http
POST /events/{eventId}/register
```

##### Request Body
```json
{
    "userId": "1"
}
```

##### Response
```text
204 - No Content
```

#### Unregister from an event
```http
POST /events/{eventId}/unregister
```

##### Request Body
```json
{
    "userId": "1"
}
```

##### Response
```text
204 - No Content
```

#### Cancel an event
```http
Endpoint: POST /events/{eventId}/cancel
```

##### Response
```text
204 - No Content
```