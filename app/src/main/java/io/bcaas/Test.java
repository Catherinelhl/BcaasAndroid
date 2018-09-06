package io.bcaas;

import java.security.NoSuchAlgorithmException;

import io.bcaas.tools.ecc.Sha256Tool;

/**
 * @projectName: BcaasAndroid
 * @packageName: io.bcaas
 * @author: catherine
 * @time: 2018/9/6
 */
public class Test {
    public static void main(String[] args) {
        String sha = "{\"_id\":\"5b87d9a3119f7c37a45e5760\",\"tc\":{\"previous\":\"4c44935e5f69b8f827f2493feae613095e468b72f631406edac792a4f2354197\",\"blockService\":\"BCC\",\"blockType\":\"Send\",\"blockTxType\":\"Matrix\",\"destination_wallet\":\"1Q1Jqa8k5H5zKyxZeryXJ49L5bpBHuXzok\",\"balance\":\"400\",\"amount\":\"1\",\"representative\":\"1Q1Jqa8k5H5zKyxZeryXJ49L5bpBHuXzok\",\"wallet\":\"1Q1Jqa8k5H5zKyxZeryXJ49L5bpBHuXzok\",\"work\":\"0\",\"date\":\"1535629726000\"},\"signature\":\"GyWLQPilZdzh6tNCz3bbPm2Al8hQuh8ujQxLbggmW18mC0jrE3ZmSxZUlQnAiZowhEPYbbz3x5ED4C8iAyLqDGQ=\",\"publicKey\":\"04d9b5456bb2197e5a6a2de49b47a27155507c59fcb64cc46ec411c4ca819043c9d1a5ee1991958bae4a5eff496530e7ef0f3e4bd80c75feb20204669352861a1e\",\"height\":16,\"produceKeyType\":\"ECC\",\"systemTime\":\"2018-08-30T11:48:51.303Z\"}";
        try {
            System.out.println(Sha256Tool.doubleSha256ToString(sha));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
