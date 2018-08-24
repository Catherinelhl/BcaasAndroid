package io.bcaas.tools;

import java.util.List;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 */
public class ListTool {

    //判断当前的list是否为空
    public static  <T> boolean isEmpty(List<T> list) {
        if (list == null) {
            return true;
        } else if (list.size() == 0) {
            return true;
        }
        return false;

    }

}
