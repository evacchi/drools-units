package org.jbpm.units.internal;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Quick and dirty reflection utility
 */
public class BeanMap extends AbstractMap<String, Object> {

    private final Object bean;
    private final HashMap<String, Entry<String, Object>> descriptors = new HashMap<>();

    public BeanMap(Object bean) {
        this.bean = bean;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                if (propertyDescriptor.getName().equals("class")) continue;
                descriptors.put(propertyDescriptor.getName(), new Entry<String, Object>() {
                    @Override
                    public String getKey() {
                        return propertyDescriptor.getName();
                    }

                    @Override
                    public Object getValue() {
                        try {
                            return propertyDescriptor.getReadMethod().invoke(bean);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public Object setValue(Object o) {
                        try {
                            return propertyDescriptor.getWriteMethod().invoke(bean, o);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object put(String key, Object value) {
        if (descriptors.containsKey(key)) {
            return descriptors.get(key).setValue(value);
        } else {
            return null;
        }
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return new HashSet<>(descriptors.values());
    }
}
