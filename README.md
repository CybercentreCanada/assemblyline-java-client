# Assemblyline Java Client Library

The assemblyline java client library provides methods to submit requests to assemblyline.

## Using the client

To instantiate the client bean set the application properties associated with the desired authentication method. The client can be accessed by autowiring the bean into the class using it.

There are two authentication methods: username/apikey or username/password.

### API Key Authentication

To instantiate an API key authenticated assemblyline client, define the following properties:

    assemblyline-java-client:
        url: <assemblyline-instance-url>
        api-auth:
            apikey: <api-key>
            username: <username>

### Password Authentication

To instantiate a password authenticated assemblyline client, define the following properties:

    assemblyline-java-client:
        url: <assemblyline-instance-url>
        password-auth:
            password: <password>
            username: <username>
 
