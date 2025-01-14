package cash.z.ecc.android.sdk.internal

import cash.z.ecc.android.sdk.internal.model.JniBlockMeta
import cash.z.ecc.android.sdk.internal.model.JniSubtreeRoot
import cash.z.ecc.android.sdk.internal.model.ScanProgress
import cash.z.ecc.android.sdk.internal.model.ScanRange
import cash.z.ecc.android.sdk.internal.model.SubtreeRoot
import cash.z.ecc.android.sdk.internal.model.TreeState
import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.FirstClassByteArray
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.android.sdk.model.WalletBalance
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZcashNetwork
import kotlinx.coroutines.withContext

@Suppress("TooManyFunctions")
internal class TypesafeBackendImpl(private val backend: Backend) : TypesafeBackend {

    override val network: ZcashNetwork
        get() = ZcashNetwork.from(backend.networkId)

    override suspend fun createAccountAndGetSpendingKey(
        seed: ByteArray,
        treeState: TreeState,
        recoverUntil: BlockHeight?
    ): UnifiedSpendingKey {
        return UnifiedSpendingKey(
            backend.createAccount(
                seed = seed,
                treeState = treeState.encoded,
                recoverUntil = recoverUntil?.value
            )
        )
    }

    @Suppress("LongParameterList")
    override suspend fun createToAddress(
        usk: UnifiedSpendingKey,
        to: String,
        value: Long,
        memo: ByteArray?
    ): FirstClassByteArray = FirstClassByteArray(
        backend.createToAddress(
            usk.account.value,
            usk.copyBytes(),
            to,
            value,
            memo
        )
    )

    override suspend fun shieldToAddress(
        usk: UnifiedSpendingKey,
        memo: ByteArray?
    ): FirstClassByteArray = FirstClassByteArray(
        backend.shieldToAddress(
            usk.account.value,
            usk.copyBytes(),
            memo
        )
    )

    override suspend fun getCurrentAddress(account: Account): String {
        return backend.getCurrentAddress(account.value)
    }

    override suspend fun listTransparentReceivers(account: Account): List<String> {
        return backend.listTransparentReceivers(account.value)
    }

    override suspend fun getBalance(account: Account): Zatoshi {
        return Zatoshi(backend.getBalance(account.value))
    }

    override fun getBranchIdForHeight(height: BlockHeight): Long {
        return backend.getBranchIdForHeight(height.value)
    }

    override suspend fun getVerifiedBalance(account: Account): Zatoshi {
        return Zatoshi(backend.getVerifiedBalance(account.value))
    }

    override suspend fun getNearestRewindHeight(height: BlockHeight): BlockHeight {
        return BlockHeight.new(
            ZcashNetwork.from(backend.networkId),
            backend.getNearestRewindHeight(height.value)
        )
    }

    override suspend fun rewindToHeight(height: BlockHeight) {
        backend.rewindToHeight(height.value)
    }

    override suspend fun getLatestCacheHeight(): BlockHeight? {
        return backend.getLatestCacheHeight()?.let {
            BlockHeight.new(
                ZcashNetwork.from(backend.networkId),
                it
            )
        }
    }

    override suspend fun findBlockMetadata(height: BlockHeight): JniBlockMeta? {
        return backend.findBlockMetadata(height.value)
    }

    override suspend fun rewindBlockMetadataToHeight(height: BlockHeight) {
        backend.rewindBlockMetadataToHeight(height.value)
    }

    override suspend fun getDownloadedUtxoBalance(address: String): WalletBalance {
        // Note this implementation is not ideal because it requires two database queries without a transaction, which
        // makes the data potentially inconsistent.  However the verified amount is queried first which makes this less
        // bad.
        val verified = withContext(SdkDispatchers.DATABASE_IO) {
            backend.getVerifiedTransparentBalance(address)
        }
        val total = withContext(SdkDispatchers.DATABASE_IO) {
            backend.getTotalTransparentBalance(
                address
            )
        }
        return WalletBalance(Zatoshi(total), Zatoshi(verified))
    }

    @Suppress("LongParameterList")
    override suspend fun putUtxo(
        tAddress: String,
        txId: ByteArray,
        index: Int,
        script: ByteArray,
        value: Long,
        height: BlockHeight
    ) {
        return backend.putUtxo(
            tAddress,
            txId,
            index,
            script,
            value,
            height.value
        )
    }

    override suspend fun getMemoAsUtf8(txId: ByteArray, outputIndex: Int): String? =
        backend.getMemoAsUtf8(txId, outputIndex)

    override suspend fun initDataDb(seed: ByteArray?): Int = backend.initDataDb(seed)

    override suspend fun putSaplingSubtreeRoots(startIndex: Long, roots: List<SubtreeRoot>) =
        backend.putSaplingSubtreeRoots(
            startIndex = startIndex,
            roots = roots.map {
                JniSubtreeRoot.new(
                    rootHash = it.rootHash,
                    completingBlockHeight = it.completingBlockHeight.value
                )
            }
        )

    override suspend fun updateChainTip(height: BlockHeight) = backend.updateChainTip(height.value)

    override suspend fun getFullyScannedHeight(): BlockHeight? {
        return backend.getFullyScannedHeight()?.let {
            BlockHeight.new(
                ZcashNetwork.from(backend.networkId),
                it
            )
        }
    }

    override suspend fun getMaxScannedHeight(): BlockHeight? {
        return backend.getMaxScannedHeight()?.let {
            BlockHeight.new(
                ZcashNetwork.from(backend.networkId),
                it
            )
        }
    }

    override suspend fun scanBlocks(fromHeight: BlockHeight, limit: Long) = backend.scanBlocks(fromHeight.value, limit)

    override suspend fun getScanProgress(): ScanProgress? = backend.getScanProgress()?.let { jniScanProgress ->
        ScanProgress.new(jniScanProgress)
    }

    override suspend fun suggestScanRanges(): List<ScanRange> = backend.suggestScanRanges().map { jniScanRange ->
        ScanRange.new(
            jniScanRange,
            network
        )
    }

    override suspend fun decryptAndStoreTransaction(tx: ByteArray) = backend.decryptAndStoreTransaction(tx)

    override fun getSaplingReceiver(ua: String): String? = backend.getSaplingReceiver(ua)

    override fun getTransparentReceiver(ua: String): String? = backend.getTransparentReceiver(ua)

    override suspend fun initBlockMetaDb(): Int = backend.initBlockMetaDb()

    override suspend fun writeBlockMetadata(blockMetadata: List<JniBlockMeta>) =
        backend.writeBlockMetadata(blockMetadata)

    override fun isValidShieldedAddr(addr: String): Boolean = backend.isValidShieldedAddr(addr)

    override fun isValidTransparentAddr(addr: String): Boolean = backend.isValidTransparentAddr(addr)

    override fun isValidUnifiedAddr(addr: String): Boolean = backend.isValidUnifiedAddr(addr)
}
