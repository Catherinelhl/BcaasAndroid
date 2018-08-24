package io.bcaas.http.JsonTypeAdapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import io.bcaas.vo.GenesisVO;

/**
 * it works
 *
 * */
public class GenesisVOTypeAdapter extends TypeAdapter<GenesisVO> {

    @Override
    public void write(JsonWriter out, GenesisVO value) throws IOException {
        out.beginObject();
        //按自定义顺序输出字段信息
        out.name("_id").value(value.get_id());
        out.name("previous").value(value.getPrevious());
        out.name("blockService").value(value.getBlockService());
        out.name("currencyUnit").value(value.getCurrencyUnit());
        out.name("work").value(value.getWork());
        out.name("systemTime").value(value.getSystemTime());
        out.endObject();
    }

    @Override
    public GenesisVO read(JsonReader in) throws IOException {
        return null;
    }
}
