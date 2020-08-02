package com.blockone.electronicstore.repository

import com.blockone.electronicstore.db.model.BundleDeal
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BundleDealRepository : CrudRepository<BundleDeal, UUID>
