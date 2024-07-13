package com.util;

import org.hibernate.Session;

@FunctionalInterface
public interface TransactionFunction<T> {
    T apply(Session session) throws Exception;
}
