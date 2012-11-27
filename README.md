saveitapp
=========

A web service to save articles for later retrieval.

API Specification
=================

Introduction
------------

In addition to the requested information, a "status" integer and a "message"
string will are returned to provide information on the success of the request.
For example, a successful message will include:

```javascript
{
  "status": 200,
  "message": "Success"
}
```

While an error looks like:

```javascript
{
  "status": 404,
  "message": "The user [/users/1234] could not be found."
}
```


POST /users
-----------

Create a new user. The content type of the body must be JSON and it must include
a username:

```javascript
{
  "username": "myuser"
}
```

Returns the standard response.


GET /users/{id}
---------------

Get information about a particular user. In addition to the standard response:

```javascript
{
  "id": "/users/1234"
}
```

GET /users/{id}/items
---------------------

Get a list of items IDs for a particular user. In addition to the standard
response:

```javascript
{
  "user": {
    "id": "/users/1234"
  },
  "items": [
    "/users/1234/items/item-id-1",
    "/users/1234/items/item-id-2",
    "/users/1234/items/item-id-3"
  ]
}
```

POST /users/{username}/items
----------------------------

Create a new item for a user. The content type of the body must be JSON and it
must include a title:

```javascript
{
  "title": "this is a note title"
}
```

Returns the standard response.

GET /users/{user_id}/items/{item_id}
------------------------------------

Get a particular item. In addition to the standard response:

```javascript
{
  "user": {
    "id": "/users/1234"
  },
  id: "/users/1234/items/item-id-1"
}
```

