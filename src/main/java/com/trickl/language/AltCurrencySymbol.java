package com.trickl.language;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AltCurrencySymbol {
  USD("USD", "$", "Dollar Sign"),
  GBP("GBP", "£", "Pound Sign"),
  JPY("JPY", "¥", "Yen Sign"),
  AFN("AFN", "؋", "Afghani Sign"),
  THB("THB", "฿", "Thai Currency Symbol Baht"),
  CRC("CRC", "₡", "Colon Sign"),
  FRF("FRF", "₣", "French Franc Sign"),
  ITL("ITL", "₤", "Lira Sign"),
  NGN("NGN", "₦", "Naira Sign"),
  ESP("ESP", "₧", "Peseta Sign"),
  PKR("PKR", "₨", "Rupee Sign"),
  KRW("KRW", "₩", "Won Sign"),
  ILS("ILS", "₪", "New Sheqel Sign"),
  VND("VND", "₫", "Dong Sign"),
  EUR("EUR", "€", "Euro Sign"),
  LAK("LAK", "₭", "Kip Sign"),
  MNT("MNT", "₮", "Tugrik Sign"),
  GRD("GRD", "₯", "Drachma Sign"),
  MXN("MXN", "₱", "Peso Sign"),
  PYG("PYG", "₲", "Guarani Sign"),
  UAH("UAH", "₴", "Hryvnia Sign"),
  GHS("GHS", "₵", "Cedi Sign"),
  KZT("KZT", "₸", "Tenge Sign"),
  INR("INR", "₹", "Indian Rupee Sign"),
  TRY("TRY", "₺", "Turkish Lira Sign"),
  FIM("FIM", "₻", "Nordic Mark Sign"),
  TMM("TMM", "₼", "Manat Sign"),
  BYR("BYR", "₽", "Ruble Sign"),
  GEL("GEL", "₾", "Lari Sign"),
  IRR("IRR", "﷼", "Rial Sign"),
  USD2("USD", "﹩", "Small Dollar Sign"),
  USD3("USD", "＄", "Fullwidth Dollar Sign"),
  GBP2("GBP", "￡", "Fullwidth Pound Sign"),
  JPY2("JPY", "￥", "Fullwidth Yen Sign"),
  KRW2("KRW", "￦", "Fullwidth Won Sign");

  private final String code;
  private final String symbol;
  private final String name;
}
