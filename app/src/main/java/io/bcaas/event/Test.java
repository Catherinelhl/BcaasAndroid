package io.bcaas.event;

import java.security.NoSuchAlgorithmException;

import io.bcaas.ecc.Sha256Tool;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/24
 */
public class Test {
    public static void main(String[] args) {
        String str = " {\"_id\":\"5b7fb6193c87ba2987dbd846\",\"tc\":{\"previous\":\"96afadeff591b742720cacdaf667ab5f454d7deb54665d8b123b866e7cc9c06f\",\"blockService\":\"BCC\",\"blockType\":\"Open\",\"blockTxType\":\"Matrix\",\"amount\":\"100\",\"representative\":\"194nd3nQ4rfPwHL5cyrFwu53TWAZca99yi\",\"wallet\":\"194nd3nQ4rfPwHL5cyrFwu53TWAZca99yi\",\"work\":\"0\",\"date\":\"1535096343428\"},\"signature\":\"HKfRlPYLbBgZ+GN+hzhat83lQFnH0FRtrcu8KLrfg4G/SkVTVHBzd7Fk1UUfELK7AU2FQeFJRqgnjFT2aQ2HH+k=\",\"signatureSend\":\"HGx8g/n/83LKzYoRD9kId44r2PEj5HoBNBsADTviSNfSIRjmSsnUky8m+K4TR5J+owCG6bfhb0rpk8dHjMAULIk=\",\"publicKey\":\"04259b2943d2ecbe300aea876e76952567b54858f9c39c72b3966dfb40aa47db8023f9c2546cce7748cace8c76bbfc8f7a16f66ee8bef4c751289a1e5fc51908f0\",\"height\":1,\"produceKeyType\":\"ECC\",\"systemTime\":\"2018-08-24T07:39:05.839Z\"}";
        try {
            String previousDoubleHashStr = Sha256Tool.doubleSha256ToString(str);
            System.out.println(previousDoubleHashStr);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}
