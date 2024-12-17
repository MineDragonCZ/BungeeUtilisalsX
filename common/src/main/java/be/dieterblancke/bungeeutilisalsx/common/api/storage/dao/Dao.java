package be.dieterblancke.bungeeutilisalsx.common.api.storage.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public interface Dao {

    ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    static String formatDateToString(final Date date) {
        if (date == null) {
            return null;
        }
        return SIMPLE_DATE_FORMAT.get().format(date);
    }

    static Date formatStringToDate(final String date) {
        if (date == null) {
            return null;
        }
        try {
            return SIMPLE_DATE_FORMAT.get().parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }

    static String getInsertDateParameter() {
        return "?";
    }

    UserDao getUserDao();

    OfflineMessageDao getOfflineMessageDao();

    ApiTokenDao getApiTokenDao();
}
