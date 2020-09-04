package tech.neverzore.framework.common.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author zhouzb
 * @date 2019/9/11
 */
public class ReflectionUtil {
    public static Class<?> getSuperClassGenericType(final Class<?> clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        } else {
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            if (index < params.length && index >= 0) {
                if (!(params[index] instanceof Class)) {
                    return Object.class;
                } else {
                    return (Class) params[index];
                }
            } else {
                return Object.class;
            }
        }
    }
}
