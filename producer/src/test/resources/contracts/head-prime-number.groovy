package contracts

org.springframework.cloud.contract.spec.Contract.make {
    description "should return ok for 7 as it is a prime number"
    request {
        method 'HEAD'
        url '/primes/7'
    }
    response {
        status 204
    }
}