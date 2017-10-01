package ch.adesso.party;

import java.time.LocalDateTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by aranda on 10.06.2017.
 */
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {


    @Override
    public LocalDateTime unmarshal(String strLocalDate) throws Exception {
        return LocalDateTime.parse(strLocalDate);
    }

    @Override
    public String marshal(LocalDateTime localDateTime) throws Exception {
        return localDateTime.toString();
    }
}
