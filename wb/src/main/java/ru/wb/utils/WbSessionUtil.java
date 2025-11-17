//package ru.wb.utils;
//
//import com.microsoft.playwright.BrowserContext;
//import lombok.extern.slf4j.Slf4j;
//
//
//@Slf4j
//public class WbSessionUtil {
//
//  public static boolean checkSessionAlive(BrowserContext ctx) {
//    return ctx.cookies().stream().anyMatch(c ->
//        c.domain.contains("wildberries.ru") &&
//            ("x_wbaas_token".equalsIgnoreCase(c.name) || c.name.startsWith("wbx-") || "_wbauid".equalsIgnoreCase(c.name))
//    );
//  }
//}
