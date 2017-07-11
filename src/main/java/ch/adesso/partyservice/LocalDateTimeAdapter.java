package ch.adesso.partyservice;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;

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
