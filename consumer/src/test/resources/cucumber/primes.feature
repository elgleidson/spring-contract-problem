Feature: prime numbers

  Scenario: HEAD prime number
    Given the number 7
    When I hit HEAD
    Then the API returns status 204

  Scenario: GET prime number
    Given the number 7
    When I hit GET
    Then the API returns status 200
    Then the API returns body
    | 2 |
    | 3 |
    | 5 |
