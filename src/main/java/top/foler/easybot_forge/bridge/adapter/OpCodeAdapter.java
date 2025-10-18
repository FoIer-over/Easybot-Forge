package top.foler.easybot_forge.bridge.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import top.foler.easybot_forge.bridge.OpCode;

import java.io.IOException;

public class OpCodeAdapter extends TypeAdapter<OpCode> {
    @Override
    public void write(JsonWriter out, OpCode opCode) throws IOException {
        out.value(opCode.getValue());
    }

    @Override
    public OpCode read(JsonReader in) throws IOException {
        int value = in.nextInt();
        return OpCode.fromValue(value);
    }
}