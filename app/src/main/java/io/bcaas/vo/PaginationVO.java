package io.bcaas.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 分頁 Object
 *
 * @author Costa Peng
 * @version 1.0.0
 * @since 2018/02/01
 */

public class PaginationVO implements Serializable {

    private static final long serialVersionUID = 2728515357810146958L;

    // Data list
    private List<Object> objectList;
    // Next page objectId
    private String nextObjectId;


    public PaginationVO() {
        super();
    }

    public PaginationVO(String nextObjectId) {
        super();
        this.nextObjectId = nextObjectId;
    }

    public PaginationVO(List<Object> objectList, String nextObjectId) {
        super();
        this.objectList = objectList;
        this.nextObjectId = nextObjectId;
    }

    public List<Object> getObjectList() {
        return objectList;
    }

    public void setObjectList(List<Object> objectList) {
        this.objectList = objectList;
    }

    public String getNextObjectId() {
        return nextObjectId;
    }

    public void setNextObjectId(String nextObjectId) {
        this.nextObjectId = nextObjectId;
    }


}
