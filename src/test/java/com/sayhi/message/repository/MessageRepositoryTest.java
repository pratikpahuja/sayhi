package com.sayhi.message.repository;

import com.sayhi.message.domain.Message;
import com.sayhi.user.domain.AppUser;
import com.sayhi.user.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.util.stream.Stream;

import static com.sayhi.fixtures.MessageFixtures.sampleMessage;
import static com.sayhi.fixtures.UserFixtures.sampleUser;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageRepositoryTest {
  @Autowired AppUserRepository userRepository;
  @Autowired MessageRepository repository;
  @Autowired JdbcTemplate jdbcTemplate;
  private AppUser sender, receiver;

  @BeforeAll
  void beforeAll() {
    sender = userRepository.save(sampleUser("214-description"));
    receiver = userRepository.save(sampleUser("516-description"));
  }

  @BeforeEach
  void setup() {
    truncateTable();
  }

  @ParameterizedTest
  @MethodSource("invalidMessageData")
  void saveWithInvalidData(Message message) {
    assertThrows(DataIntegrityViolationException.class, ()-> repository.save(message));
  }

  @Test
  void save() {
    var sentAt = now();
    var message  = createTestMessage(sentAt).build();
    var messageInDb = repository.save(message);

    assertThat(messageInDb.getId(), is(notNullValue()));
    assertThat(messageInDb.getBody(), is("sample-body"));
    assertThat(messageInDb.getSenderUserId(), is(sender.getId()));
    assertThat(messageInDb.getReceiverUserId(), is(receiver.getId()));
    assertThat(messageInDb.getSentAt(), is(sentAt));
  }

  @Test
  void findByReceiverWhenNoMessage() {
    var messages = repository.findByReceiverUserIdOrderBySentAtDesc(receiver.getId());
    assertThat(messages, hasSize(0));
  }

  @Test
  void findByReceiver() {
    var sentAt = now().truncatedTo(MILLIS);
    var expectedMessage = repository.save(createTestMessage(sentAt).build());
    var messages = repository.findByReceiverUserIdOrderBySentAtDesc(receiver.getId());

    assertThat(messages, hasSize(1));
    var actualMessage = messages.getFirst();
    assertThat(actualMessage.getId(), is(expectedMessage.getId()));
    assertThat(actualMessage.getBody(), is("sample-body"));
    assertThat(actualMessage.getSenderUserId(), is(sender.getId()));
    assertThat(actualMessage.getReceiverUserId(), is(receiver.getId()));
    assertThat(actualMessage.getSentAt(), is(sentAt));
  }

  @Test
  void testFindByReceiverSorting() {
    var sentAtMessage1 = now().minusSeconds(60).truncatedTo(MILLIS);
    var sentAtMessage2 = now().truncatedTo(MILLIS);
    var expectedMessage1 = repository.save(createTestMessage(sentAtMessage1).build());
    var expectedMessage2 = repository.save(createTestMessage(sentAtMessage2).build());
    var messages = repository.findByReceiverUserIdOrderBySentAtDesc(receiver.getId());

    assertThat(messages, hasSize(2));
    assertThat(messages.getFirst().getId(), is(expectedMessage2.getId()));
    assertThat(messages.getLast().getId(), is(expectedMessage1.getId()));
  }

  @Test
  void findBySenderWhenNoMessage() {
    var messages = repository.findBySenderUserIdOrderBySentAtDesc(sender.getId());
    assertThat(messages, hasSize(0));
  }

  @Test
  void findBySender() {
    var sentAt = now().truncatedTo(MILLIS);
    var expectedMessage = repository.save(createTestMessage(sentAt).build());
    var messages = repository.findBySenderUserIdOrderBySentAtDesc(sender.getId());

    assertThat(messages, hasSize(1));
    var actualMessage = messages.getFirst();
    assertThat(actualMessage.getId(), is(expectedMessage.getId()));
    assertThat(actualMessage.getBody(), is("sample-body"));
    assertThat(actualMessage.getSenderUserId(), is(sender.getId()));
    assertThat(actualMessage.getReceiverUserId(), is(receiver.getId()));
    assertThat(actualMessage.getSentAt(), is(sentAt));
  }

  @Test
  void testFindBySenderSorting() {
    var sentAtMessage1 = now().minusSeconds(60).truncatedTo(MILLIS);
    var sentAtMessage2 = now().truncatedTo(MILLIS);
    var expectedMessage1 = repository.save(createTestMessage(sentAtMessage1).build());
    var expectedMessage2 = repository.save(createTestMessage(sentAtMessage2).build());
    var messages = repository.findBySenderUserIdOrderBySentAtDesc(sender.getId());

    assertThat(messages, hasSize(2));
    assertThat(messages.getFirst().getId(), is(expectedMessage2.getId()));
    assertThat(messages.getLast().getId(), is(expectedMessage1.getId()));
  }

  @Test
  void findByReceiverAndSenderWhenNoMessage() {
    var messages = repository.findByReceiverUserIdAndSenderUserIdOrderBySentAtDesc(receiver.getId(), sender.getId());

    assertThat(messages, hasSize(0));
  }

  @Test
  void findByReceiverAndSender() {
    var sentAt = now().truncatedTo(MILLIS);
    var expectedMessage = repository.save(createTestMessage(sentAt).build());
    var messages = repository.findByReceiverUserIdAndSenderUserIdOrderBySentAtDesc(receiver.getId(), sender.getId());

    assertThat(messages, hasSize(1));
    var actualMessage = messages.getFirst();
    assertThat(actualMessage.getId(), is(expectedMessage.getId()));
    assertThat(actualMessage.getBody(), is("sample-body"));
    assertThat(actualMessage.getSenderUserId(), is(sender.getId()));
    assertThat(actualMessage.getReceiverUserId(), is(receiver.getId()));
    assertThat(actualMessage.getSentAt(), is(sentAt));
  }

  @Test
  void testFindByReceiverAndSenderSorting() {
    var sentAtMessage1 = now().minusSeconds(60).truncatedTo(MILLIS);
    var sentAtMessage2 = now().truncatedTo(MILLIS);
    var expectedMessage1 = repository.save(createTestMessage(sentAtMessage1).build());
    var expectedMessage2 = repository.save(createTestMessage(sentAtMessage2).build());
    var messages = repository.findByReceiverUserIdAndSenderUserIdOrderBySentAtDesc(receiver.getId(), sender.getId());

    assertThat(messages, hasSize(2));
    assertThat(messages.getFirst().getId(), is(expectedMessage2.getId()));
    assertThat(messages.getLast().getId(), is(expectedMessage1.getId()));
  }

  Stream<Arguments> invalidMessageData() {
    var now = Instant.now();
    return Stream.of(
      Arguments.of(createTestMessage(now).body(null).build()),          //null body
      Arguments.of(createTestMessage(now).receiverUserId(-1).build()),  //Incorrect receiver
      Arguments.of(createTestMessage(now).senderUserId(-1).build()),    //Incorrect sender
      Arguments.of(createTestMessage(now).sentAt(null).build())         //null sent at
    );
  }

  private Message.MessageBuilder createTestMessage(Instant sentAt) {
    return sampleMessage(sender, receiver, sentAt);
  }

  void truncateTable() {
    jdbcTemplate.execute("TRUNCATE TABLE message");
  }
}