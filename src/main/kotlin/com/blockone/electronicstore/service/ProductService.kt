package com.blockone.electronicstore.service

import com.blockone.electronicstore.db.model.Product
import com.blockone.electronicstore.repository.ProductRepository
import com.blockone.electronicstore.util.SupportException
import org.joda.money.Money
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.Exception
import java.math.BigDecimal
import java.sql.SQLException
import java.util.*

@Service
class ProductService(
    private val productRepository: ProductRepository
) {
    private val log: Logger = LoggerFactory.getLogger(ProductService::class.java)

    fun storeProduct(product: Product): Product {
        log.info("Storing new product: $product")
        return try {
            productRepository.save(product)
        } catch (e: SQLException) {
            log.error("Exception $e thrown while saving product: $product")
            throw SupportException("Error while saving product $product")
        }
    }
    fun getProductById(id: UUID): Product? {
        log.info("Getting product for id: $id")
        return try {
            val product = productRepository.findById(id)
            when {
                product.isEmpty -> null
                else -> product.get()
            }
        } catch (e: Exception) {
            log.error("Exception $e thrown while getting product for id: $id")
            throw SupportException("Error while getting product for id: $id")
        }
    }
    fun getAllProducts(): List<Product>? {
        log.info("Getting all products")
        return try {
            productRepository.findAll().toList()
        } catch (e: Exception) {
            log.error("Exception $e thrown while getting all products")
            throw SupportException("Error while getting all products")
        }
    }

    fun updateProduct(id: UUID, description: String?, price: BigDecimal?): Product? {
        val product = getProductById(id)
        return if (product != null) {
            productRepository.save(
                product.copy(
                    description = description ?: product.description,
                    price = if (price != null) Money.of(product.price.currencyUnit, price) else product.price
                )
            )
        } else null
    }

    fun disableProduct(id: UUID): Product? {
        val product = getProductById(id)
        return if (product != null) {
            productRepository.save(product.copy(isAvailableForSale = false))
        } else null
    }
}
