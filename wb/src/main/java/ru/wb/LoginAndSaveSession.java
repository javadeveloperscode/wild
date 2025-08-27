package ru.wb;


import com.microsoft.playwright.*;

import java.nio.file.Paths;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;

import java.nio.file.Paths;

public class LoginAndSaveSession {
  public static void main(String[] args) {
    try (Playwright pw = Playwright.create()) {
      BrowserContext ctx = pw.chromium().launchPersistentContext(
          Paths.get("wb-profile"),
          new BrowserType.LaunchPersistentContextOptions()
              .setHeadless(false)
              .setLocale("ru-RU")
              .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
      );
      Page page = ctx.newPage();
      page.navigate("https://www.wildberries.ru/security/login");
      // руками номер + код из SMS
      page.waitForURL("**/lk/**", new Page.WaitForURLOptions().setTimeout(300_000));
      System.out.println("✅ Профиль wb-profile обновлён (cookies/localStorage сохранены)");
      ctx.close();
    }
  }
}