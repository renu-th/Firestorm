/*************************************************************************

ADOBE CONFIDENTIAL
Copyright 2019 Adobe
All Rights Reserved.
NOTICE: All information contained herein is, and remains
the property of Adobe and its suppliers, if any. The intellectual
and technical concepts contained herein are proprietary to Adobe
and its suppliers and are protected by all applicable intellectual
property laws, including trade secret and copyright laws.
Dissemination of this information or reproduction of this material
is strictly forbidden unless prior written permission is obtained
from Adobe.
**************************************************************************/

package com.adobe.commerce.ordercapturescripts.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UUIDGeneratorUtil {

    private static final Logger LOG = LoggerFactory.getLogger(UUIDGeneratorUtil.class);

    private static String baseUUID = null;
    private static long incrementingValue = 0;
    private static Random myRand = null;

    UUIDGeneratorUtil() {
        throw new UnsupportedOperationException();
    }

    // MD5 a random string with localhost/date etc will return 128 bits
    // construct a string of 18 characters from those bits.
    public static String getUUID() {
        if (baseUUID == null) {
            getInitialUUID();
        }
        long i = ++incrementingValue;
        if (i >= 10000 || i < 0) {
            incrementingValue = 0;
            i = 0;
        }
        return baseUUID + System.currentTimeMillis() + i;
    }

    private static synchronized void getInitialUUID() {
        if (baseUUID != null) {
            return;
        }
        if (myRand == null) {
            myRand = new Random();
        }

        long rand = myRand.nextLong();
        String sid;

        try {
            sid = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            LOG.info(e.getMessage(), e);
            sid = Thread.currentThread().getName();
        }
        String s1 = sid + ":" + Long.toString(rand);

        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            LOG.debug(e.getMessage(), e);
            throw new UnsupportedOperationException();
        }

        md5.update(s1.getBytes());
        byte[] md5ByteArray = md5.digest();
        StringBuilder builder = new StringBuilder();
        for (byte md5Byte : md5ByteArray) {
            int b = md5Byte & 0xFF;
            builder.append(Integer.toHexString(b));
        }

        int begin = myRand.nextInt();
        if (begin < 0) {
            begin = begin * -1;
        }
        begin = begin % 8;
        baseUUID = builder.toString().substring(begin, begin + 12).toUpperCase();
    }
}
