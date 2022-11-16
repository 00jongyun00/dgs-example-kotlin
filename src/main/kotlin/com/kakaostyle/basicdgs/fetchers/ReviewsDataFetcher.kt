package com.kakaostyle.basicdgs.fetchers

import com.kakaostyle.DgsConstants
import com.kakaostyle.basicdgs.dataloaders.ReviewsDataLoader
import com.kakaostyle.basicdgs.service.ReviewsService
import com.kakaostyle.types.Review
import com.kakaostyle.types.Show
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import java.util.concurrent.CompletableFuture
import org.dataloader.DataLoader

@DgsComponent
class ReviewsDataFetcher {

    @DgsData(parentType = DgsConstants.SHOW.TYPE_NAME)
    fun reviews(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Review>> {
        val reviewsDataLoader: DataLoader<Int, List<Review>> = dfe.getDataLoader(ReviewsDataLoader::class.java)
        val show: Show = dfe.getSource()
        val result = reviewsDataLoader.load(show.id)
        return result
    }
}