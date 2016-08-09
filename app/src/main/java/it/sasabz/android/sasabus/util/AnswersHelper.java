package it.sasabz.android.sasabus.util;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.LoginEvent;
import com.crashlytics.android.answers.SearchEvent;
import com.crashlytics.android.answers.SignUpEvent;

import it.sasabz.android.sasabus.BuildConfig;

public final class AnswersHelper {

    public static final String TYPE_LINE_DETAILS = "Line details";
    public static final String TYPE_LINE_PATH = "Line path";
    public static final String TYPE_LINE_COURSE = "Line course";

    public static final String TYPE_BUS = "Bus";
    public static final String TYPE_BUS_STOP = "Bus stop";
    public static final String TYPE_POI = "POI";
    public static final String TYPE_PARKING = "Parking";

    public static final String CATEGORY_ROUTE = "Route";
    public static final String CATEGORY_BUS_STOP = "Bus stop";

    private AnswersHelper() {
    }

    public static void logSearch(String category, String query) {
        if (!BuildConfig.DEBUG) {
            SearchEvent event = new SearchEvent()
                    .putCustomAttribute("Category", category)
                    .putQuery(query);

            Answers.getInstance().logSearch(event);
        }
    }

    public static void logContentView(String type, String name, String id) {
        if (!BuildConfig.DEBUG) {
            ContentViewEvent event = new ContentViewEvent()
                    .putContentType(type)
                    .putContentName(name)
                    .putContentId(id);

            Answers.getInstance().logContentView(event);
        }
    }

    public static void logContentView(String type, String id) {
        if (!BuildConfig.DEBUG) {
            ContentViewEvent event = new ContentViewEvent()
                    .putContentType(type)
                    .putContentId(id);

            Answers.getInstance().logContentView(event);
        }
    }

    public static void logLogin() {
        if (!BuildConfig.DEBUG) {
            LoginEvent event = new LoginEvent()
                    .putSuccess(true);

            Answers.getInstance().logLogin(event);
        }
    }

    public static void logLogin(String error) {
        if (!BuildConfig.DEBUG) {
            LoginEvent event = new LoginEvent()
                    .putSuccess(false)
                    .putCustomAttribute("Error", error);

            Answers.getInstance().logLogin(event);
        }
    }

    public static void logSignUp() {
        if (!BuildConfig.DEBUG) {
            SignUpEvent event = new SignUpEvent()
                    .putSuccess(true);

            Answers.getInstance().logSignUp(event);
        }
    }

    public static void logSignUp(String error) {
        if (!BuildConfig.DEBUG) {
            SignUpEvent event = new SignUpEvent()
                    .putSuccess(false)
                    .putCustomAttribute("Error", error);

            Answers.getInstance().logSignUp(event);
        }
    }
}
