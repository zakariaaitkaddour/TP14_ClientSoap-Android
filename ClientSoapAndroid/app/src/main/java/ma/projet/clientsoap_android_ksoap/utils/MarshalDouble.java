package ma.projet.clientsoap_android_ksoap.utils;

import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * Marshal pour sérialiser/désérialiser les valeurs double dans SOAP
 */
public class MarshalDouble implements Marshal {

    @Override
    public Object readInstance(XmlPullParser parser, String namespace, String name,
                               PropertyInfo expected) throws IOException, XmlPullParserException {
        return Double.parseDouble(parser.nextText());
    }

    @Override
    public void writeInstance(XmlSerializer writer, Object obj) throws IOException {
        writer.text(obj.toString());
    }

    @Override
    public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(envelope.xsd, "double", Double.class, this);
    }
}