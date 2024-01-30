package com.sayhi.e2e;

import java.util.List;

import com.sayhi.messaging.domain.Message;
import com.sayhi.user.api.interfaces.CreateUserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Clock;

import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.http.HttpMethod.GET;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class E2EIT {
  @Autowired TestRestTemplate restTemplate;
  @Autowired Clock clock;

  @Test
  void createUserAndSendMessage() {
    var user1 = performCreateUser(STR."user1-\{currentTimeMillis()}");
    var user2 = performCreateUser(STR."user2-\{currentTimeMillis()}");
    var user3 = performCreateUser(STR."user3-\{currentTimeMillis()}");

    //User1 sending message to user2
    performSendMessage(user1.getUser().getId(), user2.getUser().getId(), STR."message u1 -> u2 @ \{currentTimeMillis()}");

    //User1 sending message to user3
    performSendMessage(user1.getUser().getId(), user2.getUser().getId(), STR."message u1 -> u3 @ \{currentTimeMillis()}");

    //Get messages sent by user1
    var messages = performGetMessages(user1.getUser().getId());
    assertThat(messages, hasSize(2));

    assertThat(messages.getFirst().getBody(), startsWith("message u1 -> u3"));
    assertThat(messages.getLast().getBody(), startsWith("message u1 -> u2"));
  }

  List<Message> performGetMessages(long userId) {
    var headers = new HttpHeaders();
    headers.set("x-user-id", valueOf(userId));

    var uri = UriComponentsBuilder.fromUriString("/api/v1/messages")
      .queryParam("transfer_type", "sent")
      .build();

    var request = new RequestEntity(headers, GET, uri.toUri());

    return restTemplate.exchange(request, new ParameterizedTypeReference<List<Message>>() {}).getBody();
  }

  void performSendMessage(long senderId, long receiverId, String body) {
    var requestBody = STR."""
      {
        "receiverUserId": \{receiverId},
        "messageBody": "\{body}"
      }
      """;

    var request = RequestEntity.post("/api/v1/messages")
      .contentType(MediaType.APPLICATION_JSON)
      .header("x-user-id", valueOf(senderId))
      .body(requestBody);

    restTemplate.exchange(request, String.class);
  }

  CreateUserResponse performCreateUser(String nickName) {
    var requestBody = STR."""
      {
        "nickName": "\{nickName}"
      }
      """;

    var request = RequestEntity.post("/api/v1/users")
      .contentType(MediaType.APPLICATION_JSON)
      .body(requestBody);

    return restTemplate.exchange(request, CreateUserResponse.class).getBody();
  }

}
