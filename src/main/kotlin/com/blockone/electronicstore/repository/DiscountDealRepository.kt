package com.blockone.electronicstore.repository

import com.blockone.electronicstore.db.model.DiscountDeal
import org.springframework.data.repository.CrudRepository
import java.util.*

interface DiscountDealRepository : CrudRepository<DiscountDeal, UUID>
