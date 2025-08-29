package com.example.digigoods.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.digigoods.exception.InvalidDiscountException;
import com.example.digigoods.model.Discount;
import com.example.digigoods.model.DiscountType;
import com.example.digigoods.repository.DiscountRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("DiscountService Tests")
class DiscountServiceTest {

  @Mock
  private DiscountRepository discountRepository;

  @InjectMocks
  private DiscountService discountService;

  private Discount validDiscount;
  private Discount expiredDiscount;
  private Discount notYetValidDiscount;
  private Discount noUsesLeftDiscount;

  @BeforeEach
  void setUp() {
    LocalDate today = LocalDate.now();

    validDiscount = new Discount();
    validDiscount.setId(1L);
    validDiscount.setCode("VALID20");
    validDiscount.setPercentage(new BigDecimal("20.00"));
    validDiscount.setType(DiscountType.GENERAL);
    validDiscount.setValidFrom(today.minusDays(1));
    validDiscount.setValidUntil(today.plusDays(30));
    validDiscount.setRemainingUses(5);
    validDiscount.setApplicableProducts(new HashSet<>());

    expiredDiscount = new Discount();
    expiredDiscount.setId(2L);
    expiredDiscount.setCode("EXPIRED10");
    expiredDiscount.setPercentage(new BigDecimal("10.00"));
    expiredDiscount.setType(DiscountType.GENERAL);
    expiredDiscount.setValidFrom(today.minusDays(30));
    expiredDiscount.setValidUntil(today.minusDays(1));
    expiredDiscount.setRemainingUses(3);
    expiredDiscount.setApplicableProducts(new HashSet<>());

    notYetValidDiscount = new Discount();
    notYetValidDiscount.setId(3L);
    notYetValidDiscount.setCode("FUTURE15");
    notYetValidDiscount.setPercentage(new BigDecimal("15.00"));
    notYetValidDiscount.setType(DiscountType.GENERAL);
    notYetValidDiscount.setValidFrom(today.plusDays(1));
    notYetValidDiscount.setValidUntil(today.plusDays(30));
    notYetValidDiscount.setRemainingUses(2);
    notYetValidDiscount.setApplicableProducts(new HashSet<>());

    noUsesLeftDiscount = new Discount();
    noUsesLeftDiscount.setId(4L);
    noUsesLeftDiscount.setCode("NOUSES25");
    noUsesLeftDiscount.setPercentage(new BigDecimal("25.00"));
    noUsesLeftDiscount.setType(DiscountType.GENERAL);
    noUsesLeftDiscount.setValidFrom(today.minusDays(1));
    noUsesLeftDiscount.setValidUntil(today.plusDays(30));
    noUsesLeftDiscount.setRemainingUses(0);
    noUsesLeftDiscount.setApplicableProducts(new HashSet<>());
  }

  @Test
  @DisplayName("Given repository with discounts, when getAllDiscounts, then return all discounts")
  void givenRepositoryWithDiscounts_whenGetAllDiscounts_thenReturnAllDiscounts() {
    // Arrange
    List<Discount> expectedDiscounts = List.of(validDiscount, expiredDiscount);
    when(discountRepository.findAll()).thenReturn(expectedDiscounts);

    // Act
    List<Discount> result = discountService.getAllDiscounts();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(expectedDiscounts, result);
    verify(discountRepository).findAll();
  }

  @Test
  @DisplayName("Given empty repository, when getAllDiscounts, then return empty list")
  void givenEmptyRepository_whenGetAllDiscounts_thenReturnEmptyList() {
    // Arrange
    when(discountRepository.findAll()).thenReturn(new ArrayList<>());

    // Act
    List<Discount> result = discountService.getAllDiscounts();

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(discountRepository).findAll();
  }

  @Test
  @DisplayName("Given null discount codes, when validateAndGetDiscounts, then return empty list")
  void givenNullDiscountCodes_whenValidateAndGetDiscounts_thenReturnEmptyList() {
    // Act
    List<Discount> result = discountService.validateAndGetDiscounts(null);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(discountRepository, never()).findAllByCodeIn(anyList());
  }

  @Test
  @DisplayName("Given empty discount codes, when validateAndGetDiscounts, then return empty list")
  void givenEmptyDiscountCodes_whenValidateAndGetDiscounts_thenReturnEmptyList() {
    // Act
    List<Discount> result = discountService.validateAndGetDiscounts(new ArrayList<>());

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(discountRepository, never()).findAllByCodeIn(anyList());
  }

  @Test
  @DisplayName("Given valid discount codes, when validateAndGetDiscounts, then return valid discounts")
  void givenValidDiscountCodes_whenValidateAndGetDiscounts_thenReturnValidDiscounts() {
    // Arrange
    List<String> discountCodes = List.of("VALID20");
    when(discountRepository.findAllByCodeIn(discountCodes)).thenReturn(List.of(validDiscount));

    // Act
    List<Discount> result = discountService.validateAndGetDiscounts(discountCodes);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(validDiscount, result.get(0));
    verify(discountRepository).findAllByCodeIn(discountCodes);
  }

  @Test
  @DisplayName("Given non-existent discount code, when validateAndGetDiscounts, then throw InvalidDiscountException")
  void givenNonExistentDiscountCode_whenValidateAndGetDiscounts_thenThrowInvalidDiscountException() {
    // Arrange
    List<String> discountCodes = List.of("NONEXISTENT");
    when(discountRepository.findAllByCodeIn(discountCodes)).thenReturn(new ArrayList<>());

    // Act & Assert
    InvalidDiscountException exception = assertThrows(InvalidDiscountException.class,
        () -> discountService.validateAndGetDiscounts(discountCodes));

    assertTrue(exception.getMessage().contains("NONEXISTENT"));
    assertTrue(exception.getMessage().contains("discount code not found"));
    verify(discountRepository).findAllByCodeIn(discountCodes);
  }

  @Test
  @DisplayName("Given expired discount code, when validateAndGetDiscounts, then throw InvalidDiscountException")
  void givenExpiredDiscountCode_whenValidateAndGetDiscounts_thenThrowInvalidDiscountException() {
    // Arrange
    List<String> discountCodes = List.of("EXPIRED10");
    when(discountRepository.findAllByCodeIn(discountCodes)).thenReturn(List.of(expiredDiscount));

    // Act & Assert
    InvalidDiscountException exception = assertThrows(InvalidDiscountException.class,
        () -> discountService.validateAndGetDiscounts(discountCodes));

    assertTrue(exception.getMessage().contains("EXPIRED10"));
    assertTrue(exception.getMessage().contains("discount has expired"));
    verify(discountRepository).findAllByCodeIn(discountCodes);
  }

  @Test
  @DisplayName("Given not yet valid discount code, when validateAndGetDiscounts, then throw InvalidDiscountException")
  void givenNotYetValidDiscountCode_whenValidateAndGetDiscounts_thenThrowInvalidDiscountException() {
    // Arrange
    List<String> discountCodes = List.of("FUTURE15");
    when(discountRepository.findAllByCodeIn(discountCodes)).thenReturn(List.of(notYetValidDiscount));

    // Act & Assert
    InvalidDiscountException exception = assertThrows(InvalidDiscountException.class,
        () -> discountService.validateAndGetDiscounts(discountCodes));

    assertTrue(exception.getMessage().contains("FUTURE15"));
    assertTrue(exception.getMessage().contains("discount is not yet valid"));
    verify(discountRepository).findAllByCodeIn(discountCodes);
  }

  @Test
  @DisplayName("Given discount with no uses left, when validateAndGetDiscounts, then throw InvalidDiscountException")
  void givenDiscountWithNoUsesLeft_whenValidateAndGetDiscounts_thenThrowInvalidDiscountException() {
    // Arrange
    List<String> discountCodes = List.of("NOUSES25");
    when(discountRepository.findAllByCodeIn(discountCodes)).thenReturn(List.of(noUsesLeftDiscount));

    // Act & Assert
    InvalidDiscountException exception = assertThrows(InvalidDiscountException.class,
        () -> discountService.validateAndGetDiscounts(discountCodes));

    assertTrue(exception.getMessage().contains("NOUSES25"));
    assertTrue(exception.getMessage().contains("discount has no remaining uses"));
    verify(discountRepository).findAllByCodeIn(discountCodes);
  }

  @Test
  @DisplayName("Given discounts to update, when updateDiscountUsage, then decrease remaining uses and save")
  void givenDiscountsToUpdate_whenUpdateDiscountUsage_thenDecreaseRemainingUsesAndSave() {
    // Arrange
    Discount discount1 = new Discount();
    discount1.setRemainingUses(5);

    Discount discount2 = new Discount();
    discount2.setRemainingUses(3);

    List<Discount> discounts = List.of(discount1, discount2);

    // Act
    discountService.updateDiscountUsage(discounts);

    // Assert
    assertEquals(4, discount1.getRemainingUses());
    assertEquals(2, discount2.getRemainingUses());
    verify(discountRepository, times(2)).save(any(Discount.class));
  }

  @Test
  @DisplayName("Given empty discount list, when updateDiscountUsage, then no repository calls")
  void givenEmptyDiscountList_whenUpdateDiscountUsage_thenNoRepositoryCalls() {
    // Act
    discountService.updateDiscountUsage(new ArrayList<>());

    // Assert
    verify(discountRepository, never()).save(any(Discount.class));
  }

  @Test
  @DisplayName("Given multiple discount codes with one missing, when validateAndGetDiscounts, then throw InvalidDiscountException with missing code")
  void givenMultipleDiscountCodesWithOneMissing_whenValidateAndGetDiscounts_thenThrowInvalidDiscountExceptionWithMissingCode() {
    // Arrange
    List<String> discountCodes = List.of("MISSING1", "VALID25");
    when(discountRepository.findAllByCodeIn(discountCodes)).thenReturn(List.of(validDiscount));

    // Act & Assert
    InvalidDiscountException exception = assertThrows(InvalidDiscountException.class,
        () -> discountService.validateAndGetDiscounts(discountCodes));

    // The exception message format is: "Invalid discount code 'MISSING1': discount code not found"
    assertTrue(exception.getMessage().contains("MISSING1"));
    assertTrue(exception.getMessage().contains("discount code not found"));
    verify(discountRepository).findAllByCodeIn(discountCodes);
  }
}
