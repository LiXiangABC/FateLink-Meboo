package com.crush.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionUtils {

  public static boolean isEmpty(Collection<?> t) {
    return null == t || t.size() < 1;
  }
  public static boolean isNotEmpty(Collection<?> t) {
    return null != t && t.size() > 0;
  }

  public static int getSize(Collection<?> t) {
    return t == null ? 0 : t.size();
  }

  public static <T> List<T> avoidEmptyList(List<T> t) {
    if (t == null) {
      return new ArrayList<T>();
    } else {
      return t;
    }
  }
}
