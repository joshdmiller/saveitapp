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

GET /users/{id}
---------------

Get information about a particular user. In addition to the standard response:

```javascript
{
  "user": {
    "id": /users/1234
  }
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

