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
  "message": "The user [1234] could not be found."
}
```

GET /users/{id}
---------------

Get information about a particular user. In addition to the standard response:

```javascript
{
  "user": {
    "id": 1234
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
    "id": 1234
  },
  "items": [
    "item-id-1",
    "item-id-2",
    "item-id-3"
  ]
}
```

