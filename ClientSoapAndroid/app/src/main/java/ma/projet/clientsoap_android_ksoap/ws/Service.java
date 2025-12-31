package ma.projet.clientsoap_android_ksoap.ws;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ma.projet.clientsoap_android_ksoap.beans.Compte;
import ma.projet.clientsoap_android_ksoap.beans.TypeCompte;
import ma.projet.clientsoap_android_ksoap.utils.MarshalDouble;

public class Service {
    private static final String TAG = "SoapService";

    private static final String NAMESPACE = "http://ws.soap_jaxws_spring.tp.com/";
    private static final String URL = "http://10.47.55.147:8082/services/ws";

    private static final String METHOD_GET_COMPTES = "getComptes";
    private static final String METHOD_CREATE_COMPTE = "createCompte";
    private static final String METHOD_DELETE_COMPTE = "deleteCompte";

    /**
     * Récupère la liste des comptes depuis le service SOAP
     */
    public List<Compte> getComptes() {
        List<Compte> comptes = new ArrayList<>();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_COMPTES);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = false;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            // Enregistrer MarshalDouble pour la sérialisation des doubles
            MarshalDouble marshalDouble = new MarshalDouble();
            marshalDouble.register(envelope);

            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.debug = true;

            Log.d(TAG, "Calling getComptes...");
            transport.call("", envelope);

            Log.d(TAG, "Request: " + transport.requestDump);
            Log.d(TAG, "Response: " + transport.responseDump);

            // Parse response
            if (envelope.bodyIn instanceof SoapObject) {
                SoapObject response = (SoapObject) envelope.bodyIn;

                // Vérifier si la propriété "return" existe
                if (response.hasProperty("return")) {
                    int propertyCount = response.getPropertyCount();

                    for (int i = 0; i < propertyCount; i++) {
                        String propertyName = response.getPropertyInfo(i).name;

                        if ("return".equals(propertyName)) {
                            Object resultObj = response.getProperty(i);

                            if (resultObj instanceof SoapObject) {
                                SoapObject soapCompte = (SoapObject) resultObj;

                                // Vérifier si c'est un compte unique ou une liste
                                if (soapCompte.hasProperty("id")) {
                                    // C'est un seul compte
                                    Compte compte = parseCompte(soapCompte);
                                    if (compte != null) {
                                        comptes.add(compte);
                                    }
                                } else {
                                    // C'est potentiellement une liste de comptes
                                    for (int j = 0; j < soapCompte.getPropertyCount(); j++) {
                                        Object property = soapCompte.getProperty(j);
                                        if (property instanceof SoapObject) {
                                            SoapObject innerCompte = (SoapObject) property;
                                            Compte compte = parseCompte(innerCompte);
                                            if (compte != null) {
                                                comptes.add(compte);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Log.d(TAG, "Successfully parsed " + comptes.size() + " comptes");

        } catch (Exception e) {
            Log.e(TAG, "Error in getComptes: " + e.getMessage(), e);
            e.printStackTrace();
        }

        return comptes;
    }

    /**
     * Crée un nouveau compte
     */
    public boolean createCompte(double solde, TypeCompte type) {
        try {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_CREATE_COMPTE);

            // Ajouter les paramètres directement
            request.addProperty("solde", solde);
            request.addProperty("type", type.name());

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = false;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            // IMPORTANT: Enregistrer MarshalDouble pour la sérialisation des doubles
            MarshalDouble marshalDouble = new MarshalDouble();
            marshalDouble.register(envelope);

            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.debug = true;

            Log.d(TAG, "Calling createCompte with solde=" + solde + ", type=" + type);
            transport.call("", envelope);

            Log.d(TAG, "Request: " + transport.requestDump);
            Log.d(TAG, "Response: " + transport.responseDump);

            Log.d(TAG, "Compte created successfully");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error in createCompte: " + e.getMessage(), e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime un compte
     */
    public boolean deleteCompte(Long id) {
        try {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_DELETE_COMPTE);
            request.addProperty("id", id);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = false;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);

            // Enregistrer MarshalDouble
            MarshalDouble marshalDouble = new MarshalDouble();
            marshalDouble.register(envelope);

            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.debug = true;

            Log.d(TAG, "Calling deleteCompte with id=" + id);
            transport.call("", envelope);

            Log.d(TAG, "Request: " + transport.requestDump);
            Log.d(TAG, "Response: " + transport.responseDump);

            // Vérifier la réponse
            Object result = envelope.getResponse();
            if (result instanceof SoapPrimitive) {
                boolean success = Boolean.parseBoolean(result.toString());
                Log.d(TAG, "Delete result: " + success);
                return success;
            }

            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error in deleteCompte: " + e.getMessage(), e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Parse un SoapObject en objet Compte
     */
    private Compte parseCompte(SoapObject soapCompte) {
        try {
            Long id = getPropertyAsLong(soapCompte, "id");
            double solde = getPropertyAsDouble(soapCompte, "solde");
            Date dateCreation = parseDate(getPropertyAsString(soapCompte, "dateCreation"));
            String typeStr = getPropertyAsString(soapCompte, "type");
            TypeCompte type = TypeCompte.valueOf(typeStr);

            Log.d(TAG, "Parsed compte: id=" + id + ", solde=" + solde + ", type=" + type);
            return new Compte(id, solde, dateCreation, type);

        } catch (Exception e) {
            Log.e(TAG, "Error parsing Compte: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Récupère une propriété String de manière sécurisée
     */
    private String getPropertyAsString(SoapObject soapObject, String propertyName) {
        try {
            Object property = soapObject.getProperty(propertyName);
            if (property == null) {
                return "";
            }
            if (property instanceof SoapPrimitive) {
                return ((SoapPrimitive) property).toString();
            }
            return property.toString();
        } catch (Exception e) {
            Log.w(TAG, "Error getting property " + propertyName + ": " + e.getMessage());
            return "";
        }
    }

    /**
     * Récupère une propriété Long de manière sécurisée
     */
    private Long getPropertyAsLong(SoapObject soapObject, String propertyName) {
        try {
            String value = getPropertyAsString(soapObject, propertyName);
            if (value.isEmpty() || value.equals("anyType{}")) {
                return null;
            }
            return Long.parseLong(value);
        } catch (Exception e) {
            Log.w(TAG, "Error parsing Long property " + propertyName + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Récupère une propriété Double de manière sécurisée
     */
    private double getPropertyAsDouble(SoapObject soapObject, String propertyName) {
        try {
            String value = getPropertyAsString(soapObject, propertyName);
            if (value.isEmpty() || value.equals("anyType{}")) {
                return 0.0;
            }
            return Double.parseDouble(value);
        } catch (Exception e) {
            Log.w(TAG, "Error parsing Double property " + propertyName + ": " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Parse une date au format ISO
     */
    private Date parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return new Date();
        }

        try {
            // Essayer plusieurs formats de date
            String[] formats = {
                    "yyyy-MM-dd",
                    "yyyy-MM-dd'T'HH:mm:ss",
                    "yyyy-MM-dd'T'HH:mm:ss.SSS",
                    "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                    "yyyy-MM-dd'T'HH:mm:ssXXX"
            };

            for (String format : formats) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                    return sdf.parse(dateString);
                } catch (Exception e) {
                    // Continuer avec le format suivant
                }
            }

            Log.w(TAG, "Could not parse date: " + dateString);
            return new Date();

        } catch (Exception e) {
            Log.e(TAG, "Error parsing date: " + e.getMessage());
            return new Date();
        }
    }
}