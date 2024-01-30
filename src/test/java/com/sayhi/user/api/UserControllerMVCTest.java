package com.sayhi.user.api;

import com.sayhi.user.exception.UserAlreadyExistsException;
import com.sayhi.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.stream.Stream;

import static com.sayhi.fixtures.UserFixtures.sampleUser;
import static org.mockito.Mockito.*;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerMVCTest {

  @Autowired MockMvc mvc;
  @MockBean UserService userService;


  @ParameterizedTest
  @MethodSource("invalidCreateUserRequestBody")
  void createUserWithInvalidRequestData(String requestBody) throws Exception {
    performCreateUser(requestBody)
      .andExpect(status().isBadRequest());

    verifyNoInteractions(userService);
  }

  @Test
  void createUserWhenAlreadyExists() throws Exception {
    when(userService.createUser(any())).thenThrow(UserAlreadyExistsException.class);

    var requestBody = createUserRequestBody("test_nick");
    performCreateUser(requestBody)
      .andExpect(status().isConflict());
  }

  @Test
  void createUser() throws Exception {
    when(userService.createUser(sampleUser("test_nick")))
      .thenReturn(sampleUser(123L, "test_nick"));

    var requestBody = createUserRequestBody("test_nick");
    var responseBody = performCreateUser(requestBody)
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();

    assertEquals(responseBody, expectedResponseBody(), LENIENT);
  }

  private String expectedResponseBody() {
    return """
      {
        "user": {
            "id": 123,
            "nickName": "test_nick"
        }
      }
      """;
  }

  static Stream<Arguments> invalidCreateUserRequestBody() {
    return Stream.of(
      Arguments.of(createUserRequestBody(null)),  //No nickname supplied
      Arguments.of(createUserRequestBody("")),     //Empty nickname supplied
      Arguments.of(createUserRequestBody(STR."\{"testm".repeat(50)}1"))     //Nickname longer than valid
    );
  }

  static String createUserRequestBody(String nickName) {
    if (nickName == null)
      return "{}";

    return STR."""
      {
        "nickName": "\{nickName}"
      }
      """;
  }

  ResultActions performCreateUser(String requestBody) throws Exception {
    return mvc.perform(post("/api/v1/users")
      .contentType(MediaType.APPLICATION_JSON)
      .content(requestBody));
  }

}