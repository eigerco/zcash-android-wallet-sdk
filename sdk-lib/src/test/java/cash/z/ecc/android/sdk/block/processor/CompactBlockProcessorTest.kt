package cash.z.ecc.android.sdk.block.processor

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CompactBlockProcessorTest {

    @Test
    fun should_refresh_preparation_test() {
        assertTrue {
            CompactBlockProcessor.shouldRefreshPreparation(
                lastPreparationTime = CompactBlockProcessor.SYNCHRONIZATION_RESTART_TIMEOUT,
                currentTimeMillis = CompactBlockProcessor.SYNCHRONIZATION_RESTART_TIMEOUT * 2,
                limitTime = CompactBlockProcessor.SYNCHRONIZATION_RESTART_TIMEOUT
            )
        }
    }

    @Test
    fun should_not_refresh_preparation_test() {
        assertFalse {
            CompactBlockProcessor.shouldRefreshPreparation(
                lastPreparationTime = CompactBlockProcessor.SYNCHRONIZATION_RESTART_TIMEOUT,
                currentTimeMillis = CompactBlockProcessor.SYNCHRONIZATION_RESTART_TIMEOUT,
                limitTime = CompactBlockProcessor.SYNCHRONIZATION_RESTART_TIMEOUT
            )
        }
    }
}
