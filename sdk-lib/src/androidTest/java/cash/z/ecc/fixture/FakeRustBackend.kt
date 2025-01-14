package cash.z.ecc.fixture

import cash.z.ecc.android.sdk.internal.Backend
import cash.z.ecc.android.sdk.internal.model.JniBlockMeta
import cash.z.ecc.android.sdk.internal.model.JniScanProgress
import cash.z.ecc.android.sdk.internal.model.JniScanRange
import cash.z.ecc.android.sdk.internal.model.JniSubtreeRoot
import cash.z.ecc.android.sdk.internal.model.JniUnifiedSpendingKey

internal class FakeRustBackend(
    override val networkId: Int,
    val metadata: MutableList<JniBlockMeta>
) : Backend {

    override suspend fun writeBlockMetadata(blockMetadata: List<JniBlockMeta>) {
        metadata.addAll(blockMetadata)
    }

    override suspend fun rewindToHeight(height: Long) {
        metadata.removeAll { it.height > height }
    }

    override suspend fun putSaplingSubtreeRoots(
        startIndex: Long,
        roots: List<JniSubtreeRoot>,
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun updateChainTip(height: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getFullyScannedHeight(): Long? {
        TODO("Not yet implemented")
    }

    override suspend fun getMaxScannedHeight(): Long? {
        TODO("Not yet implemented")
    }

    override suspend fun getScanProgress(): JniScanProgress {
        TODO("Not yet implemented")
    }

    override suspend fun suggestScanRanges(): List<JniScanRange> {
        TODO("Not yet implemented")
    }

    override suspend fun getLatestCacheHeight(): Long = metadata.maxOf { it.height }

    override suspend fun getVerifiedTransparentBalance(address: String): Long {
        TODO("Not yet implemented")
    }

    override suspend fun getTotalTransparentBalance(address: String): Long {
        TODO("Not yet implemented")
    }

    override suspend fun putUtxo(
        tAddress: String,
        txId: ByteArray,
        index: Int,
        script: ByteArray,
        value: Long,
        height: Long
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun findBlockMetadata(height: Long): JniBlockMeta? {
        return metadata.findLast { it.height == height }
    }

    override suspend fun rewindBlockMetadataToHeight(height: Long) {
        metadata.removeAll { it.height > height }
    }

    override suspend fun initBlockMetaDb(): Int =
        error("Intentionally not implemented in mocked FakeRustBackend implementation.")

    override suspend fun createToAddress(
        account: Int,
        unifiedSpendingKey: ByteArray,
        to: String,
        value: Long,
        memo: ByteArray?
    ): ByteArray {
        TODO("Not yet implemented")
    }

    override suspend fun shieldToAddress(account: Int, unifiedSpendingKey: ByteArray, memo: ByteArray?): ByteArray {
        TODO("Not yet implemented")
    }

    override suspend fun decryptAndStoreTransaction(tx: ByteArray) =
        error("Intentionally not implemented in mocked FakeRustBackend implementation.")

    override suspend fun initDataDb(seed: ByteArray?): Int =
        error("Intentionally not implemented in mocked FakeRustBackend implementation.")

    override suspend fun createAccount(
        seed: ByteArray,
        treeState: ByteArray,
        recoverUntil: Long?
    ): JniUnifiedSpendingKey =
        error("Intentionally not implemented in mocked FakeRustBackend implementation.")

    override fun isValidShieldedAddr(addr: String): Boolean =
        error("Intentionally not implemented in mocked FakeRustBackend implementation.")

    override fun isValidTransparentAddr(addr: String): Boolean =
        error("Intentionally not implemented in mocked FakeRustBackend implementation.")

    override fun isValidUnifiedAddr(addr: String): Boolean =
        error("Intentionally not implemented in mocked FakeRustBackend implementation.")

    override suspend fun getCurrentAddress(account: Int): String {
        TODO("Not yet implemented")
    }

    override fun getTransparentReceiver(ua: String): String? =
        error("Intentionally not implemented in mocked FakeRustBackend implementation.")

    override fun getSaplingReceiver(ua: String): String? =
        error("Intentionally not implemented in mocked FakeRustBackend implementation.")

    override suspend fun listTransparentReceivers(account: Int): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getBalance(account: Int): Long {
        TODO("Not yet implemented")
    }

    override fun getBranchIdForHeight(height: Long): Long {
        TODO("Not yet implemented")
    }

    override suspend fun getMemoAsUtf8(txId: ByteArray, outputIndex: Int): String? =
        error("Intentionally not implemented in mocked FakeRustBackend implementation.")

    override suspend fun getVerifiedBalance(account: Int): Long {
        TODO("Not yet implemented")
    }

    override suspend fun getNearestRewindHeight(height: Long): Long {
        TODO("Not yet implemented")
    }

    override suspend fun scanBlocks(fromHeight: Long, limit: Long) =
        error("Intentionally not implemented in mocked FakeRustBackend implementation.")
}
