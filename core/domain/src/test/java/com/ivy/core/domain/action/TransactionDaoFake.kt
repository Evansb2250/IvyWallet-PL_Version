package com.ivy.core.domain.action

import androidx.sqlite.db.SupportSQLiteQuery
import com.ivy.core.persistence.dao.trn.AccountIdAndTrnTime
import com.ivy.core.persistence.dao.trn.SaveTrnData
import com.ivy.core.persistence.dao.trn.TransactionDao
import com.ivy.core.persistence.entity.attachment.AttachmentEntity
import com.ivy.core.persistence.entity.trn.TransactionEntity
import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import com.ivy.core.persistence.entity.trn.TrnTagEntity
import com.ivy.data.SyncState

/***
 *  1. findAllBlocking() -> TransactionEntity
 *  2. save() -> SaveTrnData
 */

class TransactionDaoFake : TransactionDao() {
   private val trnData = mutableListOf<TransactionEntity>()
   private val trnTags = mutableMapOf<String, TrnTagEntity>()
   private val attachments = mutableMapOf<String, AttachmentEntity>()
   private val trnMetadata = mutableMapOf<String, TrnMetadataEntity>()

    override suspend fun save(data: SaveTrnData) {
        val trnId: String = data.entity.id // the entity id

        trnData.add(data.entity) // save entity to fake memory storage

        //remove tags from memory
        updateTrnTagsSyncByTrnId(trnId, sync = SyncState.Deleting)
        saveTags(data.tags)

        updateAttachmentsSyncByAssociatedId(trnId, sync = SyncState.Deleting)
        saveAttachments(data.attachments)

        //Delete Existing metadata key-values
        updateMetadataSyncByTrnId(trnId, sync = SyncState.Deleting)
        saveMetadata(data.metadata)

        super.save(data)
    }

    override suspend fun saveTrnEntity(entity: TransactionEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTrnTagsSyncByTrnId(trnId: String, sync: SyncState) {
        val updatedTrnTag: TrnTagEntity? = trnTags[trnId]?.copy(
            sync = sync
        )
        if (updatedTrnTag != null) {
            trnTags.set(trnId, updatedTrnTag)
        }
    }

    override suspend fun saveTags(entity: List<TrnTagEntity>) {
        entity.forEach { newTrnTagEntity ->
            trnTags[newTrnTagEntity.tagId] = newTrnTagEntity
        }
    }

    override suspend fun updateAttachmentsSyncByAssociatedId(
        associatedId: String,
        sync: SyncState
    ) {
        val updatedAttachment = attachments[associatedId]?.copy(
            sync = sync
        )
        if(updatedAttachment != null){
            attachments[associatedId] = updatedAttachment
        }
    }

    override suspend fun saveAttachments(entity: List<AttachmentEntity>) {
        entity.forEach { attachmentEntity ->
            attachments[attachmentEntity.id] = attachmentEntity
        }
    }

    override suspend fun updateMetadataSyncByTrnId(trnId: String, sync: SyncState) {
        trnMetadata[trnId] = trnMetadata[trnId]?.copy(
            sync = sync
        ) ?: return
    }

    override suspend fun saveMetadata(entity: List<TrnMetadataEntity>) {
        entity.forEach { trnMeta ->
            trnMetadata[trnMeta.id] = trnMeta
        }
    }

    override suspend fun findAllBlocking(): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findBySQL(query: SupportSQLiteQuery): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAccountIdAndTimeById(trnId: String): AccountIdAndTrnTime? {
        TODO("Not yet implemented")
    }

    override suspend fun updateTrnEntitySyncById(trnId: String, sync: SyncState) {
        TODO("Not yet implemented")
    }
}