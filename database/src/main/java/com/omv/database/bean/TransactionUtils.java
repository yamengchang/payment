package com.omv.database.bean;

import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * Created by zwj on 2018/7/11.
 */
public class TransactionUtils {
    public static void rollback() {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
}
