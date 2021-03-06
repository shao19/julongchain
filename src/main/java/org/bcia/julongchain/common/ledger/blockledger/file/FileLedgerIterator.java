/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.julongchain.common.ledger.blockledger.file;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.ledger.blockledger.IIterator;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.julongchain.protos.common.Common;

import java.util.AbstractMap;
import java.util.Map;

/**
 * 文件账本迭代器
 *
 * @author sunzongyu
 * @date 2018/04/27
 * @company Dingxuan
 */
public class FileLedgerIterator implements IIterator {
    private static JulongChainLog log = JulongChainLogFactory.getLog(FileLedger.class);
    private FileLedger ledger;
    private long blockNum;
    private IResultsIterator commonIterator;

    public FileLedgerIterator(){}

    public FileLedgerIterator(FileLedger fl, long blockNum, IResultsIterator itr){
        this.ledger = fl;
        this.blockNum = blockNum;
        this.commonIterator = itr;
    }

    /**
     * @return Map.Entry<QueryResult, Common.Status>
     */
    @Override
    public QueryResult next() throws LedgerException {
        Map.Entry<QueryResult, Common.Status> map;
        QueryResult result;
        try {
            result = commonIterator.next();
        } catch (LedgerException e) {
            log.error(e.getMessage(), e);
            map = new AbstractMap.SimpleEntry<>(null, Common.Status.SERVICE_UNAVAILABLE);
            return new QueryResult(map);
        }
        map = new AbstractMap.SimpleEntry<>(result
                , result == null ? Common.Status.SERVICE_UNAVAILABLE : Common.Status.SUCCESS);
        return new QueryResult(map);
    }

    @Override
    public void readyChain() throws LedgerException{
        synchronized (FileLedger.LOCK) {
            if (blockNum > ledger.height() - 1) {
                try {
                    log.debug("Require block num is [{}], ledger height is[{}], wait block append", blockNum, ledger.height());
                    FileLedger.LOCK.wait();
                } catch (InterruptedException e) {
                    throw new LedgerException(e);
                }
            }
        }
    }

    @Override
    public void close() throws LedgerException{
        commonIterator.close();
    }

    public FileLedger getLedger() {
        return ledger;
    }

    public void setLedger(FileLedger ledger) {
        this.ledger = ledger;
    }

    public long getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(long blockNum) {
        this.blockNum = blockNum;
    }

    public IResultsIterator getCommonIterator() {
        return commonIterator;
    }

    public void setCommonIterator(IResultsIterator commonIterator) {
        this.commonIterator = commonIterator;
    }
}
