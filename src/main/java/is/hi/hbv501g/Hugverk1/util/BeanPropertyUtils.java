package is.hi.hbv501g.Hugverk1.util;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeanUtils;
import java.util.HashSet;
import java.util.Set;

public class BeanPropertyUtils {

    public static void copyNonNullProperties(Object src, Object target, String... ignoreProperties) {
        // First get the names of all null properties in the source
        String[] nullPropertyNames = getNullPropertyNames(src);
        // Combine with any additional ignore properties provided
        Set<String> ignoreSet = new HashSet<>();
        if (ignoreProperties != null) {
            for (String prop : ignoreProperties) {
                ignoreSet.add(prop);
            }
        }
        for (String nullProp : nullPropertyNames) {
            ignoreSet.add(nullProp);
        }
        BeanUtils.copyProperties(src, target, ignoreSet.toArray(new String[0]));
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> nullPropertyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            // Exclude "class" property
            if ("class".equals(pd.getName())) continue;
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                nullPropertyNames.add(pd.getName());
            }
        }
        return nullPropertyNames.toArray(new String[0]);
    }
}