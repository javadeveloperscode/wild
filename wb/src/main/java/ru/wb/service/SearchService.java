//package ru.wb.service;
//
//import ru.wb.entity.TowarEntity;
//import ru.wb.repo.MacBookRepo;
//import ru.wb.utils.WbSessionUtil;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.microsoft.playwright.*;
//import com.microsoft.playwright.options.RequestOptions;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Paths;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Locale;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class SearchService {
//
//  private final TelegramService telegram;
//
//  // ---- Поисковые настройки (меняй тут при желании) ----
//  private static final String SEARCH_QUERY = "macbook";
//  private static final int PAGE_LIMIT = 150;
//  private static final String REGIONS = "64,83,4,38,33,70,68,30,40";
//  private static final String DEST = "-1257786,-131723,12358299,12358417";
//  private static final int REQUEST_TIMEOUT = 15_000;
//
//  private static final int BUY_THRESHOLD_PRICE_U = 5_200_000;
//
//  private static final List<String> EXCLUDED_KEYWORDS = Arrays.asList(
//      "витрин", "образец", "refurb", "реф", "восстанов", "уцен",
//      "demo", "демо", "без короб", "б/у", "бу", "used"
//  );
//
//  private final MacBookRepo macBookRepo;
//  private final ObjectMapper objectMapper = new ObjectMapper();
//
//  /**
//   * Пуллер раз в 2 минуты. По умолчанию ищем 50–60к.
//   */
//  @Scheduled(fixedDelayString = "PT2M")
//  public void poll() {
//    pollWithRangeRub(40_000, 60_000);
//  }
//
//  /**
//   * Общий метод: ищем по диапазону цен в рублях.
//   */
//  public void pollWithRangeRub(int minPriceRub, int maxPriceRub) {
//    log.info("▶ Старт опроса WB ({}–{} ₽)", minPriceRub, maxPriceRub);
//
//    try (Playwright playwright = Playwright.create()) {
//      BrowserContext context = createBrowserContext(playwright);
//      try {
//        if (!isSessionValid(context)) {
//          log.warn("⚠️ Сессия WB протухла, надо перелогиниться");
//          return;
//        }
//
//        setupTimeouts(context);
//
//        int totalFound = pollAllPages(context, minPriceRub, maxPriceRub);
//        logResults(totalFound, minPriceRub, maxPriceRub);
//
//      } finally {
//        context.close();
//      }
//    } catch (Exception e) {
//      log.error("WB poller unexpected error", e);
//    }
//  }
//
//
//  private BrowserContext createBrowserContext(Playwright playwright) {
//    return playwright.chromium().launchPersistentContext(
//        Paths.get("wb-profile"),
//        new BrowserType.LaunchPersistentContextOptions()
//            .setHeadless(true)
//            .setLocale("ru-RU")
//            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
//    );
//  }
//
//  private boolean isSessionValid(BrowserContext context) {
//    if (WbSessionUtil.checkSessionAlive(context)) {
//      log.info("✅ С токеном всё ок");
//      return true;
//    }
//    return false;
//  }
//
//  private void setupTimeouts(BrowserContext context) {
//    context.setDefaultNavigationTimeout(REQUEST_TIMEOUT);
//    context.setDefaultTimeout(REQUEST_TIMEOUT);
//  }
//
//  private int pollAllPages(BrowserContext context, int minPriceRub, int maxPriceRub) throws JsonProcessingException {
//    int totalFound = 0;
//    int page = 1;
//
//    while (true) {
//      JsonNode products = fetchProductsFromPage(context, page, minPriceRub, maxPriceRub);
//
//      if (!hasProducts(products)) {
//        if (page == 1) {
//          log.info("ℹ️ По фильтрам ({}–{} ₽) товаров не найдено.", minPriceRub, maxPriceRub);
//        }
//        break;
//      }
//
//      int pageFound = processProducts(products, minPriceRub, maxPriceRub);
//      totalFound += pageFound;
//
//      if (pageFound == 0) break;
//
//      page++;
//    }
//
//    return totalFound;
//  }
//
//  private JsonNode fetchProductsFromPage(BrowserContext context, int page, int minPriceRub, int maxPriceRub) throws JsonProcessingException {
//    String url = buildSearchUrl(page, minPriceRub, maxPriceRub);
//    APIResponse response = executeApiRequest(context, url);
//    String responseBody = response.text();
//
//    JsonNode root = objectMapper.readTree(responseBody);
//    return root.path("products");
//  }
//
//  private String buildSearchUrl(int page, int minPriceRub, int maxPriceRub) {
//    int minPriceU = minPriceRub * 100; // priceU — в копейках
//    int maxPriceU = maxPriceRub * 100;
//
//    return "https://search.wb.ru/exactmatch/ru/common/v18/search"
//        + "?query=" + URLEncoder.encode(SEARCH_QUERY, StandardCharsets.UTF_8)
//        + "&page=" + page
//        + "&limit=" + PAGE_LIMIT
//        + "&sort=priceup&resultset=catalog"
//        + "&regions=" + URLEncoder.encode(REGIONS, StandardCharsets.UTF_8)
//        + "&dest=" + URLEncoder.encode(DEST, StandardCharsets.UTF_8)
//        + "&priceU=" + minPriceU + ";" + maxPriceU;
//  }
//
//  private APIResponse executeApiRequest(BrowserContext context, String url) {
//    APIRequestContext api = context.request();
//    return api.get(url, RequestOptions.create()
//        .setHeader("Accept", "application/json")
//        .setHeader("Accept-Language", "ru-RU,ru;q=0.9")
//        .setHeader("Origin", "https://www.wildberries.ru")
//        .setHeader("Referer", "https://www.wildberries.ru/catalog/0/search.aspx?search=" + SEARCH_QUERY)
//    );
//  }
//
//  private boolean hasProducts(JsonNode products) {
//    return products.isArray() && !products.isEmpty();
//  }
//
//  private int processProducts(JsonNode products, int minPriceRub, int maxPriceRub) {
//    int pageFound = 0;
//
//    for (JsonNode product : products) {
//      if (shouldProcessProduct(product, minPriceRub, maxPriceRub)) {
//        processValidProduct(product);
//        pageFound++;
//      }
//    }
//
//    return pageFound;
//  }
//
//  private boolean shouldProcessProduct(JsonNode product, int minPriceRub, int maxPriceRub) {
//    String name = getProductNameLower(product);
//    long productId = product.path("id").asLong(0);
//    int priceU = extractPriceU(product);
//
//
//    boolean priceInRange = priceU >= minPriceRub * 100 && priceU <= maxPriceRub * 100;
//
//    return containsMacbook(name)
//        && !containsExcludedKeywords(name)
//        && productId != 0
//        && priceInRange;
//  }
//  private String getProductNameLower(JsonNode product) {
//    return product.path("name").asText("").toLowerCase(Locale.ROOT);
//  }
//
//  private boolean containsMacbook(String nameLower) {
//    return nameLower.contains("macbook");
//  }
//
//  private boolean containsExcludedKeywords(String nameLower) {
//    return EXCLUDED_KEYWORDS.stream().anyMatch(nameLower::contains);
//  }
//
//  private int extractPriceU(JsonNode product) {
//    JsonNode sizes = product.path("sizes");
//    if (sizes.isArray() && !sizes.isEmpty()) {
//      JsonNode firstSize = sizes.get(0);
//      int p = firstSize.path("price").path("product").asInt(0);
//      if (p == 0) p = firstSize.path("price").path("total").asInt(0);
//      if (p != 0) return p;
//    }
//    int p = product.path("salePriceU").asInt(0);
//    if (p != 0) return p;
//    return product.path("priceU").asInt(0);
//  }
//
//  private void processValidProduct(JsonNode product) {
//    String nameOriginal = product.path("name").asText("");
//    long productId = product.path("id").asLong(0);
//    int priceU = extractPriceU(product);
//    double priceRub = priceU / 100.0;
//    String url = "https://www.wildberries.ru/catalog/" + productId + "/detail.aspx";
//
//    log.info("📦 nm:{} | {} — {} ₽ | {}", productId, nameOriginal,
//        String.format(Locale.ROOT, "%.2f", priceRub), url);
//
//    if (priceU < BUY_THRESHOLD_PRICE_U) {
//      log.info("💸 BUY SIGNAL: {}", nameOriginal);
//      saveProduct(product, nameOriginal, priceRub, url, productId);
//    }
//  }
//
//  private void saveProduct(JsonNode product, String nameOriginal, double priceRub, String url, long productId) {
//    TowarEntity towar = TowarEntity.builder()
//        .nmId(productId)
//        .name(nameOriginal)
//        .brand(product.path("brand").asText(""))
//        .priceRub(priceRub)
//        .url(url)
//        .build();
//    macBookRepo.save(towar);
//    telegram.sendMacBookAlert(towar);
//  }
//
//  private void logResults(int totalFound, int minPriceRub, int maxPriceRub) {
//    if (totalFound == 0) {
//      log.info("ℹ️ Ничего не найдено в диапазоне {}–{} ₽ после отсева мусора.", minPriceRub, maxPriceRub);
//    } else {
//      log.info("✅ Всего найдено MacBook {}–{} ₽ (после отсева мусора): {}", minPriceRub, maxPriceRub, totalFound);
//    }
//  }
//
//}
