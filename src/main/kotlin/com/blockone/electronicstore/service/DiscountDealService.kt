package com.blockone.electronicstore.service

import com.blockone.electronicstore.db.model.DiscountDeal
import com.blockone.electronicstore.repository.DiscountDealRepository
import com.blockone.electronicstore.repository.ProductRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class DiscountDealService(
    private val discountDealRepository: DiscountDealRepository,
    private val productRepository: ProductRepository
) {

    fun createDeal(
        name: String,
        discount: BigDecimal,
        mainProductId: UUID,
        discountedProductId: UUID
    ): DiscountDeal = discountDealRepository.save(
        DiscountDeal(
            name = name,
            discount = discount,
            mainProduct = productRepository.findById(mainProductId).get(),
            discountedProduct = productRepository.findById(discountedProductId).get()
        )
    )
}
