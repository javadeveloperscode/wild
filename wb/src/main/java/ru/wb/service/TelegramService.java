//package ru.wb.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import ru.wb.entity.TowarEntity;
//
//
//@Slf4j
//@Service
//public class TelegramService extends TelegramLongPollingBot {
//
//  private final String botUsername;
//  private final String configuredChatId;
//  private volatile String lastSeenChatId;
//
//  public TelegramService(
//      @Value("${telegram.bot.token}") String botToken,
//      @Value("${telegram.bot.username}") String botUsername,
//      @Value("${telegram.chat.id:}") String chatId
//  ) {
//    super(botToken);
//    this.botUsername = botUsername;
//    this.configuredChatId = chatId == null ? "" : chatId.trim();
//  }
//
//  private String effectiveChatId() {
//    String id = !configuredChatId.isBlank() ? configuredChatId : (lastSeenChatId == null ? "" : lastSeenChatId);
//    if (id.isBlank()) log.warn("⚠️ ChatId не задан. Напиши боту /start или /id.");
//    return id;
//  }
//
//  @Override public String getBotUsername() { return botUsername; }
//
//  @Override
//  public void onUpdateReceived(Update update) {
//    if (update.hasMessage() && update.getMessage().hasText()) {
//      String msg = update.getMessage().getText();
//      String from = String.valueOf(update.getMessage().getChatId());
//      lastSeenChatId = from; // запоминаем актуальный чат
//      log.info("📨 {}: {}", from, msg);
//
//      if ("/id".equalsIgnoreCase(msg)) {
//        sendSimpleMessage(from, "chat_id = <code>" + from + "</code>");
//        return;
//      }
//      if ("/start".equalsIgnoreCase(msg)) {
//        sendSimpleMessage(from, "🤖 Бот для мониторинга MacBook запущен!");
//        sendSimpleMessage(from, "Ваш chat_id = <code>" + from + "</code>");
//        return;
//      }
//    }
//  }
//
//  public void sendMacBookAlert(TowarEntity macbook) {
//    String chatId = effectiveChatId();
//    if (chatId.isBlank()) {
//      log.warn("⚠️ ChatId пуст. Напиши боту /start или /id из нужного чата.");
//      return;
//    }
//    String message = formatMacBookMessage(macbook);
//    sendSimpleMessage(chatId, message);
//    log.info("✅ TG уведомление отправлено для nm:{} в чат {}", macbook.getNmId(), chatId);
//  }
//
//  private String formatMacBookMessage(TowarEntity m) {
//    String name  = escape(m.getName());
//    String brand = escape(m.getBrand() != null ? m.getBrand() : "Apple");
//    return String.format(
//        "🔥 <b>НАЙДЕН MACBOOK!</b>\n\n" +
//            "📱 <b>%s</b>\n" +
//            "💰 <b>%.0f ₽</b>\n" +
//            "🏷️ Артикул: <code>%s</code>\n" +
//            "🏪 Бренд: %s\n\n" +
//            "🔗 <a href=\"%s\">Открыть на WB</a>\n",
//        name, m.getPriceRub(), m.getNmId(), brand, m.getUrl()
//    );
//  }
//  private String escape(String s) {
//    return s == null ? ""
//        : s.replace("&","&amp;")
//        .replace("<","&lt;")
//        .replace(">","&gt;");
//  }
//
//
//
//  public void sendSimpleMessage(String chatId, String text) {
//    try {
//      SendMessage msg = new SendMessage();
//      msg.setChatId(chatId.trim());
//      msg.setText(text);
//      msg.setParseMode("HTML");
//      msg.disableWebPagePreview();
//      execute(msg);
//    } catch (TelegramApiException e) {
//      log.error("❌ Ошибка отправки в TG (chatId={}): {}", chatId, e.getMessage(), e);
//    }
//  }
//
//}
