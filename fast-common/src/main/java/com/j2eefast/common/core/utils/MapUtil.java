/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.utils;

import java.util.HashMap;


/**
 * Map 工具
 * @author zhouzhou
 * @date 2020-03-12 16:02
 */
public class MapUtil extends HashMap<String, Object> {

	private static final long 					serialVersionUID 					= 1L;

	@Override
	public MapUtil put(String key, Object value) {
		super.put(key, value);
		return this;
	}

}
