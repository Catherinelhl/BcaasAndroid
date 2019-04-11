package io.bcaas.listener;
/*
+--------------+---------------------------------
+ author       +   Catherine Liu
+--------------+---------------------------------
+ since        +   2019/4/11 17:19
+--------------+---------------------------------
+ projectName  +   BcaasAndroid
+--------------+---------------------------------
+ packageName  +   io.bcaas.listener
+--------------+---------------------------------
+ description  +   监听app更新结果
+--------------+---------------------------------
+ version      +  
+--------------+---------------------------------
*/

public interface UpdateVersionListener {
    //更新失败
    void updateFailure(String installType);
}
