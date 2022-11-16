package com.kakaostyle.basicdgs.dataloaders

import com.kakaostyle.basicdgs.service.ReviewsService
import com.kakaostyle.types.Review
import com.netflix.graphql.dgs.DgsDataLoader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import org.dataloader.MappedBatchLoader

@DgsDataLoader(name = "reviews", maxBatchSize = 3)
class ReviewsDataLoader(val reviewsService: ReviewsService) : MappedBatchLoader<Int, List<Review>> {
    override fun load(keys: MutableSet<Int>): CompletionStage<Map<Int, List<Review>>> {
        return CompletableFuture.supplyAsync { reviewsService.reviewsForShows(keys.toList()) }
    }
}