package com.db.awmd.challenge;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.MoneyTransferRequest;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AccountsControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private NotificationService notificationService;

    @Before
    public void prepareMockMvc() {
        this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
        // Reset the existing accounts before each test.
        accountsService.getAccountsRepository().clearAccounts();
    }

    @Test
    public void createAccount() throws Exception {
        this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

        Account account = accountsService.getAccount("Id-123");
        assertThat(account.getAccountId()).isEqualTo("Id-123");
        assertThat(account.getBalance()).isEqualByComparingTo("1000");
    }

    @Test
    public void createDuplicateAccount() throws Exception {
        this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

        this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isBadRequest());
    }

    @Test
    public void createAccountNoAccountId() throws Exception {
        this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"balance\":1000}")).andExpect(status().isBadRequest());
    }

    @Test
    public void createAccountNoBalance() throws Exception {
        this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"Id-123\"}")).andExpect(status().isBadRequest());
    }

    @Test
    public void createAccountNoBody() throws Exception {
        this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createAccountNegativeBalance() throws Exception {
        this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"Id-123\",\"balance\":-1000}")).andExpect(status().isBadRequest());
    }

    @Test
    public void createAccountEmptyAccountId() throws Exception {
        this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"\",\"balance\":1000}")).andExpect(status().isBadRequest());
    }

    @Test
    public void getAccount() throws Exception {
        String uniqueAccountId = "Id-" + System.currentTimeMillis();
        Account account = new Account(uniqueAccountId, new BigDecimal("123.45"));
        this.accountsService.createAccount(account);
        this.mockMvc.perform(get("/v1/accounts/" + uniqueAccountId))
                .andExpect(status().isOk())
                .andExpect(
                        content().string("{\"accountId\":\"" + uniqueAccountId + "\",\"balance\":123.45}"));
    }

    @Test
    public void transferMoneyWithEmptyAccountId() throws Exception {
        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest("", "1234", BigDecimal.valueOf(100));
        this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
                .content(toJson(moneyTransferRequest))).andExpect(status().isBadRequest());

        moneyTransferRequest = new MoneyTransferRequest("1234", "", BigDecimal.valueOf(100));
        this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
                .content(toJson(moneyTransferRequest))).andExpect(status().isBadRequest());

        moneyTransferRequest = new MoneyTransferRequest("", "", BigDecimal.valueOf(100));
        this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
                .content(toJson(moneyTransferRequest))).andExpect(status().isBadRequest());
    }

    @Test
    public void transferMoneyWithNotExitsAccounts() throws Exception {
        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest("123", "1234", BigDecimal.valueOf(100));
        this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
                .content(toJson(moneyTransferRequest))).andExpect(status().isBadRequest());

        Account fromAccount = new Account(UUID.randomUUID().toString(), BigDecimal.valueOf(5000));
        this.accountsService.createAccount(fromAccount);
        moneyTransferRequest = new MoneyTransferRequest(fromAccount.getAccountId(), "1234", BigDecimal.valueOf(100));
        this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
                .content(toJson(moneyTransferRequest))).andExpect(status().isBadRequest());

        Account toAccount = new Account(UUID.randomUUID().toString(), BigDecimal.valueOf(3000));
        this.accountsService.createAccount(toAccount);
        moneyTransferRequest = new MoneyTransferRequest("123", toAccount.getAccountId(), BigDecimal.valueOf(100));
        this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
                .content(toJson(moneyTransferRequest))).andExpect(status().isBadRequest());
    }

    @Test
    public void transferMoneyWithInsufficientBalance() throws Exception {
        Account fromAccount = new Account(UUID.randomUUID().toString(), BigDecimal.valueOf(5000));
        this.accountsService.createAccount(fromAccount);

        Account toAccount = new Account(UUID.randomUUID().toString(), BigDecimal.valueOf(3000));
        this.accountsService.createAccount(toAccount);

        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest(fromAccount.getAccountId(), toAccount.getAccountId(), BigDecimal.valueOf(5500));

        this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
                .content(toJson(moneyTransferRequest))).andExpect(status().isBadRequest());
    }

    @Test
    public void transferMoney() throws Exception {
        Account fromAccount = new Account(UUID.randomUUID().toString(), BigDecimal.valueOf(5000));
        this.accountsService.createAccount(fromAccount);

        Account toAccount = new Account(UUID.randomUUID().toString(), BigDecimal.valueOf(3000));
        this.accountsService.createAccount(toAccount);

        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest(fromAccount.getAccountId(), toAccount.getAccountId(), BigDecimal.valueOf(500));

        this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
                .content(toJson(moneyTransferRequest))).andExpect(status().isOk());

        fromAccount = accountsService.getAccount(fromAccount.getAccountId());
        Assert.assertThat(fromAccount.getBalance(), Matchers.is(BigDecimal.valueOf(4500)));

        toAccount = accountsService.getAccount(toAccount.getAccountId());
        Assert.assertThat(toAccount.getBalance(), Matchers.is(BigDecimal.valueOf(3500)));

        Mockito.verify(notificationService, Mockito.times(2)).notifyAboutTransfer(Mockito.any(), Mockito.any());
    }

    private String toJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}
