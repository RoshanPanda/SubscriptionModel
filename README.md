# SubscriptionModel
A sample Demonstration using Scala and ZIO

### SignUp
signup valid request : 

```
{
"FirstName": "Roshan",
"LastName": "Panda",
"UserName": "RP",
"PassWord": "Roshan@2021",
"age": 30
}
```
Response:
```
{
    "id": "888d7bee-59c9-475a-ac04-2f7af98812cf",
    "message": "welcome to the family",
    "success": true
}
```
Error Response:
```
{
    "error": "Not a valid password",
    "success": false
}
```

