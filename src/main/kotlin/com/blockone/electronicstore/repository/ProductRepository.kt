package com.blockone.electronicstore.repository

import com.blockone.electronicstore.db.model.Product
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductRepository : CrudRepository<Product, UUID> {
    @Query("SELECT p from Product p where p.isAvailableForSale = true and p.productId = ?1")
    override fun findById(productId: UUID): Optional<Product>

    @Query("SELECT p from Product p where p.isAvailableForSale = true")
    override fun findAll(): Iterable<Product>
}
