package ru.wb.cfg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.wb.service.TelegramService;

@Configuration
public class TelegramBotConfig {

  @Bean
  public TelegramBotsApi telegramBotsApi(TelegramService bot) throws TelegramApiException {
    TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
    api.registerBot(bot);
    return api;
  }
}
