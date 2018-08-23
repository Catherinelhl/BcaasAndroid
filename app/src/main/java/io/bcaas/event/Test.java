package io.bcaas.event;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import io.bcaas.ecc.Sha256Tool;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/23
 */
public class Test {

    public static void main(String[] args) {
//        String str="{\"_id\":\"5b7d6ceed4e64b4ba1e9f927\",\"previous\":\"0000000000000000000000000000000000000000000000000000000000000000\",\"blockService\":\"BCC\",\"currencyUnit\":\"BCC\",\"work\":\"0\",\"systemTime\":\"2018-08-22T14:02:21.098Z\"}";
        String str = " {\n" +
                "      \"_id\": \"5b7d6ceed4e64b4ba1e9f927\",\n" +
                "      \"previous\": \"0000000000000000000000000000000000000000000000000000000000000000\",\n" +
                "      \"blockService\": \"BCC\",\n" +
                "      \"currencyUnit\": \"BCC\",\n" +
                "      \"work\": \"0\",\n" +
                "      \"systemTime\": \"2018-08-22T14:02:21.098Z\"\n" +
                "    }";

        try {
            String s = Sha256Tool.doubleSha256ToString(str);
            System.out.println(s);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Observable.interval(100, 1000, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        System.out.println(aLong);
                    }
                });
    }
}
