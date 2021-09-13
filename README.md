# Assemblyline Java Client Library

The assemblyline java client library provides methods to submit requests to assemblyline.

## Using the client

To instantiate the client bean set the application properties associated with the desired authentication method. The
client can be accessed by autowiring the bean into the class using it.

There are two authentication methods: username/apikey or username/password.

### API Key Authentication

To instantiate an API key authenticated assemblyline client, define the following properties:

    assemblyline-java-client:
        url: <assemblyline-instance-url>
        api-auth:
            apikey: <api-key>
            username: <username>

### Password Authentication

To instantiate a password-authenticated assemblyline client, define the following properties:

    assemblyline-java-client:
        url: <assemblyline-instance-url>
        password-auth:
            password: <password>
            username: <username>

### HttpClient Configuration

By default, the AssemblyLine client will use an HttpClient with default settings and HTTPS support.

#### Proxy

To go through a proxy, configure a custom ```reactor.netty.httpclient.HttpClient``` bean that includes proxy settings.
For example:

    @Bean
    public HttpClient httpClient() {
        return HttpClient.create()
            // Enable HTTPS support
            .secure()
            // Configure proxy
            .proxy(proxyOptions -> proxyOptions
                .type(ProxyProvider.Proxy.HTTP)
                .host("https://proxy.example.com")
                .port(443));
    }

#### HTTPS

HTTPS options can be configured with one of two methods:

1. Use the standard JVM options (```-Djavax.net.ssl.trustStore```, ```-Djavax.net.ssl.trustStorePassword```, etc)
2. Configure a custom ```reactor.netty.httpclient.HttpClient``` bean that includes the desired settings.