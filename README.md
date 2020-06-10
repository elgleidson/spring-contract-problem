This is just a simple project showing the problem with Spring Contract tests.

# The sample project:

## producer:
* _HEAD_ endpoint `/primes/{number}` which checks if the number is a prime number and returns `204` if the number is prime or `404` otherwise.
* _GET_ endpoint `/primes/{number}` which returns a list of prime number up to the number with status `200` or `404` otherwise.

## consumer:
* _HEAD_ endpoint `/primes/{number}` which consumes the producer's _HEAD_ endpoint.
* _GET_ endpoint `/primes/{number}` which consumes the producer's _GET_ endpoint.

# The problem:

Our HEAD endpoint has the following annotation: 

```java
@RequestMapping(method = RequestMethod.HEAD, value = "/primes/{number}")
public Mono<ResponseEntity<Object>> head(@PathVariable int number) {
    ...
}
```
We got the following error:
```
Scenario: HEAD prime number       # src/test/resources/cucumber/primes.feature:3
  Given the number 7              # cucumber.CucumberSteps.givenANumber(int)
2020-06-10 09:02:43.249  INFO 14810 --- [ctor-http-nio-2] c.g.e.p.consumer.ConsumerController      : enter GET: 7
2020-06-10 09:02:43.324  INFO 14810 --- [tp413046447-327] w.o.e.j.s.handler.ContextHandler.ROOT    : RequestHandlerClass from context returned com.github.tomakehurst.wiremock.http.StubRequestHandler. Normalized mapped under returned 'null'
2020-06-10 09:02:43.424  INFO 14810 --- [tp413046447-327] WireMock                                 : Request received:
127.0.0.1 - GET /primes/7

Accept: [application/json]
User-Agent: [ReactorNetty/0.9.7.RELEASE]
Host: [localhost:8080]
Accept-Encoding: [gzip]



Matched response definition:
{
  "status" : 200,
  "body" : "{\"primes\":[2,3,5]}",
  "headers" : {
    "Content-Type" : "application/json"
  },
  "transformers" : [ "response-template" ]
}

Response:
HTTP/1.1 200
Content-Type: [application/json]
Matched-Stub-Id: [95c10331-dc6b-4c93-9f3d-822396a50a34]


  When I hit HEAD                 # cucumber.CucumberSteps.whenIHitHead()
  Then the API returns status 204 # cucumber.CucumberSteps.thenApiReturnsStatus(int)
      org.opentest4j.AssertionFailedError: expected: <204 NO_CONTENT> but was: <200 OK>
      ...
```

As we can see, the HEAD test calls GET instead of HEAD method.

But if we add `produces` to our HEAD:
```java
@RequestMapping(method = RequestMethod.HEAD, value = "/primes/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
public Mono<ResponseEntity<Object>> head(@PathVariable int number) {
    ...
}
```
Then we got:
```
Scenario: HEAD prime number       # src/test/resources/cucumber/primes.feature:3
  Given the number 7              # cucumber.CucumberSteps.givenANumber(int)
2020-06-10 09:07:42.654  INFO 14958 --- [ctor-http-nio-2] c.g.e.p.consumer.ConsumerController      : enter HEAD: 7
2020-06-10 09:07:42.721  INFO 14958 --- [p1150585542-327] w.o.e.j.s.handler.ContextHandler.ROOT    : RequestHandlerClass from context returned com.github.tomakehurst.wiremock.http.StubRequestHandler. Normalized mapped under returned 'null'
2020-06-10 09:07:42.729  INFO 14958 --- [p1150585542-327] WireMock                                 : Request received:
127.0.0.1 - HEAD /primes/7

Accept: [*/*]
User-Agent: [ReactorNetty/0.9.7.RELEASE]
Host: [localhost:8080]
Accept-Encoding: [gzip]



Matched response definition:
{
  "status" : 204,
  "transformers" : [ "response-template" ]
}

Response:
HTTP/1.1 204
Matched-Stub-Id: [c7f67bed-09c7-482e-bf9d-4443748f47bd]


  When I hit HEAD                 # cucumber.CucumberSteps.whenIHitHead()
  Then the API returns status 204 # cucumber.CucumberSteps.thenApiReturnsStatus(int)

```

As we can see, now the HEAD method is called as expected.