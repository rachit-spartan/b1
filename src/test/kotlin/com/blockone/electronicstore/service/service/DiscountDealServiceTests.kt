// package com.blockone.electronicstore.service
//
// import com.blockone.electronicstore.db.model.DiscountDeal
// import com.blockone.electronicstore.db.model.Product
// import com.blockone.electronicstore.repository.DiscountDealRepository
// import com.blockone.electronicstore.repository.ProductRepository
// import com.nhaarman.mockitokotlin2.mock
// import com.nhaarman.mockitokotlin2.whenever
// import org.joda.money.CurrencyUnit
// import org.joda.money.Money
// import org.junit.jupiter.api.BeforeEach
// import org.junit.jupiter.api.Test
// import java.math.BigDecimal
// import java.util.*
//
// class DiscountDealServiceTests {
//
//    private lateinit var discountDealService: DiscountDealService
//    private val productRepositoryMock: ProductRepository = mock()
//    private val discountDealRepositoryMock: DiscountDealRepository = mock()
//    private val firstProductId = UUID.randomUUID()
//    private val secondProductId = UUID.randomUUID()
//    private val dummyFirstProduct = Product(
//        productId = firstProductId,
//        name = "FirstProduct",
//        description = "",
//        price = Money.of(CurrencyUnit.USD, BigDecimal.TEN)
//    )
//    private val dummySecondProduct = Product(
//        productId = secondProductId,
//        name = "SecondProduct",
//        description = "",
//        price = Money.of(CurrencyUnit.USD, BigDecimal.TEN)
//    )
//    private val dummyDiscountDeal = DiscountDeal(
//        name = "SecondProduct",
//        discount = BigDecimal.TEN
//    )
//
//
//    @BeforeEach
//    fun setup() {
//        discountDealService = DiscountDealService(
//            productRepository = productRepositoryMock,
//            discountDealRepository = discountDealRepositoryMock
//        )
//    }
//
//
//    @Test
//    fun `For an existing discount and two products, we associate the discount to the products`() {
//        whenever(productRepositoryMock.findById(firstProductId)).thenReturn(Optional.of(dummyFirstProduct))
//        whenever(productRepositoryMock.findById(secondProductId)).thenReturn(Optional.of(dummySecondProduct))
//        whenever(discountDealRepositoryMock.findById(firstProductId)).thenReturn(Optional.of(dummyFirstProduct))
//
//    }
// }
