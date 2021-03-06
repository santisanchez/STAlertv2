/*
 * SODA Android SDK - Socrata, Inc
 *
 * Copyright (C) 2013 Socrata, Inc
 * All rights reserved.
 *
 * Developed for Socrata, Inc by:
 * 47 Degrees, LLC
 * http://47deg.com
 * hello@47deg.com
 */

package com.socrata.android.client.converters;

import com.socrata.android.client.DataTypesMapper;

import java.lang.reflect.Field;

/**
 * @see DataTypeConverter
 */
public class PassthruConverter implements DataTypeConverter<Object> {

    @Override
    public Object getValue(DataTypesMapper dataTypesMapper, Field field, String type, Object value) {
        return value;
    }

}
