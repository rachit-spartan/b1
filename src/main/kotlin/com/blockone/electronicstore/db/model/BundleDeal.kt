package com.blockone.electronicstore.db.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "bundle_deals")
data class BundleDeal(
    @Column
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_product_id")
    @JsonManagedReference("main_product")
    val mainProduct: Product,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "free_product_id")
    @JsonManagedReference("free_product")
    val freeProduct: Product
)
