package pl.lodz.p.stanczyk.userservice.api

import groovyx.net.http.HttpResponseDecorator
import pl.lodz.p.stanczyk.userservice.IntegrationSpec
import spock.lang.Unroll

import static pl.lodz.p.stanczyk.userservice.api.CreateUserRequestBuilder.aCreateUserRequest

class CreateUserSpec extends IntegrationSpec {

    def "should create user when the request has proper data"() {
        given:
        def request = aCreateUserRequest()
                .withLogin("TestLogin")
                .withName("Test Name")
                .withPassword("StrongPassword1234!")
                .build()

        when:
        def response = createUserRequestIsSent(request)

        then:
        response.status == 200
        with(response.data) {
            id != null
            type = "READER"
            login == "TestLogin"
            password == null
            name == "Test Name"
        }

        and:
        with(storedUser(response.data.id)) {
            type == "READER" //todo
            login == "TestLogin"
            password == "StrongPassword1234!" //todo hashed
            name == "Test Name"
        }
    }

    @Unroll
    def "should create user when login #desc"() {
        given:
        def request = aCreateUserRequest()
                .withLogin(login)
                .build()

        when:
        def response = createUserRequestIsSent(request)

        then:
        response.status == 200
        with(response.data) {
            login == login
        }

        where:
        login    || desc
        "123456" || "has 6 characters"
        "1" * 20 || "has 20 characters"
    }

    @Unroll
    def "should not create user when login #desc"() {
        given:
        def request = aCreateUserRequest()
                .withLogin(login)
                .build()

        when:
        def response = createUserRequestIsSent(request)

        then:
        response.status == 400

        where:
        login         || desc
        "12345"       || "is shorter than 6 characters"
        "1" * 21      || "is longer than 20 characters"
        "White space" || "contains white spaces"
    }

    @Unroll
    def "should create user when name #desc"() {
        given:
        def request = aCreateUserRequest()
                .withName(name)
                .build()

        when:
        def response = createUserRequestIsSent(request)

        then:
        response.status == 200
        with(response.data) {
            name == name
        }

        where:
        name     || desc
        "1"      || "has 1 character"
        "1" * 30 || "has 30 characters"
    }

    @Unroll
    def "should not create user when name #desc"() {
        given:
        def request = aCreateUserRequest()
                .withName(name)
                .build()

        when:
        def response = createUserRequestIsSent(request)

        then:
        response.status == 400

        where:
        name     || desc
        ""       || "is empty"
        " "      || "is blank"
        "1" * 31 || "is longer than 30 characters"
    }

    @Unroll
    def "should create user when password #desc"() {
        given:
        def request = aCreateUserRequest()
                .withPassword(password)
                .build()

        when:
        def response = createUserRequestIsSent(request)

        then:
        response.status == 200
        with(response.data) {
            password == password
        }

        where:
        password   || desc
        "12345678" || "has 8 characters"
        "1" * 50   || "has 50 characters"
    }

    @Unroll
    def "should not create user when password #desc"() {
        given:
        def request = aCreateUserRequest()
                .withPassword(password)
                .build()

        when:
        def response = createUserRequestIsSent(request)

        then:
        response.status == 400

        where:
        password  || desc
        "Str0ng!" || "is shorter than 8 characters"
        "1" * 51  || "is longer than 50 characters"
    }

    HttpResponseDecorator createUserRequestIsSent(Map request) {
        client.post(request) as HttpResponseDecorator
    }

    void storedUser(String userId) {

    }
}
