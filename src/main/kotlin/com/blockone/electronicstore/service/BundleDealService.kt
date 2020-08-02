package com.blockone.electronicstore.service

import com.blockone.electronicstore.db.model.BundleDeal
import com.blockone.electronicstore.repository.BundleDealRepository
import com.blockone.electronicstore.repository.ProductRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class BundleDealService(
    private val bundleDealRepository: BundleDealRepository,
    private val productRepository: ProductRepository
) {

    fun createBundleDeal(
        name: String,
        mainProductId: UUID,
        discountedProductId: UUID
    ): BundleDeal = bundleDealRepository.save(
        BundleDeal(
            name = name,
            mainProduct = productRepository.findById(mainProductId).get(),
            freeProduct = productRepository.findById(discountedProductId).get()
        )
    )
}
