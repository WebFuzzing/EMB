package se.devscout.scoutapi.textanalyzer.report;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringMapXmlAdapter extends XmlAdapter<StringMapXmlAdapter.MapStringString, Map<String, String>> {
    @Override
    public Map<String, String> unmarshal(MapStringString v) throws Exception {
        return null;
    }

    @Override
    public MapStringString marshal(Map<String, String> v) throws Exception {
        MapStringString res = new MapStringString();

        Map<String, List<String>> temp = new HashMap<>();

        for (Map.Entry<String, String> entry : v.entrySet()) {
            String from = entry.getKey();
            String to = entry.getValue();

            if (!temp.containsKey(to)) {
                temp.put(to, new ArrayList<String>());
            }
            temp.get(to).add(from);
        }
        for (Map.Entry<String, List<String>> entry : temp.entrySet()) {
            res.entries.add(new MapStringString.Entry(entry.getValue(), entry.getKey()));
        }
        return res;
    }

    public static class MapStringString {
        @XmlElement(name = "v")
        List<Entry> entries = new ArrayList<>();

        public static class Entry {
            @XmlElement(name = "v")
            List<String> from = new ArrayList<>();
            @XmlAttribute
            String to;

            public Entry() {
            }

            public Entry(List<String> from, String to) {
                this.from = from;
                this.to = to;
            }
        }
    }
}
