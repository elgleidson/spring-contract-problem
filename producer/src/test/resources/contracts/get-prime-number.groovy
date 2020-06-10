package contracts

org.springframework.cloud.contract.spec.Contract.make {
    description "should return a list of prime numbers less than 7"
    request {
        method 'GET'
        url '/primes/7'
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(file("get-prime-number.response.json"))
    }
}